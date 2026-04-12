package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_WarehouseDeleteReq extends JsonBasicReq {
    private DCP_WarehouseDeleteReq.levelElm request;

    @Data
    public class levelElm{
        private String warehouseNo;
        private String organizationNo;
        private String opType;
    }
}
