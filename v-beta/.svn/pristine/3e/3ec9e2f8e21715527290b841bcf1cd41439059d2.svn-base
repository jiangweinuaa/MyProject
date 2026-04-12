package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 {
 "serviceId": "DCP_GetEnterpriseChatLoginInfo_Open",
 "request": {
 "appType": "RETAIL",
 "redirectUri": "http://eliutong2.digiwin.com.cn"
 }
 }

 POS调用
 {
 "serviceId": "DCP_GetEnterpriseChatLoginInfo_Open",
 "request": {
 "appType": "POS",
 "shopId": "01",
 "machineId": "m01",
 "machineCode": "78db1b9c-8740-42b1-af2a-07f806182720"
 }
 }

 https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=ww6cbc67ae603a4bdf&agentid=1000006&redirect_uri=http%3A%2F%2Feliutong2.digiwin.com.cn%2Fdcpservice%2FDCP%2Fservices%2Fwecom%3Fparams%3DZUlkPTk5JmFnZW50SWQ9MTAwMDAwNiZhcHBUeXBlPVBPUyZzaG9wSWQ9MDEmbWFjaGluZUlkPTkxNi00&state=5541fd0d-3c88-4e24-8bc7-495e1f2492cb

 */
@Data
public class DCP_GetEnterpriseChatLoginInfoReq extends JsonBasicReq {

    private levelRequest request;

    @Data
    public static class levelRequest {
        private String eId;
        private String appType;
        private String shopId;
        private String machineId;
        private String machineCode;
        private String redirectUri;
    }
}
