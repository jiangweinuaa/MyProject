package com.dsc.spos.service.imp.json;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.json.cust.req.DCP_GetEnterpriseChatUserInfoReq;
import com.dsc.spos.json.cust.res.DCP_GetEnterpriseChatUserInfoRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.wxcp.WxCpServiceUtils;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import me.chanjar.weixin.cp.bean.WxCpUser;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class DCP_GetEnterpriseChatUserInfo extends SPosBasicService<DCP_GetEnterpriseChatUserInfoReq, DCP_GetEnterpriseChatUserInfoRes> {

    @Override
    public String execute(String json) throws Exception {
        // Check
        com.alibaba.fastjson.JSONObject fastReqJson = com.alibaba.fastjson.JSONObject.parseObject(json, Feature.OrderedField);
        Assert.isTrue(fastReqJson.get("request") != null, "request不能为空");
        String reqJsonStr = fastReqJson.get("request").toString();
        DCP_GetEnterpriseChatUserInfoReq.levelRequest request = JSON.parseObject(reqJsonStr, DCP_GetEnterpriseChatUserInfoReq.levelRequest.class);
        Assert.isTrue(StrUtil.isNotEmpty(request.getAppType()), "appType不可为空值");
        Assert.isTrue(StrUtil.isNotEmpty(request.getCode()), "code不可为空值");
        DCP_GetEnterpriseChatUserInfoRes res = new DCP_GetEnterpriseChatUserInfoRes();
        res.setSuccess(false);

        WxCpServiceUtils.WxCpConfig wxCpConfig = WxCpServiceUtils.getWxCpConfigFromDB();
        if (wxCpConfig == null) {
            res.setServiceDescription("未查询到企业微信接入配置，请检查配置");
            return JSON.toJSONString(res);
        }

        WxCpServiceUtils.WxCpLoginLog log = new WxCpServiceUtils.WxCpLoginLog();
        log.setEId(wxCpConfig.getEId());
        log.setAppType(request.getAppType());
        log.setCorpId(wxCpConfig.getCorpId());
        log.setAgentId(wxCpConfig.getAgentId());
        log.setMachineId(request.getMachineId());
        log.setShopId(request.getShopId());
        log.setLoginTime(DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS"));
        log.setStatus("0");
        String machineCode = request.getMachineCode();
        if (Check.Null(machineCode)){
            machineCode = "";
        }
        log.setMachineCode(machineCode);  //机器码 仅供云POS使用 by jinzma 20210830

        WxCpUser wxCpUser = null;
        try {
            // 根据CODE获取用户信息
            WxCpService wxCpService = WxCpServiceUtils.getWxCpService(Integer.parseInt(wxCpConfig.getAgentId()));
            WxCpOauth2UserInfo userInfo = wxCpService.getOauth2Service().getUserInfo(request.getCode());
            if (userInfo == null) {
                res.setServiceDescription("授权失败，用户不存在");
                return JSON.toJSONString(res);
            }

            wxCpUser = wxCpService.getUserService().getById(userInfo.getUserId());
            //1.仅用户状态为1.已激活时返回true,否则提示[非企业成员] 或 [企业成员未激活]
            if(wxCpUser.getStatus() != 1){
                res.setServiceDescription("非企业成员或企业成员未激活");
                return JSON.toJSONString(res);
            }

            DCP_GetEnterpriseChatUserInfoRes.Level1Elm datas = new DCP_GetEnterpriseChatUserInfoRes.Level1Elm();
            datas.setUserId(wxCpUser.getUserId());
            datas.setStatus(String.valueOf(wxCpUser.getStatus()));
            log.setStatus("1");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setDatas(datas);
        } finally {
            if (wxCpUser != null) {
                log.setUserId(wxCpUser.getUserId());
                log.setName(wxCpUser.getName());
            }
            WxCpServiceUtils.writeLoginLog(log);
        }

        return JSON.toJSONString(res);
    }

    @Override
    protected boolean isVerifyFail(DCP_GetEnterpriseChatUserInfoReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_GetEnterpriseChatUserInfoReq> getRequestType() {
        return new TypeToken<DCP_GetEnterpriseChatUserInfoReq>() {
        };
    }

    @Override
    protected DCP_GetEnterpriseChatUserInfoRes getResponseType() {
        return new DCP_GetEnterpriseChatUserInfoRes();
    }

    @Override
    protected DCP_GetEnterpriseChatUserInfoRes processJson(DCP_GetEnterpriseChatUserInfoReq req) throws Exception {
        return null;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_GetEnterpriseChatUserInfoReq req) throws Exception {
        return null;
    }
}