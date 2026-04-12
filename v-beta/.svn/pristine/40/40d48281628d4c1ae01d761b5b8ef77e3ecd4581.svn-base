package com.dsc.spos.utils;

import com.dsc.spos.json.JsonBasicReq;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

    //
    private static Logger logger = LogManager.getLogger(Log.class);


    /**
     * error日志
     * @param req 服务的请求对象，job等任务传null
     * @param tips 要记录提示的内容
     * @param sql 要记录的SQL内容
     */
    public  static  void error(JsonBasicReq req, String tips, String sql)
    {
        if (req == null)
        {
            logger.error( tips==null?"":"tips=" +tips+ sql==null?"":",sql="+sql);
        }
        else
        {
            logger.error("requestId="+req.getRequestId()+ (tips==null?"":",tips=" +tips+ sql==null?"":",sql="+sql));
        }
    }

    /**
     * info日志
     * @param req 服务的请求对象，job等任务传null
     * @param tips 要记录提示的内容
     * @param sql 要记录的SQL内容
     */
    public  static  void info(JsonBasicReq req,String tips, String sql)
    {
        if (req == null)
        {
            logger.info( tips==null?"":"tips=" +tips+ sql==null?"":",sql="+sql);
        }
        else
        {
            logger.info("requestId="+req.getRequestId()+ (tips==null?"":",tips=" +tips+ sql==null?"":",sql="+sql));
        }
    }

    /**
     * debug日志
     * @param req 服务的请求对象，job等任务传null
     * @param tips 要记录提示的内容
     * @param sql 要记录的SQL内容
     */
    public  static  void debug(JsonBasicReq req,String tips, String sql)
    {
        if (req == null)
        {
            logger.debug( tips==null?"":"tips=" +tips+ sql==null?"":",sql="+sql);
        }
        else
        {
            logger.debug("requestId="+req.getRequestId()+ (tips==null?"":",tips=" +tips+ sql==null?"":",sql="+sql));

        }
    }


}
