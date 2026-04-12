package com.dsc.spos.thirdpart.wecom;

import com.dsc.spos.dao.*;
import com.dsc.spos.thirdpart.wecom.aes.WXBizJsonMsgCrypt;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.XmlAndJsonConvert;
import com.dsc.spos.waimai.HelpTools;
import org.apache.cxf.common.util.CollectionUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：IsvWeComUtils
 * 服务说明：企业微信服务商通用方法
 * @author jinzma
 * @since  2023-09-06
 */
public class ISVWeComUtils {

    //获取第三方应用凭证suiteAccessToken
    public String getSuiteAccessToken(DsmDAO dao,String suiteId) throws Exception{
        try {
            String suiteAccessToken;
            String sql =" select a.*,to_char(sysdate,'yyyy-mm-dd HH24:MI:SS') as sdate,to_char(expires_in,'yyyy-mm-dd HH24:MI:SS') as edate"
                    + " from isv_wecom_suite a"
                    + " where suiteid='"+suiteId+"' " ;
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                suiteAccessToken = getQData.get(0).get("SUITEACCESSTOKEN").toString();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sDate = getQData.get(0).get("SDATE").toString();
                String eDate = getQData.get(0).get("EDATE").toString();
                if (!Check.Null(eDate)){
                    try {
                        if (df.parse(eDate).getTime() > df.parse(sDate).getTime()) {
                            return suiteAccessToken;
                        }
                    } catch (Exception ignored) {
                    }
                }
                
                String suiteSecret = getQData.get(0).get("SECRET").toString();
                String suiteTicket = getQData.get(0).get("SUITETICKET").toString();
                
                if (Check.Null(suiteTicket)){
                    HelpTools.writelog_fileName("企业微信服务商获取第三方应用凭证失败,suiteTicket为空,suiteId:"+suiteId, "isvwecom");
                    return null;
                }
                
                //调用企业微信平台获取第三方应用凭证suiteAccessToken
                JSONObject reqObject = new JSONObject();
                reqObject.put("suite_id", suiteId);
                reqObject.put("suite_secret", suiteSecret);
                reqObject.put("suite_ticket", suiteTicket);
                
                String url="https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token";
                
                //调企业微信接口
                HelpTools.writelog_fileName("企业微信请求get_suite_token:" + reqObject + " URL:" + url + " ", "isvwecom");
                String res = HttpSend.Sendhttp("POST", reqObject.toString(), url);
                
                JSONObject resObject=new JSONObject(res);
                suiteAccessToken = resObject.optString("suite_access_token");
                if (Check.Null(suiteAccessToken)){
                    String errmsg = resObject.optString("errmsg");
                    HelpTools.writelog_fileName("企业微信获取access_token失败,原因:"+errmsg+" ", "isvwecom");
                    return null;
                }
                
                int expires_in = resObject.optInt("expires_in");  //秒
                if (expires_in > 0){
                    expires_in = expires_in - 15 ; //失效时间减少15秒（从获取token到发起请求有一个时间差，避免token失效）
                }
                
                long milliseconds = df.parse(sDate).getTime() + expires_in * 1000L; //单位： 毫秒
                
                Date date = new Date();
                date.setTime(milliseconds);
                
                UptBean ub = new UptBean("ISV_WECOM_SUITE");
                ub.addUpdateValue("SUITEACCESSTOKEN", new DataValue(suiteAccessToken, Types.VARCHAR));
                ub.addUpdateValue("EXPIRES_IN", new DataValue(df.format(date), Types.DATE));
                ub.addUpdateValue("LASTMODITIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                //Condition
                ub.addCondition("SUITEID", new DataValue(suiteId, Types.VARCHAR));
                
                List<DataProcessBean> data = new ArrayList<>();
                data.add(new DataProcessBean(ub));
                dao.useTransactionProcessData(data);
                
                return suiteAccessToken;
                
            } else {
                HelpTools.writelog_fileName("企业微信服务商获取第三方应用凭证失败,isv_wecom_suite不存在suiteId:"+suiteId, "isvwecom");
                return null;
            }
            
        } catch(Exception e) {
            HelpTools.writelog_fileName("企业微信服务商获取第三方应用凭证异常:"+ e.getMessage()+" ", "isvwecom");
            return null;
        }
    }

