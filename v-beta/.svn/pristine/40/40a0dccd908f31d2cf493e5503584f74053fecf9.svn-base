package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.imp.json.DCP_ConversionTimeFormat;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
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

//********************要货申请单上传**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CustomerRequisitionCreate extends InitJob {
    public CustomerRequisitionCreate() {

    }
    Logger logger = LogManager.getLogger(CustomerRequisitionCreate.class.getName());
    String logFileName = "CustomerRequisitionCreate";
    static boolean bRun=false;//标记此服务是否正在执行中
    public String doExe() throws Exception {
        //返回信息
        String sReturnInfo="";
        //此服务是否正在执行中
        if (bRun) {
            HelpTools.writelog_fileName("大客户要货单customerRequisition.create正在执行中,本次调用取消",logFileName);
            logger.info("\r\n*********大客户要货单customerRequisition.create正在执行中,本次调用取消:************\r\n");
            sReturnInfo="定时传输任务-大客户要货单customerRequisition.create正在执行中！";
            return sReturnInfo;
        }
        
        bRun=true;//
        HelpTools.writelog_fileName("大客户要货单customerRequisition.create正定时调用Start",logFileName);
        logger.info("\r\n*********大客户要货单customerRequisition.create定时调用Start:************\r\n");
        boolean runTimeFlag = this.jobRunTimeFlag();
        if(!runTimeFlag)
        {
            sReturnInfo= "【同步任务大客户要货单customerRequisition.create】不在job运行时间内！";
            HelpTools.writelog_fileName("大客户要货单customerRequisition.create不在job运行时间内",logFileName);
            logger.info("\r\n*********customerRequisition.create定时调用不在job运行时间内************\r\n");
            return sReturnInfo;
        }
        try {
            //POrder抛转
            String sql = this.getQuerySql100_01();
            HelpTools.writelog_fileName("大客户要货单customerRequisition.create查询单头sql语句:"+sql,logFileName);
            logger.info("\r\n*********大客户要货单customerRequisition.create查询单头sql语句:************\r\n"+sql);
            List<Map<String, Object>> getQData100_01 = this.doQueryData(sql,null);
            String version = "3.0";
            if (getQData100_01 != null && getQData100_01.isEmpty() == false) {
                for (Map<String, Object> oneData100: getQData100_01) {
                    try
                    {
                        String shopNo = oneData100.get("SHOPNO").toString();
                        String eId = oneData100.get("EID").toString();
                        String pOrderNo = oneData100.get("PORDERNO").toString();
                        String bDate = oneData100.get("BDATE").toString();
                        String customerNo = oneData100.get("CUSTOMERNO").toString();
                        String telephone = oneData100.get("TELEPHONE").toString();
                        String address = oneData100.get("ADDRESS").toString();
                        String rDate = oneData100.get("RDATE").toString();
                        String salesMan = oneData100.get("SALESMANNO").toString();

                        String createOpId = oneData100.get("CREATEOPID").toString();
                        String createTime = oneData100.get("CREATETIME").toString();
                        String lastModiOpId = oneData100.get("LASTMODIOPID").toString();
                        String lastModiTime = oneData100.get("LASTMODITIME").toString();
                        String confirmOpId = oneData100.get("CONFIRMOPID").toString();
                        String confirmTime = oneData100.get("CONFIRMTIME").toString();

                        String tot_qty=oneData100.get("TOT_PQTY").toString();
                        String tot_amt = oneData100.get("TOT_AMT").toString();
                        String templateNo = oneData100.get("TEMPLATENO").toString();


                        // 获取单身数据
                        sql ="";
                        sql = this.getQuerySql101_01(shopNo, eId, pOrderNo);
                        String[] conditionValues101_01 = {  }; // 查詢條件
                        List<Map<String, Object>> getQData101_01 = this.doQueryData(sql, conditionValues101_01);

                        if (getQData101_01 != null	&& getQData101_01.isEmpty() == false) {
                            // t100req中的payload对象
                            JSONObject payload = new JSONObject();

                            // 自定义payload中的json结构
                            JSONObject std_data = new JSONObject();
                            JSONObject parameter = new JSONObject();

                            JSONArray request = new JSONArray();
                            JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
                            JSONArray request_detail = new JSONArray(); // 存所有单身

                            for (Map<String, Object> oneData101 : getQData101_01) {
                                // 获取单身数据并赋值
                                JSONObject body = new JSONObject(); // 存一笔单身
                                String item = oneData101.get("ITEM").toString();
                                String pluNo = oneData101.get("PLUNO").toString();
                                String featureNo = oneData101.get("FEATURENO").toString();
                                String pluName = "";
                                String unit = oneData101.get("UNIT").toString();
                                String qty = oneData101.get("QTY").toString();
                                String oPrice = oneData101.get("OPRICE").toString();
                                String price = oneData101.get("PRICE").toString();
                                String amt = oneData101.get("AMT").toString();

                                body.put("item", item);
                                body.put("pluNo", pluNo);
                                body.put("featureNo",featureNo);
                                body.put("pluName", pluName);
                                body.put("unit", unit);
                                body.put("qty", qty);
                                body.put("oPrice", oPrice);
                                body.put("price", price);
                                body.put("price", price);
                                body.put("amt", amt);

                                request_detail.put(body);
                            }

                            // 给单头赋值
                            header.put("version", version);
                            header.put("pOrderNo", pOrderNo);
                            header.put("shopNo", shopNo);
                            header.put("bDate", bDate);
                            header.put("customerNo", customerNo);
                            header.put("telephone", telephone);
                            header.put("address", address);
                            header.put("rDate", rDate);
                            header.put("salesMan", salesMan);
                            header.put("createOpId", createOpId);
                            header.put("createTime", createTime);
                            header.put("lastModiOpId", lastModiOpId);
                            header.put("lastModiTime", lastModiTime);
                            header.put("confirmOpId", confirmOpId);
                            header.put("confirmTime", confirmTime);
                            header.put("tot_qty", tot_qty);
                            header.put("tot_amt", tot_amt);
                            header.put("templateNo", templateNo);

                            header.put("request_detail", request_detail);

                            request.put(header);

                            parameter.put("request", request);

                            std_data.put("parameter", parameter);
                            payload.put("std_data", std_data);

                            String str = payload.toString();// 将json对象转换为字符串
                            String resbody = "";
                            // 执行请求操作，并拿到结果（同步阻塞）
                            try {
                                HelpTools.writelog_fileName("调用ERP接口customerRequisition.create请求req:"+str+",门店=" +shopNo+",企业ID=" +eId +",单号="  +pOrderNo,logFileName);
                                resbody=HttpSend.Send(str, "customerRequisition.create", eId, shopNo,shopNo,pOrderNo);
                                HelpTools.writelog_fileName("调用ERP接口customerRequisition.create返回res:"+resbody+",门店=" +shopNo+",企业ID=" +eId +",单号="  +pOrderNo,logFileName);
                                if(Check.Null(resbody) || resbody.isEmpty() ) {
                                    continue;
                                }
                                JSONObject jsonres = new JSONObject(resbody);
                                JSONObject std_data_res = jsonres.getJSONObject("std_data");
                                JSONObject execution_res = std_data_res.getJSONObject("execution");

                                // parameter 节点，记录ERP对应的单号和 ERP 对应的组织
                                String docNo = "";
                                String orgNo = "";
                               /* if(std_data_res.has("parameter")){
                                    JSONObject parameter_res = std_data_res.getJSONObject("parameter");
                                    if(parameter_res.has("doc_no") && parameter_res.has("org_no")){
                                        docNo = parameter_res.get("doc_no").toString();
                                        orgNo = parameter_res.get("org_no").toString();
                                    }
                                }*/

                                String code = execution_res.getString("code");
                                String description ="";
                                if  (!execution_res.isNull("description") ) {
                                    description = execution_res.getString("description");
                                }
                                if (code.equals("0")) {
                                    // values
                                    Map<String, DataValue> values = new HashMap<String, DataValue>() ;
                                    DataValue v= new DataValue("Y", Types.VARCHAR);
                                    values.put("process_status", v);
                                    DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
                                    values.put("UPDATE_TIME", v1);

                                    // condition
                                    Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
                                    DataValue c1 = new DataValue(shopNo, Types.VARCHAR);
                                    conditions.put("SHOPNO", c1);
                                    DataValue c2 = new DataValue(eId, Types.VARCHAR);
                                    conditions.put("EID", c2);
                                    DataValue c3 = new DataValue(pOrderNo, Types.VARCHAR);
                                    conditions.put("PORDERNO", c3);

                                    this.doUpdate("DCP_CUSTOMERPORDER", values, conditions);
                                    InsertWSLOG.delete_WSLOG(eId, shopNo,"1", pOrderNo);
                                    //

                                    sReturnInfo="0";
                                } else {
                                    sReturnInfo="ERP返回错误信息:" + code + "," + description;
                                    //写数据库
                                    InsertWSLOG.insert_WSLOG("customerRequisition.create",pOrderNo,eId,shopNo,"1",str,resbody,code,description) ;
                                }
                            } catch (Exception e) {
                                //
                                InsertWSLOG.insert_WSLOG("customerRequisition.create",pOrderNo,eId,shopNo,"1",str,resbody,"-1",e.getMessage());
                                sReturnInfo="错误信息:" + e.getMessage();
                                HelpTools.writelog_fileName("调用ERP接口customerRequisition.create报错信息:"+e.getMessage()+",门店=" +shopNo+",企业ID=" +eId +",单号="  +pOrderNo,logFileName);
                                logger.error("\r\n******大客户要货单customerRequisition.create：门店=" +shopNo+",组织编码=" + shopNo + ",公司编码=" +eId +",单号="  +pOrderNo + "\r\n报错信息："+e.getMessage()+"******\r\n");
                            }
                        } else {
                            sReturnInfo="错误信息:无单身数据！";
                            HelpTools.writelog_fileName("调大客户要货单customerRequisition.create查询无单身数据,门店=" +shopNo+",企业ID=" +eId +",单号="  +pOrderNo,logFileName);
                            logger.error("\r\n******大客户要货单customerRequisition.create：门店=" +shopNo+",公司编码=" +eId +",单号="  +pOrderNo + "无单身数据！******\r\n");
                        }

                    }
                    catch (Exception e)
                    {

                    }
                }
            } else {
                sReturnInfo="错误信息:无单头数据！";
                HelpTools.writelog_fileName("大客户要货单customerRequisition.create没有要上传的单头数据",logFileName);
                logger.info("\r\n******大客户要货单customerRequisition.create没有要上传的单头数据******\r\n");
            }
            
        } catch (Exception e) {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                logger.error("\r\n******大客户要货单customerRequisition.create报错信息" + e.getMessage()+"\r\n" + errors + "******\r\n");
                HelpTools.writelog_fileName("大客户要货单customerRequisition.create报错信息:"+ e.getMessage()+"\r\n" + errors,logFileName);
                pw=null;
                errors=null;
            } catch (IOException e1) {
                HelpTools.writelog_fileName("大客户要货单customerRequisition.create,IOException报错信息:"+e1.getMessage(),logFileName);
                logger.error("\r\n******大客户要货单customerRequisition.create,IOException报错信息" + e1.getMessage() + "******\r\n");
            }
            
            sReturnInfo="错误信息:" + e.getMessage();
            
        } finally {
            bRun=false;//
            HelpTools.writelog_fileName("大客户要货单customerRequisition.create定时调用End",logFileName);
            logger.info("\r\n*********大客户要货单customerRequisition.create定时调用End:************\r\n");
        }
        return sReturnInfo;
    }
    
    
    //DCP_CUSTOMERPORDER
    protected String getQuerySql100_01() throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("");
        sqlbuf.append(" select * from DCP_CUSTOMERPORDER A ");
        sqlbuf.append(" WHERE A.status = '1'  AND A.process_status = 'N'");
        return sqlbuf.toString();
    }

    //DCP_CUSTOMERPORDER_DETAIL
    protected String getQuerySql101_01(String shopId, String eId, String porderNO100) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" select * from DCP_CUSTOMERPORDER_DETAIL A");
        sqlbuf.append(" WHERE A.SHOPNO = '"+shopId+"'  AND A.eId = '"+eId+"' AND a.PORDERNO = '"+porderNO100 +"'");
        sqlbuf.append("");
        return sqlbuf.toString();
    }

    /**
     * job运行时间，（如果没有设置，默认早上6点到晚上23点）
     * @return
     * @throws Exception
     */
    private boolean jobRunTimeFlag() throws Exception
    {
        boolean flag = true;
        String sdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String stime = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        // 先查 job 执行时间，然后再执行后续操作
        String getTimeSql = "select * from job_quartz_detail where job_name = 'MTSGAutoUpdateStock'  and cnfflg = 'Y' ";
        List<Map<String, Object>> getTimeDatas = this.doQueryData(getTimeSql, null);
        if (getTimeDatas != null && !getTimeDatas.isEmpty())
        {
            boolean isTime = false;
            for (Map<String, Object> map : getTimeDatas)
            {
                String beginTime = map.get("BEGIN_TIME").toString();
                String endTime = map.get("END_TIME").toString();

                // 如果当前时间在 执行时间范围内， 就执行
                if (stime.compareTo(beginTime) >= 0 && stime.compareTo(endTime) < 0)
                {
                    isTime = true;
                    break;
                }
            }
            if (!isTime)
            {
                return false;
            }

        }
        else// 如果没设置执行时间， 默认6点到晚上22点执行
        {
            return true;
        }
        return flag;
    }
    
    /**
     * 120000 转换为 12:00:00 为避免93版本追版错误，这个方法单独在这里写， ConversionTimeFormat 类中也有。
     * @param time 120000
     * @return  12:00:00
     */
    private String converToTime(String time) {
        String hour = time.substring(0,2);
        String mi = time.substring(2,4);
        String sec = time.substring(4,6);
        String f_time = hour + ":" + mi +":" + sec ;
        return  f_time;
    }
    
}
