package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_PurchaseApplyDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PurchaseApplyDeleteReq.Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String billNo;
    }
}
