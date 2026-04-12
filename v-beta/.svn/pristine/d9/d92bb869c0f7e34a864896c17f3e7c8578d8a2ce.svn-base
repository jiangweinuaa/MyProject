package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_MStockOutDetailQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_MStockOutDetailQueryReq.levelRequest request;

    @Data
    public class levelRequest{
        @JSONFieldRequired
        private String mStockOutNo;
    }
}
