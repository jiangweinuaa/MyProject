package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_RouteDetailQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_RouteDetailQueryReq.levelRequest request;

    @Data
    public class levelRequest {
        private String routeNo;
    }
}
