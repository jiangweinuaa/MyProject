package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.waimai.HelpTools;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.service.webhook.WebHookService;


/**
 * @author Administrator
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WebHookGoods extends InitJob
{
    //	Logger logger = LogManager.getLogger(WebHook.class.getName());
    //默认一天执行一次
    static boolean bRun=false;//标记此服务是否正在执行中

    public WebHookGoods(){

//		//测试用 begin
//		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:dsmServiceModule.xml");
//        MySpringContext mySpringContext = new MySpringContext();
//        mySpringContext.setApplicationContext(classPathXmlApplicationContext);
//        DsmDAO dsmDao=(DsmDAO) mySpringContext.getContext().getBean("sposDao");
//        StaticInfo.yichengVIPDao=(DsmDAO) mySpringContext.getContext().getBean("yichengVipPosDao");
//        StaticInfo.dao=dsmDao;
//        //测试用 end
    }

    public String doExe() throws Exception
    {

        String jobName=WebHookGoods.class.getSimpleName();
        if (bRun){
            HelpTools.writelog_fileName("***************WebHook 正在执行中,本次调用取消****************",jobName);
            return null;
        }

        bRun=true;//
        HelpTools.writelog_fileName("***************WebHookGoods START****************",jobName);
        Calendar cal = Calendar.getInstance();// 获得当前时间
//		Date dataNow=cal.getTime();
        //时间
        SimpleDateFormat dfTime=new SimpleDateFormat("HHmmss");

        String dateNowStr=dfTime.format(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 4);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date beginTime=cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 6);
        Date endTime=cal.getTime();
        try{
            Date dataNow=dfTime.parse(dateNowStr);
            String getShopDatasSql = " select * from JOB_QUARTZ_DETAIL WHERE JOB_NAME ='"+jobName+"' AND CNFFLG='Y'";
            //不设置时间，默认4点-6点执行
            List<Map<String, Object>> getShopDatas = this.doQueryData(getShopDatasSql, null);
            if(getShopDatas!=null&&getShopDatas.size()>0){
                boolean isTime = false;
                for (Map<String, Object> map : getShopDatas) {
                    String BEGIN_TIME = map.get("BEGIN_TIME").toString();
                    String END_TIME = map.get("END_TIME").toString();
                    //String BEGIN_TIME=getShopDatas.get(0).get("BEGIN_TIME")==null?"":getShopDatas.get(0).get("BEGIN_TIME").toString();
                    //String END_TIME=getShopDatas.get(0).get("END_TIME")==null?"":getShopDatas.get(0).get("END_TIME").toString();
                    beginTime= dfTime.parse(BEGIN_TIME);
                    endTime= dfTime.parse(END_TIME);
                    // 如果当前时间在 执行时间范围内，  就执行自动收货
                    if(dataNow.compareTo(beginTime)>=0 && dataNow.compareTo(endTime)<0) {
                        isTime = true;
                        break;
                    }
                }
                if(!isTime) {
                    HelpTools.writelog_fileName("***************WebHookGoods 当前时间["+dfTime.format(dataNow)+"];"
                            + "开始时间点["+dfTime.format(beginTime)+"]"
                            + "结束时间点["+dfTime.format(endTime)+"],本次不执行传输****************",jobName);
                    return "";
                }

            }
            SimpleDateFormat dfTime2=new SimpleDateFormat("yyyyMMddHHmmss");
            if(dataNow.compareTo(beginTime)>=0&&endTime.compareTo(dataNow)>=0){
                String sql = " select DISTINCT A.EID from NRC_WEBHOOK A INNER JOIN NRC_WEBHOOK_URL B ON A.EID=B.EID AND A.EVENTID=B.EVENTID AND A.STATUS=B.STATUS "
                           + " INNER JOIN DCP_ECOMMERCE C ON C.EID=B.EID AND C.LOADDOCTYPE=B.FORMAT AND C.STATUS=B.STATUS "
                           + " WHERE A.STATUS=100 AND A.EVENTID='GOODS' ";
                HelpTools.writelog_fileName("查询是否需要同步商品sql:"+sql,jobName);
                List<Map<String, Object>> oneLv1 = this.doQueryData(sql, null);
                if(oneLv1!=null&&oneLv1.size()>0){
                    for (Map<String, Object> map : oneLv1) {
                        Object eidObj = map.get("EID");
                        if(eidObj!=null&&eidObj.toString().length()>0){
                            HelpTools.writelog_fileName("***************WebHookService.AllGoods开始执行****************",jobName);
                            WebHookService.AllGoods(eidObj.toString());
                            HelpTools.writelog_fileName("***************WebHookService.AllGoods执行结束****************",jobName);
                        }
                    }
                }

            }else{
                HelpTools.writelog_fileName("***************WebHookGoods 当前时间["+dfTime.format(dataNow)+"];"
                        + "开始时间点["+dfTime.format(beginTime)+"]"
                        + "结束时间点["+dfTime.format(endTime)+"],本次不执行传输****************",jobName);
            }
        }catch(Exception e){
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);
                pw.flush();
                pw.close();
                errors.flush();
                errors.close();

                HelpTools.writelog_fileName("******报错信息" + e.getMessage() +"\r\n" + errors.getBuffer().toString()+ "******",jobName);

                pw=null;
                errors=null;
            }
            catch (IOException e1) {
                HelpTools.writelog_fileName("******报错信息" + e.getMessage() + "******",jobName);
            }
        }finally {
            bRun = false;
        }

        HelpTools.writelog_fileName("***************WebHookGoods END****************",jobName);
        return null;
    }

}
