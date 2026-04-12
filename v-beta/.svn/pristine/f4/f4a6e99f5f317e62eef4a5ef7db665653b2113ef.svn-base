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

//********************调拨入单据上传**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class TransferUpdate extends InitJob
{

    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    public TransferUpdate()
    {

    }

    public TransferUpdate(String eId,String shopId,String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }

    Logger logger = LogManager.getLogger(TransferUpdate.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public String doExe()
    {
        //返回信息
        String sReturnInfo="";

        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.info("\r\n*********调拨入transfer.update正在执行中,本次调用取消:************\r\n");
            sReturnInfo="定时传输任务-调拨入transfer.update正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//
        logger.info("\r\n*********调拨入transfer.update定时调用Start:************\r\n");
        try
        {
            String sql = this.getQuerySql100_03();
            logger.info("\r\n******调拨入transfer.update 执行SQL语句："+sql+"******\r\n");
            String[] conditionValues100_03 = { }; // 查詢條件
            List<Map<String, Object>> getQData100_03 = this.doQueryData(sql, conditionValues100_03);
            if (getQData100_03 != null && getQData100_03.isEmpty() == false)
            {
                for (Map<String, Object> oneData100 : getQData100_03)
                {
                    String shopId = oneData100.get("SHOPID") == null ? "" : oneData100.get("SHOPID").toString();
                    String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                    String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
                    String stockInNO100 = oneData100.get("STOCKINNO") == null ? "" : oneData100.get("STOCKINNO").toString();
                    //String bDate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
                    String memo = oneData100.get("MEMO") == null ? "" : oneData100.get("MEMO").toString();
                    //String status = oneData100.get("STATUS") == null ? "" : oneData100.get("STATUS").toString();
                    String docType = oneData100.get("DOCTYPE") == null ? "" : oneData100.get("DOCTYPE").toString();
                    if (docType.equals("0"))
                    {
                        docType = "2";
                    }
                    String loadDocNo = oneData100.get("LOADDOCNO") == null ? "" : oneData100.get("LOADDOCNO").toString();
                    String createBy = oneData100.get("CREATEBY") == null ? "" : oneData100.get("CREATEBY").toString();
                    String createDate = oneData100.get("CREATEDATE") == null ? "" : oneData100.get("CREATEDATE").toString();
                    String createTime = oneData100.get("CREATETIME") == null ? "" : oneData100.get("CREATETIME").toString();
                    String create_datetime = "";
                    if (createDate != null && createDate.length() > 0 && createTime != null&& createTime.length() > 0)
                    {
                        create_datetime = DCP_ConversionTimeFormat.converToDatetime(createDate + createTime);
                    }
                    String modifyBy = oneData100.get("MODIFYBY") == null ? "" : oneData100.get("MODIFYBY").toString();
                    String modifyDate = oneData100.get("MODIFYDATE") == null ? "" : oneData100.get("MODIFYDATE").toString();
                    String modifyTime = oneData100.get("MODIFYTIME") == null ? "" : oneData100.get("MODIFYTIME").toString();
                    String modify_datetime = "";
                    if (modifyDate != null && modifyDate.length() > 0 && modifyTime != null&& modifyTime.length() > 0)
                    {
                        modify_datetime = DCP_ConversionTimeFormat.converToDatetime(modifyDate + modifyTime);
                    }
                    String confirmBy = oneData100.get("CONFIRMBY") == null ? "" : oneData100.get("CONFIRMBY").toString();
                    String confirmDate = oneData100.get("CONFIRMDATE") == null ? "" : oneData100.get("CONFIRMDATE").toString();
                    String confirmTime = oneData100.get("CONFIRMTIME") == null ? "" : oneData100.get("CONFIRMTIME").toString();
                    String approve_datetime = "";
                    if (confirmDate != null && confirmDate.length() > 0 && confirmTime != null&& confirmTime.length() > 0)
                    {
                        approve_datetime = DCP_ConversionTimeFormat.converToDatetime(confirmDate + confirmTime);
                    }
                    String accountBy = oneData100.get("ACCOUNTBY") == null ? "" : oneData100.get("ACCOUNTBY").toString();
                    String accountDate = oneData100.get("ACCOUNTDATE") == null ? "" : oneData100.get("ACCOUNTDATE").toString();
                    String accountTime = oneData100.get("ACCOUNTTIME") == null ? "" : oneData100.get("ACCOUNTTIME").toString();
                    String posted_datetime = "";
                    if (accountDate != null && accountDate.length() > 0 && accountTime != null&& accountTime.length() > 0)
                    {
                        posted_datetime = DCP_ConversionTimeFormat.converToDatetime(accountDate + accountTime);
                    }
                    String transferShop = oneData100.get("TRANSFERSHOP") == null ? "" : oneData100.get("TRANSFERSHOP").toString();
                    String site_no = oneData100.get("SHOPID").toString();
                    String poPTemplateNO = oneData100.get("PTEMPLATENO").toString();

                    // 获取单身数据
                    sql = this.getQuerySql101_03(shopId, eId, organizationNO, stockInNO100);
                    String[] conditionValues101_03 = {  }; // 查詢條件
                    List<Map<String, Object>> getQData101_03 = this.doQueryData(sql, conditionValues101_03);
                    if (getQData101_03 != null	&& getQData101_03.isEmpty() == false)
                    {
                        // payload对象
                        JSONObject payload = new JSONObject();
                        JSONObject std_data = new JSONObject();
                        JSONObject parameter = new JSONObject();
                        JSONArray transfer = new JSONArray();
                        JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
                        JSONArray transfer_detail = new JSONArray(); // 存所有单身

                        for (Map<String, Object> oneData101 : getQData101_03)
                        {
                            // 获取单身数据并赋值
                            JSONObject body = new JSONObject(); // 存一笔单身
                            String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();
                            String oitem = oneData101.get("OITEM") == null ? "" : oneData101.get("OITEM").toString();
                            String pluNO = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
                            String pluMemo = oneData101.get("PLUMEMO") == null ? "" : oneData101.get("PLUMEMO").toString();
                            String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
                            String punit = oneData101.get("PUNIT") == null ? "" : oneData101.get("PUNIT").toString();
                            String pqty = oneData101.get("PQTY") == null ? "0" : oneData101.get("PQTY").toString();
                            String baseQty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
                            String price = oneData101.get("PRICE") == null ? "0" : oneData101.get("PRICE").toString();
                            String amt = oneData101.get("AMT") == null ? "0" : oneData101.get("AMT").toString();
                            String plu_barcode = oneData101.get("PLUBARCODE") == null ? "" : oneData101.get("PLUBARCODE").toString();
                            String WAREHOUSE=oneData101.get("WAREHOUSE") == null ? "" : oneData101.get("WAREHOUSE").toString();
                            String batchNO=oneData101.get("BATCH_NO") == null ? "" : oneData101.get("BATCH_NO").toString();
                            String prodDate=oneData101.get("PROD_DATE") == null ? "" : oneData101.get("PROD_DATE").toString();
                            // 新增进货单价和进货金额  BY JZMA 2019-09-12
                            String distriPrice = oneData101.get("DISTRIPRICE") == null ? "0" : oneData101.get("DISTRIPRICE").toString();
                            String distriAmt = oneData101.get("DISTRIAMT") == null ? "0" : oneData101.get("DISTRIAMT").toString();
                            String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();

                            body.put("seq", item);
                            if(docType.equals("2") && oitem.equals("0"))
                            {
                                body.put("source_seq", item);
                            }else
                            {
                                body.put("source_seq", oitem);
                            }
                            body.put("source_no", loadDocNo);
                            body.put("po_template_no", poPTemplateNO);
                            body.put("item_memo", pluMemo);
                            body.put("item_no", pluNO);//20180326 规格修改
                            body.put("item_feature_no", " ");
                            body.put("item_barcode", plu_barcode);//20180326 规格修改
                            body.put("base_unit", baseUnit);
                            body.put("packing_unit", punit);
                            body.put("packing_qty", pqty);
                            body.put("base_qty", baseQty);
                            body.put("price", price);
                            body.put("amount", amt);
                            body.put("warehouse_no", WAREHOUSE);
                            body.put("item_batch_no", batchNO);
                            body.put("prod_date", prodDate);
                            body.put("distri_price", distriPrice);
                            body.put("distri_amount", distriAmt);
                            body.put("feature_no", featureNo);
                            transfer_detail.put(body);
                        }

                        // 给单头赋值
                        header.put("front_no", stockInNO100);
                        header.put("source_no", loadDocNo);//20180326 规格修改
                        if (docType.equals("2") && eId.equals("66") && transferShop.isEmpty() ) //格意特殊处理
                        {
                            //transferShop = "HJ0";   //写死不太好，3.0 注释掉  BY JZMA 20200716
                        }
                        header.put("transfer_out_site_no", transferShop);//20180326 规格修改
                        header.put("site_no",site_no);
                        header.put("remark", memo);
                        header.put("creator", createBy);
                        header.put("create_datetime", create_datetime);
                        header.put("modify_no", modifyBy);
                        header.put("modify_datetime", modify_datetime);
                        header.put("approve_no", confirmBy);
                        header.put("approve_datetime", approve_datetime);
                        header.put("posted_no", accountBy);
                        header.put("transfer_type", docType);
                        header.put("version", "3.0");
                        header.put("posted_datetime", posted_datetime);
                        header.put("transfer_detail", transfer_detail);

                        transfer.put(header);
                        parameter.put("transfer", transfer);
                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);


                        String str = payload.toString();// 将json对象转换为字符串
                        logger.info("\r\n******调拨入transfer.update请求T100传入参数：  " + str + "\r\n");

                        // 执行请求操作，并拿到结果（同步阻塞）
                        String resbody="";
                        try
                        {
                            resbody = HttpSend.Send(str, "transfer.update", eId, shopId,organizationNO,stockInNO100);
                            logger.info("\r\n******调拨入transfer.update请求T100返回参数：  "+"公司编号:"+ eId + " 组织编号:"+organizationNO+" 单号:"+ stockInNO100 + "\r\n" + resbody + "******\r\n");
                            if(Check.Null(resbody) || resbody.isEmpty() )
                            {
                                continue;
                            }
                            JSONObject jsonres = new JSONObject(resbody);
                            //JSONObject payload_res = jsonres.getJSONObject("payload");
                            //JSONObject std_data_res = payload_res.getJSONObject("std_data");
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
                            if  (!execution_res.isNull("description") )
                            {
                                description = execution_res.getString("description");
                            }
                            if (code.equals("0"))
                            {
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
                                DataValue c4 = new DataValue(stockInNO100, Types.VARCHAR);
                                conditions.put("stockInNO", c4);

                                this.doUpdate("DCP_stockin", values, conditions);
                                //删除WS日志 By jzma 20190524
                                InsertWSLOG.delete_WSLOG(eId,shopId,"1",stockInNO100);
                                //
                                sReturnInfo="0";
                            }
                            else
                            {
                                //
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                InsertWSLOG.insert_WSLOG("transfer.update",stockInNO100,eId,organizationNO,"1",str,resbody,code,description) ;
                            }

                        }
                        catch (Exception e)
                        {
                            //记录WS日志 By jzma 20190524
                            InsertWSLOG.insert_WSLOG("transfer.update",stockInNO100,eId,shopId,"1",str, resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();
                            //System.out.println(e.toString());
                            logger.info("\r\n******调拨入transfer.update：公司编号=" + eId + ",组织编号=" +organizationNO +",单号="  +stockInNO100 +"\r\n报错信息："+e.getMessage()+"******\r\n");
                        }
                    }
                    else
                    {
                        //
                        sReturnInfo="错误信息:无单身数据！";
                        logger.info("\r\n******调拨入transfer.update：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +stockInNO100 + "无单身数据！******\r\n");
                    }
                }
            }
            else
            {
                //
                sReturnInfo="错误信息:无单头数据！";
                logger.info("\r\n******调拨入transfer.update没有要上传的单头数据******\r\n");
            }
        }
        catch (Exception e)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);
                pw.flush();
                pw.close();
                errors.flush();
                errors.close();
                logger.info("\r\n******调拨入transfer.update报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");
                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.info("\r\n******调拨入transfer.update报错信息" + e.getMessage() + "******\r\n");
            }
            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********调拨入transfer.update定时调用End:************\r\n");
        }

        //
        return sReturnInfo;

    }



    //DCP_STOCKIN
    protected String getQuerySql100_03() throws Exception
    {
        String sql = null;

        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append(""
                + "select EID,ORGANIZATIONNO,STOCKINNO,SHOPID,BDATE,MEMO,STATUS,DOCTYPE,OTYPE,OFNO,LOADDOCTYPE,LOADDOCNO,PTEMPLATENO,CREATEBY,CREATEDATE,CREATETIME,MODIFYBY,"
                + " MODIFYDATE,MODIFYTIME,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME,CANCELBY,CANCELDATE,CANCELTIME,TRANSFERSHOP,"
                + " TOTPQTY,TOTAMT,TOTCQTY"
                + " from ( "
                + "SELECT A.EID,A.ORGANIZATIONNO,A.STOCKINNO as stockInNO,A.SHOPID as SHOPID,A.BDATE as bDate,A.MEMO as memo,A.STATUS as status,A.DOC_TYPE as docType,"
                + " A.OTYPE as otype,A.OFNO as ofno,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO,A.PTEMPLATENO as pTemplateNO,A.CREATEBY as createBy,"
                + " A.CREATE_DATE as createDate,A.CREATE_TIME as createTime,A.modifyBY as modifyBy,"
                + " A.modify_DATE as modifyDate,A.modify_TIME as modifyTime,A.CONFIRMBY as confirmBy,A.CONFIRM_DATE as confirmDate,"
                + " A.CONFIRM_TIME as confirmTime,A.ACCOUNTBY as accountBy,A.ACCOUNT_DATE as accountDate,A.ACCOUNT_TIME as accountTime,"
                + " A.CANCELBY as cancelBy,A.CANCEL_DATE as cancelDate,A.CANCEL_TIME as cancelTime,A.TRANSFER_SHOP as transferShop,"
                + " A.TOT_PQTY as totpqty,A.TOT_AMT as totamt,A.TOT_CQTY as totcqty"
                + " FROM DCP_STOCKIN A "
                + " WHERE A.status = '2'  AND A.process_status = 'N' "
                + " and A.DOC_TYPE<>'3' "//0-配送收货  1-调拨收货 3-其他入库单
        );


        //******兼容即时服务的,只查询指定的那张单据******
        if (pEId.equals("")==false)
        {
            sqlbuf.append(" and EID='"+pEId+"' "
                    + " and SHOPID='"+pShop+"' "
                    + " and ORGANIZATIONNO='"+pOrganizationNO+"' "
                    + " and STOCKINNO='"+pBillNo+"' ");

        }

        sqlbuf.append(" ) TBL ");

        sql = sqlbuf.toString();

        return sql;
    }

    //DCP_STOCKIN_DETAIL
    protected String getQuerySql101_03(String shopId, String eId, String organizationNO, String stockInNO100) throws Exception
    {
        String sql = null;

        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append(""
                + " select ITEM,OITEM,PLUNO,PLUMEMO,baseUNIT,PUNIT,PQTY,baseQTY,UNITRATIO,PRICE,AMT,PLUBARCODE,WAREHOUSE,"
                + " batch_no,prod_date,DISTRIPRICE,DISTRIAMT,featureno "
                + " from ("
                + " SELECT  a.item,a.oitem,a.pluNO, a.plu_memo as pluMemo , a.baseunit, a.punit,a.pqty,a.baseqty,a.UNIT_RATIO as unitRatio,"
                + " a.price,a.amt,a.plu_barcode as pluBarcode,WAREHOUSE,batch_no,prod_date,DISTRIPRICE,DISTRIAMT,featureno "
                + " FROM DCP_STOCKIN_DETAIL A"
                + " WHERE A.SHOPID = '" +shopId+"' AND A.EID = '"+eId+"' AND A.ORGANIZATIONNO = '"+organizationNO+"'  AND A.STOCKINNO = '"+stockInNO100+"' "
        );

        sqlbuf.append(" ) TBL ");

        sql = sqlbuf.toString();

        return sql;
    }




}
