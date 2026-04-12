package com.dsc.spos.service.imp.json;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.json.cust.req.DCP_GetEnterpriseChatLoginStatusReq;
import com.dsc.spos.json.cust.res.DCP_GetEnterpriseChatLoginStatusRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.wxcp.WxCpServiceUtils;
import com.google.gson.reflect.TypeToken;
import me.chanjar.weixin.cp.bean.WxCpUser;

import java.util.List;
import java.util.Map;

public class DCP_GetEnterpriseChatLoginStatus extends SPosBasicService<DCP_GetEnterpriseChatLoginStatusReq, DCP_GetEnterpriseChatLoginStatusRes> {
    
    @Override
    public String execute(String requestJson) throws Exception {
        com.alibaba.fastjson.JSONObject fastReqJson = com.alibaba.fastjson.JSONObject.parseObject(requestJson, Feature.OrderedField);
        Assert.isTrue(fastReqJson.get("request") != null, "request不能为空");
        
        String reqJsonStr = fastReqJson.get("request").toString();
        DCP_GetEnterpriseChatLoginStatusReq.levelRequest request = JSON.parseObject(reqJsonStr, DCP_GetEnterpriseChatLoginStatusReq.levelRequest.class);
        Assert.isTrue(StrUtil.isNotEmpty(request.getMachineCode()), "machineCode不可为空值");
        
        DCP_GetEnterpriseChatLoginStatusRes res = new DCP_GetEnterpriseChatLoginStatusRes();
        res.setSuccess(false);
        WxCpServiceUtils.WxCpConfig wxCpConfig = WxCpServiceUtils.getWxCpConfigFromDB();
        
        if (wxCpConfig == null) {
            res.setServiceDescription("未查询到企业微信接入配置，请检查配置");
            return JSON.toJSONString(res);
        }
        // 根据机台编号获取授权用户信息
        String key = "WECOM_STATE" + ":" + request.getMachineCode();
        RedisPosPub redisPosPub = new RedisPosPub();
        String json = redisPosPub.getString(key);
        DCP_GetEnterpriseChatLoginStatusRes.Level1Elm datas = new DCP_GetEnterpriseChatLoginStatusRes.Level1Elm();
        if (StrUtil.isEmpty(json)) {
            
            //【ID1025260】【嘉华3.0】升级311后，POS登录，开启企业微信登录参数，未扫码直接进入软件， by jinzma 20220418
            /*
            String sql = " select * from dcp_enterprisechatloginlog where machinecode='"+request.getMachineCode()+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()){
                String userId = getQData.get(0).get("USERID").toString();
                String status = getQData.get(0).get("STATUS").toString();

                if (!Check.Null(status) && status.equals("1")){
                    datas.setUserId(userId);
                    datas.setStatus(status);
                }else{
                    res.setServiceDescription("非企业成员或企业成员未激活");
                    return JSON.toJSONString(res);
                }
            }else{
                res.setServiceDescription("授权失败，CODE已失效，请重新授权");
                return JSON.toJSONString(res);
            }*/
            
            res.setServiceDescription("授权失败，CODE已失效，请重新授权");
            return JSON.toJSONString(res);
            
        }else{
            WxCpServiceUtils.WxCpUserRedisData redisData = JSON.parseObject(json, WxCpServiceUtils.WxCpUserRedisData.class);
            if (redisData.getErrcode() != 0) {
                res.setServiceDescription(redisData.getErrmsg());
                return JSON.toJSONString(res);
            }
            WxCpUser wxCpUser = redisData.getUser();
            if(wxCpUser == null || wxCpUser.getStatus() != 1){
                res.setServiceDescription("非企业成员或企业成员未激活");
                return JSON.toJSONString(res);
            }
            
            datas.setUserId(wxCpUser.getUserId());
            datas.setStatus(String.valueOf(wxCpUser.getStatus()));
        }
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        res.setDatas(datas);
        return JSON.toJSONString(res);
    }
    
    @Override
    protected boolean isVerifyFail(DCP_GetEnterpriseChatLoginStatusReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_GetEnterpriseChatLoginStatusReq> getRequestType() {
        return new TypeToken<DCP_GetEnterpriseChatLoginStatusReq>() {
        };
    }
    
    @Override
    protected DCP_GetEnterpriseChatLoginStatusRes getResponseType() {
        return new DCP_GetEnterpriseChatLoginStatusRes();
    }
    
    @Override
    protected DCP_GetEnterpriseChatLoginStatusRes processJson(DCP_GetEnterpriseChatLoginStatusReq req) throws Exception {
        return null;
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_GetEnterpriseChatLoginStatusReq req) throws Exception {
        return null;
    }
}