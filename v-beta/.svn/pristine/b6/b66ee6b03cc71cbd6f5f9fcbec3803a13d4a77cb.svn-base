package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.waimai.isv.ISV_HelpTools;
import com.dsc.spos.waimai.isv.ISV_WMUtils;
import com.dsc.spos.waimai.isv.ISV_WM_WebHookService;
import eleme.openapi.sdk.api.entity.user.OUser;
import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.config.ElemeSdkLogger;
import eleme.openapi.sdk.oauth.OAuthClient;
import eleme.openapi.sdk.oauth.response.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ISV_ELMTokenRefresh extends InitJob {
    Logger logger = LogManager.getLogger(ISV_ELMTokenRefresh.class.getName());
    static boolean bRun=false;//标记此服务是否正在执行中
    String goodsLogFileName = "ISV_ELMTokenRefresh";

    public ISV_ELMTokenRefresh()
    {

    }
    public String doExe() throws Exception
    {
        String sReturnInfo="";
        try 
        {
            if (bRun )
            {
                logger.info("\r\n*********饿了么token更新ISV_ELMTokenRefresh正在执行中,本次调用取消:************\r\n");
                ISV_HelpTools.writelog_fileName("【同步任务ISV_ELMTokenRefresh】同步正在执行中,本次调用取消！",goodsLogFileName);
                sReturnInfo="饿了么token更新ISV_ELMTokenRefresh正在执行中！";
                return sReturnInfo;
            }
            bRun=true;
            logger.info("\r\n*********饿了么token更新ISV_ELMTokenRefresh定时调用Start:************\r\n");
            ISV_HelpTools.writelog_fileName("【同步任务ISV_ELMTokenRefresh】定时调用Start",goodsLogFileName);
            long curTimestamp = System.currentTimeMillis()/1000;
            //提前5分钟，刷新token
            curTimestamp = curTimestamp + 600;
            String sql = "select * from ISV_WM_ELM_TOKEN where REFRESH_TOKEN is not null and ACCESS_TOKEN_EXPIRES_IN<='"+curTimestamp+"' order by ACCESS_TOKEN_EXPIRES_IN ";
            ISV_HelpTools.writelog_fileName("【同步任务ISV_ELMTokenRefresh】查询token即将过期sql="+sql,goodsLogFileName);
            List<Map<String, Object>> tokenList = this.doQueryData(sql, null);
            if (tokenList==null||tokenList.isEmpty())
            {
                sReturnInfo= "【同步任务ISV_ELMTokenRefresh】查询token即将过期数据为空！";
                ISV_HelpTools.writelog_fileName("【同步任务ISV_ELMTokenRefresh】查询token即将过期数据为空！",goodsLogFileName);
                return  sReturnInfo;
            }
            String logStart = "";
            for (Map<String, Object> mapToken : tokenList)
            {
                String userId = mapToken.getOrDefault("USERID","").toString();
                String clientNo = mapToken.getOrDefault("CLIENTNO","").toString();
                String userName = mapToken.getOrDefault("USERNAME","").toString();
                logStart = "【循环刷新即将过期token】开始，客户标识clientNo="+clientNo+",授权商户userId="+userId;
                try
                {
                    ISV_HelpTools.writelog_fileName(logStart,goodsLogFileName);
                    String appKey = mapToken.getOrDefault("APPKEY","").toString();
                    String appSecret = mapToken.getOrDefault("APPSECRET","").toString();
                    String refresh_token = mapToken.getOrDefault("REFRESH_TOKEN","").toString();
                    String isTest = mapToken.getOrDefault("ISTEST","").toString();
                    if (refresh_token.isEmpty()||userId.isEmpty()||clientNo.isEmpty())
                    {
                        continue;
                    }
                    if (appKey.isEmpty()||appSecret.isEmpty())
                    {
                        appKey = ISV_WMUtils.elm_appKey;
                        appSecret = ISV_WMUtils.elm_appSecret;
                    }
                    boolean isSandbox = false;
                    if ("Y".equals(isTest))
                    {
                        isSandbox = true;
                    }
                    if ("ZssNWsz5".equals(clientNo))
                    {
                        isSandbox = true;
                    }
                    //实例化一个配置类
                    Config config = new Config(isSandbox, appKey, appSecret);
                    config.setLog(new ElemeSdkLogger() {
                        @Override
                        public void info(String message) {
                            try {
                                ISV_HelpTools.writelog_fileName("获取新token请求req:"+message,goodsLogFileName);
                            }
                            catch (Exception e)
                            {

                            }

                        }

                        @Override
                        public void error(String message) {
                            try {
                                ISV_HelpTools.writelog_fileName("获取新token返回res:"+message,goodsLogFileName);
                            }
                            catch (Exception e)
                            {

                            }

                        }
                    });

                    //使用config对象，实例化一个授权类
                    OAuthClient client = new OAuthClient(config);

                    //根据refreshToken,刷新token
                    Token token = client.getTokenByRefreshToken(refresh_token);
                    if (token==null||token.getAccessToken()==null||token.getAccessToken().isEmpty())
                    {
                        ISV_HelpTools.writelog_fileName(logStart+",获取新token失败！",goodsLogFileName);
                        continue;
                    }
                    ISV_HelpTools.writelog_fileName(logStart+",获取新token成功，对应"+token.toString(),goodsLogFileName);
                    String requestId = UUID.randomUUID().toString().replace("-","");
                    long timestamp = System.currentTimeMillis();//当前时间戳

                    long exprieSeconds = token.getExpires();//过期时间秒
                    long timestamp_second = timestamp/1000;
                    long exprieTimestamp = timestamp_second + exprieSeconds;
                    String accessToken = token.getAccessToken();//新的token
                    String refreshToken = token.getRefreshToken();//新的refreshToken

                    String modiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    String updateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

                    List<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
                    UptBean up1 = new UptBean("ISV_WM_ELM_TOKEN");
                    up1.addCondition("CLIENTNO",new DataValue(clientNo, Types.VARCHAR));
                    up1.addCondition("USERID",new DataValue(userId, Types.VARCHAR));

                    if (appKey!=null&&!appKey.isEmpty())
                    {
                        up1.addUpdateValue("APPKEY",new DataValue(appKey, Types.VARCHAR));
                        up1.addUpdateValue("APPSECRET",new DataValue(appSecret, Types.VARCHAR));
                    }

                    up1.addUpdateValue("ACCESS_TOKEN",new DataValue(accessToken, Types.VARCHAR));
                    up1.addUpdateValue("ACCESS_TOKEN_EXPIRES_IN",new DataValue(exprieTimestamp, Types.VARCHAR));
                    up1.addUpdateValue("REFRESH_TOKEN",new DataValue(refreshToken, Types.VARCHAR));
                    up1.addUpdateValue("LASTMODITIME",new DataValue(modiTime, Types.DATE));
                    up1.addUpdateValue("UPDATE_TIME",new DataValue(updateTime, Types.VARCHAR));

                    DPB.add(new DataProcessBean(up1));
                    try
                    {
                        this.doExecuteDataToDB(DPB);
                        ISV_HelpTools.writelog_fileName(logStart+",新token更新服务端数据库成功，更新数据库ISV_WM_ELM_TOKEN表成功",goodsLogFileName);
                    }
                    catch (Exception e)
                    {
                        ISV_HelpTools.writelog_fileName(logStart+",新token更新服务端数据库异常："+e.getMessage(),goodsLogFileName);
                    }


                    //推送给客户端,组装推送结构体，同ISV_WMELMCallBackService服务结构体
                    OUser oUser = new OUser();
                    oUser.setUserId(Long.parseLong(userId));
                    oUser.setUserName(userName);
                    oUser.setAuthorizedShops(new ArrayList<>());
                    String msgType = "ShopBinding";//特殊，不是标准的饿了么推送消息类型。我们自己定义结构体
                    com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("token",token);
                    jsonObject.put("oUser",oUser);
                    jsonObject.put("config",config);
                    jsonObject.put("state",clientNo);
                    jsonObject.put("requestId",requestId);
                    jsonObject.put("timestamp",timestamp);
                    jsonObject.put("messageName","服务端刷新token推送给客户端");
                    String sendMessage = jsonObject.toJSONString();
                    ISV_HelpTools.writelog_fileName(logStart+",新token推送给客户端内容:"+sendMessage,goodsLogFileName);
                    ISV_WM_WebHookService.sendELMMsgToClient(clientNo,msgType,requestId,sendMessage);
                    ISV_HelpTools.writelog_fileName(logStart+",新token推送给客户端完成",goodsLogFileName);
                }
                catch (Exception e)
                {
                    ISV_HelpTools.writelog_fileName(logStart+",获取新token异常:"+e.getMessage(),goodsLogFileName);
                }
            }

        }
        catch (Exception e)
        {
            logger.error("\r\n******饿了么token更新ISV_ELMTokenRefresh报错信息:" + e.getMessage()+"\r\n******\r\n");
            ISV_HelpTools.writelog_fileName("【同步任务ISV_ELMTokenRefresh】同步正在执行中,异常:"+ e.getMessage(),goodsLogFileName);
            sReturnInfo="错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********饿了么token更新ISV_ELMTokenRefresh定时调用End:************\r\n");
            ISV_HelpTools.writelog_fileName("【同步任务ISV_ELMTokenRefresh】定时调用End",goodsLogFileName);
        }
        return sReturnInfo;
    }

    protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
        if (pData == null || pData.size() == 0) {
            return;
        }
        StaticInfo.dao.useTransactionProcessData(pData);
    }
}
