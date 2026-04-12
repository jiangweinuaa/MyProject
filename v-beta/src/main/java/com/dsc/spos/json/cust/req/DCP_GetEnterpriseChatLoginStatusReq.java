package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/*
 {
   "serviceId": "DCP_GetEnterpriseChatLoginStatus_Open",
   "request": {
        "machineCode": "78db1b9c-8740-42b1-af2a-07f806182720"
    }
 }

 */
@Data
public class DCP_GetEnterpriseChatLoginStatusReq extends JsonBasicReq {

    private levelRequest request;

    @Data
    public static class levelRequest {
        private String machineCode;
    }
}
