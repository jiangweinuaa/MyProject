package com.dsc.spos.scheduler.job;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.json.cust.req.DCP_ProcessPlanCreateTaskReq;
import com.dsc.spos.json.utils.ParseJson;

import com.dsc.spos.service.utils.DispatchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *每天早上4点，根据生产计划单产生当天的单日加工任务
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DCP_PROCESSPLAN_AutoCreateTask extends InitJob
{


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(DCP_PROCESSPLAN_AutoCreateTask.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public  DCP_PROCESSPLAN_AutoCreateTask()
    {

    }

    public String doExe()
    {
        //返回信息
        String sReturnInfo = "";
        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.debug("\r *********生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask正在执行中,本次调用取消:************\r ");

            sReturnInfo = "定时传输任务-生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//			

        logger.debug("\r *********生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask定时调用Start:************\r ");

        try
        {

            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            //先查 job 执行时间，然后再执行后续操作
            String getTimeSql = "select * from job_quartz_detail where job_name = 'DCP_PROCESSPLAN_AutoCreateTask'  and STATUS = '100' ";
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


            //通过配置文件读取
            String langtype="zh_CN";
            List<SPosConfig.ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
            if(lstProd!=null&&!lstProd.isEmpty())
            {
                langtype=lstProd.get(0).getHostLang().getValue();
            }
            ParseJson pj = new ParseJson();

            String sysDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

            //当天产生，每天1次，预估500家店*每家店10张计划单=5000笔资料，分多次执行吧
            boolean b_Exists=true;
            int totalRecords;//总笔数
            int totalPages;

            String sql_count="select count(*) NUM from DCP_PROCESSPLAN a  " +
                    "where a.status=1  " +
                    "and a.autocreatetask=1  " +
                    "and a.pdate_end>=to_date('"+sysDate+"','yyyyMMdd') ";
            List<Map<String, Object>> getQData_Count = this.doQueryData(sql_count, null);
            if (getQData_Count != null && getQData_Count.isEmpty() == false)
            {
                totalRecords= Convert.toInt(getQData_Count.get(0).get("NUM"),0);
                //有记录，继续
                if (totalRecords>0)
                {
                    //算總頁數
                    int pageNumber = 1;
                    int pageSize = 30;

                    totalPages = totalRecords / pageSize;
                    totalPages = (totalRecords % pageSize > 0) ? totalPages + 1 : totalPages;

                    while (b_Exists)
                    {
                        //計算起啟位置
                        int startRow=(pageNumber-1) * pageSize;

                        String sql = "select * from ( " +
                                "select row_number() over(order by a.create_time) rn, a.*,to_char(a.pdate_begin,'YYYYMMDD') as A_PDATE_BEGIN,to_char(a.Pdate_End,'YYYYMMDD') as A_PDATE_END from DCP_PROCESSPLAN a  " +
                                "where a.status=1  " +
                                "and a.autocreatetask=1  " +
                                "and a.pdate_end>=to_date('"+sysDate+"','yyyyMMdd') " +
                                ") where rn>"+startRow+" and rn<="+(startRow+pageSize);
                        List<Map<String, Object>> getQData100_01 = this.doQueryData(sql, null);
                        if (getQData100_01 != null && getQData100_01.isEmpty() == false)
                        {
                            for (Map<String, Object> oneData100: getQData100_01)
                            {
                                String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
                                String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
                                String processplanno = oneData100.get("PROCESSPLANNO") == null ? "" : oneData100.get("PROCESSPLANNO").toString();
                                String pdate_begin = oneData100.get("A_PDATE_BEGIN") == null ? "" : oneData100.get("A_PDATE_BEGIN").toString();
                                String pdate_end = oneData100.get("A_PDATE_END") == null ? "" : oneData100.get("A_PDATE_END").toString();

                                //调产生任务的服务
                                String json = "";

                                DCP_ProcessPlanCreateTaskReq dcp_processPlanCreateTaskReq=new DCP_ProcessPlanCreateTaskReq();
                                DCP_ProcessPlanCreateTaskReq.levelRequest request=dcp_processPlanCreateTaskReq.new levelRequest();
                                request.seteType("0");//不检查token
                                request.seteId(eId);
                                request.setOrganizationNo(organizationNO);
                                request.setProcessPlanNo(processplanno);
                                request.setpDateBegin(pdate_begin);
                                request.setpDateEnd(pdate_end);

                                Map<String, Object> jsonMap = new HashMap<String, Object>();
                                jsonMap.put("serviceId", "DCP_ProcessPlanCreateTask");
                                jsonMap.put("langType", langtype);
                                jsonMap.put("opNO", "admin");
                                jsonMap.put("opName", "admin");

                                //这个token是无意义的
                                jsonMap.put("token", "abecbc7b42eb286a0d1f8587a9df97e5");
                                jsonMap.put("request", request);
                                //json
                                json = pj.beanToJson(jsonMap);
                                DispatchService ds = DispatchService.getInstance();
                                String resXML = ds.callService(json, StaticInfo.dao);
                                JSONObject json_res = new JSONObject(resXML);
                                String v_success=json_res.optString("success");
                                if (!v_success.toLowerCase().equals("true"))
                                {
                                    logger.debug("\r ******生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask报错信息,eid="+eId+",organizationNO="+organizationNO+",processplanno="+processplanno+",报错返回"+resXML+"******\r ");
                                }
                            }
                        }
                        else
                        {
                            //没记录了
                            b_Exists=false;

                            sReturnInfo="错误信息:无单头数据！";
                            logger.debug("\r ******生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask,没有要产生任务的单头数据******\r ");
                        }

                        //页码+1
                        pageNumber+=1;
                    }
                }
                else
                {
                    logger.debug("\r *********生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask,没记录SQL="+sql_count+"************\r ");
                }
            }
            else
            {
                logger.debug("\r *********生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask,没记录SQL="+sql_count+"************\r ");
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

                logger.error("\r ******生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask报错信息" + e.getMessage()+"\r " + errors.toString() + "******\r ");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r ******生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask报错信息" + e.getMessage() + "******\r ");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.debug("\r *********生产计划单产生当天的单日加工任务DCP_PROCESSPLAN_AutoCreateTask定时调用End:************\r ");
        }
        return sReturnInfo;

    }

}
