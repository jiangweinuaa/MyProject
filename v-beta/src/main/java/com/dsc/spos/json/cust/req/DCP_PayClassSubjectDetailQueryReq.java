package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_PayClassSubjectDetailQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public static class Request {
        private String status;
        @JSONFieldRequired
        private String accountId;
        private String coaRefID;
    }
}
