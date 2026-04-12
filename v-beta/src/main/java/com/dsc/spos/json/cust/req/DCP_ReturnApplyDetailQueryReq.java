package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ReturnApplyDetailQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ReturnApplyDetailQueryReq.level1Elm request;

    @Data
    public class level1Elm{

        private String appStatus;
        @JSONFieldRequired
        private String billNo;
    }
}
