package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/*
http://localhost:8080/DCP/sposServiceTest.jsp
{
    "serviceId": "DCP_GetEnterpriseChatUserInfo_Open",
    "request": {
        "appType": "RETAIL",
        "code": "2f1I0KRSS3KSRBNeiI8J83O1WzvR16tRrNJ1RNJeMOY"
    }
}

 */
@Data
public class DCP_GetEnterpriseChatUserInfoReq extends JsonBasicReq {

    private levelRequest request;

    @Data
    public static class levelRequest {
        private String appType;
        private String shopId;
        private String machineId;
        private String machineCode;
        private String code;
    }
}
