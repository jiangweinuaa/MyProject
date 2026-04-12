package com.dsc.spos.waimai.isv;

import com.dsc.spos.dao.*;
import com.dsc.spos.waimai.SWaimaiBasicService;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import eleme.openapi.sdk.api.entity.user.OAuthorizedShop;
import eleme.openapi.sdk.api.entity.user.OUser;
import eleme.openapi.sdk.api.service.UserService;
import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.config.ElemeSdkLogger;
import eleme.openapi.sdk.oauth.OAuthClient;
import eleme.openapi.sdk.oauth.response.Token;
import org.json.JSONObject;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class ISV_WMELMCallBackService extends SWaimaiBasicService {

    String logFileName = "ELMReqestShopBindingLog";
    @Override
    public String execute(String json) throws Exception {
        if (json == null || json.length() == 0) {
            ISV_HelpTools.writelog_fileName("饿了么授权后回调消息为空！",logFileName);
            return null;
        }
        ISV_HelpTools.writelog_fileName("饿了么授权后回调消息:"+json,logFileName);
        JSONObject reqJson = new JSONObject(json);
        String code = reqJson.optString("code");
        String state = reqJson.optString("state");
        String error = reqJson.optString("error");
        String error_description = reqJson.optString("error_description");
        if (code!=null&&!code.trim().isEmpty())
        {
            try
            {
                boolean isSandbox = false;
                if ("ZssNWsz5".equalsIgnoreCase(state))
                {
                    isSandbox = true;
                }
                eleme.openapi.sdk.config.Config config = null;
                if (isSandbox)
                {
                    config = new Config(isSandbox,ISV_WMUtils.elm_appKey_sandbox,ISV_WMUtils.elm_appSecret_sandbox);
                }
                else
                {
                    config = new Config(isSandbox,ISV_WMUtils.elm_appKey,ISV_WMUtils.elm_appSecret);
                }
                String configStr = com.alibaba.fastjson.JSON.toJSONString(config);
                String callback_url = ISV_WMUtils.elm_auth_callback_url;
                if (config!=null){
                    config.setLog(new ElemeSdkLogger() {
                        @Override
                        public void info(String message) {
                            try {
                                ISV_HelpTools.writelog_fileName("饿了么客户同意授权后，获取token请求req:"+message+",授权时code="+code+"，客户唯一标识state="+state+"，配置文件参数config="+configStr,logFileName);
                            }
                            catch (Exception e)
                            {

                            }

                        }

                        @Override
                        public void error(String message) {
                            try {
                                ISV_HelpTools.writelog_fileName("饿了么客户同意授权后，获取token返回res:"+message+",授权时code="+code+"，客户唯一标识state="+state+"，配置文件参数config="+configStr,logFileName);
                            }
                            catch (Exception e)
                            {

                            }
                        }
                    });

                }

                // 使用config对象，实例化一个授权类
                OAuthClient client = new OAuthClient(config);
                Token token = client.getTokenByCode(code, callback_url);
                String requestId = UUID.randomUUID().toString().replace("-","");
                String timestamp = System.currentTimeMillis()+"";
                if (token==null||token.getAccessToken()==null||token.getAccessToken().trim().isEmpty())
                {
                    ISV_HelpTools.writelog_fileName("饿了么客户同意授权后，获取token为空！授权时code="+code+"，客户唯一标识state="+state+"，配置文件参数config="+configStr,logFileName);
                    return "";
                    //token = client.getTokenInClientCredentials();
                }
                //String tokenStr = com.alibaba.fastjson.JSON.toJSONString(token);
                //token获取后，需要获取usedId，否则无法关联，
                UserService userService = new UserService(config, token);
                OUser oUser = userService.getUser();
                if (oUser==null)
                {
                    ISV_HelpTools.writelog_fileName("饿了么客户同意授权后，获取获取商户账号信息userId失败！授权时code="+code+"，客户唯一标识state="+state+"，配置文件参数config="+configStr,logFileName);
                    return "";
                }
                //后续，这个门店，需要推送到客户端
                //String oUserStr = com.alibaba.fastjson.JSON.toJSONString(oUser);
                saveELMTokenUserId(state,config,token,oUser);

                String msgType = "ShopBinding";//特殊，不是标准的饿了么推送消息类型。我们自己定义结构体
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("token",token);
                jsonObject.put("oUser",oUser);
                jsonObject.put("config",config);
                jsonObject.put("state",state);
                jsonObject.put("requestId",requestId);
                jsonObject.put("timestamp",timestamp);

                String sendMessage = jsonObject.toJSONString();
                String clientNo = state;
                ISV_HelpTools.writelog_fileName("饿了么客户同意授权后，获取获取商户账号信息成功！返回的token以及商户信息:"+sendMessage,logFileName);
                saveELMMessage(clientNo,requestId,timestamp,sendMessage);
                ISV_WM_WebHookService.sendELMMsgToClient(clientNo,msgType,requestId,sendMessage);
            }
            catch (Exception e)
            {

            }

        }
         return "";
    }

    @Override
    protected void processDUID(String req, Map<String, Object> res) throws Exception {

    }

    @Override
    protected List<InsBean> prepareInsertData(String req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(String req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(String req) throws Exception {
        return null;
    }

    /**
     * 授权成功后保存token及商户信息
     * @param clientNo 客户端唯一标识(对应授权时state)
     * @param config 配置文件
     * @param token 授权后获取的token
     * @param oUser 授权后根据token获取授权商户信息
     * @return
     * @throws Exception
     */
    private boolean saveELMTokenUserId(String clientNo,Config config,Token token,OUser oUser) throws Exception
    {
        boolean nRet = false;
        try
        {
            String appKey = "";
            String appSecret = "";
            //保存token
            if (token!=null&&oUser!=null&&token.getAccessToken()!=null&&!token.getAccessToken().trim().isEmpty())
            {
                this.pData.clear();
                long exprieSeconds = token.getExpires();//过期时间秒
                long curTimestamp = System.currentTimeMillis()/1000;//当前时间戳，转成秒
                long exprieTimestamp = curTimestamp + exprieSeconds;
                String accessToken = token.getAccessToken();
                String refreshToken = token.getRefreshToken();
                if (config!=null)
                {
                    appKey = config.getApp_key();
                    appSecret = config.getApp_secret();
                }
                long userId = oUser.getUserId();
                String userName = oUser.getUserName();
                if (userName != null && userName.length() > 255)
                {
                    userName = userName.substring(0, 255);
                }
                DelBean del = new DelBean("ISV_WM_ELM_TOKEN");
                del.addCondition("CLIENTNO",new DataValue(clientNo, Types.VARCHAR));
                del.addCondition("USERID",new DataValue(userId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(del));

                String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                String[] columns1 =
                        {"CLIENTNO", "USERID","USERNAME","APPKEY", "APPSECRET", "ACCESS_TOKEN", "ACCESS_TOKEN_EXPIRES_IN", "REFRESH_TOKEN", "MEMO",
                                "CREATETIME"};
                DataValue[] insValue1 = null;
                insValue1 = new DataValue[]
                        {
                                new DataValue(clientNo, Types.VARCHAR),
                                new DataValue(userId, Types.VARCHAR),
                                new DataValue(userName, Types.VARCHAR),
                                new DataValue(appKey, Types.VARCHAR),
                                new DataValue(appSecret, Types.VARCHAR),
                                new DataValue(accessToken, Types.VARCHAR),
                                new DataValue(exprieTimestamp, Types.VARCHAR),
                                new DataValue(refreshToken, Types.CLOB),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(createTime, Types.DATE)
                        };

                InsBean ib1 = new InsBean("ISV_WM_ELM_TOKEN", columns1);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));
                this.doExecuteDataToDB();
                ISV_HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存token成功,客户端唯一标识="+clientNo+",授权商户userId="+userId,logFileName);
            }

            if (oUser!=null)
            {
                this.pData.clear();
                if (config!=null)
                {
                    appKey = config.getApp_key();
                    appSecret = config.getApp_secret();
                }
                long userId = oUser.getUserId();
                List<OAuthorizedShop> authorizedShops = oUser.getAuthorizedShops();
                if (authorizedShops==null||authorizedShops.isEmpty())
                {
                    ISV_HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】查询获取的商户信息对应的店铺列表为空,客户端唯一标识="+clientNo+",授权商户userId="+userId,logFileName);
                    return true;
                }
                List<DataProcessBean> pData_del = new ArrayList<DataProcessBean>();
                List<DataProcessBean> pData_ins = new ArrayList<DataProcessBean>();
                String[] columns1 =
                        { "CLIENTNO","LOAD_DOCTYPE", "BUSINESSID", "ORDERSHOPNO", "ORDERSHOPNAME","APPKEY", "APPSECRET", "APPNAME", "ISTEST",
                                "MEMO", "ISJBP", "CHANNELID","USERID"};
                DataValue[] insValue1 = null;
                String loadDocType = orderLoadDocType.ELEME;
                String channelId = orderLoadDocType.ELEME+"001";//默认渠道ID
                String isTest = "N";
                String isJbp = "Y";//服务商都是Y
                String appName = "饿了么外卖(服务商)";
                String businessId = "2";//外卖默认2
                String memo = "饿了么外卖授权的店铺列表";
                for (OAuthorizedShop elmShop : authorizedShops)
                {
                    long shopId = elmShop.getId();
                    String shopName = elmShop.getName();
                    if (shopName != null && shopName.length() > 255)
                    {
                        shopName = shopName.substring(0, 255);
                    }
                    DelBean del = new DelBean("ISV_WM_MAPPINGSHOP");
                    del.addCondition("CLIENTNO",new DataValue(clientNo, Types.VARCHAR));
                    del.addCondition("LOAD_DOCTYPE",new DataValue(loadDocType, Types.VARCHAR));
                    del.addCondition("ORDERSHOPNO",new DataValue(shopId, Types.VARCHAR));
                    pData_del.add(new DataProcessBean(del));

                    insValue1 = new DataValue[]
                            {
                                    new DataValue(clientNo, Types.VARCHAR),
                                    new DataValue(loadDocType, Types.VARCHAR),
                                    new DataValue(businessId, Types.VARCHAR), // 1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
                                    new DataValue(shopId, Types.VARCHAR),// 平台门店ID
                                    new DataValue(shopName, Types.VARCHAR),// 平台门店名称
                                    new DataValue(appKey, Types.VARCHAR),
                                    new DataValue(appSecret, Types.VARCHAR),
                                    new DataValue(appName, Types.VARCHAR),
                                    new DataValue(isTest, Types.VARCHAR),
                                    new DataValue(memo, Types.VARCHAR),
                                    new DataValue(isJbp, Types.VARCHAR),
                                    new DataValue(channelId, Types.VARCHAR),
                                    new DataValue(userId, Types.VARCHAR)
                            };

                    InsBean ib1 = new InsBean("ISV_WM_MAPPINGSHOP", columns1);
                    ib1.addValues(insValue1);
                    pData_ins.add(new DataProcessBean(ib1));
                }

                //先删后保存
                if (!pData_ins.isEmpty())
                {
                    for (DataProcessBean del : pData_del)
                    {
                        this.addProcessData(del);
                    }
                    for (DataProcessBean ib : pData_ins)
                    {
                        this.addProcessData(ib);
                    }
                    this.doExecuteDataToDB();
                    nRet = true;
                    ISV_HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存商户授权的店铺列表成功,客户端唯一标识="+clientNo+",授权商户userId="+userId,logFileName);
                }

            }

        }
        catch (Exception e)
        {
            ISV_HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存token及商户信息异常："+e.getMessage()+",客户端唯一标识="+clientNo,logFileName);
        }

        return nRet;
    }

    /**
     * 饿了么授权后获取token以及商户信息后，我们自己组成结构体数据保存到数据库,后续可以重新推送给客户端
     * @param clientNo
     * @param requestId
     * @param timestamp
     * @param originMessage
     * @return
     * @throws Exception
     */
    private boolean saveELMMessage(String clientNo, String requestId,String timestamp,String originMessage) throws Exception
    {
        boolean nRet = false;
        if (originMessage==null||originMessage.isEmpty())
        {
            return false;
        }
        try
        {

            String messageType = "ShopBinding";
            String messageTypeStatus = "";//
            String orderNo = "";
            String shopId = "";
            String process_status = "Y";//是否推送到客户端(Y是；N否) ，默认Y，推送失败更新N


            String[] columns1 =
                    {"ID", "CLIENTNO","SHOPID", "ORDERNO", "MESSAGETYPE", "MESSAGETYPESTATUS", "MESSAGE", "TIMESTAMP",
                            "PROCESS_STATUS"};
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[]
                    {
                            new DataValue(requestId, Types.VARCHAR),
                            new DataValue(clientNo, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(orderNo, Types.VARCHAR),
                            new DataValue(messageType, Types.VARCHAR),
                            new DataValue(messageTypeStatus, Types.VARCHAR),
                            new DataValue(originMessage, Types.CLOB),
                            new DataValue(timestamp, Types.VARCHAR),
                            new DataValue(process_status, Types.VARCHAR)
                    };

            InsBean ib1 = new InsBean("ISV_WM_ELM_MESSAGE", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
            this.doExecuteDataToDB();
            nRet = true;
            ISV_HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】成功,消息id="+requestId,logFileName);

        }
        catch (Exception e)
        {
            ISV_HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】异常:"+e.getMessage()+",消息id="+requestId,logFileName);
        }

        return nRet;
    }

}
