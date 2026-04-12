package com.dsc.spos.service.imp.json;


import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.json.cust.req.DCP_AIParameterQueryReq;
import com.dsc.spos.json.cust.res.DCP_AIParameterQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_AIParameterQuery
 * 服务说明：AI应用信息查询
 * @author jinzma
 * @since  2025-10-23
 */
public class DCP_AIParameterQuery extends SPosBasicService<DCP_AIParameterQueryReq, DCP_AIParameterQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AIParameterQueryReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_AIParameterQueryReq> getRequestType() {
        return new TypeToken<DCP_AIParameterQueryReq>(){};
    }
    
    @Override
    protected DCP_AIParameterQueryRes getResponseType() {
        return new DCP_AIParameterQueryRes();
    }
    
    @Override
    protected DCP_AIParameterQueryRes processJson(DCP_AIParameterQueryReq req) throws Exception {
        
        DCP_AIParameterQueryRes res = this.getResponse();
        DCP_AIParameterQueryRes.Datas datas = new DCP_AIParameterQueryRes.Datas();
        
        String eId = req.geteId();
        String sql = " select * from DCP_AIPARAMETER where eid='"+eId+"'  ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        
        if (!CollectionUtil.isEmpty(getQData)){
            
            datas.setTenantId(getQData.get(0).get("TENANTID").toString());         //租户ID
            datas.setAppId(getQData.get(0).get("APPID").toString());               //应用ID
            datas.setAppName(getQData.get(0).get("APPNAME").toString());           //应用名称
            datas.setAppToken(getQData.get(0).get("APPTOKEN").toString());         //应用token
            datas.setAppSecret(getQData.get(0).get("APPSECRET").toString());       //应用密钥
            datas.setSkcAppToken(getQData.get(0).get("SKCAPPTOKEN").toString());   //应用令牌
            datas.setAuthCode(getQData.get(0).get("AUTHCODE").toString());         //授权码
            datas.setNnaAuthCode(getQData.get(0).get("NNAAUTHCODE").toString());   //鼎捷云应用管理里面的娜娜入口(NNA)的授权码
            
            //ID：20251030009 [产品3.0]中台NNA娜娜入口登入-服务端  by jinzma 20251030
            datas.setIamUrl(getQData.get(0).get("IAMURL").toString());             //IAM服务地址
            datas.setNnaUrl(getQData.get(0).get("NNAURL").toString());             //娜娜入口地址
            
            //ID：20251031020【产品3.0】AI应用信息配置-增加NNA访问地址-服务端 by jinzma 20251031
            datas.setTenantName(getQData.get(0).get("TENANTNAME").toString());     //租户名称
            
            
        }
        
        
        res.setDatas(datas);
        
        return res;
        
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_AIParameterQueryReq req) throws Exception {
        return "";
    }
}
