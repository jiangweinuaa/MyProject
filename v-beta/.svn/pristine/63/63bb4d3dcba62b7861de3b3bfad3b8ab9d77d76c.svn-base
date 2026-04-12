package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_RouteQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_RouteQueryReq.levelRequest request;

    @Data
    public class levelRequest {
        private String status;
        private String keyTxt;
    }
}
