package com.dsc.spos.waimai.isv;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.waimai.SWaimaiBasicService;
import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.oauth.OAuthClient;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ISV_WMELMClientAuthUrlGetService extends SWaimaiBasicService {

    String logFileName = "ELMClientAuthUrlGetLog";
    @Override
    public String execute(String json) throws Exception {
        if (json == null || json.length() == 0) {
            ISV_HelpTools.writelog_fileName("饿了么客户获取授权URL发送的请求为空！",logFileName);
            return null;
        }
        JSONObject obj = new JSONObject(json);
        String isTest = obj.optString("isTest","");
        String state = obj.optString("state","");
        if (state.trim().isEmpty())
        {
            ISV_HelpTools.writelog_fileName("饿了么客户获取授权URL发送的请求节点state为空！",logFileName);
            return null;
        }
        boolean isSandbox = false;
        if ("Y".equalsIgnoreCase(isTest))
        {
            isSandbox = true;
        }
        if ("ZssNWsz5".equals(state))//鼎捷自己客户标识测试
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
        // 使用config对象，实例化一个授权类
        OAuthClient client = new OAuthClient(config);
        // 根据OAuth2.0中的对应state，scope和callback_url，获取授权URL
        String callback_url = ISV_WMUtils.elm_auth_callback_url;//redirect_uri指的是应用发起请求时，所传的回调地址参数，在用户授权后页面会跳转至redirect_uri。要求与应用注册时填写的授权回调地址一致或域名一致，并且必须支持https访问。需要进行Url Encode编码
        String scope = "all";//商户授权的权限范围，固定为all，表示所有权限
        String authUrl = client.getAuthUrl(callback_url, scope, state);
        ISV_HelpTools.writelog_fileName("饿了么客户获取授权URL，客户唯一标识state="+state+",返回授权url链接："+authUrl,logFileName);
        return authUrl;
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
}
