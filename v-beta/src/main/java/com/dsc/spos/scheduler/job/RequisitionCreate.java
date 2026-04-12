package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.imp.json.DCP_ConversionTimeFormat;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;

//********************要货申请单上传**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RequisitionCreate extends InitJob {
    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";
    
    public RequisitionCreate() {
    
    }
    
    public RequisitionCreate(String eId,String shopId,String organizationNO, String billNo) {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }
    
    Logger logger = LogManager.getLogger(RequisitionCreate.class.getName());
    
    static boolean bRun=false;//标记此服务是否正在执行中
    public String doExe() {
        //返回信息
        String sReturnInfo="";
        //此服务是否正在执行中
        if (bRun && pEId.equals("")) {
            logger.info("\r\n*********要货单requisition.create正在执行中,本次调用取消:************\r\n");
            sReturnInfo="定时传输任务-要货单requisition.create正在执行中！";
            return sReturnInfo;
        }
        
        bRun=true;//
        logger.info("\r\n*********要货单requisition.create定时调用Start:************\r\n");
        
        try {
            //POrder抛转
            String sql = this.getQuerySql100_01();
            List<Map<String, Object>> getQData100_01 = this.doQueryData(sql,null);
            
            if (getQData100_01 != null && getQData100_01.isEmpty() == false) {
                for (Map<String, Object> oneData100: getQData100_01) {
                    String shopId = oneData100.get("SHOPID") == null ? "" : oneData100.get("SHOPID").toString();
                    String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                    String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
                    String porderNO100 = oneData100.get("PORDERNO") == null ? "" : oneData100.get("PORDERNO").toString();
                    String bDate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
                    String rDate = oneData100.get("RDATE") == null ? "" : oneData100.get("RDATE").toString();
                    String rTime = oneData100.get("RTIME") == null ? "" : oneData100.get("RTIME").toString();
                    String requisition_date  = "";
                    String requisition_time = "";
                    if (rDate != null && rDate.length() >0) {
                        requisition_date  = DCP_ConversionTimeFormat.converToDate(rDate);
                    }
                    
                    if (rTime != null && rTime.length() >0) {
                        requisition_time  = converToTime(rTime);
                    }
                    
                    String create_date = "";
                    if (bDate != null && bDate.length() >0) {
                        create_date = DCP_ConversionTimeFormat.converToDate(bDate);
                    }
                    
                    String PTEMPLATENO = oneData100.get("PTEMPLATENO").toString();//易成后加
                    String IS_ADD = oneData100.get("IS_ADD").toString();
                    
                    String memo = oneData100.get("MEMO") == null ? "" : oneData100.get("MEMO").toString();
                    String createBy = oneData100.get("CREATEBY") == null ? "" : oneData100.get("CREATEBY").toString();
                    String createDate = oneData100.get("CREATEDATE") == null ? "" : oneData100.get("CREATEDATE").toString();
                    String createTime = oneData100.get("CREATETIME") == null ? "" : oneData100.get("CREATETIME").toString();
                    String create_datetime = "";
                    if (createDate != null && createDate.length() >0 && createTime != null && createTime.length() >0) {
                        create_datetime  = DCP_ConversionTimeFormat.converToDatetime(createDate+createTime);
                    }
                    String modifyBy = oneData100.get("MODIFYBY") == null ? "" : oneData100.get("MODIFYBY").toString();
                    String modifyDate = oneData100.get("MODIFYDATE") == null ? "" : oneData100.get("MODIFYDATE").toString();
                    String modifyTime = oneData100.get("MODIFYTIME") == null ? "" : oneData100.get("MODIFYTIME").toString();
                    String modify_datetime = "";
                    if (modifyDate != null && modifyDate.length() >0 && modifyTime != null && modifyTime.length() >0) {
                        modify_datetime = DCP_ConversionTimeFormat.converToDatetime(modifyDate+modifyTime);
                    }
                    String confirmBy = oneData100.get("CONFIRMBY") == null ? "" : oneData100.get("CONFIRMBY").toString();
                    //String confirmDate = oneData100.get("CONFIRMDATE") == null ? "" : oneData100.get("CONFIRMDATE").toString();
                    //【ID1020339】[泰奇3.0]要货单传递的数量和易成计算逻辑不同？ by jinzma 20210827
                    String accountDate = oneData100.get("ACCOUNT_DATE") == null ? "" : oneData100.get("ACCOUNT_DATE").toString();
                    //【ID1022003】 3.0要货单、拆解单上传ERP少传对账日期 by jinzma 20211117
                    String account_date = "";
                    if (accountDate != null && accountDate.length() >0) {
                        account_date = DCP_ConversionTimeFormat.converToDate(accountDate);
                    }
                    
                    String confirmTime = oneData100.get("CONFIRMTIME") == null ? "" : oneData100.get("CONFIRMTIME").toString();
                    
                    String ISURGENTORDER=oneData100.get("ISURGENTORDER") == null ? "N" : oneData100.get("ISURGENTORDER").toString();
                    
                    // 2019-07-26 增加来源单号等三个参数
                    // oType 是新加的字段， 以前的要货单单据都为空（手工要货）， 就默认为0
                    String OFNO = oneData100.get("OFNO") == null ? "" : oneData100.get("OFNO").toString();
                    String oType = oneData100.get("OTYPE") == null ? "0" : oneData100.get("OTYPE").toString();
                    String loadShop = oneData100.get("LOADSHOP") == null ? "" : oneData100.get("LOADSHOP").toString();
                    // 上传发货组织字段， 优先取发货组织字段，如果没有，就取DCP_ORG 表对应的 所属公司字段。
                    String receipt_org = oneData100.get("RECEIPT_ORG") == null ? oneData100.get("BELFIRM").toString() : oneData100.get("RECEIPT_ORG").toString();
                    
                    String approve_datetime = "";
                    if (accountDate != null && accountDate.length() >0 && confirmTime != null && confirmTime.length() >0) {
                        approve_datetime = DCP_ConversionTimeFormat.converToDatetime(accountDate+confirmTime);
                    }
                    
                    //String rTime = oneData100.get("rTime").toString();
                    String demand_date = "";
                    if (rDate != null && rDate.length() >0 ) {
                        demand_date = DCP_ConversionTimeFormat.converToDate(rDate);
                    }
                    //【ID1023434】【霸王3.0】门店要货增加配送费功能,配送费用 by jinzma 20220120
                    String distributionFee = oneData100.get("DISTRIBUTIONFEE") == null ? "" : oneData100.get("DISTRIBUTIONFEE").toString();
                    
                    // 获取单身数据
                    sql = this.getQuerySql101_01(shopId, eId, organizationNO, porderNO100);
                    String[] conditionValues101_01 = {  }; // 查詢條件
                    List<Map<String, Object>> getQData101_01 = this.doQueryData(sql, conditionValues101_01);
                    
                    if (getQData101_01 != null	&& getQData101_01.isEmpty() == false) {
                        // t100req中的payload对象
                        JSONObject payload = new JSONObject();
                        
                        // 自定义payload中的json结构
                        JSONObject std_data = new JSONObject();
                        JSONObject parameter = new JSONObject();
                        
                        JSONArray request = new JSONArray();
                        JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
                        JSONArray request_detail = new JSONArray(); // 存所有单身
                        
                        for (Map<String, Object> oneData101 : getQData101_01) {
                            // 获取单身数据并赋值
                            JSONObject body = new JSONObject(); // 存一笔单身
                            String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();
                            String pluNO = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
                            String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
                            String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
                            String punit = oneData101.get("PUNIT") == null ? "" : oneData101.get("PUNIT").toString();
                            String pqty = oneData101.get("PQTY") == null ? "0" : oneData101.get("PQTY").toString();
                            String SO_QTY = oneData101.get("SO_QTY") == null ? "0" : oneData101.get("SO_QTY").toString();
                            String baseQty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
                            String price = oneData101.get("PRICE") == null ? "0" : oneData101.get("PRICE").toString();
                            String amt = oneData101.get("AMT") == null ? "0" : oneData101.get("AMT").toString();
                            String WAREHOUSE= oneData101.get("WAREHOUSE") == null ? "" : oneData101.get("WAREHOUSE").toString();
                            String MEMO= oneData101.get("MEMO") == null ? "" : oneData101.get("MEMO").toString();
                            
                            // 新增进货单价和进货金额  BY JZMA 2019-09-12
                            String distriPrice = oneData101.get("DISTRIPRICE") == null ? "0" : oneData101.get("DISTRIPRICE").toString();
                            String distriAmt = oneData101.get("DISTRIAMT") == null ? "0" : oneData101.get("DISTRIAMT").toString();
                            
                            // 新增原进货单价和原进货金额 BY WANGZYC 2020-11-23
                            String uDistriPrice = oneData101.get("UDISTRIPRICE").toString();
                            String uDistriAmount = "0";
                            // 如果原进货单价为空时  原进货金额也为空
                            if(PosPub.isNumericType(uDistriPrice)){
                                double udistriAmount = Double.parseDouble(uDistriPrice) * Double.parseDouble(pqty);
                                // 取小数点后两位
                                uDistriAmount= Double.toString(udistriAmount);
                            }else {
                                uDistriPrice = "0";
                                uDistriAmount = "0";
                            }
                            body.put("seq", item);
                            body.put("item_no", pluNO);
                            body.put("feature_no",featureNo);
                            body.put("base_unit", baseUnit);
                            body.put("packing_unit", punit);
                            body.put("base_qty", baseQty);
                            body.put("packing_qty", pqty);
                            body.put("so_qty", SO_QTY);
                            body.put("price", price);
                            body.put("amount", amt);
                            body.put("warehouse_no", WAREHOUSE);
                            body.put("remark", MEMO);
                            body.put("distri_price", distriPrice);
                            body.put("distri_amount", distriAmt);
                            // 新增原进货单价和原进货金额 BY WANGZYC 2020-11-23
                            body.put("udistri_price", uDistriPrice);
                            body.put("udistri_amount", uDistriAmount);
                            request_detail.put(body);
                        }
                        
                        // 给单头赋值
                        header.put("requisition_date", requisition_date);
                        header.put("requisition_time", requisition_time);
                        header.put("create_date", create_date);
                        header.put("account_date", account_date);
                        header.put("po_template_no", PTEMPLATENO);
                        header.put("is_add_po", IS_ADD);
                        header.put("front_no", porderNO100);
                        header.put("remark", memo);
                        header.put("status", "Y");
                        header.put("creator", createBy);
                        header.put("create_datetime", create_datetime);
                        header.put("modify_no", modifyBy);
                        header.put("modify_datetime", modify_datetime);
                        header.put("approve_no", confirmBy);
                        header.put("approve_datetime", approve_datetime);
                        header.put("site_no", shopId);
                        header.put("demand_date", demand_date);
                        header.put("request_detail", request_detail);
                        //是否紧急要货,由于项目中已经使用了isurgentorder，中台实际审核的是is_urgent_order，后面会去掉isurgentorder
                        header.put("isurgentorder", ISURGENTORDER);
                        header.put("is_urgent_order", ISURGENTORDER);
                        header.put("load_shop", loadShop);
                        header.put("load_doc_no", OFNO);
                        header.put("load_type", oType);
                        header.put("receipt_org", receipt_org);
                        header.put("distributionFee",distributionFee );
                        header.put("version", "3.0");
                        request.put(header);
                        
                        parameter.put("request", request);
                        
                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);
                        
                        String str = payload.toString();// 将json对象转换为字符串
                        String resbody = "";
                        // 执行请求操作，并拿到结果（同步阻塞）
                        try {
                            resbody=HttpSend.Send(str, "requisition.create", eId, shopId,organizationNO,porderNO100);
                            if(Check.Null(resbody) || resbody.isEmpty() ) {
                                continue;
                            }
                            JSONObject jsonres = new JSONObject(resbody);
                            JSONObject std_data_res = jsonres.getJSONObject("std_data");
                            JSONObject execution_res = std_data_res.getJSONObject("execution");
                            
                            // parameter 节点，记录ERP对应的单号和 ERP 对应的组织
                            String docNo = "";
                            String orgNo = "";
                            if(std_data_res.has("parameter")){
                                JSONObject parameter_res = std_data_res.getJSONObject("parameter");
                                if(parameter_res.has("doc_no") && parameter_res.has("org_no")){
                                    docNo = parameter_res.get("doc_no").toString();
                                    orgNo = parameter_res.get("org_no").toString();
                                }
                            }
                            
                            String code = execution_res.getString("code");
                            String description ="";
                            if  (!execution_res.isNull("description") ) {
                                description = execution_res.getString("description");
                            }
                            if (code.equals("0")) {
                                // values
                                Map<String, DataValue> values = new HashMap<String, DataValue>() ;
                                DataValue v= new DataValue("Y", Types.VARCHAR);
                                values.put("process_status", v);
                                DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
                                values.put("UPDATE_TIME", v1);
                                
                                //记录ERP 返回的单号和组织
                                DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
                                DataValue orgNoVal = new DataValue(orgNo, Types.VARCHAR);
                                values.put("PROCESS_ERP_NO", docNoVal);
                                values.put("PROCESS_ERP_ORG", orgNoVal);
                                
                                // condition
                                Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
                                DataValue c1 = new DataValue(organizationNO, Types.VARCHAR);
                                conditions.put("OrganizationNO", c1);
                                DataValue c2 = new DataValue(eId, Types.VARCHAR);
                                conditions.put("EID", c2);
                                DataValue c3 = new DataValue(shopId, Types.VARCHAR);
                                conditions.put("SHOPID", c3);
                                DataValue c4 = new DataValue(porderNO100, Types.VARCHAR);
                                conditions.put("porderNO", c4);
                                
                                this.doUpdate("DCP_porder", values, conditions);
                                InsertWSLOG.delete_WSLOG(eId, shopId,"1", porderNO100);
                                //
                                
                                sReturnInfo="0";
                            } else {
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                //写数据库
                                InsertWSLOG.insert_WSLOG("requisition.create",porderNO100,eId,organizationNO,"1",str,resbody,code,description) ;
                            }
                        } catch (Exception e) {
                            //
                            InsertWSLOG.insert_WSLOG("requisition.create",porderNO100,eId,shopId,"1",str,resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();
                            logger.error("\r\n******要货单requisition.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +porderNO100 + "\r\n报错信息："+e.getMessage()+"******\r\n");
                        }
                    } else {
                        sReturnInfo="错误信息:无单身数据！";
                        logger.error("\r\n******要货单requisition.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +porderNO100 + "无单身数据！******\r\n");
                    }
                }
            } else {
                sReturnInfo="错误信息:无单头数据！";
                logger.info("\r\n******要货单requisition.create没有要上传的单头数据******\r\n");
            }
            
        } catch (Exception e) {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                logger.error("\r\n******要货单requisition.create报错信息" + e.getMessage()+"\r\n" + errors + "******\r\n");
                
                pw=null;
                errors=null;
            } catch (IOException e1) {
                logger.error("\r\n******要货单requisition.create,IOException报错信息" + e1.getMessage() + "******\r\n");
            }
            
            sReturnInfo="错误信息:" + e.getMessage();
            
        } finally {
            bRun=false;//
            logger.info("\r\n*********要货单requisition.create定时调用End:************\r\n");
        }
        return sReturnInfo;
    }
    
    
    //DCP_PORDER
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + "select ORGANIZATIONNO, EID, PORDERNO, SHOPID, BDATE, MEMO,STATUS,CREATEBY,CREATEDATE,CREATETIME,MODIFYBY,MODIFYDATE,MODIFYTIME,"
                + " CONFIRMBY,CONFIRMDATE,CONFIRMTIME,SUBMITBY,SUBMITDATE,COMPLETEDATE,TOTQTY,TOTAMT,totcqty,RDATE,RTIME,PTEMPLATENO,"
                + " IS_ADD,ISURGENTORDER "
                + " ,OFNO, OTYPE, LOADSHOP, receipt_org , belfirm,ACCOUNT_DATE,DISTRIBUTIONFEE "
                + " from ("
                + "SELECT A.ORGANIZATIONNO as organizationNO,A.EID as EID,A.PORDERNO as porderNO,A.SHOPID as SHOPID,A.BDATE as bDate,A.MEMO as memo,"
                + " A.STATUS as status,	A.CREATEBY as createBy,A.CREATE_DATE as createDate,A.CREATE_TIME as createTime,A.modifyBy as modifyBy,"
                + " A.modify_Date as modifyDate,A.modify_Time as modifyTime,"
                + " A.confirmBy as confirmBy,A.confirm_Date as confirmDate,A.confirm_Time as confirmTime,A.SUBMITBY as submitBy, "
                + " A.SUBMIT_DATE as submitDate,A.COMPLETE_DATE as completeDate,"
                + " A.TOT_PQTY as totqty,A.TOT_AMT as totamt,A.TOT_CQTY as totcqty,rDate,rTime,A.PTEMPLATENO,A.IS_ADD,A.ISUrgentOrder ,"
                + " A.oType , A.OFNO,  a.Load_shop as loadShop , a.receipt_org "
                + " , b.belfirm,a.ACCOUNT_DATE,a.DISTRIBUTIONFEE "
                + " FROM DCP_PORDER A "
                + " LEFT JOIN DCP_ORG b ON a.EID = b.EID AND a.SHOPID = b.organizationno AND b.STATUS = '100' "
                + " WHERE A.status = '2'  AND A.process_status = 'N' "
        );
        
        
        //******兼容即时服务的,只查询指定的那张单据******
        if (pEId.equals("")==false) {
            sqlbuf.append(" and EID='"+pEId+"' "
                    + " and SHOPID='"+pShop+"' "
                    + " and ORGANIZATIONNO='"+pOrganizationNO+"' "
                    + " and PORDERNO='"+pBillNo+"' ");
        }
        
        sqlbuf.append(" ) TBL ");
        return sqlbuf.toString();
    }
    
    //DCP_PORDER_DETAIL
    protected String getQuerySql101_01(String shopId, String eId, String organizationNO, String porderNO100) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + " select ITEM,PLUNO,baseunit,PUNIT,PQTY,baseqty,UNITRATIO,PRICE,AMT,STOCKINQTY,DETAILSTATUS,SO_QTY,"
                + " WAREHOUSE,MEMO,"
                + " DISTRIPRICE,DISTRIAMT,featureno,UDISTRIPRICE "
                + " from ("
                + "SELECT  a.item,a.pluNO ,a.baseunit, a.punit,a.pqty,a.baseqty,a.UNIT_RATIO as unitRatio,a.price,a.amt,"
                + " a.stockin_qty as stockInqty ,a.detail_status as detailStatus,a.SO_QTY,WAREHOUSE,a.memo, "
                + " a.DISTRIPRICE,a.DISTRIAMT,a.featureno,a.UDISTRIPRICE "
                + " FROM DCP_PORDER_DETAIL  a "
                + " WHERE a.SHOPID = '"+shopId+"'  AND a.eId = '"+eId+"' AND A.ORGANIZATIONNO ='"+organizationNO+"' AND a.PORDERNO = '"+porderNO100 +"' "
        );
        sqlbuf.append(" ) TBL ");
        return sqlbuf.toString();
    }
    
    /**
     * 120000 转换为 12:00:00 为避免93版本追版错误，这个方法单独在这里写， ConversionTimeFormat 类中也有。
     * @param time 120000
     * @return  12:00:00
     */
    private String converToTime(String time) {
        String hour = time.substring(0,2);
        String mi = time.substring(2,4);
        String sec = time.substring(4,6);
        String f_time = hour + ":" + mi +":" + sec ;
        return  f_time;
    }
    
}
