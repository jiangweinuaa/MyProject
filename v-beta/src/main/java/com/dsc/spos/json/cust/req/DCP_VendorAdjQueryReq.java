package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_VendorAdjQueryReq  extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_VendorAdjQueryReq.levelElm request;

    @Data
    public class levelElm{
        private String bdate;
        private String organizationNo;
        private String org_Name;
        private String supplierNo;
        private String supplierName;
        private String status;

        private String startDate;
        private String endDate;
    }
}
