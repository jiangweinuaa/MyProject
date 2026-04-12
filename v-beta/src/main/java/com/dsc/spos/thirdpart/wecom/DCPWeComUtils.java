package com.dsc.spos.thirdpart.wecom;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.thirdpart.wecom.aes.WXBizJsonMsgCrypt;
import com.dsc.spos.thirdpart.wecom.entity.*;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.json.JSONObject;
import org.json.XML;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：DcpIsvWeComUtils
 * 服务说明：企业微信客户端通用方法
 * @author jinzma
 * @since  2024-01-22
 */
public class DCPWeComUtils {
    //企业凭证
    //public static Map<String,Object> accessTokenMap = new HashMap<>() ;

    //获取企业应用凭证accessToken
    public String getAccessToken(DsmDAO dao,boolean isUpdate) throws Exception{
        try {
            RedisPosPub redis = new RedisPosPub();
            String tokenKey = "DcpIsvWeCom_Token";
            String token = redis.getString(tokenKey);
            if (!Check.Null(token) && !isUpdate) {
                return token;
            }

            //获取access_token
            String sql = "select corpid,permanentcode from dcp_isvwecom_empower " ;
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                String corpId = getQData.get(0).get("CORPID").toString();
                String permanentCode = getQData.get(0).get("PERMANENTCODE").toString();
                //获取企业凭证
                String url="https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+corpId+"&corpsecret="+permanentCode;
                String res = HttpSend.Sendhttp("GET", null, url);

                JSONObject resObject=new JSONObject(res);
                int errcode = resObject.optInt("errcode");
                String errmsg = resObject.optString("errmsg");
                if (errcode==0 && "ok".equals(errmsg)) {
                    String access_token = resObject.optString("access_token");
                    int expires_in = resObject.optInt("expires_in");  //秒
                    if (expires_in > 0){
                        expires_in = expires_in - 60 ; //失效时间减少60秒（从获取token到发起请求有一个时间差，避免token失效）
                    }

                    redis.setEx(tokenKey,expires_in,access_token);

                    return access_token;
                } else {
                    HelpTools.writelog_fileName("企微获取access_token失败,原因:"+errmsg+" ", "dcpisvwecom");
                    return null;
                }
            } else {
                HelpTools.writelog_fileName("企微获取access_token失败,原因:dcp_isvwecom_empower表未维护 ", "dcpisvwecom");
                return null;
            }

        } catch(Exception e) {
            HelpTools.writelog_fileName("企业微信获取access_token失败,原因:"+ e.getMessage()+" ", "dcpisvwecom");
            return null;
        }
    }

    //url回调地址验证(验证客户环境的)
    public String dcpVerify(DsmDAO dao,String sVerifyMsgSig,String sVerifyTimeStamp,String sVerifyNonce,String sVerifyEchoStr) throws IOException {
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
            HelpTools.writelog_fileName("企业微信应用url地址验证 msg_signature=" + sVerifyMsgSig + ",timestamp=" + sVerifyTimeStamp + ","
                    + "nonce=" + sVerifyNonce + ",echostr =" + sVerifyEchoStr + " ", "dcpisvwecom");

            String sql =" select * from dcp_isvwecom_empower " ;
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (CollectionUtils.isEmpty(getQData)) {
                HelpTools.writelog_fileName("企业微信url地址验证失败: " + "dcp_isvwecom_empower 缺失资料" + " ", "dcpisvwecom");
                return null;
            }

            String corpId = getQData.get(0).get("CORPID").toString();
            String token = getQData.get(0).get("TOKEN").toString();
            String encodingAESKey = getQData.get(0).get("ENCODINGAESKEY").toString();

            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(token, encodingAESKey, corpId);

            try {
                String sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sVerifyEchoStr);  //需要返回的明文
                HelpTools.writelog_fileName("企业微信url地址验证 sEchoStr=" + sEchoStr + " ", "dcpisvwecom");

                return sEchoStr;
            } catch (Exception e) {
                HelpTools.writelog_fileName("企业微信url地址验证失败: " + e.getMessage() + " ", "dcpisvwecom");
                return null;
            }
        } catch (Exception e) {
            HelpTools.writelog_fileName("企业微信url地址验证失败: " + e.getMessage() + " ", "dcpisvwecom");
            return null;
        }
    }

    //手机号获取userid https://developer.work.weixin.qq.com/document/path/91693
    public String getUserid(DsmDAO dao,String mobile) throws Exception{
        String accessToken = this.getAccessToken(dao,false);
        String url="https://qyapi.weixin.qq.com/cgi-bin/user/getuserid?access_token=";
        JSONObject reqObject = new JSONObject();
        reqObject.put("mobile", mobile);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            //{"errcode":0,"errmsg":"ok","userid":"woONs8DQAAWkTZJuZt15b2fxnGXoppBQ"}
            return resObject.opt("userid").toString();
        }

        return "";
    }

    //获取企业标签库 get_corp_tag_list https://developer.work.weixin.qq.com/document/path/92696
    public JSONObject getCorpTagList(DsmDAO dao) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_corp_tag_list?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        String res = HttpSend.Sendhttp("POST", "", url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");
        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", "", url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容: ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            //企微返回：{"errcode":0,"errmsg":"ok","tag_group":[{"group_id":"TAG_GROUPID1","group_name":"GOURP_NAME","create_time":1557838797,"order":1,"deleted":false,"tag":[{"id":"TAG_ID1","name":"NAME1","create_time":1557838797,"order":1,"deleted":false},{"id":"TAG_ID2","name":"NAME2","create_time":1557838797,"order":2,"deleted":true}]}]}
            return resObject;
        }

        return null;
    }

    //添加企业客户标签 add_corp_tag  https://developer.work.weixin.qq.com/document/path/92696
    public JSONObject addCorpTag(DsmDAO dao,JSONObject reqObject) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_corp_tag?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");
        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            //企微返回参考 "errcode":0,"errmsg":"ok","tag_group":{"group_id":"TAG_GROUPID1","group_name":"GOURP_NAME","create_time":1557838797,"order":1,"tag":[{"id":"TAG_ID1","name":"NAME1","create_time":1557838797,"order":1},{"id":"TAG_ID2","name":"NAME2","create_time":1557838797,"order":2}]}
            return resObject;
        }

        return null;
    }

    //编辑企业客户标签 edit_corp_tag  https://developer.work.weixin.qq.com/document/path/92696
    public JSONObject editCorpTag(DsmDAO dao,JSONObject reqObject) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/edit_corp_tag?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        //需要针对不同的返回错误进行解析和提示
        return resObject;

        /*if (errcode==0){
            //企微返回： {"errcode":0,"errmsg":"ok"}
            return resObject;
        }

        return null;*/
    }

    //删除企业客户标签 del_corp_tag  https://developer.work.weixin.qq.com/document/path/92696
    public JSONObject delCorpTag(DsmDAO dao,JSONObject reqObject) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/del_corp_tag?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        //需要针对不同的返回错误进行解析和提示
        return resObject;

    }

    //批量获取客户详情  https://developer.work.weixin.qq.com/document/path/93010
    public ExternaContactBatch getExternalContactBatch (DsmDAO dao,String[] userid_list) throws Exception {
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/batch/get_by_user?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        JSONObject reqObject = new JSONObject();
        reqObject.put("userid_list",userid_list);
        reqObject.put("limit",100);      //每次多少笔


        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");
        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", "", url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){

            ParseJson pj = new ParseJson();
            ExternaContactBatch externaContactBatchReturn = new ExternaContactBatch();
            externaContactBatchReturn.setExternal_contact_list(new ArrayList<>());
            while (true){

                ExternaContactBatch externaContactBatchReturn_RES = pj.jsonToBean(resObject.toString(),new TypeToken<ExternaContactBatch>(){});
                externaContactBatchReturn.getExternal_contact_list().addAll(externaContactBatchReturn_RES.getExternal_contact_list());

                String next_cursor = resObject.optString("next_cursor","");
                if (Check.Null(next_cursor)){
                    break;
                }

                //查下一笔
                reqObject.put("cursor",next_cursor);
                res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

                HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");
                resObject = new JSONObject(res);
                errcode = resObject.optInt("errcode");
                if (errcode!=0){
                    break;
                }
            }

            return externaContactBatchReturn;
        }

        return null;


    }

    //获取客户详情  https://developer.work.weixin.qq.com/document/path/96315
    public ExternaContact getExternalContact (DsmDAO dao,String external_userid) throws Exception {
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        String res = HttpSend.Sendhttp("POST", "", url+accessToken+"&external_userid="+external_userid);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", "", url+accessToken+"&external_userid="+external_userid);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+"&external_userid="+external_userid+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容: ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){

            ParseJson pj = new ParseJson();
            ExternaContact externaContact = pj.jsonToBean(resObject.toString(),new TypeToken<ExternaContact>(){});

            return externaContact;
        }

        return null;

    }

    //编辑客户企业标签 https://developer.work.weixin.qq.com/document/path/96322
    public String markTag(DsmDAO dao,String userid,String external_userid,String[]add_tag,String[]remove_tag) throws Exception {
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/mark_tag?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("userid",userid);
        reqObject.put("external_userid",external_userid);
        reqObject.put("add_tag",add_tag);
        reqObject.put("remove_tag",remove_tag);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            return "";
        }else{
            return resObject.optString("errmsg","");
        }

    }

    //客户或客户群变更回调 （不清楚后续是否都走这一个回调)  https://developer.work.weixin.qq.com/document/path/96360
    public void callBackProcess(DsmDAO dao,String reqJson,String msg_signature,String timestamp,String nonce) throws Exception {
        try {
            JSONObject reqJsonObj = new JSONObject(reqJson);
            String ToUserName = reqJsonObj.optString("ToUserName");  //CORPID 授权方企业微信ID
            String Encrypt = reqJsonObj.optString("Encrypt");
            String AgentID = reqJsonObj.optString("AgentID");

            String sql =" select * from dcp_isvwecom_empower where corpid='"+ToUserName+"' " ;
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (CollectionUtils.isEmpty(getQData)) {
                HelpTools.writelog_fileName("企业微信回调失败:dcp_isvwecom_empower 找不到对应的corpid:" +ToUserName+" ", "dcpisvwecom");
            }

            String token = getQData.get(0).get("TOKEN").toString();
            String encodingAESKey = getQData.get(0).get("ENCODINGAESKEY").toString();
            String eId = getQData.get(0).get("EID").toString();

            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(token, encodingAESKey, ToUserName);
            String sMsg = wxcpt.DecryptMsg(msg_signature,timestamp,nonce,reqJson);
            //HelpTools.writelog_fileName("企业微信回调明文XML: " + sMsg + " ", "dcpisvwecom");


            //xml2json 这个转换有问题，无法转换XML里面的数组，严重BUG by jinzma 20240206
            //String xml2json = XmlAndJsonConvert.xml2json(sMsg);
            JSONObject xml2json = XML.toJSONObject(sMsg);
            JSONObject sMsgJson = xml2json.optJSONObject("xml");
            HelpTools.writelog_fileName("企业微信回调明文: " + sMsgJson + " ", "dcpisvwecom");


            DCPWeComService dcpWeComService = new DCPWeComService();
            //解析回调
            String Event = sMsgJson.optString("Event");                 //事件的类型
            String ChangeType = sMsgJson.optString("ChangeType");       //变更类型

            //企业客户事件回调
            if ("change_external_contact".equals(Event)){
                switch (ChangeType){
                    case "add_external_contact":      //添加企业客户事件---内部员工添加客人，推送欢迎语，调企微客户详情，客人已存在则刷新客户详情
                        dcpWeComService.ExternalContactAdd(dao,eId,sMsgJson);
                        break;
                    case "edit_external_contact":     //编辑企业客户事件---调企微客户详情，刷新客户详情
                        dcpWeComService.ExternalContactEdit(dao,eId,sMsgJson);
                        break;
                    case "add_half_external_contact": //外部联系人免验证添加成员事件---客人添加员工，推送欢迎语，调企微客户详情，客人已存在则刷新客户详情
                        dcpWeComService.ExternalContactAddHalf(dao,eId,sMsgJson);
                        break;
                    case "del_external_contact":      //删除企业客户事件---员工删除了客人，调企微客户详情，更新客户信息，如果该客人流失了，则变更本地客人状态为流失
                        dcpWeComService.ExternalContactDelExternal(dao,eId,sMsgJson);
                        break;
                    case "del_follow_user":          //删除跟进成员事件---客人删除了员工，调企微客户详情，更新客户信息，如果该客人流失了，则变更本地客人状态为流失，根据配置，是否推送信息通知被删员工
                        dcpWeComService.ExternalContactDelFollow(dao,eId,sMsgJson);
                        break;
                    case "transfer_fail":           //客户接替失败事件---企业将客户分配给新的成员接替后，客户添加失败时回调该事件
                        dcpWeComService.ExternalContactTransferFail(dao,eId,sMsgJson);
                        break;
                }
            }

            //企业客户群事件回调
            if ("change_external_chat".equals(Event)){
                switch (ChangeType){
                    case "create":       //客户群创建事件---有新增客户群时，回调该事件。收到该事件后，企业可以调用获取客户群详情接口获取客户群详情。
                        dcpWeComService.ExternalChatCreate(dao,eId,sMsgJson);
                        break;
                    case "update":       //客户群变更事件---客户群被修改后（群名变更，群成员增加或移除，群主变更，群公告变更），回调该事件。收到该事件后，企业需要再调用获取客户群详情接口，以获取最新的群详情。
                        dcpWeComService.ExternalChatUpdate(dao,eId,sMsgJson);
                        break;
                    case "dismiss":     //客户群解散事件---当客户群被群主解散后，回调该事件。 需注意的是，如果发生群信息变动，会立即收到此事件，但是部分信息是异步处理，可能需要等一段时间(例如2秒)调用获取客户群详情接口才能得到最新结果
                        dcpWeComService.ExternalChatDismiss(dao,eId,sMsgJson);
                        break;
                }
            }

            //企业客户标签事件回调
            if ("change_external_tag".equals(Event)){
                switch (ChangeType){
                    case "create":       //企业客户标签创建事件---企业/管理员创建客户标签/标签组时（包括规则组的标签），回调此事件。收到该事件后，企业需要调用获取企业标签库来获取标签/标签组的详细信息。
                        dcpWeComService.ExternalTagCreate(dao,eId,sMsgJson);
                        break;
                    case "update":       //企业客户标签变更事件---当企业客户标签/标签组（包括规则组的标签）被修改时，回调此事件。收到该事件后，企业需要调用获取企业标签库来获取标签/标签组的详细信息。
                        dcpWeComService.ExternalTagUpdate(dao,eId,sMsgJson);
                        break;
                    case "delete":     //企业客户标签删除事件---当企业客户标签/标签组被删除时，回调此事件。删除标签组时，该标签组下的所有标签将被同时删除，但不会进行回调。
                        dcpWeComService.ExternalTagDelete(dao,eId,sMsgJson);
                        break;
                    case "shuffle":     //企业客户标签重排事件---当企业管理员在终端/管理端调整标签顺序时，可能导致标签顺序整体调整重排，引起大部分标签的order值发生变化，此时会回调此事件，收到此事件后企业应尽快全量同步标签的order值，防止后续调用接口排序出现非预期结果。
                        dcpWeComService.ExternalTagShuffle(dao,eId,sMsgJson);
                        break;
                }
            }


        } catch (Exception e) {
            HelpTools.writelog_fileName("企业微信回调异常: " + e.getMessage() + " ", "dcpisvwecom");
        }
    }

    //获取客户群列表 https://developer.work.weixin.qq.com/document/path/96337
    //带离职员工查询才会返回 离职的群 （离职待继承、离职继承中、离职继承完成）
    //不带员工查询不会返回离职的群 （仅指定范围不清楚是否会返回离职的群，未测）
    public GroupChatList getGroupChatList (DsmDAO dao, int status_filter, String[] userid_list) throws Exception {
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/list?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("status_filter", status_filter);  //客户群跟进状态过滤。0 - 所有列表(即不过滤) 1 - 离职待继承2 - 离职继承中 3 - 离职继承完成 默认为0
        //群主过滤。如果不填，表示获取应用可见范围内全部群主的数据（但是不建议这么用，如果可见范围人数超过1000人，为了防止数据包过大，会报错 81017）
        //和安驰确认了，如果传入，每次只会给一个客户群
        if (userid_list!=null && userid_list.length>0){
            JSONObject user = new JSONObject();
            user.put("userid_list",userid_list);
            reqObject.put("owner_filter",user);
        }
        reqObject.put("limit", 1000);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject, "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            ParseJson pj = new ParseJson();
            GroupChatList groupChatListReturn = new GroupChatList();
            groupChatListReturn.setGroup_chat_list(new ArrayList<>());

            while (true){
                GroupChatList groupChatList_RES = pj.jsonToBean(resObject.toString(),new TypeToken<GroupChatList>(){});
                groupChatListReturn.getGroup_chat_list().addAll(groupChatList_RES.getGroup_chat_list());

                String next_cursor = resObject.optString("next_cursor","");
                if (Check.Null(next_cursor)){
                    break;
                }

                //查下一笔
                reqObject.put("cursor",next_cursor);
                res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

                HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");
                resObject = new JSONObject(res);
                errcode = resObject.optInt("errcode");
                if (errcode!=0){
                    break;
                }
            }

            return groupChatListReturn;

        }

        return null;


    }

    //获取客户群详情 https://developer.work.weixin.qq.com/document/path/96338
    //群主离职未分配的，企微不会返回详情
    public GroupChatDetail getGroupChat (DsmDAO dao,String chat_id) throws Exception {
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/get?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("chat_id", chat_id);
        reqObject.put("need_name", 1);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject, "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            ParseJson pj = new ParseJson();
            GroupChatDetail groupChatDetail = pj.jsonToBean(resObject.toString(),new TypeToken<GroupChatDetail>(){});
            return groupChatDetail;
        }

        return null;


    }

    //发送新客户欢迎语 https://developer.work.weixin.qq.com/document/path/96356
    public boolean send_welcome_msg(DsmDAO dao, WelcomeMsg welcomeMsg) throws Exception {

        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/send_welcome_msg?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        ParseJson pj = new ParseJson();
        String reqJson = pj.beanToJson(welcomeMsg);

        String res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqJson +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            return true;
        }else {
            return false;
        }

    }

    //上传临时素材 https://developer.work.weixin.qq.com/document/path/96484  type: 媒体文件类型，分别有图片（image）、语音（voice）、视频（video），普通文件（file）
    public String upload(DsmDAO dao,String type,File file) throws Exception {
        String media_id = "";
        String accessToken = this.getAccessToken(dao,false);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token="+accessToken+"&type="+type;  //这个上传只调一次，此处url直接拼接

        OutputStream outputStream = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConn = null;

        try {
            //1.建立连接
            httpUrlConn = (HttpURLConnection) new URL(url).openConnection();

            //1.1输入输出设置
            httpUrlConn.setDoInput(true);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setUseCaches(false); // post方式不能使用缓存
            //1.2设置请求头信息
            httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConn.setRequestProperty("Charset", "UTF-8");

            //1.3设置边界
            String BOUNDARY = "----------" + System.currentTimeMillis();
            httpUrlConn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);


            // 请求正文信息
            // 第一部分：
            //2.将文件头输出到微信服务器
            StringBuffer sb = new StringBuffer();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"media\";filename=\"" + file.getName() + "\"; filelength="+file.length()+"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");

            byte[] head = sb.toString().getBytes("utf-8");
            // 获得输出流
            outputStream = new DataOutputStream(httpUrlConn.getOutputStream());
            // 将表头写入输出流中：输出表头
            outputStream.write(head);

            //3.将文件正文部分输出到微信服务器
            // 把文件以流文件的方式 写入到微信服务器中

            byte[] byteGet;
            try(FileInputStream fileInputStream = new FileInputStream(file)){
                byteGet = new byte[(int) file.length()];
                fileInputStream.read(byteGet);
            }catch (IOException e){
                HelpTools.writelog_fileName("企微上传临时素材异常: " + e.getMessage() + " ", "dcpisvwecom");
                return "";
            }

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(byteGet));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                outputStream.write(bufferOut, 0, bytes);
            }
            in.close();
            //4.将结尾部分输出到微信服务器
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes(StandardCharsets.UTF_8);// 定义最后数据分隔线
            outputStream.write(foot);
            outputStream.flush();

            //5.将微信服务器返回的输入流转换成字符串
            inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

            HelpTools.writelog_fileName("企微请求URL:"+url, "dcpisvwecom");
            HelpTools.writelog_fileName("企微请求内容:"+" ", "dcpisvwecom");
            HelpTools.writelog_fileName("企微返回内容:"+ buffer +" ", "dcpisvwecom");

            if (!Check.Null(buffer.toString())) {
                JSONObject resObject = new JSONObject(buffer.toString());
                int errcode = resObject.optInt("errcode");
                String errmsg = resObject.optString("errmsg");

                if (errcode == 0 && "ok".equals(errmsg)) {
                    media_id = resObject.optString("media_id");
                }
            }


        } catch (IOException e) {
            HelpTools.writelog_fileName("企微上传临时素材异常: " + e.getMessage() + " ", "dcpisvwecom");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }

        return media_id;
    }

    //上传永久图片 https://developer.work.weixin.qq.com/document/path/96485
    public String upLoadImg(DsmDAO dao, File file) throws Exception {

        OutputStream outputStream = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConn = null;
        String accessToken = this.getAccessToken(dao,false);

        try {
            String url="https://qyapi.weixin.qq.com/cgi-bin/media/uploadimg?access_token=";

            //1.建立连接
            httpUrlConn = (HttpURLConnection) new URL(url+accessToken).openConnection();

            //1.1输入输出设置
            httpUrlConn.setDoInput(true);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setUseCaches(false); // post方式不能使用缓存
            //1.2设置请求头信息
            httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConn.setRequestProperty("Charset", "UTF-8");
            //1.3设置边界
            String BOUNDARY = "----------" + System.currentTimeMillis();
            httpUrlConn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);

            // 请求正文信息
            // 第一部分：
            //2.将文件头输出到微信服务器
            StringBuffer sb = new StringBuffer();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"image\";filename=\"" + file.getName() + "\"\r\n");
            sb.append("Content-Type:image/png\r\n\r\n");
            sb.append("Content-Length:"+file.length()+"\r\n\r\n");

            byte[] head = sb.toString().getBytes("utf-8");
            // 获得输出流
            outputStream = new DataOutputStream(httpUrlConn.getOutputStream());
            // 将表头写入输出流中：输出表头
            outputStream.write(head);

            //3.将文件正文部分输出到微信服务器
            // 把文件以流文件的方式 写入到微信服务器中

            byte[] byteGet;
            try(FileInputStream fileInputStream = new FileInputStream(file)){
                byteGet = new byte[(int) file.length()];
                fileInputStream.read(byteGet);
            }catch (IOException e){
                HelpTools.writelog_fileName("企微上传图片异常:"+e.getMessage()+" ", "dcpisvwecom");
                return "";
            }

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(byteGet));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                outputStream.write(bufferOut, 0, bytes);
            }
            in.close();
            //4.将结尾部分输出到微信服务器
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes(StandardCharsets.UTF_8);// 定义最后数据分隔线
            outputStream.write(foot);
            outputStream.flush();

            //5.将微信服务器返回的输入流转换成字符串
            inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

            HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
            HelpTools.writelog_fileName("企微请求内容:"+ "" +" ", "dcpisvwecom");
            HelpTools.writelog_fileName("企微返回内容:"+ buffer +" ", "dcpisvwecom");

            if (!Check.Null(buffer.toString())) {
                JSONObject resObject = new JSONObject(buffer.toString());
                int errcode = resObject.optInt("errcode");
                String errmsg = resObject.optString("errmsg");
                if (errcode == 0 && "ok".equals(errmsg)) {
                    return resObject.optString("url");
                }
            }

        } catch (IOException e) {
            HelpTools.writelog_fileName("企微上传图片异常:"+e.getMessage()+" ", "dcpisvwecom");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }

        return "";

    }

    //上传附件资源 https://developer.work.weixin.qq.com/document/path/96347
    public String upload_attachment(DsmDAO dao,String media_type,String attachment_type, File file) throws Exception {
        String media_id = "";
        String accessToken = this.getAccessToken(dao,false);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload_attachment?access_token="+accessToken+"&media_type="+media_type+"&attachment_type="+attachment_type;  //这个上传只调一次，此处url直接拼接

        OutputStream outputStream = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConn = null;

        try {
            //1.建立连接
            httpUrlConn = (HttpURLConnection) new URL(url).openConnection();

            //1.1输入输出设置
            httpUrlConn.setDoInput(true);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setUseCaches(false); // post方式不能使用缓存
            //1.2设置请求头信息
            httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConn.setRequestProperty("Charset", "UTF-8");

            //1.3设置边界
            String BOUNDARY = "----------" + System.currentTimeMillis();
            httpUrlConn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);


            // 请求正文信息
            // 第一部分：
            //2.将文件头输出到微信服务器
            StringBuffer sb = new StringBuffer();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"media\";filename=\"" + file.getName() + "\"; filelength="+file.length()+"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");

            byte[] head = sb.toString().getBytes("utf-8");
            // 获得输出流
            outputStream = new DataOutputStream(httpUrlConn.getOutputStream());
            // 将表头写入输出流中：输出表头
            outputStream.write(head);

            //3.将文件正文部分输出到微信服务器
            // 把文件以流文件的方式 写入到微信服务器中

            byte[] byteGet;
            try(FileInputStream fileInputStream = new FileInputStream(file)){
                byteGet = new byte[(int) file.length()];
                fileInputStream.read(byteGet);
            }catch (IOException e){
                HelpTools.writelog_fileName("企微上传临时素材异常: " + e.getMessage() + " ", "dcpisvwecom");
                return "";
            }

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(byteGet));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                outputStream.write(bufferOut, 0, bytes);
            }
            in.close();
            //4.将结尾部分输出到微信服务器
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes(StandardCharsets.UTF_8);// 定义最后数据分隔线
            outputStream.write(foot);
            outputStream.flush();

            //5.将微信服务器返回的输入流转换成字符串
            inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

            HelpTools.writelog_fileName("企微请求URL:"+url, "dcpisvwecom");
            HelpTools.writelog_fileName("企微请求内容:"+" ", "dcpisvwecom");
            HelpTools.writelog_fileName("企微返回内容:"+ buffer +" ", "dcpisvwecom");

            if (!Check.Null(buffer.toString())) {
                JSONObject resObject = new JSONObject(buffer.toString());
                int errcode = resObject.optInt("errcode");
                String errmsg = resObject.optString("errmsg");

                if (errcode == 0 && "ok".equals(errmsg)) {
                    media_id = resObject.optString("media_id");
                }
            }


        } catch (IOException e) {
            HelpTools.writelog_fileName("企微上传临时素材异常: " + e.getMessage() + " ", "dcpisvwecom");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }

        return media_id;
    }

    //unionid与external_userid的关联   https://developer.work.weixin.qq.com/document/path/97108  unionid转换为第三方external_userid
    public JSONObject unionid_to_external_userid (DsmDAO dao,String unionid,String openid) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/idconvert/unionid_to_external_userid?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        JSONObject reqObject = new JSONObject();
        reqObject.put("unionid", unionid);
        reqObject.put("openid", openid);
        reqObject.put("subject_type", 0); //小程序或公众号的主体类型：0表示主体名称是企业的 (默认)，1表示主体名称是服务商的

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            //｛"errcode":0,"errmsg":"ok","external_userid":"ooAAAAAAAAAAA","pending_id":"ooBBBBBB"｝
            return resObject;
        }

        return null;
    }

    //unionid与external_userid的关联   https://developer.work.weixin.qq.com/document/path/97108  external_userid查询pending_id
    public ExternalUseridToPending external_userid_to_pending_id (DsmDAO dao, String[] external_userid) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/idconvert/batch/external_userid_to_pending_id?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        JSONObject reqObject = new JSONObject();
        reqObject.put("external_userid", external_userid);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            ParseJson pj = new ParseJson();
            ExternalUseridToPending result = pj.jsonToBean(resObject.toString(),new TypeToken<ExternalUseridToPending>(){});

            return result;
        }

        return null;
    }

    //获取「联系客户统计」数据  https://developer.work.weixin.qq.com/document/path/96358
    public UserBehaviorData getUserBehaviorData (DsmDAO dao,String userId,String date) throws Exception{

        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_user_behavior_data?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        Date start_time = new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse(date+" 00:00:00");   //2024-02-25 00:00:00
        Date end_time = new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse(PosPub.GetStringDate(date,1)+" 00:00:00"); //2024-02-26 00:00:00

        JSONObject reqObject = new JSONObject();
        reqObject.put("userid", new String[]{userId});
        reqObject.put("start_time", start_time.getTime()/1000);
        reqObject.put("end_time", end_time.getTime()/1000);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            ParseJson pj = new ParseJson();
            UserBehaviorData userBehaviorData = pj.jsonToBean(resObject.toString(),new TypeToken<UserBehaviorData>(){});

            return userBehaviorData;
        }

        return null;

    }

    //配置客户联系「联系我」方式 https://developer.work.weixin.qq.com/document/path/96348
    public JSONObject add_contact_way (DsmDAO dao,ContactWay contactWay) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_contact_way?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        ParseJson pj = new ParseJson();
        String reqJson = pj.beanToJson(contactWay);
        String res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqJson +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            //{"errcode": 0, "errmsg": "ok", "config_id":"42b34949e138eb6e027c123cba77fAAA", "qr_code":"http://p.qpic.cn/wwhead/duc2TvpEgSdicZ9RrdUtBkv2UiaA/0"}
            return resObject;
        }

        return null;

    }

    //更新企业已配置的「联系我」方式 https://developer.work.weixin.qq.com/document/path/96348
    public boolean update_contact_way (DsmDAO dao,ContactWay contactWay) throws Exception{

        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/update_contact_way?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        ParseJson pj = new ParseJson();
        String reqJson = pj.beanToJson(contactWay);
        String res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqJson +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        return errcode == 0 && "ok".equals(errmsg);

    }

    //删除企业已配置的「联系我」方式 https://developer.work.weixin.qq.com/document/path/96348
    public boolean del_contact_way (DsmDAO dao,String config_id)  throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/del_contact_way?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("config_id", config_id);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject, "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        return errcode == 0 && "ok".equals(errmsg);
    }

    //配置客户群「加入群聊」管理  https://developer.work.weixin.qq.com/document/path/92229
    public String add_join_way (DsmDAO dao,JoinWay joinWay) throws Exception{
        String config_id = "";

        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/add_join_way?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        ParseJson pj = new ParseJson();
        String reqJson = pj.beanToJson(joinWay);
        String res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqJson +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            config_id = resObject.optString("config_id");
        }
        return config_id;

    }

    //获取客户群「加入群聊」管理  https://developer.work.weixin.qq.com/document/path/92229
    public String get_join_way (DsmDAO dao,String config_id) throws Exception{
        String qr_code="";
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/get_join_way?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("config_id", config_id);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject, "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            JSONObject join_way = resObject.getJSONObject("join_way");
            if (join_way!=null) {
                qr_code = join_way.optString("qr_code","");
            }
        }
        return qr_code;
    }

    //更新客户群「加入群聊」管理  https://developer.work.weixin.qq.com/document/path/92229
    public boolean update_join_way (DsmDAO dao,JoinWay joinWay) throws Exception{

        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/update_join_way?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        ParseJson pj = new ParseJson();
        String reqJson = pj.beanToJson(joinWay);
        String res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqJson +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");


        return errcode == 0 && "ok".equals(errmsg);
    }

    //删除客户群「加入群聊」管理  https://developer.work.weixin.qq.com/document/path/92229
    public boolean del_join_way (DsmDAO dao,String config_id) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/del_join_way?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("config_id", config_id);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject, "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        return errcode == 0 && "ok".equals(errmsg);
    }

    //创建企业群发 https://developer.work.weixin.qq.com/document/path/96366
    public JSONObject add_msg_template (DsmDAO dao,MsgTemplate msgTemplate) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_msg_template?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        ParseJson pj = new ParseJson();
        String reqJson = pj.beanToJson(msgTemplate);
        String res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqJson +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            return resObject;
        }

        return null;
    }

    //提醒成员群发 https://developer.work.weixin.qq.com/document/path/97618
    public boolean remind_groupmsg_send (DsmDAO dao,String msgId) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/remind_groupmsg_send?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("msgid", msgId);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject, "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        return errcode == 0 && "ok".equals(errmsg);
    }

    //停止企业群发 https://developer.work.weixin.qq.com/document/path/97619
    public boolean cancel_groupmsg_send (DsmDAO dao,String msgId) throws Exception{
        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/cancel_groupmsg_send?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("msgid", msgId);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject, "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        return errcode == 0 && "ok".equals(errmsg);
    }

    //获取群发成员发送任务列表 https://developer.work.weixin.qq.com/document/path/96355
    public GetGroupMsgTask get_groupmsg_task(DsmDAO dao,String msgId) throws Exception{

        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_groupmsg_task?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        JSONObject reqObject = new JSONObject();
        reqObject.put("msgid",msgId);
        reqObject.put("limit",1000);
        reqObject.put("cursor","");

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            ParseJson pj = new ParseJson();
            GetGroupMsgTask getGroupMsgTask = new GetGroupMsgTask();
            getGroupMsgTask.setTask_list(new ArrayList<>());

            while (true){
                GetGroupMsgTask getGroupMsgTask_RES = pj.jsonToBean(resObject.toString(),new TypeToken<GetGroupMsgTask>(){});
                getGroupMsgTask.getTask_list().addAll(getGroupMsgTask_RES.getTask_list());

                String next_cursor = resObject.optString("next_cursor","");
                if (Check.Null(next_cursor)){
                    break;
                }

                //查下一笔
                reqObject.put("cursor",next_cursor);
                res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

                HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");
                resObject = new JSONObject(res);
                errcode = resObject.optInt("errcode");
                if (errcode!=0){
                    break;
                }
            }

            return getGroupMsgTask;

        }

        return null;

    }

    //获取企业群发成员执行结果 https://developer.work.weixin.qq.com/document/path/96355
    public GetGroupMsgSendResult get_groupmsg_send_result(DsmDAO dao,String msgId,String userId) throws Exception{

        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_groupmsg_send_result?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        JSONObject reqObject = new JSONObject();
        reqObject.put("msgid",msgId);
        reqObject.put("userid",userId);
        reqObject.put("limit",1000);
        reqObject.put("cursor","");

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            ParseJson pj = new ParseJson();
            GetGroupMsgSendResult getGroupMsgSendResult = new GetGroupMsgSendResult();
            getGroupMsgSendResult.setSend_list(new ArrayList<>());

            while (true){
                GetGroupMsgSendResult getGroupMsgSendResult_RES = pj.jsonToBean(resObject.toString(),new TypeToken<GetGroupMsgSendResult>(){});
                getGroupMsgSendResult.getSend_list().addAll(getGroupMsgSendResult_RES.getSend_list());

                String next_cursor = resObject.optString("next_cursor","");
                if (Check.Null(next_cursor)){
                    break;
                }

                //查下一笔
                reqObject.put("cursor",next_cursor);
                res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

                HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");
                resObject = new JSONObject(res);
                errcode = resObject.optInt("errcode");
                if (errcode!=0){
                    break;
                }
            }

            return getGroupMsgSendResult;

        }


        return null;

    }

    //企业发表内容到客户的朋友圈 https://developer.work.weixin.qq.com/document/path/96351
    public String add_moment_task(DsmDAO dao,AddMomentTask addMomentTask) throws Exception{

        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_moment_task?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        ParseJson pj = new ParseJson();
        String reqJson = pj.beanToJson(addMomentTask);
        String res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqJson +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            return resObject.optString("jobid");
        }

        return null;


    }

    //停止发表企业朋友圈  https://developer.work.weixin.qq.com/document/path/97615
    public boolean cancel_moment_task(DsmDAO dao,String moment_id) throws Exception{

        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/cancel_moment_task?access_token=";
        String accessToken = this.getAccessToken(dao,false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("moment_id", moment_id);

        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject, "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        return errcode == 0 && "ok".equals(errmsg);

    }

    //获取客户朋友圈企业发表的列表  https://developer.work.weixin.qq.com/document/path/96352
    public GetMomentTask get_moment_task(DsmDAO dao,String moment_id) throws Exception{


        String url="https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_moment_task?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        JSONObject reqObject = new JSONObject();
        reqObject.put("moment_id",moment_id);
        reqObject.put("cursor","");
        reqObject.put("limit",1000);  //返回的最大记录数，整型，最大值1000，默认值500，超过最大值时取默认值



        String res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)){
            ParseJson pj = new ParseJson();
            GetMomentTask getMomentTask = new GetMomentTask();
            getMomentTask.setTask_list(new ArrayList<>());

            while (true){
                GetMomentTask getMomentTask_RES = pj.jsonToBean(resObject.toString(),new TypeToken<GetMomentTask>(){});
                getMomentTask.getTask_list().addAll(getMomentTask_RES.getTask_list());

                String next_cursor = resObject.optString("next_cursor","");
                if (Check.Null(next_cursor)){
                    break;
                }

                //查下一笔
                reqObject.put("cursor",next_cursor);
                res = HttpSend.Sendhttp("POST", reqObject.toString(), url+accessToken);

                HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微请求内容:"+ reqObject +" ", "dcpisvwecom");
                HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");
                resObject = new JSONObject(res);
                errcode = resObject.optInt("errcode");
                if (errcode!=0){
                    break;
                }
            }

            return getMomentTask;

        }


        return null;



    }

    //获取客户朋友圈任务创建结果 https://developer.work.weixin.qq.com/document/path/96351
    public GetMomentTaskResult get_moment_task_result(DsmDAO dao,String jobid) throws Exception{

        String accessToken = this.getAccessToken(dao,false);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_moment_task_result?access_token="+accessToken+"&jobid="+jobid;

        String res = HttpSend.Sendhttp("GET", null, url);
        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_moment_task_result?access_token="+accessToken+"&jobid="+jobid;
            res = HttpSend.Sendhttp("GET", null, url);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url, "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ "", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        if (errcode==0 && "ok".equals(errmsg)) {
            ParseJson pj = new ParseJson();
            GetMomentTaskResult getMomentTaskResult = pj.jsonToBean(resObject.toString(), new TypeToken<GetMomentTaskResult>() {});

            return getMomentTaskResult;
        }

        return null;
    }

    //发送应用消息 https://developer.work.weixin.qq.com/document/path/96458
    public boolean messageSend (DsmDAO dao,MessageSend messageSend) throws Exception{

        String url="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
        String accessToken = this.getAccessToken(dao,false);

        ParseJson pj = new ParseJson();
        String reqJson = pj.beanToJson(messageSend);
        String res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);

        JSONObject resObject=new JSONObject(res);
        int errcode = resObject.optInt("errcode");
        String errmsg = resObject.optString("errmsg");

        //40014 accessToken失效，需要再次调用
        if (errcode==40014){
            accessToken = this.getAccessToken(dao,true);
            res = HttpSend.Sendhttp("POST", reqJson, url+accessToken);
            resObject=new JSONObject(res);
            errcode = resObject.optInt("errcode");
            errmsg = resObject.optString("errmsg");
        }

        HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微请求内容:"+ reqJson +" ", "dcpisvwecom");
        HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

        return errcode == 0 && "ok".equals(errmsg);

    }

    //获取企业的jsapi_ticket https://developer.work.weixin.qq.com/document/path/90506
    public String get_jsapi_ticket (DsmDAO dao) throws Exception {
        try {

            RedisPosPub redis = new RedisPosPub();
            String jsapiTicketKey = "DcpIsvWeCom_JsapiTicket";
            String jsapiTicket = redis.getString(jsapiTicketKey);
            if (!Check.Null(jsapiTicket)) {
                return jsapiTicket;
            }

            String url="https://qyapi.weixin.qq.com/cgi-bin/ticket/get?access_token=";

            String accessToken = this.getAccessToken(dao,false);

            String res = HttpSend.Sendhttp("GET", null, url+accessToken+"&type=agent_config");

            JSONObject resObject=new JSONObject(res);
            int errcode = resObject.optInt("errcode");
            String errmsg = resObject.optString("errmsg");

            //40014 accessToken失效，需要再次调用
            if (errcode==40014){
                accessToken = this.getAccessToken(dao,true);
                res = HttpSend.Sendhttp("GET", null, url+accessToken+"&type=agent_config");
                resObject=new JSONObject(res);
                errcode = resObject.optInt("errcode");
                errmsg = resObject.optString("errmsg");
            }

            HelpTools.writelog_fileName("企微请求URL:"+url+accessToken+"&type=agent_config"+" ", "dcpisvwecom");
            HelpTools.writelog_fileName("企微请求内容:"+ " " +" ", "dcpisvwecom");
            HelpTools.writelog_fileName("企微返回内容:"+ res +" ", "dcpisvwecom");

            if (errcode==0 && "ok".equals(errmsg)){


                String ticket = resObject.optString("ticket");
                int expires_in = resObject.optInt("expires_in");  //秒
                if (expires_in > 0){
                    expires_in = expires_in - 60 ; //失效时间减少60秒（从获取token到发起请求有一个时间差，避免token失效）
                }

                redis.setEx(jsapiTicketKey,expires_in,ticket);
                return resObject.optString("ticket");
            }else {
                HelpTools.writelog_fileName("企业微信获取企业的jsapi_ticket失败,原因:" + errmsg + " ", "dcpisvwecom");
            }
            return null;

        } catch(Exception e) {
            HelpTools.writelog_fileName("企业微信获取企业的jsapi_ticket失败,原因:"+ e.getMessage()+" ", "dcpisvwecom");
            return null;
        }







    }
}
