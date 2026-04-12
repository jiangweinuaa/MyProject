package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PreCostCalChkProcessReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PreCostCalChkProcessReq.levelElm request;

    @Data
    public class levelElm {
        private String corp;
        private String corp_Name;
        @JSONFieldRequired
        private String accountID;
        private String account;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String period;
        private String bType;
    }
}
