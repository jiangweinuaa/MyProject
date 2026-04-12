package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_COADetailQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_COADetailQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        @JSONFieldRequired
        private String subjectId;
        private String accountId;
        @JSONFieldRequired
        private String coaRefID;
    }

}
