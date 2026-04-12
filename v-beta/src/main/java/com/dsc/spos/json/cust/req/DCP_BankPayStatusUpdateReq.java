package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_BankPayStatusUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String cmNo;
        private String status;
        @JSONFieldRequired
        private String opType;
        private String eId;
    }
}
