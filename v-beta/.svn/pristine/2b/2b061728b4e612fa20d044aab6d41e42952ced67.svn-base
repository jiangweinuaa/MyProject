package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_VendorAdjDetailQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_VendorAdjDetailQueryReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String adjustNo;
        @JSONFieldRequired
        private String organizationNo;
    }
}
