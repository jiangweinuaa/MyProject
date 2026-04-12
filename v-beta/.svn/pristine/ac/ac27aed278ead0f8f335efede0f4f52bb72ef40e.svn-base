package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
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
 *MES退货入库上传
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_ReStockInToErp extends InitJob
{


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(MES_ReStockInToErp.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    protected DsmDAO dao;

    public void setDao(DsmDAO dao) {
        this.dao = dao;
    }

    public MES_ReStockInToErp()
    {

    }

    public MES_ReStockInToErp(String eId, String shopId, String organizationNO, String billNo)
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
            logger.debug("\r\n*********MES退货入库上传MES_ReStockInToErp正在执行中,本次调用取消:************\r\n");

            sReturnInfo = "定时传输任务-MES退货入库上传MES_ReStockInToErp正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//			

        logger.debug("\r\n*********MES退货入库上传MES_ReStockInToErp定时调用Start:************\r\n");

        try
        {

            String sql = this.getQuerySql100_01();
            logger.info("\r\n******MES退货入库上传MES_ReStockInToErp单头sql:" + sql);
            List<Map<String, Object>> getQData100_01 = this.doQueryData(sql, null);
            if (getQData100_01 != null && getQData100_01.isEmpty() == false)
            {

                for (Map<String, Object> oneData100: getQData100_01)
                {
                    String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                    String organizationNo = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
                    String receiptno = oneData100.get("RECEIPTNO") == null ? "" : oneData100.get("RECEIPTNO").toString();
                    String warehouseNo = oneData100.get("WAREHOUSENO") == null ? "" : oneData100.get("WAREHOUSENO").toString();
                    String bDate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
                    String tot_Qty = oneData100.get("TOT_QTY") == null ? "" : oneData100.get("TOT_QTY").toString();
                    String tot_CQty = oneData100.get("TOT_CQTY") == null ? "" : oneData100.get("TOT_CQTY").toString();
                    String createBy = oneData100.get("CREATEOPID") == null ? "" : oneData100.get("CREATEOPID").toString();
                    String createByName = oneData100.get("CREATEOPNAME") == null ? "" : oneData100.get("CREATEOPNAME").toString();
                    String createTime = oneData100.get("CREATETIME") == null ? "" : oneData100.get("CREATETIME").toString();
                    String b_ofno = oneData100.get("B_OFNO") == null ? "" : oneData100.get("B_OFNO").toString();
                    String b_shopid = oneData100.get("B_SHOPID") == null ? "" : oneData100.get("B_SHOPID").toString();

                    // 获取单身数据
                    sql = this.getQuerySql101_01(eId, organizationNo, receiptno, warehouseNo);
                    logger.info("\r\n******MES退货入库上传MES_ReStockInToErp单身sql:" + sql);
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
                            String eid = oneData101.get("EID") == null ? "" : oneData101.get("EID").toString();
                            String item = oneData101.get("ITEM") == null ? "0" : oneData101.get("ITEM").toString();
                            String pluNo = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
                            String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
                            String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
                            String pUnit = oneData101.get("PUNIT") == null ? "" : oneData101.get("PUNIT").toString();
                            String pqty = oneData101.get("PQTY") == null ? "0" : oneData101.get("PQTY").toString();

                            String baseQty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
                            String location = oneData101.get("LOCATION") == null ? "0" : oneData101.get("LOCATION").toString();
                            String batchNo = oneData101.get("BATCHNO") == null ? "0" : oneData101.get("BATCHNO").toString();

                            body.put("item", item);
                            body.put("pluNo", pluNo);
                            body.put("featureNo",featureNo);
                            body.put("baseUnit", baseUnit);
                            body.put("pUnit", pUnit);
                            body.put("pQty", pqty);
                            body.put("baseQty", baseQty);
                            body.put("location", location);
                            body.put("batchNo", batchNo);

                            //
                            request_detail.put(body);
                        }

                        // 给单头赋值
                        header.put("version", "3.0");
                        header.put("eId", eId);
                        header.put("organizationNo", organizationNo);
                        header.put("receiptNo", receiptno);
                        header.put("load_doc_no", b_ofno);
                        header.put("shopNo", b_shopid);
                        header.put("warehouseNo", warehouseNo);
                        header.put("bDate", bDate);
                        header.put("tot_Qty", tot_Qty);
                        header.put("tot_CQty", tot_CQty);
                        header.put("createBy", createBy);
                        header.put("createByName", createByName);
                        header.put("createTime", createTime);
                        header.put("mesReStockin_detail", request_detail);
                        request.put(header);

                        parameter.put("mesReStockin", request);

                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);

                        String str = payload.toString();// 将json对象转换为字符串
                        String resbody = "";

                        // 执行请求操作，并拿到结果（同步阻塞）
                        try
                        {
                            logger.info("\r\n******MES退货入库上传mesReStockIn.create的数据:" + str);
                            resbody= HttpSend.Send(str, "mesReStockIn.create", eId, organizationNo, organizationNo, receiptno);
                            logger.info("\r\n******MES退货入库上传mesReStockIn.create erp返回的内容" + resbody);
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
                                DataValue c3 = new DataValue(receiptno, Types.VARCHAR);
                                conditions.put("RECEIPTNO", c3);
                                this.doUpdate("MES_RESTOCKIN", values, conditions);
                                InsertWSLOG.delete_WSLOG(eId, organizationNo,"1", receiptno);
                                //

                                sReturnInfo="0";
                            }
                            else
                            {
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                //写数据库
                                InsertWSLOG.insert_WSLOG("mesReStockIn.create",receiptno,eId,organizationNo,"1",str,resbody,code,description) ;
                            }
                        }
                        catch (Exception e)
                        {
                            //
                            InsertWSLOG.insert_WSLOG("mesReStockIn.create",receiptno,eId,organizationNo,"1",str,resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();
                            logger.error("\r\n******MES退货入库上传mesReStockIn.create：组织编码=" + organizationNo + ",公司编码=" +eId +",退货单号="  +receiptno + "\r\n报错信息："+e.getMessage()+"******\r\n");
                        }

                    }
                    else
                    {
                        logger.debug("\r\n******MES退货入库上传MES_ReStockInToErp,receiptno="+receiptno+"没有要上传的明细数据******\r\n");
                    }
                        
                }
            }
            else
            {
                sReturnInfo="错误信息:无单头数据！";
                logger.debug("\r\n******MES退货入库上传MES_ReStockInToErp,没有要上传的单头数据******\r\n");
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

                logger.error("\r\n******MES退货入库上传MES_ReStockInToErp报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******MES退货入库上传MES_ReStockInToErp报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.debug("\r\n*********MES退货入库上传MES_ReStockInToErp定时调用End:************\r\n");
        }
        return sReturnInfo;
    }



    //DCP_STOCKOUT
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from ( "
                              + " select row_number() over(order by a.CREATETIME) rn, a.*,a.ofno B_OFNO ,a.shopid  B_SHOPID   " +
                              " from MES_RESTOCKIN  a " +
                              " left join DCP_STOCKOUT b on b.eid=a.eid and b.STOCKOUTNO=a.OFNO " +
                              " where b.status=3 " +
                              " and b.doc_type=0 " +
                              " and a.status=1 " +
                              " and (a.process_status = 'N' or a.process_status is null) "+
                              " "
        );

        sqlbuf.append(" ) " +
                              "where rn>0 and rn<=100 ");
        return sqlbuf.toString();
    }

    //DCP_STOCKOUT_DETAIL
    protected String getQuerySql101_01(String eId, String organizationNo, String receiptno,String warehouseNo) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from MES_RESTOCKIN_DETAIL a " +
                              " where a.eid='"+eId+"'  " +
                              " and a.organizationno='"+organizationNo+"'  " +
                              " and a.receiptno='"+receiptno+"' " +
                              " and a.warehouseno='"+warehouseNo+"'  " +
                              " ");
        return sqlbuf.toString();
    }

}
