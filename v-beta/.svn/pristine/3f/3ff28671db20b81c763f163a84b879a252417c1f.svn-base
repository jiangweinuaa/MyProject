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
 *MES盘点单上传
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_StockTakeToErp extends InitJob
{


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(MES_StockTakeToErp.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public  MES_StockTakeToErp()
    {

    }

    public  MES_StockTakeToErp(String eId,String shopId,String organizationNO, String billNo)
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
            logger.debug("\r\n*********MES盘点单上传MES_StockTakeToErp正在执行中,本次调用取消:************\r\n");

            sReturnInfo = "定时传输任务-MES盘点单上传MES_StockTakeToErp正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//			

        logger.debug("\r\n*********MES盘点单上传MES_StockTakeToErp定时调用Start:************\r\n");

        try
        {

            String sql = this.getQuerySql100_01();
            List<Map<String, Object>> getQData100_01 = this.doQueryData(sql, null);
            if (getQData100_01 != null && getQData100_01.isEmpty() == false)
            {

                for (Map<String, Object> oneData100: getQData100_01)
                {
                    String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                    String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
                    String stocktakeno = oneData100.get("STOCKTAKENO") == null ? "" : oneData100.get("STOCKTAKENO").toString();
                    String warehouse = oneData100.get("WAREHOUSE") == null ? "" : oneData100.get("WAREHOUSE").toString();
                    String ofno = oneData100.get("OFNO") == null ? "" : oneData100.get("OFNO").toString();
                    String bdate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
                    String createby = oneData100.get("CREATEBY") == null ? "" : oneData100.get("CREATEBY").toString();


                    // 获取单身数据
                    sql = this.getQuerySql101_01(organizationNO, eId, stocktakeno,warehouse);
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
                        JSONArray request_detail_share = new JSONArray(); // 存所有子单身

                        for (Map<String, Object> oneData101 : getQData101_01)
                        {
                            // 获取单身数据并赋值
                            JSONObject body = new JSONObject(); // 存一笔单身
                            String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();
                            String pluNO = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
                            String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
                            String oitem = oneData101.get("OITEM") == null ? "" : oneData101.get("OITEM").toString();
                            String batchno = oneData101.get("BATCH_NO") == null ? "" : oneData101.get("BATCH_NO").toString();
                            String location = oneData101.get("LOCATION") == null ? "" : oneData101.get("LOCATION").toString();
                            String pqty = oneData101.get("PQTY") == null ? "0" : oneData101.get("PQTY").toString();
                            //String punit = oneData101.get("PUNIT") == null ? "" : oneData101.get("PUNIT").toString();
                            //String baseqty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
                            String baseunit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
                            String stockQty = oneData101.get("REF_BASEQTY") == null ? "0" : oneData101.get("REF_BASEQTY").toString();

                            body.put("pluNo", pluNO);
                            body.put("featureNo", featureNo);
                            body.put("oItem",oitem);
                            body.put("item", item);
                            body.put("batchNo", batchno);
                            body.put("location", location);
                            body.put("pQty", pqty);
                            body.put("stockQty", stockQty);
                            body.put("baseUnit", baseunit);
                            //
                            request_detail.put(body);
                        }

                        // 给单头赋值
                        header.put("eId", eId);
                        header.put("organizationNo", organizationNO);
                        header.put("stockTaskNo", stocktakeno);
                        header.put("ofNo", ofno);
                        header.put("bDate", bdate);
                        header.put("opNo", createby);
                        header.put("warehouseNo", warehouse);
                        header.put("version", "3.0");
                        header.put("messtocktake_detail", request_detail);
                        request.put(header);

                        parameter.put("messtocktake", request);

                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);

                        String str = payload.toString();// 将json对象转换为字符串
                        String resbody = "";

                        // 执行请求操作，并拿到结果（同步阻塞）
                        try
                        {
                            resbody= HttpSend.Send(str, "mesStockTake.create", eId, organizationNO, organizationNO, stocktakeno);
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

                                //记录ERP 返回的单号和组织
                                DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
                                DataValue orgNoVal = new DataValue(orgNo, Types.VARCHAR);
                                values.put("PROCESS_ERP_NO", docNoVal);
                                values.put("PROCESS_ERP_ORG", orgNoVal);

                                // condition
                                Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
                                DataValue c1 = new DataValue(organizationNO, Types.VARCHAR);
                                conditions.put("ORGANIZATIONNO", c1);
                                DataValue c2 = new DataValue(eId, Types.VARCHAR);
                                conditions.put("EID", c2);
                                DataValue c3 = new DataValue(stocktakeno, Types.VARCHAR);
                                conditions.put("STOCKTAKENO", c3);

                                this.doUpdate("DCP_STOCKTAKE", values, conditions);
                                InsertWSLOG.delete_WSLOG(eId, organizationNO,"1", stocktakeno);
                                //

                                sReturnInfo="0";
                            }
                            else
                            {
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                //写数据库
                                InsertWSLOG.insert_WSLOG("mesStockTake.create",stocktakeno,eId,organizationNO,"1",str,resbody,code,description) ;
                            }
                        }
                        catch (Exception e)
                        {
                            //
                            InsertWSLOG.insert_WSLOG("mesStockTake.create",stocktakeno,eId,organizationNO,"1",str,resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();
                            logger.error("\r\n******MES盘点单上传mesStockTake.create：组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +stocktakeno + "\r\n报错信息："+e.getMessage()+"******\r\n");
                        }

                    }
                    else
                    {
                        logger.debug("\r\n******MES盘点单上传MES_StockTakeToErp,stocktakeno="+stocktakeno+"没有要上传的明细数据******\r\n");
                    }
                        
                }
            }
            else
            {
                sReturnInfo="错误信息:无单头数据！";
                logger.debug("\r\n******MES盘点单上传MES_StockTakeToErp,没有要上传的单头数据******\r\n");
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

                logger.error("\r\n******MES盘点单上传MES_StockTakeToErp报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******MES盘点单上传MES_StockTakeToErp报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.debug("\r\n*********MES盘点单上传MES_StockTakeToErp定时调用End:************\r\n");
        }
        return sReturnInfo;
    }



    //DCP_STOCKTAKE
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from ( "
                              + "select row_number() over(order by a.create_time) rn, a.*  " +
                              "from DCP_STOCKTAKE  a " +
                              "where a.process_status='N' " +
                              "and a.status=2 " +
                              " "
        );

        sqlbuf.append(" ) " +
                              "where rn>0 and rn<=100 ");
        return sqlbuf.toString();
    }

    //DCP_STOCKTAKE_DETAIL
    protected String getQuerySql101_01(String shopId, String eId, String stocktakeno,String warehouse) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select a.* from DCP_STOCKTAKE_DETAIL a  " +
                              "where a.eid='"+eId+"'  " +
                              "and a.organizationno='"+shopId+"'  " +
                              "and a.warehouse='"+warehouse+"' " +
                              "and a.stocktakeno='"+stocktakeno+"'  " +
                              "order by a.item  ");
        return sqlbuf.toString();
    }




}
