package com.dsc.spos.service.imp.json;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.json.cust.req.DCP_GetEnterpriseChatLoginInfoReq;
import com.dsc.spos.json.cust.res.DCP_GetEnterpriseChatLoginInfoRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.wxcp.WxCpServiceUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;

public class DCP_GetEnterpriseChatLoginInfo extends SPosBasicService<DCP_GetEnterpriseChatLoginInfoReq, DCP_GetEnterpriseChatLoginInfoRes> {

    Logger logger = LogManager.getLogger(DCP_GetEnterpriseChatLoginInfo.class.getName());

    @Override
    public String execute(String json) throws Exception {
        // CHECK
        com.alibaba.fastjson.JSONObject fastReqJson = com.alibaba.fastjson.JSONObject.parseObject(json, Feature.OrderedField);
        Assert.isTrue(fastReqJson.get("request") != null, "request不能为空");
        String reqJsonStr = fastReqJson.get("request").toString();
        DCP_GetEnterpriseChatLoginInfoReq.levelRequest request = JSON.parseObject(reqJsonStr, DCP_GetEnterpriseChatLoginInfoReq.levelRequest.class);
        Assert.isTrue(StrUtil.isNotEmpty(request.getAppType()), "appType不可为空值");

        // 构建返回对象
        DCP_GetEnterpriseChatLoginInfoRes res = new DCP_GetEnterpriseChatLoginInfoRes();
        res.setSuccess(false);
        DCP_GetEnterpriseChatLoginInfoRes.Level1Elm datas = new DCP_GetEnterpriseChatLoginInfoRes.Level1Elm();
        datas.setOpenEnterpriseChatLogin("0");
        res.setDatas(datas);

        // 未查询到接入配置直接返回，作未启用处理
        WxCpServiceUtils.WxCpConfig config = WxCpServiceUtils.getWxCpConfigFromDB();
        if(config == null){
            res.setServiceDescription("未查询到企业微信接入配置，请检查配置");
            return JSON.toJSONString(res);
        }

        String redirectUrl = request.getRedirectUri();
        String state = request.getAppType();

        //非网页授权登录特殊处理, 回调使用后台地址
        if (!"RETAIL".equalsIgnoreCase(request.getAppType())) {
            // 取参数openEnterpriseChatLogin，是否启用微信登录 0，1（POS需要检查，RETAIL在前端配置)
            String openEnterpriseChatLogin = PosPub.getPARA_SMS(this.dao, config.getEId(), "", "openEnterpriseChatLogin");
            if(StrUtil.isNotEmpty(openEnterpriseChatLogin)){
                datas.setOpenEnterpriseChatLogin(openEnterpriseChatLogin);
            }

            redirectUrl = getCallBackUrl(config.getEId());
            String params =Base64.encode(StrUtil.format("eId={}&agentId={}&appType={}&shopId={}&machineId={}",
                    config.getEId(), config.getAgentId(),request.getAppType(), request.getShopId(), request.getMachineId()), "UTF-8");
            redirectUrl += "?params=" + params;
            state = request.getMachineCode();
        }

        String qrCodeUrl = "https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=" + config.getCorpId() +"&agentid=" + config.getAgentId()
                +"&redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8") +"&state=" + state;
        datas.setAppId(config.getCorpId());
        datas.setAgentId(config.getAgentId());
        datas.setState(state);
        datas.setQrCodeUrl(qrCodeUrl);

        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求内容(appType):"+request.getAppType()+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求内容(shopId):"+request.getShopId()+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求内容(machineId):"+request.getMachineId()+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求内容(redirectUri):"+request.getRedirectUri()+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求内容(machineCode):"+request.getMachineCode()+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求内容(eId):"+request.getEId()+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求返回****************************************\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求返回(appId):"+config.getCorpId()+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求返回(agentId):"+config.getAgentId()+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求返回(state):"+state+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求返回(qrCodeUrl):"+qrCodeUrl+"\r\n");
        logger.info("\r\n*********DCP_GetEnterpriseChatLoginInfo 请求返回(openEnterpriseChatLogin):"+datas.getOpenEnterpriseChatLogin()+"\r\n");

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        res.setDatas(datas);
        return JSON.toJSONString(res);
    }

    /**
     * 构建授权URL
     * @param eId 企业编号
     * @return 回调地址
     * @throws Exception 异常
     */
    public String getCallBackUrl(String eId) throws Exception {
        String platformCentreURL = PosPub.getDCP_URL(eId);
        URI uri = URLUtil.toURI(platformCentreURL);
        String scheme = uri.getScheme();
        String domainName = uri.getHost();
        String path = StrUtil.sub(uri.getPath(), 0, uri.getPath().lastIndexOf("/")) + "/wecom";

        if (uri.getScheme().equals("http")) {
            String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
            if ("1".equals(isHttps)) {
                scheme = "https";
            }
        }

        if (Check.isIP(domainName)) {
            String value = PosPub.getPARA_SMS(this.dao, eId, "", "DomainName");
            if (StrUtil.isNotEmpty(value)) {
                if (value.endsWith("/")) value = value.substring(-1);
                domainName = value;
            }
        }

        return scheme + "://" + domainName + path;
    }

    @Override
    protected boolean isVerifyFail(DCP_GetEnterpriseChatLoginInfoReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_GetEnterpriseChatLoginInfoReq> getRequestType() {
        return new TypeToken<DCP_GetEnterpriseChatLoginInfoReq>() {
        };
    }

    @Override
    protected DCP_GetEnterpriseChatLoginInfoRes getResponseType() {
        return new DCP_GetEnterpriseChatLoginInfoRes();
    }

    @Override
    protected DCP_GetEnterpriseChatLoginInfoRes processJson(DCP_GetEnterpriseChatLoginInfoReq req) throws Exception {
        return null;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_GetEnterpriseChatLoginInfoReq req) throws Exception {
        return null;
    }

    public static void main(String[] args) {
        String platformCentreURL = "http://101.37.33.19/dcpService_3.0/DCP/services/invoke";
        URI uri = URLUtil.toURI(platformCentreURL);
        String contextPath = URLUtil.getPath(platformCentreURL);
        System.out.println(contextPath); // /dcpService_3.0/DCP/services/invoke
        System.out.println(StrUtil.sub(contextPath, 0, contextPath.lastIndexOf("/")));
        System.out.println(uri.getScheme());
    }
}