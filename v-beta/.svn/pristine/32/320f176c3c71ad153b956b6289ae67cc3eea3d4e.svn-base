package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_COAQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_COAQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        private String status;
        private String keyTxt;
        @JSONFieldRequired
        private String coaRefID;
    }

}
