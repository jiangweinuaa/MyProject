package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.imp.json.DCP_ConversionTimeFormat;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;


//********************其他库存调整单据上传**************************
//****************ERP规格doc_type=3报损单***********************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class InventoryAdjustCreateLoss extends InitJob {
    
    Logger logger = LogManager.getLogger(InventoryAdjustCreateLoss.class.getName());
    static boolean bRun=false;//标记此服务是否正在执行中
    
    public String doExe() {
        //返回信息
        String sReturnInfo="";
        
        //此服务是否正在执行中
        if (bRun) {
            logger.info("\r\n*********ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create正在执行中,本次调用取消:************\r\n");
            sReturnInfo="定时传输任务-ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create正在执行中！";
            return sReturnInfo;
        }
        
        bRun=true;
        
        logger.info("\r\n*********ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create定时调用Start:************\r\n");
        
        try {
            
            String sql = this.getQuerySql();
            logger.debug("\r\n******ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create执行SQL语句："+sql+"******\r\n");
            
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            
            if (getQData != null && getQData.isEmpty() == false) {
                for (Map<String, Object> oneData : getQData) {
                    String bDate = oneData.get("BDATE") == null ? "" : oneData.get("BDATE").toString();
                    String transfer_date = "";
                    if (bDate != null && bDate.length() > 0) {
                        transfer_date = DCP_ConversionTimeFormat.converToDate(bDate);
                    }
                    
                    String loadDocType = oneData.get("LOADDOCTYPE") == null ? "" : oneData.get("LOADDOCTYPE").toString();
                    
                    if (loadDocType == null || loadDocType.length() <= 0) {
                        loadDocType = "1";
                    } else {
                        loadDocType = "2";
                    }
                    
                    String shopId = oneData.get("SHOPID") == null ? "" : oneData.get("SHOPID").toString();
                    String eId = oneData.get("EID") == null ? "": oneData.get("EID").toString();
                    String organizationNO = oneData.get("ORGANIZATIONNO") == null ? "": oneData.get("ORGANIZATIONNO").toString();
                    String lStockOutNO100 = oneData.get("LSTOCKOUTNO") == null ? "": oneData.get("LSTOCKOUTNO").toString();
                    String memo = oneData.get("MEMO") == null ? "" : oneData.get("MEMO").toString();
                    String docType = oneData.get("DOCTYPE") == null ? "" : oneData.get("DOCTYPE").toString();
                    String loadDocNo = oneData.get("LOADDOCNO") == null ? "": oneData.get("LOADDOCNO").toString();
                    String createBy = oneData.get("CREATEBY") == null ? "" : oneData.get("CREATEBY").toString();
                    String createDate = oneData.get("CREATEDATE") == null ? "": oneData.get("CREATEDATE").toString();
                    String createTime = oneData.get("CREATETIME") == null ? "": oneData.get("CREATETIME").toString();
                    String create_datetime = "";
                    if (createDate != null && createDate.length() > 0 && createTime != null&& createTime.length() > 0) {
                        create_datetime = DCP_ConversionTimeFormat.converToDatetime(createDate + createTime);
                    }
                    String modifyBy = oneData.get("MODIFYBY") == null ? "" : oneData.get("MODIFYBY").toString();
                    String modifyDate = oneData.get("MODIFYDATE") == null ? "": oneData.get("MODIFYDATE").toString();
                    String modifyTime = oneData.get("MODIFYTIME") == null ? "": oneData.get("MODIFYTIME").toString();
                    String modify_datetime = "";
                    if (modifyDate != null && modifyDate.length() > 0 && modifyTime != null&& modifyTime.length() > 0) {
                        modify_datetime = DCP_ConversionTimeFormat.converToDatetime(modifyDate + modifyTime);
                    }
                    String confirmBy = oneData.get("CONFIRMBY") == null ? "": oneData.get("CONFIRMBY").toString();
                    String confirmDate = oneData.get("CONFIRMDATE") == null ? "": oneData.get("CONFIRMDATE").toString();
                    String confirmTime = oneData.get("CONFIRMTIME") == null ? "": oneData.get("CONFIRMTIME").toString();
                    String approve_datetime = "";
                    if (confirmDate != null && confirmDate.length() > 0 && confirmTime != null&& confirmTime.length() > 0) {
                        approve_datetime = DCP_ConversionTimeFormat.converToDatetime(confirmDate + confirmTime);
                    }
                    String accountBy = oneData.get("ACCOUNTBY") == null ? "": oneData.get("ACCOUNTBY").toString();
                    String accountDate = oneData.get("ACCOUNTDATE") == null ? "": oneData.get("ACCOUNTDATE").toString();
                    String accountTime = oneData.get("ACCOUNTTIME") == null ? "": oneData.get("ACCOUNTTIME").toString();
                    String posted_datetime = "";
                    if (accountDate != null && accountDate.length() > 0 && accountTime != null&& accountTime.length() > 0) {
                        posted_datetime = DCP_ConversionTimeFormat.converToDatetime(accountDate + accountTime);
                    }
                    String transferShop = oneData.get("TRANSFERSHOP") == null ? "": oneData.get("TRANSFERSHOP").toString();
                    
                    
                    // 获取单身数据
                    sql = this.getQueryDetailSql(eId, organizationNO, lStockOutNO100);
                    List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
                    if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                        //payload对象
                        JSONObject payload = new JSONObject();
                        JSONObject std_data = new JSONObject();
                        JSONObject parameter = new JSONObject();
                        JSONArray request = new JSONArray();
                        JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
                        JSONArray request_detail = new JSONArray(); // 存所有单身
                        
                        for (Map<String, Object> oneDataDetail : getQDataDetail) {
                            JSONObject body = new JSONObject(); // 存一笔单身
                            
                            String item = oneDataDetail.get("ITEM") == null ? "" : oneDataDetail.get("ITEM").toString();
                            String oItem = oneDataDetail.get("OITEM") == null ? "" : oneDataDetail.get("OITEM").toString();
                            String pluNO = oneDataDetail.get("PLUNO") == null ? "" : oneDataDetail.get("PLUNO").toString();
                            String baseUnit = oneDataDetail.get("BASEUNIT") == null ? "" : oneDataDetail.get("BASEUNIT").toString();
                            String punit = oneDataDetail.get("PUNIT") == null ? "" : oneDataDetail.get("PUNIT").toString();
                            String pqty = oneDataDetail.get("PQTY") == null ? "0" : oneDataDetail.get("PQTY").toString();
                            String baseQty = oneDataDetail.get("BASEQTY") == null ? "0" : oneDataDetail.get("BASEQTY").toString();
                            String unitRatio = oneDataDetail.get("UNITRATIO") == null ? "0" : oneDataDetail.get("UNITRATIO").toString();
                            String price = oneDataDetail.get("PRICE") == null ? "0" : oneDataDetail.get("PRICE").toString();
                            String amt = oneDataDetail.get("AMT") == null ? "0" : oneDataDetail.get("AMT").toString();
                            String pluBarcode = oneDataDetail.get("PLUBARCODE") == null ? "" : oneDataDetail.get("PLUBARCODE").toString();
                            String bsNO = oneDataDetail.get("BSNO") == null ? "" : oneDataDetail.get("BSNO").toString();
                            String WAREHOUSE = oneDataDetail.get("WAREHOUSE") == null ? "" : oneDataDetail.get("WAREHOUSE").toString();
                            String batchNO = oneDataDetail.get("BATCH_NO") == null ? "" : oneDataDetail.get("BATCH_NO").toString();
                            String prodDate = oneDataDetail.get("PROD_DATE") == null ? "" : oneDataDetail.get("PROD_DATE").toString();
                            String distriPrice = oneDataDetail.get("DISTRIPRICE") == null ? "0" : oneDataDetail.get("DISTRIPRICE").toString();
                            String distriAmt = oneDataDetail.get("DISTRIAMT") == null ? "0" : oneDataDetail.get("DISTRIAMT").toString();
                            String featureNo = oneDataDetail.get("FEATURENO") == null ? "" : oneDataDetail.get("FEATURENO").toString();
                            
                            body.put("seq", item);
                            body.put("item_no", pluNO);
                            body.put("feature_no", featureNo);
                            body.put("warehouse_no", WAREHOUSE);
                            body.put("packing_unit", punit);
                            body.put("packing_qty", pqty);
                            body.put("base_unit", baseUnit);
                            body.put("base_qty", baseQty);
                            body.put("reason_no", bsNO);
                            body.put("item_batch_no", batchNO);
                            body.put("prod_date", prodDate);
                            body.put("price",price );
                            body.put("amount",amt );
                            body.put("distri_price",distriPrice );
                            body.put("distri_amount",distriAmt );
                            
                            request_detail.put(body);
                        }
                        
                        // 给单头赋值
                        header.put("create_date", bDate);
                        header.put("creator", createBy);
                        header.put("posted_date", accountDate);
                        header.put("site_no", shopId);
                        header.put("front_no", lStockOutNO100);
                        header.put("doc_type", "3");
                        //【ID1022290】3.0其他出库上传接口增加来源菜单字段  by jinzma 20211210
                        header.put("source_menu","");
                        header.put("remark", memo);
                        header.put("version", "3.0");
                        header.put("request_detail", request_detail);
                        request.put(header);
                        
                        parameter.put("request", request);
                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);
                        
                        String str = payload.toString();// 将json对象转换为字符串
                        
                        logger.info("\r\n******ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create请求T100传入参数：  " + str + "\r\n");
                        String resbody="";
                        //执行请求操作，并拿到结果（同步阻塞）
                        try {
                            resbody=HttpSend.Send(str, "inventory.adjust.create", eId, shopId,organizationNO,lStockOutNO100);
                            
                            logger.info("\r\n******ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create请求T100返回参数：  "+ "\r\n单号="+ lStockOutNO100 + "\r\n" + resbody + "******\r\n");
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
                            //String sqlcode = execution_res.getString("sqlcode");
                            //String description = execution_res.getString("description") == null ? "" : execution_res.getString("description");
                            String description ="";
                            if  (!execution_res.isNull("description") ) {
                                description = execution_res.getString("description");
                            }
                            if (code.equals("0")) {
                                // values
                                Map<String, DataValue> values = new HashMap<String, DataValue>();
                                values.put("process_status", new DataValue("Y", Types.VARCHAR));
                                values.put("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()),Types.VARCHAR));
                                
                                //记录ERP 返回的单号和组织
                                values.put("PROCESS_ERP_NO", new DataValue(docNo, Types.VARCHAR));
                                values.put("PROCESS_ERP_ORG", new DataValue(orgNo, Types.VARCHAR));
                                
                                // condition
                                Map<String, DataValue> conditions = new HashMap<String, DataValue>();
                                conditions.put("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                conditions.put("EID",  new DataValue(eId, Types.VARCHAR));
                                conditions.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                conditions.put("lStockOutNO", new DataValue(lStockOutNO100, Types.VARCHAR));
                                
                                this.doUpdate("DCP_LSTOCKOUT", values, conditions);
                                
                                //删除WS日志 By jzma 20190524
                                InsertWSLOG.delete_WSLOG(eId,shopId,"1",lStockOutNO100);
                                
                                sReturnInfo="0";
                            } else {
                                //
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                InsertWSLOG.insert_WSLOG("inventory.adjust.create",lStockOutNO100,eId,organizationNO,"1",str,resbody,code,description);
                            }
                        } catch (Exception e) {
                            //记录WS日志 By jzma 20190524
                            InsertWSLOG.insert_WSLOG("inventory.adjust.create",lStockOutNO100,eId,shopId,"1",str,resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();
                            
                            logger.error("\r\n******ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +lStockOutNO100 + "\r\n报错信息："+e.getMessage()+"******\r\n");
                        }
                    } else {
                        //
                        sReturnInfo="错误信息:无单身数据！";
                        logger.info("\r\n******ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +lStockOutNO100 + "无单身数据！******\r\n");
                    }
                }
            } else {
                sReturnInfo="错误信息:无单头数据！";
                logger.info("\r\n******ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create没有要上传的单头数据******\r\n");
            }
        } catch (Exception e) {
            logger.error("\r\n******ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create报错信息" + e.getMessage() +"\r\n");
            sReturnInfo="错误信息:" + e.getMessage();
            
        } finally {
            bRun=false;//
            //logger.info("\r\n*********ERP规格doc_type=3报损单,其他库存调整inventory.adjust.create定时调用End:************\r\n");
        }
        
        return sReturnInfo;
        
    }
    
    
    
    //DCP_LSTOCKOUT
    protected String getQuerySql() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + "select EID,ORGANIZATIONNO,LSTOCKOUTNO,SHOPID,BDATE,MEMO,STATUS,DOCTYPE,LOADDOCTYPE,LOADDOCNO,CREATEBY, "
                + " CREATEDATE,CREATETIME,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME, "
                + " CANCELBY,CANCELDATE,CANCELTIME,TOTPQTY,TOTAMT,TOTCQTY,MODIFYBY,MODIFYDATE,MODIFYTIME "
                + " from ("
                + " SELECT EID,ORGANIZATIONNO,LSTOCKOUTNO,SHOPID,BDATE,MEMO,STATUS,DOC_TYPE as DOCTYPE, "
                + " LOAD_DOCTYPE as LOADDOCTYPE, LOAD_DOCNO as LOADDOCNO,CREATEBY, "
                + " CREATE_DATE as CREATEDATE,CREATE_TIME as CREATETIME,CONFIRMBY,CONFIRM_DATE as CONFIRMDATE, "
                + " CONFIRM_TIME as CONFIRMTIME,ACCOUNTBY,ACCOUNT_DATE as ACCOUNTDATE,ACCOUNT_TIME as ACCOUNTTIME, "
                + " CANCELBY,CANCEL_DATE as CANCELDATE,CANCEL_TIME as CANCELTIME, "
                + " TOT_PQTY as TOTPQTY,TOT_AMT as TOTAMT,TOT_CQTY as TOTCQTY,"
                + "	MODIFYBY,MODIFY_DATE as MODIFYDATE,MODIFY_TIME as MODIFYTIME"
                + " FROM DCP_LSTOCKOUT "
                + " WHERE status = '2' AND process_status = 'N' "
        );
        sqlbuf.append(" ) TBL ");
        return  sqlbuf.toString();
    }
    
    //DCP_LSTOCKOUT_DETAIL
    protected String getQueryDetailSql(String eId, String organizationNO, String lStockOutNO100) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + "select item,pluNO ,baseunit,punit,pqty,baseqty, "
                + " unitRatio,price,amt,BSNO,WAREHOUSE,batch_no,prod_date,DISTRIPRICE,DISTRIAMT,featureno from ("
                + " SELECT ITEM,PLUNO ,baseunit,PUNIT,PQTY,baseqty,"
                + " UNIT_RATIO AS UNITRATIO,PRICE,AMT,BSNO,WAREHOUSE,batch_no,prod_date,DISTRIPRICE,DISTRIAMT,featureno"
                + " FROM DCP_LSTOCKOUT_DETAIL "
                + " WHERE EID = '"+eId+"'  " + " AND ORGANIZATIONNO = '"+organizationNO+"'  " + " AND LSTOCKOUTNO = '"+lStockOutNO100+"'  "
        );
        
        sqlbuf.append(" ) TBL ");
        return sqlbuf.toString();
    }
    
    
}
