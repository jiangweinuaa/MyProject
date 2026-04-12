package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PurchaseApplyTrackReq extends JsonBasicReq {

    private DCP_PurchaseApplyTrackReq.levelElm request;

    @Data
    public class levelElm{
        private String billNo;
    }
}
