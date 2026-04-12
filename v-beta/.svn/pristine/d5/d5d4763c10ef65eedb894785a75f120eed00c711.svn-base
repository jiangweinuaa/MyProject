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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MES报工扣料单上传erp
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_ProcessReportUpload extends InitJob
{

    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pOrganizationNo="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(MES_ProcessReportUpload.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public  MES_ProcessReportUpload()
    {

    }

    public  MES_ProcessReportUpload(String eId,String shopId,String organizationNo, String billNo)
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
            logger.info("\r\n*********MES报工扣料单上传MES_ProcessReportUpload正在执行中,本次调用取消:************\r\n");

            sReturnInfo = "定时传输任务-MES报工扣料单上传MES_ProcessReportUpload正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//			

        logger.info("\r\n*********MES报工扣料单上传MES_ProcessReportUpload定时调用Start:************\r\n");

        try
        {
            String sql = this.getQuerySql100_01();
            List<Map<String, Object>> getQData100_01 = this.doQueryData(sql, null);
            if (getQData100_01 != null && getQData100_01.isEmpty() == false)
            {
                for (Map<String, Object> oneData100: getQData100_01)
                {
                    String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                    String organizationNo = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
                    String reportno = oneData100.get("REPORTNO") == null ? "" : oneData100.get("REPORTNO").toString();
                    String createOpId = oneData100.get("CREATEOPID") == null ? "" : oneData100.get("CREATEOPID").toString();
                    String bDate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
                    String returnstatus = oneData100.get("RETURNSTATUS") == null ? "" : oneData100.get("RETURNSTATUS").toString();
                    String returnreportno = oneData100.get("RETURNREPORTNO") == null ? "" : oneData100.get("RETURNREPORTNO").toString();
                    String pgroupno = oneData100.get("PGROUPNO") == null ? "" : oneData100.get("PGROUPNO").toString();

                    // t100req中的payload对象
                    JSONObject payload = new JSONObject();

                    // 自定义payload中的json结构
                    JSONObject std_data = new JSONObject();
                    JSONObject parameter = new JSONObject();

                    JSONArray request = new JSONArray();
                    JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）

                    // 获取单身数据
                    JSONArray request_detail = new JSONArray(); // 存所有单身
                    sql = this.getQuerySql101_01(organizationNo, eId, reportno);
                    List<Map<String, Object>> getQData101_01 = this.doQueryData(sql, null);
                    if (getQData101_01 != null	&& getQData101_01.isEmpty() == false)
                    {
                        for (Map<String, Object> oneData101 : getQData101_01)
                        {
                            // 获取单身数据并赋值
                            JSONObject body = new JSONObject(); // 存一笔单身
                            String material_pluno = oneData101.get("MATERIAL_PLUNO") == null ? "" : oneData101.get("MATERIAL_PLUNO").toString();
                            String material_unit = oneData101.get("MATERIAL_UNIT") == null ? "" : oneData101.get("MATERIAL_UNIT").toString();
                            double pqty = oneData101.get("PQTY") == null ? 0 : Double.valueOf(oneData101.get("PQTY").toString());
                            String warehouse = oneData101.get("KWAREHOUSE") == null ? "" : oneData101.get("KWAREHOUSE").toString();
                            String baseunit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
                            String baseqty = oneData101.get("BASEQTY") == null ? "" : oneData101.get("BASEQTY").toString();


                            body.put("pluNo", material_pluno);
                            body.put("pUnit", material_unit);
                            body.put("pQty",pqty);
                            body.put("warehouse", warehouse);
                            body.put("baseUnit", baseunit);
                            body.put("baseQty", baseqty);
                            request_detail.put(body);
                        }

                        header.put("mesReportMaterial_detail", request_detail);
                    }

                    //查扣料的，没有记录，不用上传
                    if (request_detail.length()==0)
                    {
                        // values
                        Map<String, DataValue> values = new HashMap<String, DataValue>() ;
                        DataValue v= new DataValue("Y", Types.VARCHAR);
                        values.put("PROCESS_STATUS", v);
                        // condition
                        Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
                        DataValue c1 = new DataValue(organizationNo, Types.VARCHAR);
                        conditions.put("ORGANIZATIONNO", c1);
                        DataValue c2 = new DataValue(eId, Types.VARCHAR);
                        conditions.put("EID", c2);
                        DataValue c3 = new DataValue(reportno, Types.VARCHAR);
                        conditions.put("REPORTNO", c3);

                        this.doUpdate("MES_PROCESS_REPORT", values, conditions);

                        logger.info("\r\n******MES报工扣料单上传MES_ProcessReportUpload：组织编码=" + organizationNo + ",公司编码=" +eId +",单号="  +reportno + "\r\n查扣料的，没有记录，不用上传******\r\n");

                        continue;
                    }


                    //获取单身2=============================
                    JSONArray request_detail_2 = new JSONArray(); // 存所有单身
                    String sql_mo = this.getQuerySql101_02(organizationNo, eId, reportno);
                    List<Map<String, Object>> getQData101_02 = this.doQueryData(sql_mo, null);
                    if (getQData101_02 != null	&& getQData101_02.isEmpty() == false)
                    {
                        for (Map<String, Object> oneData101 : getQData101_02)
                        {
                            // 获取单身数据并赋值
                            JSONObject body = new JSONObject(); // 存一笔单身
                            String sourceno = oneData101.get("SOURCENO") == null ? "" : oneData101.get("SOURCENO").toString();
                            String mitem = oneData101.get("MITEM") == null ? "" : oneData101.get("MITEM").toString();
                            String load_docno = oneData101.get("LOAD_DOCNO") == null ? "" : oneData101.get("LOAD_DOCNO").toString();

                            body.put("sourceDocNo", load_docno);
                            body.put("mItem", mitem);
                            request_detail_2.put(body);
                        }
                        header.put("mesReportMaterial_mo", request_detail_2);
                    }

                    // 给单头赋值
                    header.put("eId", eId);
                    header.put("organizationNo", organizationNo);
                    header.put("reportNo", reportno);
                    header.put("createOpId", createOpId);
                    header.put("bDate", bDate);
                    header.put("returnStatus", returnstatus);
                    header.put("returnReportNo", returnreportno);
                    header.put("pGroupNo", pgroupno);
                    header.put("version", "3.0");
                    request.put(header);
                    parameter.put("mesReportMaterial", request);

                    std_data.put("parameter", parameter);
                    payload.put("std_data", std_data);
                    String str = payload.toString();// 将json对象转换为字符串
                    String resbody = "";

                    // 执行请求操作，并拿到结果（同步阻塞）
                    try
                    {
                        resbody= HttpSend.Send(str, "mesReportMaterial.create", eId, organizationNo, organizationNo, reportno);
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
                            DataValue c1 = new DataValue(orgNo, Types.VARCHAR);
                            conditions.put("ORGANIZATIONNO", c1);
                            DataValue c2 = new DataValue(eId, Types.VARCHAR);
                            conditions.put("EID", c2);
                            DataValue c3 = new DataValue(reportno, Types.VARCHAR);
                            conditions.put("REPORTNO", c3);

                            this.doUpdate("MES_PROCESS_REPORT", values, conditions);
                            InsertWSLOG.delete_WSLOG(eId, orgNo,"1", reportno);
                            //

                            sReturnInfo="0";
                        }
                        else
                        {
                            sReturnInfo="ERP返回错误信息:" + code + "," + description;
                            //写数据库
                            InsertWSLOG.insert_WSLOG("mesReportMaterial.create",orgNo,eId,orgNo,"1",str,resbody,code,description) ;
                        }
                    }
                    catch (Exception e)
                    {
                        //
                        InsertWSLOG.insert_WSLOG("mesReportMaterial.create",reportno,eId,organizationNo,"1",str,resbody,"-1",e.getMessage());
                        sReturnInfo="错误信息:" + e.getMessage();
                        logger.error("\r\n******MES报工扣料单上传MES_ProcessReportUpload：组织编码=" + organizationNo + ",公司编码=" +eId +",单号="  +reportno + "\r\n报错信息："+e.getMessage()+"******\r\n");
                    }

                }
            }
            else
            {
                sReturnInfo="错误信息:无单头数据！";
                logger.info("\r\n******MES报工扣料单上传MES_ProcessReportUpload,没有要上传的单头数据******\r\n");
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

                logger.error("\r\n******MES报工扣料单上传MES_ProcessReportUpload报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******MES报工扣料单上传MES_ProcessReportUpload报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********MES报工扣料单上传MES_ProcessReportUpload定时调用End:************\r\n");
        }
        return sReturnInfo;
    }



    //MES_PROCESS_REPORT
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from ( "
                              + "select row_number() over(order by a.LASTMODITIME) rn, a.*,b.pgroupno  " +
                              "from MES_PROCESS_REPORT a " +
                              "left join MES_BATCHTASK b on a.eid=b.eid and a.organizationno=b.organizationno and a.batchtaskno=b.batchtaskno " +
                              "where a.process_status ='N' " +
                              "and a.status=1 "
        );

        sqlbuf.append(" ) " +
                              "where rn>0 and rn<=10000 ");
        logger.info("getQuerySql100_01:"+sqlbuf.toString());
        return sqlbuf.toString();
    }

    //MES_PROCESS_REPORT_MATERIAL
    protected String getQuerySql101_01(String orgNo, String eId, String billNo) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select a.material_pluno,a.material_unit,a.kwarehouse,a.baseunit,sum(a.material_qty) PQTY,sum(a.baseqty) BASEQTY " +
                              " from MES_PROCESS_REPORT_MATERIAL a  " +
                              " where a.eid='"+eId+"'  " +
                              "and a.organizationno='"+orgNo+"'  " +
                              "and a.reportno='"+billNo+"'  " +
                              "and a.material_type='0' " +
                              "and a.isbuckle='Y' " +
                              "group by a.material_pluno,a.material_unit,a.kwarehouse,a.baseunit " +
                              "order by a.material_pluno,a.material_unit,a.kwarehouse,a.baseunit ");
        logger.info("getQuerySql101_01:"+sqlbuf.toString());
        return sqlbuf.toString();
    }

    //MES_PROCESS_REPORT_MO
    protected String getQuerySql101_02(String orgNo, String eId, String billNo) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select a.sourceno,a.mitem,b.load_docno  "
                              +" from  MES_PROCESS_REPORT_MO a "
                              +" left join mes_mo b on a.eid=b.eid and a.organizationno=b.organizationno and a.sourceno=b.mono "
                              +" where a.eid='"+eId+"' "
                              +" and a.organizationno='"+orgNo+"' "
                              +" and a.reportno='"+billNo+"' "
                              +" order by a.sourceno,a.mitem ");
        logger.info("getQuerySql101_01_01:"+sqlbuf.toString());
        return sqlbuf.toString();
    }







}
