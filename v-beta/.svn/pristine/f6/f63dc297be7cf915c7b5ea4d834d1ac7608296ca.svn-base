package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_VendorAdjDeleteReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_VendorAdjDeleteReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String organizationNo;
        @JSONFieldRequired
        private String org_Name;
        @JSONFieldRequired
        private String adjustNO;
        private String bDate;
        private String status;
    }
}
