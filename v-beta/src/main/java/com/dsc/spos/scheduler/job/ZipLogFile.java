package com.dsc.spos.scheduler.job;

import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ZipUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ZipLogFile extends InitJob
{
    Logger logger = LogManager.getLogger(ZipLogFile.class.getName());
    //一天执行一次
    static boolean bFirst=true;
    static String bFirstDate ="";


    public  ZipLogFile()
    {

    }

    public String doExe()
    {

        logger.error("\r\n***************ZipLogFile文件压缩START****************\r\n");

        //
        String edateTimeStart = "040000";   //开始时间
        String edateTimeEnd = "045959";     //结束时间

        //系统日期和时间
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());

        try
        {
            if (sTime.compareTo(edateTimeStart) >= 0 && sTime.compareTo(edateTimeEnd) < 0)
            {
                //日结JOB一天只执行一次
                if(bFirstDate.equals(PosPub.GetStringDate(sDate, -1)))
                {
                    bFirst=true;
                }
                if(bFirst==false)
                {
                    return "";
                }
                bFirstDate =sDate;
                bFirst=false;

                sDate = PosPub.GetStringDate(sDate, -1);

                if (StaticInfo.psc.getZipLogFilePath() != null)
                {
                    //获取配置的路径
                    List<SPosConfig.Value> lstPath = StaticInfo.psc.getZipLogFilePath().getFilePath();

                    if (lstPath != null)
                    {
                        for (int i = 0; i < lstPath.size(); i++)
                        {
                            ZipUtils.zipFile_NRC(lstPath.get(i).getValue());
                        }
                    }
                }else
                {
                    String logPath = System.getProperty("catalina.home");
                    String sysLogPath = logPath + "\\log";
                    String sysLogspath = logPath + "\\logs\\SposWebLog";
                    String sysMyLogPath = logPath + "\\myLog";
                    ZipUtils.zipFile_NRC(sysLogPath);
                    ZipUtils.zipFile_NRC(sysLogspath);
                    ZipUtils.zipFile_NRC(sysMyLogPath);
                }

            }
        }
        catch (Exception e)
        {
            logger.error("\r\n***********ZipLogFile文件压缩发生异常:" + e.getMessage());
        }

        logger.error("\r\n***************ZipLogFile文件压缩END****************\r\n");
        return "";
    }

}
