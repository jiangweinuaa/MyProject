package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *MES发货单上传ERP
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_StockoutUpload extends InitJob
{


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(MES_StockoutUpload.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public MES_StockoutUpload()
    {

    }

    public MES_StockoutUpload(String eId,String shopId,String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }


    public String doExe()
    {
        //返回信息
        String sReturnInfo = "";
        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.trace("\r *********MES发货单上传MES_StockoutUpload正在执行中,本次调用取消:************\r ");

            sReturnInfo = "定时传输任务-MES发货单上传MES_StockoutUpload正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//

        logger.trace("\r *********MES发货单上传MES_StockoutUpload定时调用Start:************\r ");

        try
        {
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            //先查 job 执行时间，然后再执行后续操作
            String getTimeSql = "select * from job_quartz_detail where job_name = 'MES_StockoutUpload'  and STATUS = '100' ";
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

            String sql = this.getQuerySql100_01();
            List<Map<String, Object>> getQData100_01 = this.doQueryData(sql, null);
            if (getQData100_01 != null && getQData100_01.isEmpty() == false)
            {

                for (Map<String, Object> oneData100: getQData100_01)
                {
                    String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                    String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
                    String invoiceno = oneData100.get("STOCKOUTNO") == null ? "" : oneData100.get("STOCKOUTNO").toString();
                    String BDATE = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
                    String department = oneData100.get("DEPARTMENT") == null ? "" : oneData100.get("DEPARTMENT").toString();
                    String requireno = oneData100.get("REQUIRENO") == null ? "" : oneData100.get("REQUIRENO").toString();
                    String rtemplateno = oneData100.get("RTEMPLATENO") == null ? "" : oneData100.get("RTEMPLATENO").toString();
                    String taxamout = oneData100.get("TAXAMOUNT") == null ? "" : oneData100.get("TAXAMOUNT").toString();
                    String totamount = oneData100.get("TOTAMT") == null ? "" : oneData100.get("TOTAMT").toString();
                    String opno = oneData100.get("OPNO") == null ? "" : oneData100.get("OPNO").toString();
                    String opname = oneData100.get("OP_NAME") == null ? "" : oneData100.get("OP_NAME").toString();


                    // 获取单身数据
                    sql = this.getQuerySql101_01(organizationNO, eId, invoiceno);
                    String[] conditionValues101_01 = {  }; // 查詢條件
                    List<Map<String, Object>> getQData101_01 = this.doQueryData(sql, conditionValues101_01);



                    if (getQData101_01 != null	&& getQData101_01.isEmpty() == false)
                    {
                        // t100req中的payload对象
                        JSONObject payload = new JSONObject();

                        // 自定义payload中的json结构
                        JSONObject std_data = new JSONObject();
                        JSONObject parameter = new JSONObject();

                        JSONArray request = new JSONArray();
                        JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
                        JSONArray request_detail = new JSONArray(); // 存所有单身

                        for (Map<String, Object> oneData101 : getQData101_01)
                        {
                            // 获取单身数据并赋值
                            JSONObject body = new JSONObject(); // 存一笔单身
                            String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();
                            String pluNO = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
                            String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
                            String unit = oneData101.get("UNIT") == null ? "" : oneData101.get("UNIT").toString();
                            String batchno = oneData101.get("BATCHNO") == null ? "" : oneData101.get("BATCHNO").toString();
                            String productDate = oneData101.get("PRODUCTDATE") == null ? "" : oneData101.get("PRODUCTDATE").toString();
                            String loseDate = oneData101.get("LOSEDATE") == null ? "" : oneData101.get("LOSEDATE").toString();
                            String qty = oneData101.get("QTY") == null ? "0" : oneData101.get("QTY").toString();
                            String transferout = oneData101.get("TRANSFEROUT") == null ? "" : oneData101.get("TRANSFEROUT").toString();
                            String transferin= oneData101.get("TRANSFERIN") == null ? "" : oneData101.get("TRANSFERIN").toString();
                            String baseUnit= oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
                            String baseQty = oneData101.get("BASEQTY") == null ? "" : oneData101.get("BASEQTY").toString();
                            String dprice = oneData101.get("DPRICE") == null ? "0" : oneData101.get("DPRICE").toString();
                            String damt = oneData101.get("DAMT") == null ? "0" : oneData101.get("DAMT").toString();
                            String taxrate = oneData101.get("TAXRATE") == null ? "0" : oneData101.get("TAXRATE").toString();
                            String taxamt = oneData101.get("TAXAMT") == null ? "0" : oneData101.get("TAXAMT").toString();

                            body.put("item", item);
                            body.put("pluNo", pluNO);
                            body.put("unit",unit);
                            body.put("featureNo", featureNo);
                            body.put("batch", batchno);
                            body.put("productDate", productDate);
                            body.put("loseDate", loseDate);
                            body.put("unit", unit);
                            body.put("qty", qty);
                            body.put("transferOut", transferout);
                            body.put("transferIn", transferin);
                            body.put("baseUnit", baseUnit);
                            body.put("baseQty", baseQty);
                            body.put("dPrice", dprice);
                            body.put("dAmt", damt);
                            body.put("taxRate", taxrate);
                            body.put("taxAmt", taxamt);


                            // 获取子单身数据
                            sql = this.getQuerySql101_01_01(organizationNO, eId, invoiceno,item);
                            String[] conditionValues101_01_01 = {  }; // 查詢條件
                            List<Map<String, Object>> getQData101_01_01 = this.doQueryData(sql, conditionValues101_01_01);

                            //子单身处理
                            if (getQData101_01_01 != null	&& getQData101_01_01.isEmpty() == false)
                            {
                                JSONArray request_detail_share = new JSONArray(); // 存所有子单身

                                for (Map<String, Object> Data : getQData101_01_01)
                                {
                                    JSONObject sBody = new JSONObject(); // 存一笔子单身

                                    String shareqty = Data.get("SHAREQTY") == null ? "0" : Data.get("SHAREQTY").toString();
                                    String sourcetype = Data.get("SOURCETYPE") == null ? "" : Data.get("SOURCETYPE").toString();
                                    String sourceno = Data.get("SOURCENO") == null ? "" : Data.get("SOURCENO").toString();
                                    String oitem = Data.get("OITEM") == null ? "0" : Data.get("OITEM").toString();
                                    String ocompany = Data.get("OCOMPANY") == null ? "" : Data.get("OCOMPANY").toString();
                                    String share_item = Data.get("ITEM") == null ? "0" : Data.get("ITEM").toString();
                                    String share_initem = Data.get("INITEM") == null ? "0" : Data.get("INITEM").toString();

                                    sBody.put("shareQty", shareqty);
                                    sBody.put("sourceType", sourcetype);
                                    sBody.put("sourceNo",sourceno);
                                    sBody.put("oItem", oitem);
                                    sBody.put("oCompany", ocompany);
                                    sBody.put("item", share_item);
                                    sBody.put("inItem", share_initem);

                                    request_detail_share.put(sBody);
                                }

                                body.put("shareList", request_detail_share);

                            }

                            request_detail.put(body);
                        }

                        // 给单头赋值
                        header.put("eId", eId);
                        header.put("organizationNo", organizationNO);
                        header.put("invoiceNo", invoiceno);
                        header.put("bDate", BDATE);
                        header.put("department", department);
                        header.put("requireNo", requireno);
                        header.put("rtemplateNo", rtemplateno);
                        header.put("totTaxAmount", totamount);
                        header.put("totAmt", totamount);
                        header.put("opNo", opno);
                        header.put("opName", opname);
                        header.put("version", "3.0");
                        header.put("mesinvoice_detail", request_detail);
                        request.put(header);

                        parameter.put("mesinvoice", request);

                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);

                        String str = payload.toString();// 将json对象转换为字符串
                        String resbody = "";

                        // 执行请求操作，并拿到结果（同步阻塞）
                        try
                        {
                            resbody= HttpSend.Send(str, "mesInvoice.create", eId, organizationNO, organizationNO, invoiceno);
                            if(Check.Null(resbody) || resbody.isEmpty() )
                            {
                                continue;
                            }
                            JSONObject jsonres = new JSONObject(resbody);
                            JSONObject std_data_res = jsonres.getJSONObject("std_data");
                            JSONObject execution_res = std_data_res.getJSONObject("execution");

                            // parameter 节点，记录ERP对应的单号和 ERP 对应的组织
                            String docNo = "";
                            String orgNo = "";
                            if(std_data_res.has("parameter"))
                            {
                                JSONObject parameter_res = std_data_res.getJSONObject("parameter");
                                if(parameter_res.has("doc_no") && parameter_res.has("org_no"))
                                {
                                    docNo = parameter_res.get("doc_no").toString();
                                    orgNo = parameter_res.get("org_no").toString();
                                }
                            }

                            String code = execution_res.getString("code");
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
                                values.put("PROCESS_STATUS", v);

                                // condition
                                Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
                                DataValue c1 = new DataValue(organizationNO, Types.VARCHAR);
                                conditions.put("ORGANIZATIONNO", c1);
                                DataValue c2 = new DataValue(eId, Types.VARCHAR);
                                conditions.put("EID", c2);
                                DataValue c3 = new DataValue(invoiceno, Types.VARCHAR);
                                conditions.put("STOCKOUTNO", c3);

                                this.doUpdate("MES_STOCKOUT", values, conditions);
                                InsertWSLOG.delete_WSLOG(eId, organizationNO,"1", invoiceno);
                                //

                                sReturnInfo="0";
                            }
                            else
                            {
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                //写数据库
                                InsertWSLOG.insert_WSLOG("mesInvoice.create",invoiceno,eId,organizationNO,"1",str,resbody,code,description) ;
                            }
                        }
                        catch (Exception e)
                        {
                            //
                            InsertWSLOG.insert_WSLOG("mesInvoice.create",invoiceno,eId,organizationNO,"1",str,resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();
                            logger.error("\r\n******MES发货单MES_StockoutUpload,mesInvoice.create：组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +invoiceno + "\r\n报错信息："+e.getMessage()+"******\r\n");
                        }

                    }
                    else
                    {
                        // values
                        Map<String, DataValue> values = new HashMap<String, DataValue>() ;
                        DataValue v= new DataValue("E", Types.VARCHAR);
                        values.put("PROCESS_STATUS", v);

                        // condition
                        Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
                        DataValue c1 = new DataValue(organizationNO, Types.VARCHAR);
                        conditions.put("ORGANIZATIONNO", c1);
                        DataValue c2 = new DataValue(eId, Types.VARCHAR);
                        conditions.put("EID", c2);
                        DataValue c3 = new DataValue(invoiceno, Types.VARCHAR);
                        conditions.put("STOCKOUTNO", c3);

                        this.doUpdate("MES_STOCKOUT", values, conditions);

                        logger.trace("\r\n******MES发货单上传MES_StockoutUpload,invoiceno="+invoiceno+"没有要上传的明细数据******\r\n");
                    }
                }
            }
            else
            {
                sReturnInfo="错误信息:无单头数据！";
                logger.trace("\r\n******MES发货单上传MES_StockoutUpload,没有要上传的单头数据******\r\n");
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

                logger.error("\r ******MES发货单上传MES_StockoutUpload报错信息" + e.getMessage()+"\r " + errors.toString() + "******\r ");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r ******MES发货单上传MES_StockoutUpload报错信息" + e.getMessage() + "******\r ");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.trace("\r *********MES发货单上传MES_StockoutUpload定时调用End:************\r ");
        }
        return sReturnInfo;
    }



    //MES_STOCKOUT
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from ( "
                + "select row_number() over(order by a.createtime) rn, a.*,b.op_name  " +
                "from MES_STOCKOUT a " +
                "left join platform_staffs_lang b on a.eid=b.eid and a.opno=b.opno and b.lang_type='zh_CN' " +
                "where a.process_status='N' and a.status='1' "
        );

        sqlbuf.append(" ) " +
                "where rn>0 and rn<=100 ");
        return sqlbuf.toString();
    }

    //MES_STOCKOUT_DETAIL
    protected String getQuerySql101_01(String shopId, String eId, String porderNO100) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from MES_STOCKOUT_DETAIL a   " +
                "where a.eid='"+eId+"'  " +
                "and a.organizationno='"+shopId+"'  " +
                "and a.STOCKOUTNO='"+porderNO100+"'  " +
                "order by a.item  ");
        return sqlbuf.toString();
    }

    //MES_STOCKOUT_SHARE
    protected String getQuerySql101_01_01(String shopId, String eId, String porderNO100,String inItem) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from MES_STOCKOUT_SHARE a   " +
                "where a.eid='"+eId+"'  " +
                "and a.organizationno='"+shopId+"'  " +
                "and a.STOCKOUTNO='"+porderNO100+"' " +
                "and a.INITEM="+inItem+" " +
                "and a.SHAREQTY>0 "+
                "order by a.item  ");
        return sqlbuf.toString();
    }



}
