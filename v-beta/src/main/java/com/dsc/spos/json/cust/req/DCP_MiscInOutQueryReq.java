package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DCP_MiscInOutQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String account;
        @JSONFieldRequired
        private String accountID;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        @JSONFieldRequired
        private String type;
        private String isZero;
    }

}
