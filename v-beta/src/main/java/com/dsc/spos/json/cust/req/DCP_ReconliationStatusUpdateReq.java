package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ReconliationStatusUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ReconliationStatusUpdateReq.level1Elm request;

    @Data
    public class level1Elm {
        @JSONFieldRequired
        private String reconNo;
        @JSONFieldRequired
        private String dataType;
        private String status;
        @JSONFieldRequired
        private String opType;
        private String eId;
    }
}
