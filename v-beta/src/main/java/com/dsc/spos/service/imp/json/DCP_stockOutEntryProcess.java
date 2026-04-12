package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_stockOutEntryProcessReq;
import com.dsc.spos.json.cust.req.DCP_stockOutEntryProcessReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_stockOutEntryProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;

/**
 * 服务函数：DCP_stockOutEntryProcess
 * 服务说明：退货录入提交
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_stockOutEntryProcess extends SPosAdvanceService<DCP_stockOutEntryProcessReq,DCP_stockOutEntryProcessRes> {
    @Override
    protected void processDUID(DCP_stockOutEntryProcessReq req, DCP_stockOutEntryProcessRes res) throws Exception {
        try{
            String eId = req.geteId();
            String shopId = req.getShopId();
            String stockOutEntryNo = req.getRequest().getStockOutEntryNo();
            String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
            
            String opNo = req.getOpNO();
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            String confirmTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            levelElm request = req.getRequest();
            String sql = " select b.receiptorg from dcp_stockout_entry a"
                    + " inner join dcp_stockout_entry_detail b on a.eid=b.eid and a.shopid=b.shopid and a.stockoutentryno=b.stockoutentryno"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stockoutentryno='"+stockOutEntryNo+"' and a.status='0' "
                    + " group by b.receiptorg ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (CollectionUtils.isEmpty(getQData)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退货录入单不存在 ");
            }
            
            String[] columns = {
                    "EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO","BDATE","TRANSFER_SHOP","DOC_TYPE",
                    "OFNO","BSNO","STOCKOUT_ID","DELIVERY_NO","TRANSFER_WAREHOUSE","OTYPE","WAREHOUSE","RECEIPT_ORG",
                    "LOAD_DOCNO","LOAD_DOCTYPE","LOAD_DOCSHOP","MEMO","PTEMPLATENO","STATUS","SOURCEMENU",
                    "TOT_PQTY","TOT_CQTY","TOT_AMT","TOT_DISTRIAMT","PROCESS_STATUS","PARTITION_DATE",
                    "CREATEBY","CREATE_DATE","CREATE_TIME","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
                    "ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
                    "CREATE_CHATUSERID","ACCOUNT_CHATUSERID",
            };
            
            String[] detailColumns = {
                    "EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO","WAREHOUSE","ITEM","OITEM",
                    "PLUNO","FEATURENO","BATCH_NO","PROD_DATE","PQTY","PUNIT","BASEQTY","BASEUNIT","UNIT_RATIO",
                    "PRICE","DISTRIPRICE","AMT","DISTRIAMT","PLU_MEMO","BSNO",
                    "PLU_BARCODE","RQTY","STOCKQTY","BDATE","PARTITION_DATE",
            };
            
            //产生退货单 (此处必须特殊处理，因为要产生连续的退货单且放在一个事务里提交，可能会和门店的普通退货并发，导致单号异常)  例：THCK20220402 9 0001
            sql = " select max(stockoutno) as stockoutno from dcp_stockout "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and stockoutno like 'THCK" +accountDate +"9%' ";
            List<Map<String, Object>> getMaxStockOutNoQData = this.doQueryData(sql, null);
            String stockOutNo = getMaxStockOutNoQData.get(0).get("STOCKOUTNO").toString();
            if (Check.Null(stockOutNo)){
                stockOutNo = "THCK"+accountDate+"9"+"0001";
            }else{
                stockOutNo = stockOutNo.substring(4);
                long i = Long.parseLong(stockOutNo) + 1;
                stockOutNo = "THCK" + i ;
            }
            
            //循环 RECEIPTORG
            for (Map<String, Object> oneData : getQData) {
                String receiptOrg = oneData.get("RECEIPTORG").toString();
                sql = " select b.*,a.delivery_no,a.warehouse as mainwarehouse,a.memo as mainmemo,a.bsno as mainbsno from dcp_stockout_entry a"
                        + " inner join dcp_stockout_entry_detail b on a.eid=b.eid and a.shopid=b.shopid and a.stockoutentryno=b.stockoutentryno"
                        + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stockoutentryno='"+stockOutEntryNo+"' and b.receiptorg='"+receiptOrg+"' ";
                List<Map<String, Object>> getDetailQData = this.doQueryData(sql, null);
                
                BigDecimal totPqty = new BigDecimal("0");
                BigDecimal totAmt = new BigDecimal("0");
                BigDecimal totDistriAmt = new BigDecimal("0");
                
                int item=1;
                for (Map<String, Object> oneDetailQData : getDetailQData) {
                    totPqty = totPqty.add(new BigDecimal(oneDetailQData.get("PQTY").toString()));
                    totAmt = totAmt.add(new BigDecimal(oneDetailQData.get("AMT").toString()));
                    totDistriAmt = totDistriAmt.add(new BigDecimal(oneDetailQData.get("DISTRIAMT").toString()));
                    
                    //新增 DCP_STOCKOUT_DETAIL
                    DataValue[]	insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(stockOutNo, Types.VARCHAR),
                            new DataValue(oneDetailQData.get("MAINWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(item, Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue(oneDetailQData.get("PLUNO").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("FEATURENO").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("BATCH_NO").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("PROD_DATE").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("PQTY").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("PUNIT").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("BASEQTY").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("BASEUNIT").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("UNIT_RATIO").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("PRICE").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("DISTRIPRICE").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("AMT").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("DISTRIAMT").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("MEMO").toString(), Types.VARCHAR),
                            new DataValue(oneDetailQData.get("BSNO").toString(), Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(oneDetailQData.get("STOCKQTY").toString(), Types.VARCHAR),
                            new DataValue(accountDate, Types.VARCHAR),
                            new DataValue(accountDate, Types.VARCHAR),
                    };
                    InsBean ib = new InsBean("DCP_STOCKOUT_DETAIL", detailColumns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                    
                    //库存流水处理
                    String procedure="SP_DCP_StockChange";
                    Map<Integer,Object> inputParameterOut = new HashMap<>();
                    inputParameterOut.put(1,eId);                                             //--企业ID
                    inputParameterOut.put(2,shopId);                                          //--组织
                    inputParameterOut.put(3,"03");                                            //--单据类型
                    inputParameterOut.put(4,stockOutNo);	                                  //--单据号
                    inputParameterOut.put(5,item);                                            //--单据行号
                    inputParameterOut.put(6,"-1");                                            //--异动方向 1=加库存 -1=减库存
                    inputParameterOut.put(7,accountDate);                                     //--营业日期 yyyy-MM-dd
                    inputParameterOut.put(8,oneDetailQData.get("PLUNO").toString());          //--品号
                    inputParameterOut.put(9,oneDetailQData.get("FEATURENO").toString());      //--特征码
                    inputParameterOut.put(10,oneDetailQData.get("MAINWAREHOUSE").toString()); //--仓库
                    inputParameterOut.put(11,oneDetailQData.get("BATCH_NO").toString());      //--批号
                    inputParameterOut.put(12,oneDetailQData.get("PUNIT").toString());         //--交易单位
                    inputParameterOut.put(13,oneDetailQData.get("PQTY").toString());          //--交易数量
                    inputParameterOut.put(14,oneDetailQData.get("BASEUNIT").toString());      //--基准单位
                    inputParameterOut.put(15,oneDetailQData.get("BASEQTY").toString());       //--基准数量
                    inputParameterOut.put(16,oneDetailQData.get("UNIT_RATIO").toString());    //--换算比例
                    inputParameterOut.put(17,oneDetailQData.get("PRICE").toString());         //--零售价
                    inputParameterOut.put(18,oneDetailQData.get("AMT").toString());           //--零售金额
                    inputParameterOut.put(19,oneDetailQData.get("DISTRIPRICE").toString());   //--进货价
                    inputParameterOut.put(20,oneDetailQData.get("DISTRIAMT").toString());     //--进货金额
                    inputParameterOut.put(21,accountDate);                                    //--入账日期 yyyy-MM-dd
                    inputParameterOut.put(22,oneDetailQData.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                    inputParameterOut.put(23,accountDate);                                    //--单据日期
                    inputParameterOut.put(24,"");                                            //--异动原因
                    inputParameterOut.put(25,"");                                            //--异动描述
                    inputParameterOut.put(26,opNo);                                          //--操作员
                    
                    ProcedureBean pdbOut = new ProcedureBean(procedure, inputParameterOut);
                    this.addProcessData(new DataProcessBean(pdbOut));
                    
                    item++;
                }
                
                //新增 DCP_STOCKOUT
                DataValue[]	insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(stockOutNo, Types.VARCHAR),
                        new DataValue(accountDate, Types.VARCHAR),
                        new DataValue(receiptOrg, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue(getDetailQData.get(0).get("STOCKOUTENTRYNO").toString(), Types.VARCHAR),
                        new DataValue(getDetailQData.get(0).get("MAINBSNO").toString(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(getDetailQData.get(0).get("DELIVERY_NO").toString(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("2", Types.VARCHAR),
                        new DataValue(getDetailQData.get(0).get("MAINWAREHOUSE").toString(), Types.VARCHAR),
                        new DataValue(receiptOrg, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(getDetailQData.get(0).get("MAINMEMO").toString(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("2", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(totPqty.toPlainString(), Types.VARCHAR),
                        new DataValue(item-1, Types.VARCHAR),
                        new DataValue(totAmt.toPlainString(), Types.VARCHAR),
                        new DataValue(totDistriAmt.toPlainString(), Types.VARCHAR),
                        new DataValue("N", Types.VARCHAR),
                        new DataValue(accountDate, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(accountDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                };
                InsBean ib = new InsBean("DCP_STOCKOUT", columns);
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
                
                //退货单号处理
                stockOutNo = stockOutNo.substring(4);
                long i = Long.parseLong(stockOutNo) + 1;
                stockOutNo = "THCK" + i ;
            }
            
            //修改 DCP_STOCKOUT_ENTRY
            UptBean ub = new UptBean("DCP_STOCKOUT_ENTRY");
            ub.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
            ub.addUpdateValue("CONFIRMBY", new DataValue(opNo, Types.VARCHAR));
            ub.addUpdateValue("CONFIRMBYNAME", new DataValue(req.getOpName(), Types.VARCHAR));
            ub.addUpdateValue("CONFIRMTIME", new DataValue(confirmTime, Types.DATE));
            
            // condition
            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub.addCondition("STOCKOUTENTRYNO", new DataValue(stockOutEntryNo, Types.VARCHAR));
            
            this.addProcessData(new DataProcessBean(ub));
            
            this.doExecuteDataToDB();
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        }catch (Exception e){
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
            //【ID1032555】【乐沙儿3.3.0.3】在门店管理出库单据点确定时，存在负库存时的提示返回error executing work，
            // 需要能够返回SP_DCP_StockChange返回的报错，提示门店  by jinzma 20230526
            String description=e.getMessage();
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                description = errors.toString();
                
                if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
                    description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
                }
                
            } catch (Exception ignored) {
            
            }
            
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, description);
            
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_stockOutEntryProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_stockOutEntryProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_stockOutEntryProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_stockOutEntryProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        if (request==null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else{
            if (Check.Null(request.getStockOutEntryNo())){
                errMsg.append("退货录入单号不可为空值, ");
                isFail = true;
            }
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_stockOutEntryProcessReq> getRequestType() {
        return new TypeToken<DCP_stockOutEntryProcessReq>(){};
    }
    
    @Override
    protected DCP_stockOutEntryProcessRes getResponseType() {
        return new DCP_stockOutEntryProcessRes();
    }
}
