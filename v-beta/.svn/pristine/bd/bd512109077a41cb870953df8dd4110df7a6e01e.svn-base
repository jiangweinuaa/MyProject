package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_VendorAdjStatusUpdateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_VendorAdjStatusUpdateReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String adjustNO;
        private String status;
        @JSONFieldRequired
        private String opType;
        private String eId;
    }
}
