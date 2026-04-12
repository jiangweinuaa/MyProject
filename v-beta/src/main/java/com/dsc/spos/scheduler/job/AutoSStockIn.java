package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.*;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

//********************采购入库自动收货**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoSStockIn extends InitJob{
    Logger logger = LogManager.getLogger(AutoSStockIn.class.getName());
    static boolean bRun=false;//标记此服务是否正在执行中
    public String doExe() {
        //此服务是否正在执行中
        //返回信息
        String sReturnInfo="";
        
        if (bRun) {
            logger.info("\r\n*********采购入库AutoSStockIn正在执行中,本次调用取消:************\r\n");
            sReturnInfo="采购入库AutoSStockIn正在执行中！";
            return sReturnInfo;
        }
        bRun=true;
        logger.info("\r\n*********采购入库AutoSStockIn定时调用Start:************\r\n");
        
        try {
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            //先查 job 执行时间，然后再执行后续操作
            String getTimeSql = "select * from job_quartz_detail where job_name = 'AutoSStockIn'  and STATUS = '100' ";
            List<Map<String, Object>> getTimeDatas=this.doQueryData(getTimeSql, null);
            if(getTimeDatas!= null && !getTimeDatas.isEmpty()) {
                boolean isTime = false;
                for (Map<String, Object> map : getTimeDatas) {
                    String beginTime = map.get("BEGIN_TIME").toString();
                    String endTime = map.get("END_TIME").toString();
                    // 如果当前时间在 执行时间范围内，  就执行自动收货
                    if(sTime.compareTo(beginTime)>=0 && sTime.compareTo(endTime)<0) {
                        isTime = true;
                        break;
                    }
                }
                if(!isTime) {
                    return sReturnInfo;
                }
            }
            
            //查询所有未入库的采购通知单
            String sql=" select a.* from dcp_receiving a"
                    + " left join dcp_sstockin b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.ofno"
                    + " where a.doc_type='2' and a.status='6' and a.receiptdate<='"+sDate+"' and b.sstockinno is null"
                    + " order by a.eid,a.shopid,a.receivingno";
            
            List<Map<String, Object>> getHead=this.doQueryData(sql, null);
            if(getHead!=null && !getHead.isEmpty()){
                for (Map<String, Object> oneDataHead : getHead) {
                    //事务执行
                    List<DataProcessBean> dataProcess = new ArrayList<DataProcessBean>();
                    //门店是否启用采购自动收货
                    String eId = oneDataHead.get("EID").toString();
                    String shopId = oneDataHead.get("SHOPID").toString();
                    String loadDocNo = oneDataHead.get("LOAD_DOCNO").toString();
                    String IsAutoSStockIn = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "IsAutoSStockIn");
                    if(Check.Null(IsAutoSStockIn) || !IsAutoSStockIn.equals("Y") ) {
                        continue;
                    }
                    //处理预计到货日
                    String receiptDate = oneDataHead.get("RECEIPTDATE").toString();
                    String AutoSStockInDay = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "AutoSStockInDay");
                    if (!PosPub.isNumeric(AutoSStockInDay)){
                        AutoSStockInDay = "0";
                    }
                    //预计到货日为空不做自动收货 和龙海确认 20220419 by jinzma
                    if (Check.Null(receiptDate)){
                        continue;
                    }
                    receiptDate = PosPub.GetStringDate(receiptDate,Integer.parseInt(AutoSStockInDay));
                    if (PosPub.compare_date(sDate,receiptDate) < 0){
                        continue;
                    }
                    
                    //通过来源单号再判断一次是否已经手工收货，防止重复收货  BY JZMA 20200106
                    sql = " select * from dcp_sstockin "
                            + " where eid='"+eId+"' and shopid='"+shopId+"' and load_docno='"+loadDocNo+"' ";
                    List<Map<String, Object>> checkLoadDocNo = this.doQueryData(sql, null);
                    if(checkLoadDocNo!=null && !checkLoadDocNo.isEmpty()) {
                        //此单据已经手工收货
                        continue;
                    }
                    
                    try {
                        String warehouse = oneDataHead.get("WAREHOUSE").toString();
                        ///获取门店仓库
                        if (Check.Null(warehouse)) {
                            //取门店的配送收货的默认仓库
                            sql = " select in_cost_warehouse from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
                            List<Map<String, Object>> getWarehouse = this.doQueryData(sql, null);
                            if(getWarehouse==null || getWarehouse.isEmpty()) {
                                //默认收货仓没有直接跳过
                                logger.info("\r\n*********采购入库AutoSStockIn 门店:"+shopId+"组织资料为空************\r\n");
                                continue;
                            }
                            warehouse = getWarehouse.get(0).get("IN_COST_WAREHOUSE").toString();
                            if (Check.Null(warehouse)) {
                                logger.info("\r\n*********采购入库AutoSStockIn 门店:"+shopId+"仓库资料为空************\r\n");
                                continue;
                            }
                        }
                        //查找DCP_RECEIVING_DETAIL的表数据
                        String receivingNo = oneDataHead.get("RECEIVINGNO").toString();
                        sql="  select * from dcp_receiving_detail where eid='"+eId+"' and shopid='"+shopId+"' and receivingno='"+receivingNo+"'";
                        List<Map<String, Object>> getDetail=this.doQueryData(sql, null);
                        //插入DCP_SSTOCKIN和DCP_SSTOCKIN_DETAIL
                        String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, eId, shopId);
                        String sStockInNo = getSStockInNo(eId,shopId,accountDate);
                        int item=1;
                        for (Map<String, Object> oneDetail : getDetail) {
                            String[] columnsName = {
                                    "EID","SHOPID","ORGANIZATIONNO","SSTOCKINNO","ITEM","OITEM",
                                    "PLUNO","FEATURENO","PQTY","PUNIT","BASEQTY","BASEUNIT","UNIT_RATIO",
                                    "PRICE","DISTRIPRICE","AMT","DISTRIAMT",
                                    "POQTY","RECEIVING_QTY","RETWQTY","STOCKIN_QTY",
                                    "PLU_MEMO","PROC_RATE","WAREHOUSE","BATCH_NO","PROD_DATE","BDATE"
                            };
                            
                            String featureNo=oneDetail.get("FEATURENO").toString();
                            if (Check.Null(featureNo)) {
                                featureNo = " ";
                            }
                            
                            DataValue[] insValue = new DataValue[] {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(sStockInNo, Types.VARCHAR),
                                    new DataValue(item, Types.VARCHAR),
                                    new DataValue(oneDetail.get("ITEM").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("PLUNO").toString(), Types.VARCHAR),
                                    new DataValue(featureNo, Types.VARCHAR),
                                    new DataValue(oneDetail.get("PQTY").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("PUNIT").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("BASEQTY").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("BASEUNIT").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("PRICE").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("AMT").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("DISTRIAMT").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("POQTY").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("PQTY").toString(), Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),
                                    new DataValue(oneDetail.get("PQTY").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("PLU_MEMO").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("PROC_RATE").toString(), Types.VARCHAR),
                                    new DataValue(warehouse, Types.VARCHAR),
                                    new DataValue(oneDetail.get("BATCH_NO").toString(), Types.VARCHAR),
                                    new DataValue(oneDetail.get("PROD_DATE").toString(), Types.VARCHAR),
                                    new DataValue(accountDate, Types.VARCHAR),
                            };
                            
                            // 插入DCP_SSTOCKIN_DETAIL
                            InsBean ib = new InsBean("DCP_SSTOCKIN_DETAIL", columnsName);
                            ib.addValues(insValue);
                            dataProcess.add(new DataProcessBean(ib));
                            
                            //再插入库存流水表
                            String procedure="SP_DCP_StockChange";
                            Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1,eId);                                         //--企业ID
                            inputParameter.put(2,shopId);                                      //--组织
                            inputParameter.put(3,"05");                                        //--单据类型
                            inputParameter.put(4,sStockInNo);	                               //--单据号
                            inputParameter.put(5,item);                                        //--单据行号
                            inputParameter.put(6,"1");                                         //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(7,accountDate);                                 //--营业日期 yyyy-MM-dd
                            inputParameter.put(8,oneDetail.get("PLUNO").toString());           //--品号
                            inputParameter.put(9,featureNo);                                   //--特征码
                            inputParameter.put(10,warehouse);                                  //--仓库
                            inputParameter.put(11,oneDetail.get("BATCH_NO").toString());       //--批号
                            inputParameter.put(12,oneDetail.get("PUNIT").toString());          //--交易单位
                            inputParameter.put(13,oneDetail.get("PQTY").toString());           //--交易数量
                            inputParameter.put(14,oneDetail.get("BASEUNIT").toString());       //--基准单位
                            inputParameter.put(15,oneDetail.get("BASEQTY").toString());        //--基准数量
                            inputParameter.put(16,oneDetail.get("UNIT_RATIO").toString());     //--换算比例
                            inputParameter.put(17,oneDetail.get("PRICE").toString());          //--零售价
                            inputParameter.put(18,oneDetail.get("AMT").toString());            //--零售金额
                            inputParameter.put(19,oneDetail.get("DISTRIPRICE").toString());    //--进货价
                            inputParameter.put(20,oneDetail.get("DISTRIAMT").toString());      //--进货金额
                            inputParameter.put(21,accountDate);                                //--入账日期 yyyy-MM-dd
                            inputParameter.put(22,oneDetail.get("PROD_DATE").toString());      //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(23,sDate);                                      //--单据日期
                            inputParameter.put(24,"");                                         //--异动原因
                            inputParameter.put(25,"系统自动收货");                               //--异动描述
                            inputParameter.put(26,"admin");                                    //--操作员
                            
                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            dataProcess.add(new DataProcessBean(pdb));
                            inputParameter=null;
                            
                            item++;
                        }
                        
                        //DCP_SSTOCKIN插入
                        {
                            String[] columns = {
                                    "EID", "SHOPID", "ORGANIZATIONNO", "SSTOCKINNO", "SSTOCKIN_ID", "WAREHOUSE",
                                    "OFNO", "DOC_TYPE", "OTYPE", "BDATE", "SUPPLIER", "PTEMPLATENO", "MEMO", "STATUS",
                                    "TAXCODE", "BUYERNO", "BUYERNAME", "RECEIPTDATE",
                                    "TOT_CQTY", "TOT_PQTY", "TOT_AMT", "TOT_DISTRIAMT",
                                    "PROCESS_STATUS", "PROCESS_ERP_NO", "PROCESS_ERP_ORG",
                                    "LOAD_DOCTYPE", "LOAD_DOCNO", "LOAD_RECEIPTNO",
                                    "CREATEBY", "CREATE_DATE", "CREATE_TIME",
                                    "MODIFYBY", "MODIFY_DATE", "MODIFY_TIME",
                                    "SUBMITBY", "SUBMIT_DATE", "SUBMIT_TIME",
                                    "CONFIRMBY", "CONFIRM_DATE", "CONFIRM_TIME",
                                    "ACCOUNTBY", "ACCOUNT_DATE", "ACCOUNT_TIME",
                                    "UPDATE_TIME","TRAN_TIME"
                            };
                            DataValue[] insValue = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(sStockInNo, Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),    //SSTOCKIN_ID
                                    new DataValue(warehouse, Types.VARCHAR),
                                    new DataValue(oneDataHead.get("RECEIVINGNO").toString(), Types.VARCHAR),
                                    new DataValue("2", Types.VARCHAR),
                                    new DataValue("2", Types.VARCHAR),
                                    new DataValue(accountDate, Types.VARCHAR),
                                    new DataValue(oneDataHead.get("SUPPLIER").toString(), Types.VARCHAR),
                                    new DataValue(oneDataHead.get("PTEMPLATENO").toString(), Types.VARCHAR),
                                    new DataValue("系统自动收货", Types.VARCHAR),
                                    new DataValue("2", Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),   //TAXCODE
                                    new DataValue("", Types.VARCHAR),   //BUYERNO
                                    new DataValue("", Types.VARCHAR),   //BUYERNAME
                                    new DataValue(oneDataHead.get("RECEIPTDATE").toString(), Types.VARCHAR),
                                    new DataValue(oneDataHead.get("TOT_CQTY").toString(), Types.VARCHAR),
                                    new DataValue(oneDataHead.get("TOT_PQTY").toString(), Types.VARCHAR),
                                    new DataValue(oneDataHead.get("TOT_AMT").toString(), Types.VARCHAR),
                                    new DataValue(oneDataHead.get("TOT_DISTRIAMT").toString(), Types.VARCHAR),
                                    new DataValue("N", Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),    //PROCESS_ERP_NO
                                    new DataValue("", Types.VARCHAR),    //PROCESS_ERP_ORG
                                    new DataValue(oneDataHead.get("LOAD_DOCTYPE").toString(), Types.VARCHAR),
                                    new DataValue(oneDataHead.get("LOAD_DOCNO").toString(), Types.VARCHAR),
                                    new DataValue(oneDataHead.get("LOAD_RECEIPTNO").toString(), Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(sDate, Types.VARCHAR),
                                    new DataValue(sTime, Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(sDate, Types.VARCHAR),
                                    new DataValue(sTime, Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(sDate, Types.VARCHAR),
                                    new DataValue(sTime, Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(sDate, Types.VARCHAR),
                                    new DataValue(sTime, Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(sDate, Types.VARCHAR),
                                    new DataValue(sTime, Types.VARCHAR),
									new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
									new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                            };
                            
                            InsBean ib = new InsBean("DCP_SSTOCKIN", columns);
                            ib.addValues(insValue);
                            dataProcess.add(new DataProcessBean(ib));
                        }
                        
                        //更新一下收货通知单的status
                        UptBean ub = new UptBean("DCP_RECEIVING");
                        ub.addUpdateValue("STATUS", new DataValue("7", Types.VARCHAR));
                        ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                        ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                        //Condition
                        ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub.addCondition("RECEIVINGNO", new DataValue(receivingNo, Types.VARCHAR));
                        dataProcess.add(new DataProcessBean(ub));
                        
                        
                        //插入单据检查表 DCP_PLATFORM_BILLCHECK，避免单号重复
                        String[] columns_check = {
                                "EID", "SHOPID", "BILLTYPE", "BILLNO"
                        } ;
                        DataValue[] insValue_check = new DataValue[] {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue("receivingSStockIn", Types.VARCHAR),
                                new DataValue(loadDocNo, Types.VARCHAR),
                        };
                        InsBean ib_check = new InsBean("DCP_PLATFORM_BILLCHECK", columns_check);
                        ib_check.addValues(insValue_check);
                        dataProcess.add(new DataProcessBean(ib_check));
                        
                        StaticInfo.dao.useTransactionProcessData(dataProcess);
                        dataProcess=null;

                        //***********调用库存同步给三方，这是个异步，不会影响效能*****************
                        try
                        {
                            WebHookService.stockSync(eId,shopId,sStockInNo);
                        }
                        catch (Exception e)
                        {

                        }
                        //删除JOB异常日志（不区分门店，只记录一笔)
                        InsertWSLOG.delete_JOBLOG(eId," ","AutoSStockIn");
                        
                    } catch (Exception e) {
                        //保存JOB异常日志（不区分门店，只记录一笔)
                        InsertWSLOG.insert_JOBLOG(eId," ","AutoSStockIn", "采购入库自动收货", e.getMessage());
                    }
                }
            }
            
            
        }
        catch (Exception e) {
            logger.error("\r\n******采购入库AutoSStockIn执行异常" + e.getMessage()+"\r\n******\r\n");
            sReturnInfo="执行异常:" + e.getMessage();
        } finally {
            bRun=false;
            logger.info("\r\n*********采购入库AutoSStockIn定时调用End:************\r\n");
        }
        
        return sReturnInfo;
    }
    
    private String getSStockInNo(String eId,String shopId,String accountDate) throws Exception {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sStockInNo = "CGRK" + accountDate; // 1.自采 2.统采 3.门店直供
        String sql = " select max(sstockinno) as sstockinno  from dcp_sstockin"
                + " where eid = '"+eId+"'  and shopid = '"+shopId+"' and sstockinno like '%"+sStockInNo+"%'";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        sStockInNo = getQData.get(0).get("SSTOCKINNO").toString();
        if (!Check.Null(sStockInNo)) {
            long i;
            sStockInNo = sStockInNo.substring(4);
            i = Long.parseLong(sStockInNo) + 1;
            sStockInNo = i + "";
            sStockInNo = "CGRK" + sStockInNo;
        } else {
            sStockInNo = "CGRK" + accountDate + "00001";
        }
        
        return sStockInNo;
    }
    
    
}
