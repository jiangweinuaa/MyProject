package com.dsc.spos.waimai.isv;

import com.dsc.spos.dao.*;
import com.dsc.spos.scheduler.job.StaticInfo;
import org.pentaho.di.trans.steps.sql.ExecSQL;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class ISV_WMUtils {

    /**
     * 客户中台接口地址前缀
     */
    public static final String dcpService_url = "http://[doMain]/dcpService/DCP/services";
    /**
     * 饿了么消息推送到客户端接口地址
     */
    private static final String elm_api = "/Waimai/ELM";
    /**
     * 饿了么授权回调接口地址必须是https(https://[服务端域名]/dcpService/DCP/services/ISV/Waimai/ELMCallBack)
     */
    public static final String elm_auth_callback_url = "https://eliutong2.digiwin.com.cn/dcpService/DCP/services/ISV/Waimai/ELMCallBack";//"https://retaildev.digiwin.com.cn/dcpService_3.0/DCP/services/ISV/Waimai/ELMCallBack";
    /**
     * 饿了么授权后返回的token及商户信息推送给客户端地址
     */
    private static final String elm_api_token = "/Waimai/ELMToken";
    /**
     * 美团订单消息推送接口地址
     */
    private static final String mt_api_order = "/Waimai/JBP";
    /**
     * 美团订单配送消息推送接口地址
     */
    private static final String mt_api_shippingStatus = "/Waimai/JBPShipping";
    /**
     * 美团绑定门店后推送token接口地址
     */
    private static final String mt_api_token = "/Waimai/JBPToken";
    /**
     * 美团门店解绑后回调接口地址
     */
    private static final String mt_api_JBPTokenReleaseBinding = "/Waimai/JBPTokenReleaseBinding";

    /**
     * 鼎捷美团服务商开发者id
     */
    public static final String mt_developerId = "100146";
    /**
     * 鼎捷美团服务商开发者id对应的key
     */
    public static final String mt_signKey = "jevw2dkj37mb8pun";

    /**
     * 鼎捷饿了么服务商应用key(生产环境)
     */
    public static final String elm_appKey = "VcaVTyFg3v";
    /**
     * 鼎捷饿了么服务商应用secret(生产环境)
     */
    public static final String elm_appSecret = "a979003d14ca5cee73ba923dd01cafa0a406a541";

    /**
     * 鼎捷饿了么服务商应用key(沙箱环境)
     */
    public static final String elm_appKey_sandbox = "W3Y1U1KOzs";
    /**
     * 鼎捷饿了么服务商应用secret(沙箱环境)
     */
    public static final String elm_appSecret_sandbox = "9b6ab8f33a49de4f3bdfec034d07c3a5334cc468";

    /**
     * 客户编码对应的信息
     */
    public static Map<String,Object> clientAccounts;

    /**
     * 饿了么userId授权商户ID对应的clientNo信息
     */
    public static Map<String,String> elmUserIdAccounts;

    /**
     * 查询客户主域名
     * @param clientNo
     * @return
     * @throws Exception
     */
    private static String getClientMainUrl(String clientNo) throws Exception
    {
        String domainUrl = "";
        try
        {
            if (clientAccounts!=null)
            {
                domainUrl = clientAccounts.getOrDefault(clientNo,"").toString();
                if (!domainUrl.isEmpty())
                {
                    return domainUrl;
                }
            }
            else
            {
                clientAccounts = new HashMap<>();
            }

            String sql = "Select * from ISV_WM_CLIENT where clientno='"+clientNo+"'";
            List<Map<String,Object>> clientMaps = StaticInfo.dao.executeQuerySQL(sql,null);
            if (clientMaps!=null&&!clientMaps.isEmpty())
            {
                domainUrl = clientMaps.get(0).get("CLIENTMAINURL").toString();
                clientAccounts.put(clientNo,domainUrl);
                return domainUrl;
            }


        }
        catch (Exception e)
        {

        }
        return domainUrl;

    }

    /**
     * 获取美团推送客户端对应的接口地址
     * @param clientNo 客户唯一标识
     * @param msgType 消息类型(订单，配送、门店绑定/解绑)
     * @return
     * @throws Exception
     */
    private static String getMTPostClientUrl (String clientNo,String msgType) throws Exception
    {
        String url = "";
        try
        {
            if (clientNo.trim().isEmpty()||msgType.isEmpty())
            {
                return "";
            }
            String apiMethod = "";
            //order订单详情；orderCancel取消信息；orderRefund退款消息；partOrderRefund部分退款消息；shippingStatus配送信息
            if ("order".equals(msgType)||"orderCancel".equals(msgType)||"orderRefund".equals(msgType)||"partOrderRefund".equals(msgType))
            {
                apiMethod = mt_api_order;
            }
            else if ("shippingStatus".equals(msgType))
            {
                apiMethod = mt_api_shippingStatus;
            }
            else if ("ShopBinding".equals(msgType))
            {
                apiMethod = mt_api_token;
            }
            else if ("ShopReleaseBinding".equals(msgType))
            {
                apiMethod = mt_api_JBPTokenReleaseBinding;
            }
            else
            {
                return "";
            }

            url = getClientMainUrl(clientNo);

            if (url.isEmpty())
            {
                return "";
            }
            if ("retaildev.digiwin.com.cn".equals(url))
            {
                url = dcpService_url.replace("[doMain]",url).replace("dcpService","dcpService_3.0");
            }
            else
            {
                url = dcpService_url.replace("[doMain]",url);
            }

            url = url +apiMethod;
        }
        catch (Exception e)
        {

        }
        return url;
    }

    /**
     * 转发美团推送的消息到客户端
     * @param clientNo 客户唯一标识
     * @param msgType 消息类型(订单，配送、门店绑定/解绑)
     * @param params 请求内容Map
     * @return
     * @throws Exception
     */
    public static String sendMTMsgToClient(String clientNo,String msgType,Map<String, Object> params) throws Exception
    {
        String result = null;
        try
        {
            String url = getMTPostClientUrl(clientNo,msgType);
            result = ISV_WMJBPHttpSend.doPost_form_urlencoded(url,params);
        }
        catch (Exception e)
        {

        }
        return result;
    }

    /**
     * 更新美团推送到客户端消息是否成功
     * @param id 消息id
     * @param sendFlag 是否成功(Y-是，N-否)
     * @throws Exception
     */
    public static void updateMTMsgSendFlag(String id,String sendFlag) throws Exception
    {
        try
        {
            if (id==null||id.isEmpty())
            {
                return;
            }
            List<DataProcessBean> data = new ArrayList<>();
            UptBean up1=new UptBean("ISV_WM_MT_MESSAGE");
            up1.addUpdateValue("PROCESS_STATUS", new DataValue(sendFlag, Types.VARCHAR));
            up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            up1.addCondition("ID", new DataValue(id, Types.VARCHAR));
            data.add(new DataProcessBean(up1));
            StaticInfo.dao.useTransactionProcessData(data);
        }
        catch (Exception e)
        {

        }
    }

    /**
     * 根据饿了么授权账号userId查询客户端唯一标识clientNo
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getClientNoByUserId(String userId) throws Exception
    {
        String clientNo = "";
        try
        {
            if (elmUserIdAccounts!=null)
            {
                clientNo = elmUserIdAccounts.getOrDefault(userId,"").toString();
                if (!clientNo.isEmpty())
                {
                    return clientNo;
                }
            }
            else
            {
                elmUserIdAccounts = new HashMap<>();
            }

            String sql = "Select CLIENTNO from ISV_WM_ELM_TOKEN where USERID='"+userId+"'";
            List<Map<String,Object>> clientUserIdMaps = StaticInfo.dao.executeQuerySQL(sql,null);
            if (clientUserIdMaps!=null&&!clientUserIdMaps.isEmpty())
            {
                clientNo = clientUserIdMaps.get(0).get("CLIENTNO").toString();
                elmUserIdAccounts.put(userId,clientNo);
                return clientNo;
            }


        }
        catch (Exception e)
        {

        }
        return clientNo;

    }

    /**
     * 获取饿了么推送客户端对应的接口地址
     * @param clientNo 客户唯一标识
     * @param msgType 消息类型(订单，配送、门店绑定/解绑)
     * @return
     * @throws Exception
     */
    private static String getELMPostClientUrl (String clientNo,String msgType) throws Exception
    {
        String url = "";
        try
        {
            if (clientNo.trim().isEmpty())
            {
                return "";
            }
            String apiMethod = elm_api;
            //order订单详情；orderCancel取消信息；orderRefund退款消息；partOrderRefund部分退款消息；shippingStatus配送信息
            if ("ShopBinding".equals(msgType))
            {
                apiMethod = elm_api_token;
            }

            url = getClientMainUrl(clientNo);

            if (url.isEmpty())
            {
                return "";
            }
            if ("retaildev.digiwin.com.cn".equals(url))
            {
                url = dcpService_url.replace("[doMain]",url).replace("dcpService","dcpService_3.0");
            }
            else
            {
                url = dcpService_url.replace("[doMain]",url);
            }

            url = url +apiMethod;
        }
        catch (Exception e)
        {

        }
        return url;
    }

    /**
     * 转发饿了么推送的消息到客户端
     * @param clientNo 客户唯一标识
     * @param msgType 消息类型
     * @param params 消息内容
     * @return
     * @throws Exception
     */
    public static String sendELMMsgToClient(String clientNo,String msgType,String params) throws Exception
    {
        String result = null;
        try
        {
            String url = getELMPostClientUrl(clientNo,msgType);
            result = ISV_WMELMHttpSend.doPost_json(url,params);
        }
        catch (Exception e)
        {

        }
        return result;
    }

    /**
     * 更新饿了么推送到客户端消息是否成功
     * @param id 消息id
     * @param sendFlag 是否成功(Y-是，N-否)
     * @throws Exception
     */
    public static void updateELMMsgSendFlag(String id,String sendFlag) throws Exception
    {
        try
        {
            if (id==null||id.isEmpty())
            {
                return;
            }
            List<DataProcessBean> data = new ArrayList<>();
            UptBean up1=new UptBean("ISV_WM_ELM_MESSAGE");
            up1.addUpdateValue("PROCESS_STATUS", new DataValue(sendFlag, Types.VARCHAR));
            up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            up1.addCondition("ID", new DataValue(id, Types.VARCHAR));
            data.add(new DataProcessBean(up1));
            StaticInfo.dao.useTransactionProcessData(data);
        }
        catch (Exception e)
        {

        }
    }
}
