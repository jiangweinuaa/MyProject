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
 *MES销货单上传
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_SalesSlipToErp extends InitJob
{


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(MES_SalesSlipToErp.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public MES_SalesSlipToErp()
    {

    }

    public MES_SalesSlipToErp(String eId, String shopId, String organizationNO, String billNo)
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
            logger.debug("\r\n*********MES销货单上传MES_SalesSlipToErp正在执行中,本次调用取消:************\r\n");

            sReturnInfo = "定时传输任务-MES销货单上传MES_SalesSlipToErp正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//			

        logger.debug("\r\n*********MES销货单上传MES_SalesSlipToErp定时调用Start:************\r\n");

        try
        {

            String sql = this.getQuerySql100_01();
            logger.info("\r\n******MES销货单上传MES_SalesSlipToErp单头sql:" + sql);

            List<Map<String, Object>> getQData100_01 = this.doQueryData(sql, null);
            if (getQData100_01 != null && getQData100_01.isEmpty() == false)
            {

                for (Map<String, Object> oneData100: getQData100_01)
                {
                    String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                    String organizationNo = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
                    String saleNo = oneData100.get("SALENO") == null ? "" : oneData100.get("SALENO").toString();
                    String erpOrderNo = oneData100.get("ERPORDERNO") == null ? "" : oneData100.get("ERPORDERNO").toString();
                    String bDate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
                    String customer = oneData100.get("CUSTOMER") == null ? "" : oneData100.get("CUSTOMER").toString();
                    String salesMan = oneData100.get("SALESMAN") == null ? "" : oneData100.get("SALESMAN").toString();
                    String salesManTel = oneData100.get("SALESTELE") == null ? "" : oneData100.get("SALESTELE").toString();
                    String department = oneData100.get("DEPARTMENT") == null ? "" : oneData100.get("DEPARTMENT").toString();
                    String address = oneData100.get("ADDRESS") == null ? "" : oneData100.get("ADDRESS").toString();
                    String contactName = oneData100.get("CONTACTNAME") == null ? "" : oneData100.get("CONTACTNAME").toString();
                    String contactTele = oneData100.get("CONTACTTELE") == null ? "" : oneData100.get("CONTACTTELE").toString();
                    String memo = oneData100.get("MEMO") == null ? "" : oneData100.get("MEMO").toString();
                    String tot_Qty = oneData100.get("TOT_QTY") == null ? "" : oneData100.get("TOT_QTY").toString();
                    String tot_CQty = oneData100.get("TOT_CQTY") == null ? "" : oneData100.get("TOT_CQTY").toString();
                    String createBy = oneData100.get("CREATEOPID") == null ? "" : oneData100.get("CREATEOPID").toString();
                    String createByName = oneData100.get("CREATEOPNAME") == null ? "" : oneData100.get("CREATEOPNAME").toString();
                    String createTime = oneData100.get("CREATETIME") == null ? "" : oneData100.get("CREATETIME").toString();
                    String lastModifyBy = oneData100.get("LASTMODIOPID") == null ? "" : oneData100.get("LASTMODIOPID").toString();
                    String lastModifyByName = oneData100.get("LASTMODIOPNAME") == null ? "" : oneData100.get("LASTMODIOPNAME").toString();
                    String lastModifyTime = oneData100.get("LASTMODTIME") == null ? "" : oneData100.get("LASTMODTIME").toString();

                    // 获取单身数据
                    sql = this.getQuerySql101_01(eId, organizationNo, saleNo);
                    logger.info("\r\n******MES销货单上传MES_SalesSlipToErp单身sql:" + sql);
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
                            String erpOItem = oneData101.get("ERPOTIEM") == null ? "" : oneData101.get("ERPOTIEM").toString();
                            String pluNo = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
                            String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
                            String pUnit = oneData101.get("UNIT") == null ? "" : oneData101.get("UNIT").toString();
                            String pQty = oneData101.get("PQTY") == null ? "0" : oneData101.get("PQTY").toString();
                            String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
                            String baseQty = oneData101.get("BASEQTY") == null ? "" : oneData101.get("BASEQTY").toString();
                            String warehouseNo = oneData101.get("WAREHOUSENO") == null ? "" : oneData101.get("WAREHOUSENO").toString();
                            String batchNo = oneData101.get("BATCHNO") == null ? "" : oneData101.get("BATCHNO").toString();
                            String location = oneData101.get("LOCATION") == null ? "" : oneData101.get("LOCATION").toString();

                            body.put("item", item);
                            body.put("erpOItem",erpOItem);
                            body.put("pluNo", pluNo);
                            body.put("featureNo", featureNo);
                            body.put("pUnit", pUnit);
                            body.put("pQty", pQty);
                            body.put("baseUnit", baseUnit);
                            body.put("baseQty", baseQty);
                            body.put("warehouseNo", warehouseNo);
                            body.put("batchNo", batchNo);
                            body.put("location", location);

                            //
                            request_detail.put(body);
                        }

                        // 给单头赋值
                        header.put("version", "3.0");
                        header.put("eId", eId);
                        header.put("organizationNo", organizationNo);
                        header.put("saleNo", saleNo);
                        header.put("erpOrderNo", erpOrderNo);
                        header.put("bDate", bDate);
                        header.put("customer", customer);
                        header.put("salesMan", salesMan);
                        header.put("salesManTel", salesManTel);
                        header.put("department", department);
                        header.put("address", address);
                        header.put("contactName", contactName);
                        header.put("contactTele", contactTele);
                        header.put("memo", memo);
                        header.put("tot_Qty", tot_Qty);
                        header.put("tot_CQty", tot_CQty);
                        header.put("createBy", createBy);
                        header.put("createByName", createByName);
                        header.put("createTime", createTime);
                        header.put("lastModifyBy", lastModifyBy);
                        header.put("lastModifyByName", lastModifyByName);
                        header.put("lastModifyTime", lastModifyTime);
                        header.put("mesSalesSlip_detail", request_detail);
                        request.put(header);

                        parameter.put("mesSalesSlip", request);

                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);

                        String str = payload.toString();// 将json对象转换为字符串
                        String resbody = "";

                        // 执行请求操作，并拿到结果（同步阻塞）
                        try
                        {
                            logger.info("\r\n******MES销货单上传mesSalesSlip.create的数据:" + str);
                            resbody= HttpSend.Send(str, "mesSalesSlip.create", eId, organizationNo, organizationNo, saleNo);
                            logger.info("\r\n******MES销货单上传mesSalesSlip.create ERP返回的内容:" + resbody);
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
                                DataValue c1 = new DataValue(organizationNo, Types.VARCHAR);
                                conditions.put("ORGANIZATIONNO", c1);
                                DataValue c2 = new DataValue(eId, Types.VARCHAR);
                                conditions.put("EID", c2);
                                DataValue c3 = new DataValue(saleNo, Types.VARCHAR);
                                conditions.put("SALENO", c3);

                                this.doUpdate("MES_SALESSLIP", values, conditions);
                                InsertWSLOG.delete_WSLOG(eId, organizationNo,"1", saleNo);
                                //

                                sReturnInfo="0";
                            }
                            else
                            {
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                //写数据库
                                InsertWSLOG.insert_WSLOG("mesSalesSlip.create",saleNo,eId,organizationNo,"1",str,resbody,code,description) ;
                            }
                        }
                        catch (Exception e)
                        {
                            //
                            InsertWSLOG.insert_WSLOG("mesSalesSlip.create",saleNo,eId,organizationNo,"1",str,resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();
                            logger.error("\r\n******MES销货单上传mesSalesSlip.create：组织编码=" + organizationNo + ",公司编码=" +eId +",订单编号="  +saleNo + "\r\n报错信息："+e.getMessage()+"******\r\n");
                        }

                    }
                    else
                    {
                        logger.debug("\r\n******MES销货单上传MES_SalesSlipToErp,saleNo="+saleNo+"没有要上传的明细数据******\r\n");
                    }
                        
                }
            }
            else
            {
                sReturnInfo="错误信息:无单头数据！";
                logger.debug("\r\n******MES销货单上传MES_SalesSlipToErp,没有要上传的单头数据******\r\n");
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

                logger.error("\r\n******MES销货单上传MES_SalesSlipToErp报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******MES销货单上传MES_SalesSlipToErp报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.debug("\r\n*********MES销货单上传MES_SalesSlipToErp定时调用End:************\r\n");
        }
        return sReturnInfo;
    }



    //MES_ORDER MES_SALESSLIP
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from ( "
                              + "select row_number() over(order by a.createtime) rn, a.*,b.ofno ERPORDERNO  " +
                              " from MES_SALESSLIP  a " +
                              " left join mes_order b on a.eid = b.eid and a.organizationno = b.organizationno and a.ofno= b.orderno " +
                              " where a.status=1 and (a.PROCESS_STATUS is null or  a.PROCESS_STATUS ='N') " +
                              " "
        );

        sqlbuf.append(" ) " +
                              "where rn>0 and rn<=100 ");
        return sqlbuf.toString();
    }

    //MES_ORDER_DETAIL  MES_SALESSLIP_DETAIL
    protected String getQuerySql101_01(String eId, String organizationNo, String saleNo) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select a.*,b.ofno erpOrderNo,b.oitem ERPOTIEM from MES_SALESSLIP_detail a  " +
                              "left join mes_order_detail b on a.EID=b.EID AND a.ORGANIZATIONNO=b.ORGANIZATIONNO AND a.ofno = b.orderno and a.oitem = b.item " +
                              "where a.eid='"+eId+"'  " +
                              "and a.organizationno='"+organizationNo+"'  " +
                              "and a.saleno='"+saleNo+"'  " +
                              " ");
        return sqlbuf.toString();
    }




}
