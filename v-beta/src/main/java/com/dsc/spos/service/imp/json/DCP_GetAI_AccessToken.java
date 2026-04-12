package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GetAI_AccessTokenReq;
import com.dsc.spos.json.cust.res.DCP_GetAI_AccessTokenRes;
import com.dsc.spos.json.cust.res.DCP_GetAI_AccessTokenRes.Datas;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.AI_Digiwin;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

public class DCP_GetAI_AccessToken extends SPosAdvanceService<DCP_GetAI_AccessTokenReq, DCP_GetAI_AccessTokenRes> {
    
    @Override
    protected void processDUID(DCP_GetAI_AccessTokenReq req, DCP_GetAI_AccessTokenRes res) throws Exception {
        
        Datas datas = new Datas();
        String eId = req.geteId();
        
        
        String appType = "SKC";
        String opNo = "";
        
        //ID：20251030009 [产品3.0]中台NNA娜娜入口登入-服务端 by jinzma 20251030
        if (req.getRequest() !=null) {
            appType = req.getRequest().getAppType();   //应用类型：SKC-智能体[默认] NNA-娜娜入口
            if (!Check.Null(appType) && appType.equals("NNA")) {
                opNo = req.getOpNO();
            }
        }
        
        if (Check.Null(appType)){
            appType = "SKC";
        }
        
        
        String sql = " select * from DCP_AIPARAMETER where eid='"+eId+"' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (CollectionUtil.isEmpty(getQData)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "DCP_AIPARAMETER 未设置 ");
        }
        
        //AI中心有多个产品，每个产品有自己的授权码，DCP_AIPARAMETER表里这个AUTHCODE是智能体中心的授权码
        //上面这句话是安驰让我加的，具体含义不是很懂
        
        String licenseCode = getQData.get(0).get("AUTHCODE").toString() ;  //鼎捷云应用管理里面的智能体中心（kai-skc）的授权码
        String appId = getQData.get(0).get("APPID").toString() ;           //鼎捷中间件管理系统MMC的应用ID
        String appToken = getQData.get(0).get("APPTOKEN").toString() ;     //鼎捷中间件管理系统MMC的APPTOKEN
        String appSecret = getQData.get(0).get("APPSECRET").toString() ;   //鼎捷中间件管理系统MMC的APPSECRET
        String userId = getQData.get(0).get("TENANTID").toString() ;       //鼎捷云系统的租户ID
        
        
        //ID：20251030009 [产品3.0]中台NNA娜娜入口登入-服务端 by jinzma 20251030
        String iamUrl = getQData.get(0).get("IAMURL").toString() ;            //IAM服务地址
        String nnaAuthCode = getQData.get(0).get("NNAAUTHCODE").toString();   //鼎捷云应用管理里面的娜娜入口(NNA)的授权码
        
        if (Check.Null(iamUrl)){
            iamUrl = "https://iam.digiwincloud.com.cn";
        }
        
        String accessToken = "";
        
        if (appType.equals("SKC")){
            //按照王总指示，加上缓存，
            //按照王总指示，两个token存在冲突，去掉缓存 by jinzma 20251112
            /*RedisPosPub redisPosPub = new RedisPosPub();
            String tokenKey = "Dcp_DigiwinAi_SKC_"+userId+"_Token";
            accessToken = redisPosPub.getString(tokenKey);
            if (Check.Null(accessToken)) {
                accessToken = AI_Digiwin.getAI_AccessToken(licenseCode,appId,appToken,appSecret,userId,iamUrl);
                
                if (!Check.Null(accessToken)) {
                    redisPosPub.setEx(tokenKey,60*60*2-120,accessToken);  //写死了2小时，提前2分钟
                }
            }*/
            
            accessToken = AI_Digiwin.getAI_AccessToken(licenseCode,appId,appToken,appSecret,userId,iamUrl);
            
        }
        
        if (appType.equals("NNA")){
            //按照小凤指示，不要缓存，因为确定不了token有效时间
           /* RedisPosPub redisPosPub = new RedisPosPub();
            String tokenKey = "Dcp_DigiwinAi_NNA_"+opNo+"_Token";
            accessToken = redisPosPub.getString(tokenKey);
            if (Check.Null(accessToken)) {
                accessToken = AI_Digiwin.getAI_AccessToken(nnaAuthCode,appId,appToken,appSecret,opNo,iamUrl);
                
                if (!Check.Null(accessToken)) {
                    redisPosPub.setEx(tokenKey,60*60*2-120,accessToken);  //写死了2小时，提前2分钟
                }
            }*/
            
            accessToken = AI_Digiwin.getAI_AccessToken(nnaAuthCode,appId,appToken,appSecret,opNo,iamUrl);
            
        }
        
        
        datas.setAccessToken(accessToken);
        
        res.setData(datas);
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务调用成功");
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_GetAI_AccessTokenReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_GetAI_AccessTokenReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_GetAI_AccessTokenReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_GetAI_AccessTokenReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_GetAI_AccessTokenReq> getRequestType() {
        return new TypeToken<DCP_GetAI_AccessTokenReq>(){};
    }
    
    @Override
    protected DCP_GetAI_AccessTokenRes getResponseType() {
        return new DCP_GetAI_AccessTokenRes();
    }
    
}
