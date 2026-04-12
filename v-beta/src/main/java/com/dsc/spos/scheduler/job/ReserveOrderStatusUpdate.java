package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.waimai.HelpTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

//********************预约单状态变更**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ReserveOrderStatusUpdate extends InitJob{

    Logger logger = LogManager.getLogger(ReserveOrderStatusUpdate.class.getName());

    static boolean bRun = false;//标记此服务是否正在执行中
    String orderLogFileName = "ReserveOrderStatusUpdate";
    String jobDesc = "【预约单定时自动过期任务 ReserveOrderStatusUpdate】";

    public String doExe() throws IOException {
        //此服务是否正在执行中
        //返回信息
        /**
         *  针对订单状态status==0或1 的预约单
         *  剩余服务时长如果 不足当前项目服务时长 则订单状态变更为 status == 4 已取消
         */
        String sReturnInfo = "";
        if (bRun) {
            logger.debug("\r\n*********" + jobDesc + " 正在执行中,本次调用取消:************\r\n");

            sReturnInfo = jobDesc + " 正在执行中！";
            return sReturnInfo;
        }
        bRun = true;

        try {

            logger.info("\r\n********* " + jobDesc + " 定时调用Start:************\r\n");
            HelpTools.writelog_fileName(jobDesc + "定时调用Start", orderLogFileName);

            SimpleDateFormat dfm = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dfr = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            StringBuffer sqlbuf = new StringBuffer("");
            // 先查询所有 status 等于0，1 的预约单
            sqlbuf.append("SELECT a.*,b.SERVICETIME FROM DCP_RESERVE a " +
                    " LEFT JOIN DCP_SERVICEITEMS b ON a.eid = b.eid AND a.ITEMSNO = b.ITEMSNO " +
                    " WHERE a.STATUS  in(0,1)");
            List<Map<String, Object>> getDatas = StaticInfo.dao.executeQuerySQL(sqlbuf.toString(), null);
            if(!CollectionUtils.isEmpty(getDatas)){
                for (Map<String, Object> getData : getDatas) {
                    boolean isOverdue = false; // 标记是否过期

                    String reserveno = getData.get("RESERVENO").toString();
                    String eid = getData.get("EID").toString();
                    String shopid = getData.get("SHOPID").toString();
                    String date = getData.get("BDATE").toString();
                    String time = getData.get("TIME").toString();
                    String serviceTime = getData.get("SERVICETIME").toString();
                    String status = getData.get("STATUS").toString();

                    Calendar cal = Calendar.getInstance();//获得当前时间
                    Date currentDate = dfr.parse(dfr.format(cal.getTime()));
                    Date currentDateTime = cal.getTime();

                    Date reserveDate =dfr.parse(date);
                    // 先判断预约单里的日期有无超过当前日期
                    if(currentDate.compareTo(reserveDate)>0){
                        // 当前日期超过预约日期
                        isOverdue = true;
                    }else if(currentDate.compareTo(reserveDate)==0){
                        // 当前日期等于预约日期 则判断当前时间段  有无超过预约结束时间段 + 服务时长
                        time=time.substring(time.indexOf("-")+1,time.length());
                        String reserveEndDateTime = dfr.format(reserveDate) + " " + time;
                        Date reserveEndTime = sd.parse(reserveEndDateTime);
                        cal.setTime(reserveEndTime);
                        cal.add(Calendar.MINUTE, Integer.parseInt(serviceTime));
                        Date reserveEndDate = cal.getTime();

                        if(reserveEndDate.compareTo(currentDateTime)<0){
                            isOverdue = true;
                        }
                    }

                    // 如果预约单已过期 则修改预约单状态
                    if(isOverdue){
                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                        UptBean ub = new UptBean("DCP_RESERVE");
                        ub.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));
                        ub.addUpdateValue("LASTMODITIME", new DataValue(sdf.format(new Date()), Types.DATE));
                        //condition
                        ub.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                        ub.addCondition("SHOPID", new DataValue(shopid, Types.VARCHAR));
                        ub.addCondition("RESERVENO", new DataValue(reserveno, Types.VARCHAR));
                        lstData.add(new DataProcessBean(ub));
                        // 执行
                        StaticInfo.dao.useTransactionProcessData(lstData);
                        HelpTools.writelog_fileName(jobDesc +"预约单号为"+reserveno+"的预约单已过期，状态变更前为"+status+",已变更为4", orderLogFileName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("\r\n******"+jobDesc+" 报错信息:" + e.getMessage() + "\r\n******\r\n");
            HelpTools.writelog_fileName(jobDesc +"同步正在执行中,异常:" + e.getMessage(), orderLogFileName);
            sReturnInfo = "错误信息:" + e.getMessage();
        }finally {
            bRun = false;//
            logger.info("\r\n*********"+jobDesc+"定时调用End:************\r\n");
            HelpTools.writelog_fileName(jobDesc+" 定时调用End", orderLogFileName);
        }
        return sReturnInfo;
    }
}
