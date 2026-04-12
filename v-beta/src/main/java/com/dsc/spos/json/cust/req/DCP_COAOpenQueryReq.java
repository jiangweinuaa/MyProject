package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_COAOpenQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_COAOpenQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        private String status;
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String coaRefID;
    }

}