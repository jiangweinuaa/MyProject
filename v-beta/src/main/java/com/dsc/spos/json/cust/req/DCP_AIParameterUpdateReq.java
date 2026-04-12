package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务函数：DCP_AIParameterUpdate
 * 服务说明：AI应用信息修改
 * @author jinzma
 * @since  2025-10-23
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DCP_AIParameterUpdateReq extends JsonBasicReq {
    
    private Request request;
    
    @Data
    public static class Request {
        private String tenantId;       //租户ID
        private String appId;          //应用ID
        private String appName;        //应用名称
        private String appToken;       //应用token
        private String appSecret;      //应用密钥
        private String skcAppToken;    //应用令牌
        private String authCode;       //授权码
        private String nnaAuthCode;    //鼎捷云应用管理里面的娜娜入口(NNA)的授权码
        
        //ID：20251030009 [产品3.0]中台NNA娜娜入口登入-服务端  by jinzma 20251030
        private String iamUrl;         //IAM服务地址
        private String nnaUrl;         //娜娜入口地址
        
        //ID：20251031020【产品3.0】AI应用信息配置-增加NNA访问地址-服务端 by jinzma 20251031
        private String tenantName;     //租户名称
        
    }
}

