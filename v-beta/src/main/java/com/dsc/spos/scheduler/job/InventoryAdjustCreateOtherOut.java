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
//****************ERP规格doc_type=1其他出库单***********************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class InventoryAdjustCreateOtherOut extends InitJob {
    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    public InventoryAdjustCreateOtherOut() {

    }

    public InventoryAdjustCreateOtherOut(String eId,String shopId,String organizationNO, String billNo) {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }

    Logger logger = LogManager.getLogger(InventoryAdjustCreateOtherOut.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public String doExe() {
        //返回信息
        String sReturnInfo="";

        //此服务是否正在执行中
        if (bRun && pEId.equals("")) {
            logger.info("\r *********ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create正在执行中,本次调用取消:************\r ");

            sReturnInfo="定时传输任务-ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r *********ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create定时调用Start:************\r ");


        try {
            //*******************************************************************************
            //*****************开始ERP规格doc_type=1其他出库单开始***************************************
            //logger.info("\r *********开始ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create开始************\r ");
            //*******************************************************************************
            String sql = this.getQuerySql();

            logger.debug("\r ******ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create执行SQL语句："+sql+"******\r ");

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
                    String stockOutNO = oneData.get("STOCKOUTNO") == null ? "": oneData.get("STOCKOUTNO").toString();
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
                    String create_date = oneData.get("BDATE").toString();
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
                    String bsNO = oneData.get("BSNO") == null ? "" : oneData.get("BSNO").toString();
                    String sourceMenu = oneData.get("SOURCEMENU") == null ? "" : oneData.get("SOURCEMENU").toString();
                    if (Check.Null(sourceMenu)){
                        sourceMenu = "0";
                    }
                    String oType = oneData.get("OTYPE").toString();

                    //【ID1022937】 3.0其他出入库上传时source_menu增加枚举值3-拼胚 by jinzma 20211228
                    //出库单中DCP_STOCKOUT.doc_type=3,source_menu=0,otype=1的单据上传时source_menu传3-拼胚
                    if (docType.equals("3") && sourceMenu.equals("0") && !Check.Null(oType) && oType.equals("1")){
                        sourceMenu = "3";
                    }

                    // 获取单身数据
                    sql = this.getDetailQuerySql(eId, organizationNO, stockOutNO);
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
                            // 获取单身数据并赋值
                            JSONObject body = new JSONObject(); // 存一笔单身

                            String item = oneDataDetail.get("ITEM") == null ? "" : oneDataDetail.get("ITEM").toString();
                            String oItem = oneDataDetail.get("OITEM") == null ? "": oneDataDetail.get("OITEM").toString();
                            String pluNO = oneDataDetail.get("PLUNO") == null ? "": oneDataDetail.get("PLUNO").toString();
                            String featureNo = oneDataDetail.get("FEATURENO") == null ? "": oneDataDetail.get("FEATURENO").toString();
                            String baseUnit = oneDataDetail.get("BASEUNIT") == null ? "": oneDataDetail.get("BASEUNIT").toString();
                            String punit = oneDataDetail.get("PUNIT") == null ? "": oneDataDetail.get("PUNIT").toString();
                            String pqty = oneDataDetail.get("PQTY") == null ? "0" : oneDataDetail.get("PQTY").toString();
                            String baseQty = oneDataDetail.get("BASEQTY") == null ? "0" : oneDataDetail.get("BASEQTY").toString();
                            String unitRatio = oneDataDetail.get("UNITRATIO") == null ? "0": oneDataDetail.get("UNITRATIO").toString();
                            String price = oneDataDetail.get("PRICE") == null ? "0": oneDataDetail.get("PRICE").toString();
                            String amt = oneDataDetail.get("AMT") == null ? "0" : oneDataDetail.get("AMT").toString();
                            String pluBarcode = oneDataDetail.get("PLUBARCODE") == null ? "": oneDataDetail.get("PLUBARCODE").toString();
                            String WAREHOUSE = oneDataDetail.get("WAREHOUSE") == null ? "" : oneDataDetail.get("WAREHOUSE").toString();
                            String batchNO = oneDataDetail.get("BATCH_NO") == null ? "" : oneDataDetail.get("BATCH_NO").toString();
                            String mes_location = oneDataDetail.get("MES_LOCATION") == null ? "" : oneDataDetail.get("MES_LOCATION").toString();
                            String prodDate = oneDataDetail.get("PROD_DATE") == null ? "" : oneDataDetail.get("PROD_DATE").toString();
                            String distriPrice = oneDataDetail.get("DISTRIPRICE") == null ? "0" : oneDataDetail.get("DISTRIPRICE").toString();
                            String distriAmt = oneDataDetail.get("DISTRIAMT") == null ? "0" : oneDataDetail.get("DISTRIAMT").toString();

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
                            body.put("location", mes_location);
                            body.put("prod_date", prodDate);
                            body.put("price",price );
                            body.put("amount",amt );
                            body.put("distri_price",distriPrice );
                            body.put("distri_amount",distriAmt );

                            request_detail.put(body);
                        }

                        // 给单头赋值
                        header.put("create_date", create_date);
                        header.put("creator", createBy);
                        header.put("posted_date", accountDate);
                        header.put("site_no", shopId);
                        header.put("load_doc_no", loadDocNo);
                        header.put("doc_type", "1");
                        //【ID1022290】3.0其他出库上传接口增加来源菜单字段  by jinzma 20211210
                        header.put("source_menu",sourceMenu);
                        header.put("front_no", stockOutNO);
                        header.put("remark", memo);
                        header.put("version", "3.0");
                        header.put("request_detail", request_detail);

                        request.put(header);

                        parameter.put("request", request);
                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);

                        String str = payload.toString();

                        logger.info("\r ******ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create请求T100传入参数：  " + str + "\r ");
                        String resbody="";
                        // 执行请求操作，并拿到结果（同步阻塞）
                        try {
                            resbody=HttpSend.Send(str, "inventory.adjust.create", eId,shopId,organizationNO,stockOutNO);

                            logger.info("\r ******ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create请求T100返回参数：  "+ "\r 单号="+ stockOutNO + "\r " + resbody + "******\r ");
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
                                DataValue v = new DataValue("Y", Types.VARCHAR);
                                values.put("process_status", v);
                                DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
                                values.put("UPDATE_TIME", v1);
                                values.put("TRAN_TIME", v1);
                                //记录ERP 返回的单号和组织
                                DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
                                DataValue orgNoVal = new DataValue(orgNo, Types.VARCHAR);
                                values.put("PROCESS_ERP_NO", docNoVal);
                                values.put("PROCESS_ERP_ORG", orgNoVal);

                                // condition
                                Map<String, DataValue> conditions = new HashMap<String, DataValue>();
                                DataValue c1 = new DataValue(organizationNO, Types.VARCHAR);
                                conditions.put("OrganizationNO", c1);
                                DataValue c2 = new DataValue(eId, Types.VARCHAR);
                                conditions.put("EID", c2);
                                DataValue c3 = new DataValue(shopId, Types.VARCHAR);
                                conditions.put("SHOPID", c3);
                                DataValue c4 = new DataValue(stockOutNO, Types.VARCHAR);
                                conditions.put("stockOutNO", c4);

                                this.doUpdate("DCP_STOCKOUT", values, conditions);
                                //删除WS日志 By jzma 20190524
                                InsertWSLOG.delete_WSLOG(eId, shopId,"1",stockOutNO);
                                //
                                sReturnInfo="0";
                            } else {
                                //
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;

                                InsertWSLOG.insert_WSLOG("inventory.adjust.create",stockOutNO,eId,organizationNO,"1",str,resbody,code,description);
                            }
                        } catch (Exception e) {
                            //记录WS日志 By jzma 20190524
                            InsertWSLOG.insert_WSLOG("inventory.adjust.create",stockOutNO,eId,shopId,"1",str, resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();

                            //System.out.println(e.toString());

                            logger.error("\r ******ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +stockOutNO + "\r 报错信息："+e.getMessage()+"******\r ");

                        }
                    } else {
                        //
                        sReturnInfo="错误信息:无单身数据！";

                        logger.info("\r ******ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +stockOutNO + "无单身数据！******\r ");
                    }
                }
            } else {
                //
                sReturnInfo="错误信息:无单头数据！";

                logger.info("\r ******ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create没有要上传的单头数据******\r ");
            }
            //*******************************************************************************
            //*****************结束ERP规格doc_type=1其他出库单结束***************************************
            //logger.info("\r *********结束ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create结束************\r ");
            //*******************************************************************************


        } catch (Exception e) {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                logger.error("\r ******ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create报错信息" + e.getMessage() +"\r " + errors+ "******\r ");

                pw=null;
                errors=null;
            } catch (IOException e1) {
                logger.error("\r ******ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create报错信息" + e.getMessage() + "******\r ");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        } finally {
            bRun=false;//
            logger.info("\r *********ERP规格doc_type=1其他出库单,其他库存调整inventory.adjust.create定时调用End:************\r ");
        }

        //
        return sReturnInfo;

    }

    //DCP_STOCKOUT
    protected String getQuerySql() throws Exception {

        StringBuffer sqlbuf = new StringBuffer();

        sqlbuf.append(""
                              + "select EID,ORGANIZATIONNO,STOCKOUTNO ,SHOPID,BDATE,MEMO,STATUS,DOCTYPE,OTYPE,OFNO,LOADDOCTYPE,LOADDOCNO,CREATEBY, "
                              + " CREATEDATE,CREATETIME,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME, "
                              + " CANCELBY,CANCELDATE,CANCELTIME,TRANSFERSHOP,TOTPQTY,TOTAMT,TOTCQTY,MODIFYBY,MODIFYDATE,MODIFYTIME,BSNO,SOURCEMENU "
                              + " from ("
                              + " SELECT a.EID,a.ORGANIZATIONNO,a.STOCKOUTNO,a.SHOPID,a.BDATE,a.MEMO,a.STATUS,a.DOC_TYPE as DOCTYPE,  " +
                              "   a.OTYPE,a.OFNO,a.LOAD_DOCTYPE as LOADDOCTYPE, b.ofno as LOADDOCNO,a.CREATEBY,  " +
                              "   a.CREATE_DATE as CREATEDATE,a.CREATE_TIME as CREATETIME,a.CONFIRMBY,a.CONFIRM_DATE as CONFIRMDATE,  " +
                              "   a.CONFIRM_TIME as CONFIRMTIME,a.ACCOUNTBY,a.ACCOUNT_DATE as ACCOUNTDATE,a.ACCOUNT_TIME as ACCOUNTTIME,  " +
                              "   a.CANCELBY,a.CANCEL_DATE as CANCELDATE,a.CANCEL_TIME as CANCELTIME,a.TRANSFER_SHOP as TRANSFERSHOP,  " +
                              "   a.TOT_PQTY as TOTPQTY,a.TOT_AMT as TOTAMT,a.TOT_CQTY as TOTCQTY, " +
                              "   a.MODIFYBY,a.MODIFY_DATE as MODIFYDATE,a.MODIFY_TIME as MODIFYTIME,a.BSNO,a.SOURCEMENU "
                              + " FROM DCP_STOCKOUT a "
                              + " left join  MES_TRANSAPPLICATION b on a.eid = b.eid and a.ORGANIZATIONNO  = b.ORGANIZATIONNO and a.ofno = b.APPLICATIONNO "
                              + " WHERE a.status = '2' AND a.doc_type = '3' AND a.process_status = 'N' "
        );


        //******兼容即时服务的,只查询指定的那张单据******
        if (pEId.equals("")==false) {
            sqlbuf.append(" and a.EID='"+pEId+"' "
                                  + " and a.SHOPID='"+pShop+"' "
                                  + " and a.ORGANIZATIONNO='"+pOrganizationNO+"' "
                                  + " and a.STOCKOUTNO='"+pBillNo+"' ");

        }


        sqlbuf.append(" ) TBL ");

        return sqlbuf.toString();
    }

    //DCP_STOCKOUT_DETAIL
    protected String getDetailQuerySql(String eId, String organizationNO, String stockOutNO) throws Exception {

        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                              + " select item,oItem,pluNO  ,baseunit, punit,pqty,baseqty, "
                              + " unitRatio ,price,amt,pluBarcode,WAREHOUSE,batch_no,prod_date,DISTRIPRICE,DISTRIAMT,featureno from ("
                              + " SELECT ITEM,OITEM,PLUNO,baseunit,PUNIT,PQTY,baseqty, "
                              + " UNIT_RATIO AS UNITRATIO,PRICE,AMT,PLU_BARCODE as pluBarcode,WAREHOUSE,batch_no,prod_date,"
                              + " DISTRIPRICE,DISTRIAMT,featureno,MES_LOCATION  FROM DCP_STOCKOUT_DETAIL  "
                              + " WHERE EID = '"+eId+"'  " + " AND ORGANIZATIONNO = '"+organizationNO+"'  " + " AND STOCKOUTNO = '"+stockOutNO+"' "
        );

        sqlbuf.append(" ) TBL ");

        return sqlbuf.toString();
    }



}
