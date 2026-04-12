package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ReturnApplyQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ReturnApplyQueryReq.level1Elm request;

    @Data
    public class level1Elm{
        private String status;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        private String keyTxt;
    }
}
