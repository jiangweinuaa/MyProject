package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_SupplierOpenQryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_SupplierOpenQryReq.levelItem request;

    @Data
    public class levelItem {
        private String keyTxt;
        @JSONFieldRequired
        private String isCheckRestrictGroup;

        private String purType;
        private String purCenter;

    }

}

