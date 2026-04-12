package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_CurInvCostAdjQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @Data
    public static class Request {
        @JSONFieldRequired
        private String accountID;
        private String account;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String period;
        private String dataSource;
        private String referenceNo;
    }
}
