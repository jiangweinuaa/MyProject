package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ReconliationBillUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ReconliationBillUpdateReq.level1Elm request;

    @Data
    public class level1Elm {
        private String bizPartnerNo;
        private String bizType;
        private String orgNo;
        private String bdate;
        private String edate;
        private String isCheck;
    }
}
