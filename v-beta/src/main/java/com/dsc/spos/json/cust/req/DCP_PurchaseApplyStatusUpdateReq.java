package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PurchaseApplyStatusUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PurchaseApplyStatusUpdateReq.LevelRequest request;

    @Data
    public class LevelRequest {
        private String billNo;
        private String oprType;
    }
}
