package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DCP_AcctPayDateQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_AcctPayDateQueryReq.Request request;

    @NoArgsConstructor
    @Data
    public static class Request {
        private String status;
        private String paydateType;
        @JSONFieldRequired
        private String paydateNo;
        private String bdate;
    }
}