    //应用服务商接口凭证 provider_access_token time/token
    public static Map<String,Object> providerAccessTokenMap = new HashMap<>() ;

    //应用服务商接口凭证 provider_access_token
    public String getProviderAccessToken(DsmDAO dao,boolean isUpdate) throws Exception{
        try {
            long currentTime=System.currentTimeMillis() / 1000;  //当前时间 秒
            if (!providerAccessTokenMap.isEmpty()) {
                long time = Long.parseLong(providerAccessTokenMap.get("time").toString());  //time/token
                if  ((currentTime < time) && !isUpdate ) {
                    return providerAccessTokenMap.get("token").toString();
                }
            }

            //获取provider_access_token
            String sql = "select * from isv_wecom_provider " ;
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                String corpId = getQData.get(0).get("CORPID").toString();
                String providerSecret = getQData.get(0).get("PROVIDERSECRET").toString();
                //获取服务商凭证
                String url="https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token";
                JSONObject req = new JSONObject();
                req.put("corpid",corpId);
                req.put("provider_secret",providerSecret);
                String res = HttpSend.Sendhttp("POST",req.toString(), url);

                JSONObject resObject=new JSONObject(res);

                String providerAccessToken = resObject.optString("provider_access_token");
                int expires_in = resObject.optInt("expires_in");  //秒

                if (expires_in > 0){
                    expires_in = expires_in - 15 ;     //失效时间减少15秒（从获取token到发起请求有一个时间差，避免token失效）
                }
                if (!Check.Null(providerAccessToken)) {
                    providerAccessTokenMap.put("time",currentTime + (long) expires_in);
                    providerAccessTokenMap.put("token", providerAccessToken);
                    return providerAccessToken;

                } else {
                    //int errcode = resObject.optInt("errcode");
                    String errmsg =  resObject.optString("errmsg");

                    HelpTools.writelog_fileName("企业微信服务商接口凭证调用失败,原因:"+errmsg+" ", "isvwecom");
                    providerAccessTokenMap.clear();
                    return null;
                }

            } else {
                HelpTools.writelog_fileName("企业微信服务商接口凭证调用失败,原因:isv_wecom_provider表未维护 ", "isvwecom");
                providerAccessTokenMap.clear();
                return null;
            }

        } catch(Exception e) {
            HelpTools.writelog_fileName("企业微信服务商接口凭证调用失败,原因:"+ e.getMessage()+" ", "isvwecom");
            providerAccessTokenMap.clear();
            return null;
        }
    }


    //url回调地址验证（验证服务商环境的）
    public String verify(DsmDAO dao,String sVerifyMsgSig,String sVerifyTimeStamp,String sVerifyNonce,String sVerifyEchoStr) throws IOException {
        /*
		------------使用示例一：验证回调URL---------------
		*企业开启回调模式时，企业微信会向验证url发送一个get请求
		假设点击验证时，企业收到类似请求：
		* GET /cgi-bin/wxpush?msg_signature=5c45ff5e21c57e6ad56bac8758b79b1d9ac89fd3&timestamp=1409659589&nonce=263014780&echostr=P9nAzCzyDtyTWESHep1vC5X9xho%2FqYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp%2B4RPcs8TgAE7OaBO%2BFZXvnaqQ%3D%3D
		* HTTP/1.1 Host: qy.weixin.qq.com

		接收到该请求时，企业应		1.解析出Get请求的参数，包括消息体签名(msg_signature)，时间戳(timestamp)，随机数字串(nonce)以及企业微信推送过来的随机加密字符串(echostr),
		这一步注意作URL解码。
		2.验证消息体签名的正确性
		3. 解密出echostr原文，将原文当作Get请求的response，返回给企业微信
		第2，3步可以用企业微信提供的库函数VerifyURL来实现。
		*/
        try {
            HelpTools.writelog_fileName("企业微信服务商url地址验证 sVerifyMsgSig=" + sVerifyMsgSig + ",sVerifyTimeStamp=" + sVerifyTimeStamp + ","
                    + "sVerifyNonce=" + sVerifyNonce + ",sVerifyEchoStr =" + sVerifyEchoStr + " ", "isvwecom");
            
            String sql =" select * from isv_wecom_provider " ;
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (CollectionUtils.isEmpty(getQData)) {
                HelpTools.writelog_fileName("企业微信服务商url地址验证失败: " + "isv_wecom_provider 缺失资料" + " ", "isvwecom");
                return null;
            }
            
            String corpId = getQData.get(0).get("CORPID").toString();
            String token = getQData.get(0).get("TOKEN").toString();
            String encodingAESKey = getQData.get(0).get("ENCODINGAESKEY").toString();
            
            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(token, encodingAESKey, corpId);
            
            try {
                String sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sVerifyEchoStr);  //需要返回的明文
                HelpTools.writelog_fileName("企业微信服务商url地址验证 sEchoStr=" + sEchoStr + " ", "isvwecom");
                
                return sEchoStr;
            } catch (Exception e) {
                HelpTools.writelog_fileName("企业微信服务商url地址验证失败: " + e.getMessage() + " ", "isvwecom");
                return null;
            }
        } catch (Exception e) {
            HelpTools.writelog_fileName("企业微信服务商url地址验证失败: " + e.getMessage() + " ", "isvwecom");
            return null;
        }
    }

    //回调事件统一处理
    public void callBackProcess(DsmDAO dao,String reqJson,String msg_signature,String timestamp,String nonce) throws Exception {
         /*
		------------使用示例二：对用户回复的消息解密---------------
		用户回复消息或者点击事件响应时，企业会收到回调消息，此消息是经过企业微信加密之后的密文以post形式发送给企业，密文格式请参考官方文档
		假设企业收到企业微信的回调消息如下：
		POST /cgi-bin/wxpush? msg_signature=477715d11cdb4164915debcba66cb864d751f3e6&timestamp=1409659813&nonce=1372623149 HTTP/1.1
		Host: qy.weixin.qq.com
		Content-Length:
		Content-Type:text/json
		
		{
			"tousername":"wx5823bf96d3bd56c7",
			"encrypt":"RypEvHKD8QQKFhvQ6QleEB4J58tiPdvo+rtK1I9qca6aM/wvqnLSV5zEPeusUiX5L5X/0lWfrf0QADHHhGd3QczcdCUpj911L3vg3W/sYYvuJTs3TUUkSUXxaccAS0qhxchrRYt66wiSpGLYL42aM6A8dTT+6k4aSknmPj48kzJs8qLjvd4Xgpue06DOdnLxAUHzM6+kDZ+HMZfJYuR+LtwGc2hgf5gsijff0ekUNXZiqATP7PF5mZxZ3Izoun1s4zG4LUMnvw2r+KqCKIw+3IQH03v+BCA9nMELNqbSf6tiWSrXJB3LAVGUcallcrw8V2t9EL4EhzJWrQUax5wLVMNS0+rUPA3k22Ncx4XXZS9o0MBH27Bo6BpNelZpS+/uh9KsNlY6bHCmJU9p8g7m3fVKn28H3KDYA5Pl/T8Z1ptDAVe0lXdQ2YoyyH2uyPIGHBZZIs2pDBS8R07+qN+E7Q==",
			"agentid":"218"
		}

		企业收到post请求之后应该		1.解析出url上的参数，包括消息体签名(msg_signature)，时间戳(timestamp)以及随机数字串(nonce)
		2.验证消息体签名的正确性。
		3.将post请求的数据进行json解析，并将"encrypt"标签的内容进行解密，解密出来的明文即是用户回复消息的明文，明文格式请参考官方文档
		第2，3步可以用企业微信提供的库函数DecryptMsg来实现。
		*/
        
        try {
            JSONObject reqJsonObj = new JSONObject(reqJson);
            String ToUserName = reqJsonObj.optString("ToUserName");  //就是suiteid 模板编号
            String Encrypt = reqJsonObj.optString("Encrypt");
            String AgentID = reqJsonObj.optString("AgentID");
            
            String sql =" select * from isv_wecom_suite where suiteid='"+ToUserName+"' " ;
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (CollectionUtils.isEmpty(getQData)) {
                HelpTools.writelog_fileName("企业微信服务商回调失败:isv_wecom_suite 找不到对应的模板ID:" +ToUserName+" ", "isvwecom");
            }
            
            String token = getQData.get(0).get("TOKEN").toString();
            String encodingAESKey = getQData.get(0).get("ENCODINGAESKEY").toString();
            
            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(token, encodingAESKey, ToUserName);
            List<DataProcessBean> data = new ArrayList<>();
            try {
                String sMsg = wxcpt.DecryptMsg(msg_signature,timestamp,nonce,reqJson);
                HelpTools.writelog_fileName("企业微信服务商回调明文: " + sMsg + " ", "isvwecom");
                String xml2json = XmlAndJsonConvert.xml2json(sMsg);
                JSONObject sMsgJson = new JSONObject(xml2json);
                
                String suiteId = sMsgJson.optString("SuiteId");
                String infoType = sMsgJson.optString("InfoType");
                switch (infoType){
                    case "suite_ticket":   //推送suite_ticket
                    {
                        //修改 ISV_WECOM_SUITE
                        UptBean ub = new UptBean("ISV_WECOM_SUITE");
                        ub.addUpdateValue("SUITETICKET", new DataValue(sMsgJson.optString("SuiteTicket"), Types.VARCHAR));
                        ub.addUpdateValue("LASTMODITIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                        //Condition
                        ub.addCondition("SUITEID", new DataValue(ToUserName, Types.VARCHAR));
                        
                        data.add(new DataProcessBean(ub));
                        dao.useTransactionProcessData(data);
                        data.clear();
                    }
                    break;
                    case "reset_permanent_code":   //重置永久授权码通知
                    {
                        String authCode = sMsgJson.optString("AuthCode");  //临时授权码
                        if (!Check.Null(authCode)) {
                            JSONObject reqObject = new JSONObject();
                            reqObject.put("auth_code", authCode);
                            String suiteAccessToken = getSuiteAccessToken(dao,suiteId);
                            String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=" + suiteAccessToken;
                            //调企业微信接口
                            HelpTools.writelog_fileName("企业微信请求get_permanent_code:" + reqObject + " URL:" + url + " ", "isvwecom");
                            String res = HttpSend.Sendhttp("POST", reqObject.toString(), url);
                            HelpTools.writelog_fileName("企业微信返回get_permanent_code:" + res + " ", "isvwecom");
                            
                            JSONObject resObject = new JSONObject(res);
                            String permanentCode = resObject.optString("permanent_code");
                            String corpId = "";
                            String corpName = "";
                            JSONObject resAuthCorpInfo = resObject.getJSONObject("auth_corp_info");
                            if (resAuthCorpInfo!=null){
                                corpId = resAuthCorpInfo.optString("corpid");
                                corpName = resAuthCorpInfo.optString("corp_name");
                                permanentCodeSave(dao,suiteId,corpId,corpName,permanentCode);
                            }
                        }
                    }
                    break;
                    case "create_auth":   //授权成功通知
                    {
                        String authCode = sMsgJson.optString("AuthCode");  //临时授权码
                        if (!Check.Null(authCode)) {
                            JSONObject reqObject = new JSONObject();
                            reqObject.put("auth_code", authCode);
                            String suiteAccessToken = getSuiteAccessToken(dao,suiteId);
                            String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=" + suiteAccessToken;
                            //调企业微信接口
                            HelpTools.writelog_fileName("企业微信请求get_permanent_code:" + reqObject + " URL:" + url + " ", "isvwecom");
                            String res = HttpSend.Sendhttp("POST", reqObject.toString(), url);
                            HelpTools.writelog_fileName("企业微信返回get_permanent_code:" + res + " ", "isvwecom");
                            
                            JSONObject resObject = new JSONObject(res);
                            String permanentCode = resObject.optString("permanent_code");
                            String corpId = "";
                            String corpName = "";
                            JSONObject resAuthCorpInfo = resObject.getJSONObject("auth_corp_info");
                            if (resAuthCorpInfo!=null){
                                corpId = resAuthCorpInfo.optString("corpid");
                                corpName = resAuthCorpInfo.optString("corp_name");
                                permanentCodeSave(dao,suiteId,corpId,corpName,permanentCode);
                            }
                        }
                    }
                    break;
                    case "change_auth":   //变更授权通知
                    {
                        //暂时不管
                    }
                    break;
                    case "cancel_auth":   //取消授权通知
                    {
                        String authCorpId = sMsgJson.optString("AuthCorpId");  //临时授权码
                        if (!Check.Null(authCorpId)) {
                            permanentCodeSave(dao,suiteId,authCorpId,"","");
                        }
                    }
                    break;
                }
                
            } catch (Exception e) {
                // 解密失败，失败原因请查看异常
                HelpTools.writelog_fileName("企业微信服务商解密失败: " + e.getMessage() + " ", "isvwecom");
            }
        } catch (Exception e) {
            HelpTools.writelog_fileName("企业微信服务商回调异常: " + e.getMessage() + " ", "isvwecom");
        }
    }
    
    //企业微信永久授权码保存
    public void permanentCodeSave(DsmDAO dao,String suiteId,String corpId,String corpName,String permanentCode) throws Exception {
        String sql =" select * from isv_wecom_empower where suiteid='"+suiteId+"' and corpid='"+corpId+"' " ;
        List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
        List<DataProcessBean> data = new ArrayList<>();
        if (CollectionUtils.isEmpty(getQData)) {
            String[] columns = {"SUITEID","CORPID","PERMANENTCODE","CORPNAME","PROCESS_STATUS","LASTMODITIME"} ;
            DataValue[] insValue = new DataValue[] {
                    new DataValue(suiteId, Types.VARCHAR),
                    new DataValue(corpId, Types.VARCHAR),
                    new DataValue(permanentCode, Types.VARCHAR),
                    new DataValue(corpName, Types.VARCHAR),
                    new DataValue("N", Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR),
            };
            InsBean ib = new InsBean("ISV_WECOM_EMPOWER", columns);
            ib.addValues(insValue);
            
            data.add(new DataProcessBean(ib));
            dao.useTransactionProcessData(data);
            
        }else{
            UptBean ub = new UptBean("ISV_WECOM_EMPOWER");
            ub.addUpdateValue("PERMANENTCODE", new DataValue(permanentCode, Types.VARCHAR));
            ub.addUpdateValue("CORPID", new DataValue(corpId, Types.VARCHAR));
            ub.addUpdateValue("CORPNAME", new DataValue(corpName, Types.VARCHAR));
            ub.addUpdateValue("PROCESS_STATUS", new DataValue("N", Types.VARCHAR));
            ub.addUpdateValue("LASTMODITIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            //Condition
            ub.addCondition("SUITEID", new DataValue(suiteId, Types.VARCHAR));
            ub.addCondition("CORPID", new DataValue(corpId, Types.VARCHAR));
            
            data.add(new DataProcessBean(ub));
            dao.useTransactionProcessData(data);
        }
    }

    //获取企业用户的账户列表
    public String getListActivedAccount(DsmDAO dao,String corpId,String cursor) throws Exception{
        String providerAccessToken = this.getProviderAccessToken(dao,false);

        String url="https://qyapi.weixin.qq.com/cgi-bin/license/list_actived_account?provider_access_token="+providerAccessToken;
        JSONObject reqObject = new JSONObject();
        reqObject.put("corpid", corpId); //客户方的corpId
        reqObject.put("limit", 1000);
        if (!Check.Null(cursor)) {
            reqObject.put("cursor", cursor);  //用于分页查询的游标，字符串类型，由上一次调用返回，首次调用可不填
        }

        HelpTools.writelog_fileName("企业微信服务商获取企业用户的账户列表请求:"+ reqObject +" ", "isvwecom");

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");

        //40014 providerAccessToken失效，需要再次调用
        if (errcode==40014){
            providerAccessToken = this.getProviderAccessToken(dao,true);
            url="https://qyapi.weixin.qq.com/cgi-bin/license/list_actived_account?provider_access_token="+providerAccessToken;
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
        }

        HelpTools.writelog_fileName("企业微信服务商获取企业用户的账户列表返回:"+ res +" ", "isvwecom");

        if (errcode==0){
            return res;
        }

        return "";

    }

}
