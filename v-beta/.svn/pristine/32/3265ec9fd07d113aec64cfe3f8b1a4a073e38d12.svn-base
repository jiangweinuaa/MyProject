package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ROrderDetailQueryReq  extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ROrderDetailQueryReq.levelRequest request;

    @Data
    public class levelRequest{
        @JSONFieldRequired
        private String rOrderNo;
    }
}
