package com.dsc.spos.scheduler.job;

import cn.hutool.core.collection.CollUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_BomUpload extends InitJob
{


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pOrganizationNo="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(MES_BatchUpload.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public  MES_BomUpload()
    {

    }

    public  MES_BomUpload(String eId,String shopId,String organizationNo, String billNo)
    {
        pEId=eId;
        pOrganizationNo=organizationNo;
        pBillNo=billNo;
    }


    public String doExe()
    {
        //返回信息
        String sReturnInfo = "";
        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.trace("\r\n*********MES配方创建MES_BomUpload正在执行中,本次调用取消:************\r\n");

            sReturnInfo = "定时传输任务-MES配方创建MES_BomUpload正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//

        logger.trace("\r\n*********MESMES配方创建MES_BomUpload定时调用Start:************\r\n");

        try
        {
            String sql = this.getQuerySql100_01();
            List<Map<String, Object>> getQData100_01 = this.doQueryData(sql, null);
            if (getQData100_01 != null && getQData100_01.isEmpty() == false)
            {
                for (Map<String, Object> oneData100: getQData100_01)
                {
                    String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                    String bomNo = oneData100.get("BOMNO") == null ? "" : oneData100.get("BOMNO").toString();
                    String pluNo = oneData100.get("PLUNO") == null ? "" : oneData100.get("PLUNO").toString();
                    String unit = oneData100.get("UNIT") == null ? "" : oneData100.get("UNIT").toString();
                    String mulQty = oneData100.get("MULQTY") == null ? "" : oneData100.get("MULQTY").toString();
                    String effDate = oneData100.get("EFFDATE") == null ? "" : oneData100.get("EFFDATE").toString();
                    String memo = oneData100.get("MEMO") == null ? "" : oneData100.get("MEMO").toString();
                    String status = oneData100.get("STATUS") == null ? "" : oneData100.get("STATUS").toString();
                    String restrictShop = oneData100.get("RESTRICTSHOP") == null ? "" : oneData100.get("RESTRICTSHOP").toString();
                    String prodType = oneData100.get("PRODTYPE") == null ? "" : oneData100.get("PRODTYPE").toString();
                    String batchQty = oneData100.get("BATCHQTY") == null ? "" : oneData100.get("BATCHQTY").toString();
                    String versionNum = oneData100.get("VERSIONNUM") == null ? "" : oneData100.get("VERSIONNUM").toString();
                    String remainType = oneData100.get("REMAINTYPE") == null ? "" : oneData100.get("REMAINTYPE").toString();
                    String containType = oneData100.get("CONTAINTYPE") == null ? "" : oneData100.get("CONTAINTYPE").toString();
                    String standardHours = oneData100.get("STANDARDHOURS") == null ? "" : oneData100.get("STANDARDHOURS").toString();
                    String minQty = oneData100.get("MINQTY") == null ? "" : oneData100.get("MINQTY").toString();
                    String fixedLossQty = oneData100.get("FIXEDLOSSQTY") == null ? "" : oneData100.get("FIXEDLOSSQTY").toString();
                    String createOpId = oneData100.get("CREATEOPID") == null ? "" : oneData100.get("CREATEOPID").toString();
                    String createTime = oneData100.get("CREATETIME") == null ? "" : oneData100.get("CREATETIME").toString();
                    String lastModiOpId = oneData100.get("LASTMODIOPID") == null ? "" : oneData100.get("LASTMODIOPID").toString();
                    String lastModiTime = oneData100.get("LASTMODITIME") == null ? "" : oneData100.get("LASTMODITIME").toString();

                    // t100req中的payload对象
                    JSONObject payload = new JSONObject();

                    // 自定义payload中的json结构
                    JSONObject std_data = new JSONObject();
                    JSONObject parameter = new JSONObject();

                    JSONArray request = new JSONArray();
                    JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
                    JSONArray request_detail = new JSONArray(); // 存所有单身
                    JSONArray request_range = new JSONArray();

                    // 获取单身数据
                    sql = this.getQuerySql101_01( eId, bomNo);
                    List<Map<String, Object>> getQData101_01 = this.doQueryData(sql, null);
                    if (getQData101_01 != null	&& getQData101_01.isEmpty() == false)
                    {
                        for (Map<String, Object> oneData101 : getQData101_01)
                        {
                            // 获取单身数据并赋值
                            JSONObject body = new JSONObject(); // 存一笔单身
                            String materialPluNo = oneData101.get("MATERIAL_PLUNO") == null ? "" : oneData101.get("MATERIAL_PLUNO").toString();
                            String materialUnit = oneData101.get("MATERIAL_UNIT") == null ? "" : oneData101.get("MATERIAL_UNIT").toString();
                            String materialPQty = oneData101.get("MATERIAL_QTY") == null ? "" : oneData101.get("MATERIAL_QTY").toString();
                            String qty = oneData101.get("QTY") == null ? "" : oneData101.get("QTY").toString();
                            String lossRate = oneData101.get("LOSS_RATE") == null ? "" : oneData101.get("LOSS_RATE").toString();
                            String isBuckle = oneData101.get("ISBUCKLE") == null ? "" : oneData101.get("ISBUCKLE").toString();
                            String isReplace = oneData101.get("ISREPLACE") == null ? "" : oneData101.get("ISREPLACE").toString();
                            String materialBDate = oneData101.get("MATERIAL_BDATE") == null ? "" : oneData101.get("MATERIAL_BDATE").toString();
                            String materialEDate = oneData101.get("MATERIAL_EDATE") == null ? "" : oneData101.get("MATERIAL_EDATE").toString();
                            String sortId = oneData101.get("SORTID") == null ? "" : oneData101.get("SORTID").toString();
                            String isPick = oneData101.get("ISPICK") == null ? "" : oneData101.get("ISPICK").toString();
                            String isBatch = oneData101.get("ISBATCH") == null ? "" : oneData101.get("ISBATCH").toString();

                            body.put("materialPluNo",materialPluNo);
                            body.put("materialUnit",materialUnit);
                            body.put("materialPQty",materialPQty);
                            body.put("qty",qty);
                            body.put("lossRate",lossRate);
                            body.put("isBuckle",isBuckle);
                            body.put("isReplace",isReplace);
                            body.put("materialBDate",materialBDate);
                            body.put("materialEDate",materialEDate);
                            body.put("sortId",sortId);
                            body.put("isPick",isPick);
                            body.put("isBatch",isBatch);
                            request_detail.put(body);
                        }
                    }

                    sql=this.getQuerySql101_02(eId,bomNo);
                    List<Map<String, Object>> getQData101_02 = this.doQueryData(sql, null);
                    if(CollUtil.isNotEmpty(getQData101_02)){
                        for (Map<String, Object> row : getQData101_02){
                            JSONObject body = new JSONObject(); // 存一笔单身
                            String shopId = row.get("SHOPID") == null ? "" : row.get("SHOPID").toString();
                            body.put("shopId",shopId);
                            request_range.put(body);
                        }
                    }

                    // 给单头赋值
                    header.put("eId", eId);
                    header.put("bomNo", bomNo);
                    header.put("pluNo", pluNo);
                    header.put("unit", unit);
                    header.put("mulQty", mulQty);
                    header.put("effDate", effDate);
                    header.put("memo", memo);
                    header.put("status", status);
                    header.put("restrictShop", restrictShop);
                    header.put("prodType", prodType);
                    header.put("batchQty", batchQty);
                    header.put("versionNum", versionNum);
                    header.put("remainType", remainType);
                    header.put("containType", containType);
                    header.put("standardHours", standardHours);
                    header.put("minQty", minQty);
                    header.put("fixedLossQty", fixedLossQty);
                    header.put("createOpId", createOpId);
                    header.put("createTime", createTime);
                    header.put("lastModiOpId", lastModiOpId);
                    header.put("lastModiTime", lastModiTime);
                    header.put("request_range", request_range);
                    header.put("request_detail", request_detail);

                    request.put(header);

                    parameter.put("bom", request);

                    std_data.put("parameter", parameter);
                    payload.put("std_data", std_data);

                    String str = payload.toString();// 将json对象转换为字符串
                    String resbody = "";

                    // 执行请求操作，并拿到结果（同步阻塞）
                    try
                    {
                        resbody= HttpSend.Send(str, "bom.create", eId, "", "", bomNo);
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
                        if(std_data_res.has("parameter")){
                            JSONObject parameter_res = std_data_res.getJSONObject("parameter");
                            if(parameter_res.has("doc_no") && parameter_res.has("org_no")){
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
//                                DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
//                                values.put("UPDATE_TIME", v1);
                            //记录ERP 返回的单号和组织
                            DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
                            DataValue orgNoVal = new DataValue(orgNo, Types.VARCHAR);
                            values.put("PROCESS_ERP_NO", docNoVal);
                            values.put("PROCESS_ERP_ORG", orgNoVal);
                            // condition
                            Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
                            DataValue c2 = new DataValue(eId, Types.VARCHAR);
                            conditions.put("EID", c2);
                            DataValue c3 = new DataValue(bomNo, Types.VARCHAR);
                            conditions.put("BOMNO", c3);

                            this.doUpdate("DCP_BOM", values, conditions);
                            InsertWSLOG.delete_WSLOG(eId, orgNo,"1", bomNo);
                            //

                            sReturnInfo="0";
                        }
                        else
                        {
                            sReturnInfo="ERP返回错误信息:" + code + "," + description;
                            //写数据库
                            InsertWSLOG.insert_WSLOG("bom.create",orgNo,eId,orgNo,"1",str,resbody,code,description) ;
                        }
                    }
                    catch (Exception e)
                    {
                        //
                        InsertWSLOG.insert_WSLOG("bom.create",bomNo,eId,"","1",str,resbody,"-1",e.getMessage());
                        sReturnInfo="错误信息:" + e.getMessage();
                        logger.error("\r\n******MES配料单上传 MES_BomUpload：组织编码=" + "" + ",公司编码=" +eId +",配方="  +bomNo + "\r\n报错信息："+e.getMessage()+"******\r\n");
                    }



                }
            }
            else
            {
                sReturnInfo="错误信息:无单头数据！";
                logger.trace("\r\n******MES配方创建MES_BomUpload,没有要上传的单头数据******\r\n");
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

                logger.error("\r\n******MES配方创建MES_BomUpload报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******MES配方创建MES_BomUpload报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.trace("\r\n*********MES配方创建MES_BomUpload定时调用End:************\r\n");
        }
        return sReturnInfo;
    }



    //DCP_BOM
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from ( "
                + "select row_number() over(order by a.createtime) rn, a.*  " +
                "from DCP_BOM a " +
                "where a.process_status ='N' "
        );

        sqlbuf.append(" ) " +
                "where rn>0 and rn<=10000 ");
        logger.trace("getQuerySql100_01:"+sqlbuf.toString());
        return sqlbuf.toString();
    }

    //DCP_BOM_MATERIAL
    protected String getQuerySql101_01(String eId, String bomNo) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select a.*  from DCP_BOM_MATERIAL a  " +
                " where a.eid='"+eId+"'  " +
                "and a.BOMNO='"+bomNo+"'  " +
                " ");
        logger.trace("getQuerySql101_01:"+sqlbuf.toString());
        return sqlbuf.toString();
    }

    protected String getQuerySql101_02(String eId, String bomNo) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select a.*  from DCP_BOM_RANGE a  " +
                " where a.eid='"+eId+"'  " +
                "and a.BOMNO='"+bomNo+"'  " +
                " ");
        logger.trace("getQuerySql101_02:"+sqlbuf.toString());
        return sqlbuf.toString();
    }



}