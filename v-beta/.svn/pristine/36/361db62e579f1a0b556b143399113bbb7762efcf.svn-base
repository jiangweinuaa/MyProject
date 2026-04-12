package com.dsc.spos.scheduler.job;

import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时分拣任务
 *
 * 定时调用MES服务: MES_SortingTaskAdd
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MES_SortingTaskAdd_Job extends InitJob
{

    Logger logger = LogManager.getLogger(MES_SortingTaskAdd_Job.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public MES_SortingTaskAdd_Job()
    {


    }

    public String doExe()
    {

        //返回信息
        String sReturnInfo="";
        //此服务是否正在执行中
        if (bRun)
        {
            logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-定时分拣任务MES_SortingTaskAdd_Job正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job定时调用Start:************\r\n");


        try
        {
            String sql="select distinct b.transferout,b.organizationNo,b.eid  from MES_SORTINGDATA  a " +
                    " left join MES_SORTDATADETAIL b on a.docno = b.docno and a.eid = b.eid and a.organizationno = b.organizationno " +
                    " where a.status = '0' " +
                    " order by b.eid,b.organizationno,b.transferout ";
            List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
            if (sqllist != null && sqllist.size()>0)
            {
                //随便取1个接口账号信息，接口账号没啥用,有就行
                String sql_apiuser="select * from  CRM_APIUSER  where STATUS = '100' and rownum=1 ";
                List<Map<String, Object>> sqllist_apiuser=this.doQueryData(sql_apiuser, null);
                if (sqllist_apiuser != null && sqllist_apiuser.size()>0)
                {
                    String apiUserCode=sqllist_apiuser.get(0).get("USERCODE").toString();
                    String apiUserKey=sqllist_apiuser.get(0).get("USERKEY").toString();

                    for (Map<String, Object> map_list : sqllist)
                    {
                        String eid=map_list.get("EID").toString();
                        String organizationno=map_list.get("ORGANIZATIONNO").toString();
                        String transferout=map_list.get("TRANSFEROUT").toString();

                        //调MES服务
                        //登录用户鉴权:authorization
                        //规则是：用户名+冒号+密码，utf-8后转base64,最后再用Basic加空格品在前面即可
                        String clientId="mes";
                        String clientPwd="9d3b19e7e12e4faf169d55da6424fcfe";
                        String auth = clientId + ":" + clientPwd;
                        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("utf-8")));
                        String authHeader = "Basic " + new String(encodedAuth);

                        String mesUrl = PosPub.getMES_INNER_URL(eid);

                        //2个类型，各调一次
                        for(int i = 0; i < 2; i++)
                        {
                            //
                            String requestId=PosPub.getGUID(false);

                            Map<String, Object> map = new HashMap<>();
                            map.put("serviceId", "MES_SortingTaskAdd");
                            map.put("requestId", requestId);
                            map.put("tiimstamp", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                            map.put("version", "1.0");
                            map.put("apiUserCode", apiUserCode);
                            map.put("langType", "zh_CN");
                            map.put("plantType", "MES");
                            map.put("authorization", authHeader);

                            //Body的内容
                            JSONObject payload = new JSONObject();
                            JSONObject request = new JSONObject();
                            request.put("eId",eid);
                            request.put("organizationNo",organizationno);
                            request.put("warehouseNo",transferout);
                            request.put("sortType",i+"");//分拣类型0门店大客  1散客
                            payload.put("request",request);

                            map.put("sign", PosPub.encodeMD5(payload.toString() + apiUserKey));

                            logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job requestId="+requestId+" 请求开始 "+payload.toString()+"************\r\n");

                            String resbody=HttpSend.doPost(mesUrl, payload.toString(), map,requestId);

                            if ((resbody.startsWith("{") && resbody.endsWith("}")) || (resbody.startsWith("[") && resbody.endsWith("]")))
                            {
                                JSONObject jsonres = new JSONObject(resbody);
                                if (jsonres.has("success") && jsonres.get("success").toString().toLowerCase().equals("true"))
                                {
                                    logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job requestId="+requestId+"服务执行成功 "+"************\r\n");
                                }
                                else
                                {
                                    logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job requestId="+requestId+"服务执行失败 "+"************\r\n");
                                }
                            }
                            else
                            {
                                logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job requestId="+requestId+"服务执行失败 "+"************\r\n");
                            }

                            logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job requestId="+requestId+" 请求结束 "+resbody+"************\r\n");

                            map.clear();
                            map=null;
                        }
                    }
                }
                else
                {
                    logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job 接口账号表CRM_APIUSER无记录:SQL="+sql_apiuser+"************\r\n");
                }
            }
            else
            {
                logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job SQL查询无记录:SQL="+sql+"************\r\n");
            }
            sqllist=null;
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

                logger.debug("\r\n******定时分拣任务MES_SortingTaskAdd_Job报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.debug("\r\n******定时分拣任务MES_SortingTaskAdd_Job报错信息" + e1.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.debug("\r\n*********定时分拣任务MES_SortingTaskAdd_Job定时调用End:************\r\n");
        }

        return sReturnInfo;
    }


}
