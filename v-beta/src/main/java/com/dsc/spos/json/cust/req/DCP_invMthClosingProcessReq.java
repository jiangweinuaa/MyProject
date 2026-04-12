package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_invMthClosingProcessReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_invMthClosingProcessReq.levelElm request;

    @Data
    public class levelElm {
        @JSONFieldRequired
        private String corp;
        private String corp_Name;
        @JSONFieldRequired
        private String accountID;
        private String account;
        private String cost_Calculation;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String period;

    }
}