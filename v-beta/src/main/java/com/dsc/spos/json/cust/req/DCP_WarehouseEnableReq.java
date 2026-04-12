package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_WarehouseEnableReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_WarehouseEnableReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String warehouseNo;
        @JSONFieldRequired
        private String opType;

        @JSONFieldRequired
        private String organizationNo;
    }
}
