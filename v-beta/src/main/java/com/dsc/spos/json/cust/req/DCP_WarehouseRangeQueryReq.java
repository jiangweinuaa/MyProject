package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_WarehouseRangeQueryReq  extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_WarehouseRangeQueryReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired
        private String warehouseNo;
        private String organizationNo;

    }
}
