package com.dsc.spos.scheduler.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * *****************这个类是负责定期清理日志的*****************
 * **************************************************************
 * **************************************************************
 * **************************************************************
 *
 * @author wzy
 *
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RegularClearlogs extends InitJob {

    Logger logger = LogManager.getLogger(RegularClearlogs.class.getName());

    public RegularClearlogs() {

    }

    public String doExe() {
        logger.info("\r\n***************RegularClearlogs定期清理日志功能START****************\r\n");
        // 返回信息
        String sReturnInfo = "";
        try {
            // 日志保留天数  只保留修改时间是最近天数的日志
            int days = Integer.parseInt(StaticInfo.KeepLogsDay);
            logger.info("\r\n********日志保留天数为:" + StaticInfo.KeepLogsDay + "天********\r\n");
            String logPath = System.getProperty("catalina.home");
            logger.info("\r\n********清理日志路径为:" + logPath + "********\r\n");
            String sysLogPath = logPath + "\\log";
            String sysLogspath = logPath + "\\logs";
            String sysMyLogPath = logPath + "\\myLog";

            clearLogs(sysLogPath, days); // 清理 log 目录下的文件
            clearLogs(sysLogspath, days); // 清理 logs 目录下的文件
            clearLogs(sysMyLogPath, days); // 清理 mylog 目录下的文件

        } catch (Exception e) {
            logger.error("\r\n***********RegularClearlogs定期清理日志功能发生异常:" + e.getMessage());
        }

        logger.info("\r\n***************RegularClearlogs定期清理日志功能END****************\r\n");
        return sReturnInfo;
    }

    private void clearLogs(String path, int days) throws Exception {
        File dirfile = new File(path);
        SimpleDateFormat dfDateF = new SimpleDateFormat("yyyyMMdd");
        if (dirfile.exists()) {
            Calendar c = Calendar.getInstance();// 获得当前时间
            c.add(Calendar.DATE, -days);
            Date time = c.getTime();
            String thisDate = dfDateF.format(time);
            Date newDate = dfDateF.parse(thisDate);
            File[] subfiles = dirfile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    clearLogs(file.getPath(), days);
                } else {
                    Date endDate = dfDateF.parse(dfDateF.format(file.lastModified()));
                    if (newDate.compareTo(endDate) == 1) {
                        boolean flag = file.delete(); // 删除文件
                        if (flag) { // 如果删除成功 判断 父目录 是否是空目录 如果是空目录把父目录也删除
                            File fileParent = new File(file.getParent());
                            if (fileParent.listFiles().length == 0) {
                                fileParent.delete();
                            }
                        }
                    }
                }
            }
        }
    }

}
