package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_WarehouseCreateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_WarehouseCreateReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String warehouseNo;
        @JSONFieldRequired
        private String warehouseType;
        @JSONFieldRequired
        private String isCost;
        @JSONFieldRequired
        private String isLocation;
        @JSONFieldRequired
        private String isCheckStock;
        @JSONFieldRequired
        private String status;
        @JSONFieldRequired
        private List<Lang> wareNamelang;

        private String organizationNo;
    }

    @Data
    public class Lang{

        @JSONFieldRequired
        private String name;
        @JSONFieldRequired
        private String langType;

    }
}
