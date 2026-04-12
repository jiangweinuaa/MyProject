package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataValue;
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
 *MES组合拆解单上传
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_ComposeDisToErp extends InitJob
{


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(MES_ComposeDisToErp.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public MES_ComposeDisToErp()
    {

    }

    public MES_ComposeDisToErp(String eId, String shopId, String organizationNO, String billNo)
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
            logger.trace("\r\n*********MES组合拆解单上传MES_ComposeDisToErp正在执行中,本次调用取消:************\r\n");

            sReturnInfo = "定时传输任务-MES组合拆解单上传MES_ComposeDisToErp正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//			

        logger.trace("\r\n*********MES组合拆解单上传MES_ComposeDisToErp定时调用Start:************\r\n");

        try
        {

            String sql = this.getQuerySql100_01();
            logger.trace("\r\n******MES组合拆解单上传MES_ComposeDisToErp单头sql:" + sql);
            List<Map<String, Object>> getQData100_01 = this.doQueryData(sql, null);
            if (getQData100_01 != null && getQData100_01.isEmpty() == false)
            {

                for (Map<String, Object> oneData100: getQData100_01)
                {
                    String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                    String organizationNo = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
                    String docNo1 = oneData100.get("DOCNO") == null ? "" : oneData100.get("DOCNO").toString();
                    String erpDocNo = oneData100.get("ERPDOCNO") == null ? "" : oneData100.get("ERPDOCNO").toString();
                    String type = oneData100.get("TYPE") == null ? "" : oneData100.get("TYPE").toString();
                    String bDate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
                    String featureNo = oneData100.get("FEATURENO") == null ? "" : oneData100.get("FEATURENO").toString();
                    String pluNo = oneData100.get("PLUNO") == null ? "" : oneData100.get("PLUNO").toString();
                    String pUnit = oneData100.get("PUNIT") == null ? "" : oneData100.get("PUNIT").toString();
                    String pQty = oneData100.get("PQTY") == null ? "" : oneData100.get("PQTY").toString();
                    String baseUnit = oneData100.get("BASEUNIT") == null ? "" : oneData100.get("BASEUNIT").toString();
                    String baseQty = oneData100.get("BASEQTY") == null ? "" : oneData100.get("BASEQTY").toString();
                    String batchNo = oneData100.get("BATCHNO") == null ? "" : oneData100.get("BATCHNO").toString();
                    String warehouseNo = oneData100.get("WAREHOUSENO") == null ? "" : oneData100.get("WAREHOUSENO").toString();
                    String detailItem = oneData100.get("ITEM").toString();

                    String createBy = oneData100.get("CREATEBY").toString();
                    String createDate = oneData100.get("CREATE_DATE").toString();
                    String createTime = oneData100.get("CREATE_TIME").toString();
                    String createDateTime="";
                    if(createDate.length()==8){
                        createDateTime+=createDate.substring(0,4)+"-"+createDate.substring(4,6)+"-"+createDate.substring(6,8);
                    }
                    if(createTime.length()==6){
                        createDateTime+=" ";
                        createDateTime+=createTime.substring(0,2)+":"+createTime.substring(2,4)+":"+createTime.substring(4,6);
                    }


                    // 获取单身数据
                    sql = this.getQuerySql101_01(eId, organizationNo, docNo1,detailItem);
                    logger.trace("\r\n******MES组合拆解单上传MES_ComposeDisToErp单身sql:" + sql);
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
                            String erpItem = oneData101.get("ERPITEM") == null ? "" : oneData101.get("ERPITEM").toString();
                            String subPluNo = oneData101.get("SUBPLUNO") == null ? "" : oneData101.get("SUBPLUNO").toString();
                            String unit = oneData101.get("UNIT") == null ? "" : oneData101.get("UNIT").toString();
                            String qty = oneData101.get("QTY") == null ? "" : oneData101.get("QTY").toString();
                            String featureNo1 = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
                            String baseQty1 = oneData101.get("BASEQTY") == null ? "" : oneData101.get("BASEQTY").toString();
                            String baseUnit1 = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
                            String warehouseNo1 = oneData101.get("WAREHOUSENO") == null ? "0" : oneData101.get("WAREHOUSENO").toString();
                            String batchNo1 = oneData101.get("BATCHNO") == null ? "" : oneData101.get("BATCHNO").toString();

                            body.put("item", item);
                            body.put("erpItem", erpItem);
                            body.put("subPluNo",subPluNo);
                            body.put("unit", unit);
                            body.put("qty", qty);
                            body.put("featureNo", featureNo1);
                            body.put("baseQty", baseQty1);
                            body.put("baseUnit", baseUnit1);
                            body.put("warehouseNo", warehouseNo1);
                            body.put("batchNo", batchNo1);
                            //
                            request_detail.put(body);
                        }

                        // 给单头赋值
                        header.put("version", "3.0");
                        header.put("eId", eId);
                        header.put("organizationNo", organizationNo);
                        header.put("docNo", docNo1);
                        header.put("erpDocNo", erpDocNo);
                        header.put("type", type);
                        header.put("bDate", bDate);
                        header.put("pluNo", pluNo);
                        header.put("featureNo", featureNo);
                        header.put("pUnit", pUnit);
                        header.put("pQty", pQty);
                        header.put("baseUnit", baseUnit);
                        header.put("baseQty", baseQty);
                        header.put("batchNo", batchNo);
                        header.put("warehouseNo", warehouseNo);
                        header.put("createBy", createBy);
                        header.put("createTime", createDateTime);
                        header.put("mesComposeDis_detail", request_detail);
                        request.put(header);

                        parameter.put("mesComposeDis", request);

                        std_data.put("parameter", parameter);
                        payload.put("std_data", std_data);

                        String str = payload.toString();// 将json对象转换为字符串
                        String resbody = "";

                        // 执行请求操作，并拿到结果（同步阻塞）
                        try
                        {
                            logger.trace("\r\n******MES合并拆分上传mesComposeDis.create的数据:" + str);
                            resbody= HttpSend.Send(str, "mesComposeDis.create", eId, organizationNo, organizationNo, docNo1);
                            logger.trace("\r\n******MES合并拆分上传mesComposeDis.create erp返回的内容" + resbody);
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
                                DataValue c3 = new DataValue(docNo1, Types.VARCHAR);
                                conditions.put("PSTOCKINNO", c3);

                                this.doUpdate("DCP_PSTOCKIN", values, conditions);
                                InsertWSLOG.delete_WSLOG(eId, organizationNo,"1", docNo);
                                //

                                sReturnInfo="0";
                            }
                            else
                            {
                                sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                //写数据库
                                InsertWSLOG.insert_WSLOG("mesComposeDis.create",docNo1,eId,organizationNo,"1",str,resbody,code,description) ;
                            }
                        }
                        catch (Exception e)
                        {
                            //
                            InsertWSLOG.insert_WSLOG("mesComposeDis.create",docNo1,eId,organizationNo,"1",str,resbody,"-1",e.getMessage());
                            sReturnInfo="错误信息:" + e.getMessage();
                            logger.error("\r\n******MES组合拆解单上传mesComposeDis.create：组织编码=" + organizationNo + ",公司编码=" +eId +",组合拆解单号="  +docNo1 + "\r\n报错信息："+e.getMessage()+"******\r\n");
                        }

                    }
                    else
                    {
                        logger.trace("\r\n******MES组合拆解单上传MES_ComposeDisToErp,docNo="+docNo1+"没有要上传的明细数据******\r\n");
                    }
                        
                }
            }
            else
            {
                sReturnInfo="错误信息:无单头数据！";
                logger.trace("\r\n******MES组合拆解单上传MES_ComposeDisToErp,没有要上传的单头数据******\r\n");
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

                logger.error("\r\n******MES组合拆解单上传MES_ComposeDisToErp报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******MES组合拆解单上传MES_ComposeDisToErp报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.trace("\r\n*********MES组合拆解单上传MES_ComposeDisToErp定时调用End:************\r\n");
        }
        return sReturnInfo;
    }



    //MES_COMPOSEDIS
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        //sqlbuf.append("select * from ( "
        //                      + "select row_number() over(order by a.createtime) rn, a.*  " +
        //                      "from MES_COMPOSEDIS  a " +
        //                      "where a.status=1 and (PROCESS_STATUS is null or PROCESS_STATUS = 'N') " +
        //                      " "
        //);

        sqlbuf.append("select * from( " +
                " select row_number() over(order by a.createtime) rn,a.eid,a.organizationno,a.pstockinno docno,c.ofno as erpdocno," +
                " case when a.doc_type='1' then '0' else '1' end as type,a.account_date as bdate," +
                " b.pluno,b.featureno,b.punit,b.pqty,b.baseunit,b.baseqty,b.warehouse as warehouseno,b.batch_no as batchno,b.item,a.createby,a.CREATE_DATE,a.create_time " +
                " from dcp_pstockin a " +
                " left join dcp_pstockin_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.PSTOCKINNO=b.PSTOCKINNO" +
                " left join MES_COMPOSEDIS c on a.eid=c.eid and a.organizationno=c.organizationno and a.ofno=c.docno " +
                " where a.status='2' and a.load_doctype='MES' and a.process_status='N'  and a.doc_type in ('1','2')");


        sqlbuf.append(" ) " +
                              "where rn>0 and rn<=100 ");
        return sqlbuf.toString();
    }

    //MES_COMPOSEDIS_DETAIL
    protected String getQuerySql101_01(String eId, String organizationNo, String docNo,String item) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        //sqlbuf.append("select a.* from MES_COMPOSEDIS_DETAIL a  " +
         //                     "where a.eid='"+eId+"'  " +
         //                     "and a.organizationno='"+organizationNo+"'  " +
           //                   "and a.docno='"+docNo+"' " +
           //                   "order by a.item  ");

        sqlbuf.append(" select c.item,c.oitem as erpitem,c.pluno as subpluno,c.punit as unit,c.pqty as qty,c.featureno,c.baseunit,c.baseqty," +
                " c.warehouse as warehouseno,c.batch_no as batchno" +
                " from dcp_pstockin a" +
                " left join dcp_pstockin_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.pstockinno=b.pstockinno " +
                " left join dcp_pstockin_material c on a.eid=c.eid and b.organizationno=c.organizationno and b.item=c.mitem and a.pstockinno=c.pstockinno " +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNo+"' " +
                " and a.pstockinno='"+docNo+"' and b.item='"+item+"' " +
                " order by c.item ");

        return sqlbuf.toString();
    }




}
