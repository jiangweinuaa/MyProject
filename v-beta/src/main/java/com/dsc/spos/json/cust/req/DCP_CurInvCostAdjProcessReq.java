package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_CurInvCostAdjProcessReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @Data
    public class Request {
        @JSONFieldRequired
        private String accountID;
        private String account;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String period;
        @JSONFieldRequired
        private String cost_Calculation;
        @JSONFieldRequired
        private String dataSource;


    }

}
