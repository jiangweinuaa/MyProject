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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 配料单上传erp
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_BatchUpload extends InitJob
{


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pOrganizationNo="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(MES_BatchUpload.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public  MES_BatchUpload()
    {

    }

    public  MES_BatchUpload(String eId,String shopId,String organizationNo, String billNo)
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
            logger.info("\r\n*********MES配料单上传MES_BatchUpload正在执行中,本次调用取消:************\r\n");

            sReturnInfo = "定时传输任务-MES配料单上传MES_BatchUpload正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//			

        logger.info("\r\n*********MES配料单上传MES_BatchUpload定时调用Start:************\r\n");

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
                    String batchNo = oneData100.get("BATCHNO") == null ? "" : oneData100.get("BATCHNO").toString();
                    String bDate   = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
                    String createOpId = oneData100.get("CREATEOPID") == null ? "" : oneData100.get("CREATEOPID").toString();
                    // 获取单身数据
                    sql = this.getQuerySql101_01(organizationNo, eId, batchNo);
                    List<Map<String, Object>> getQData101_01 = this.doQueryData(sql, null);
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
                            int item = oneData101.get("ITEM") == null ? 1 : Integer.valueOf(oneData101.get("ITEM").toString());
                            String pluNo = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
                            double pqty = oneData101.get("PQTY") == null ? 0 : Double.valueOf(oneData101.get("PQTY").toString());
                            String pUnit = oneData101.get("PUNIT") == null ? "" : oneData101.get("PUNIT").toString();

                            body.put("item", item);
                            body.put("pluNo", pluNo);
                            body.put("pUnit",pUnit);
                            body.put("pQty", pqty);
                            sql = this.getQuerySql101_01_01(organizationNo, eId, batchNo,Integer.valueOf(item));
                            List<Map<String, Object>> getQData101_01_01 = this.doQueryData(sql, null);

                            JSONArray request_detail_mo = new JSONArray(); // 存所有子单身

                            //子单身处理
                            if (getQData101_01_01 != null	&& getQData101_01_01.isEmpty() == false)
                            {
                                for (Map<String, Object> Data : getQData101_01_01)
                                {
                                    JSONObject sBody = new JSONObject(); // 存一笔子单身

                                    String sourceDocNo = Data.get("SOURCEDOCNO") == null ? "" : Data.get("SOURCEDOCNO").toString();
                                    String mItem = Data.get("MITEM") == null ? "1" : Data.get("MITEM").toString();
                                    String fromWarehouse = Data.get("FROMWAREHOUSE") == null ? "" : Data.get("FROMWAREHOUSE").toString();
                                    String toWarehouse = Data.get("TOWAREHOUSE") == null ? "" : Data.get("TOWAREHOUSE").toString();
                                    String totSharePQty = Data.get("TOTSHAREPQTY") == null ? "0" : Data.get("TOTSHAREPQTY").toString();
                                    String baseUnit = Data.get("BASEUNIT") == null ? "" : Data.get("BASEUNIT").toString();
                                    String totBaseQty = Data.get("TOTBASEQTY") == null ? "0" : Data.get("TOTBASEQTY").toString();
                                    String batch = Data.get("BATCH") == null ? "" : Data.get("BATCH").toString();

                                    sBody.put("sourceDocNo", sourceDocNo);
                                    sBody.put("mItem", mItem);
                                    sBody.put("fromWarehouse",fromWarehouse);
                                    sBody.put("toWarehouse",toWarehouse);
                                    sBody.put("totSharePQty", totSharePQty);
                                    sBody.put("baseUnit", baseUnit);
                                    sBody.put("totBaseQty", totBaseQty);
                                    sBody.put("batchNo", batch);
                                    request_detail_mo.put(sBody);
                                }
                            }
                            body.put("mo_detail", request_detail_mo);
                            request_detail.put(body);
                        }
                        // 给单头赋值
                        header.put("eId", eId);
                        header.put("organizationNo", organizationNo);
                        header.put("batchNo", batchNo);
                        header.put("bDate", bDate);
                        header.put("createOpId", createOpId);
                        header.put("version", "3.0");
                        header.put("mesbatch_detail", request_detail);
                        request.put(header);

                        parameter.put("mesbatch", request);

                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);

                        String str = payload.toString();// 将json对象转换为字符串
                        String resbody = "";

                        // 执行请求操作，并拿到结果（同步阻塞）
                        try
                        {
                            resbody= HttpSend.Send(str, "mesBatch.create", eId, organizationNo, organizationNo, batchNo);
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
                                DataValue c3 = new DataValue(batchNo, Types.VARCHAR);
                                conditions.put("BATCHNO", c3);

                                this.doUpdate("MES_BATCHING", values, conditions);
                                InsertWSLOG.delete_WSLOG(eId, orgNo,"1", batchNo);
                                //

                                sReturnInfo="0";
                            }
                            else
                            {
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                //写数据库
                                InsertWSLOG.insert_WSLOG("mesBatch.create",orgNo,eId,orgNo,"1",str,resbody,code,description) ;
                            }
                        }
                        catch (Exception e)
                        {
                            //
                            InsertWSLOG.insert_WSLOG("mesBatch.create",batchNo,eId,organizationNo,"1",str,resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();
                            logger.error("\r\n******MES配料单上传 MES_BatchUpload：组织编码=" + organizationNo + ",公司编码=" +eId +",单号="  +batchNo + "\r\n报错信息："+e.getMessage()+"******\r\n");
                        }

                    }
                }
            }
            else
            {
                sReturnInfo="错误信息:无单头数据！";
                logger.info("\r\n******MES配料单上传MES_BatchUpload,没有要上传的单头数据******\r\n");
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

                logger.error("\r\n******MES配料单上传MES_BatchUpload报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******MES配料单上传MES_BatchUpload报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********MES配料单上传MES_BatchUpload定时调用End:************\r\n");
        }
        return sReturnInfo;
    }



    //MES_BATCHING
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from ( "
                              + "select row_number() over(order by a.createtime) rn, a.*  " +
                              "from MES_BATCHING a " +
                              "where a.process_status ='N' "
        );

        sqlbuf.append(" ) " +
                              "where rn>0 and rn<=10000 ");
        logger.info("getQuerySql100_01:"+sqlbuf.toString());
        return sqlbuf.toString();
    }

    //MES_BATCHING_DETAIL
    protected String getQuerySql101_01(String orgNo, String eId, String billNo) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select a.*  from MES_BATCHING_DETAIL a  " +
                              " where a.eid='"+eId+"'  " +
                              "and a.organizationno='"+orgNo+"'  " +
                              "and a.BATCHNO='"+billNo+"'  " +
                              "order by a.item  ");
        logger.info("getQuerySql101_01:"+sqlbuf.toString());
        return sqlbuf.toString();
    }

    //MES_BATCHING_DETAIL_MO
    protected String getQuerySql101_01_01(String orgNo, String eId, String billNo,Integer item) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select d.load_docno AS SOURCEDOCNO,c.mitem,c.fromwarehouse,c.towarehouse, "
                              +" sum(c.sharepqty) as totsharepqty,c.baseunit,sum(c.baseqty) as totbaseqty,c.batch "
                              +" from  mes_batching_detail_mo c "
                              +" left join mes_mo d on d.eid=c.eid and d.organizationno=c.organizationno and d.mono=c.sourceno "
                              +" where c.eid='"+eId+"' "
                              +" and c.organizationno='"+orgNo+"' "
                              +" and c.batchno='"+billNo+"' "
                              +" and c.oitem="+item
                              +" group by d.load_docno,c.mitem,c.fromwarehouse,c.baseunit,c.towarehouse,c.batch ");
        logger.info("getQuerySql101_01_01:"+sqlbuf.toString());
        return sqlbuf.toString();
    }




}
