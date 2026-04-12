package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_WarehouseRangeAddReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_WarehouseRangeAddReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired
        private String warehouseNo;
        @JSONFieldRequired
        private String organizationNo;
        @JSONFieldRequired
        private List<DCP_WarehouseRangeAddReq.RangeList> rangeList;
    }

    @Data
    public class RangeList{
        @JSONFieldRequired
        private String code;
        @JSONFieldRequired
        private String type;
        @JSONFieldRequired
        private String status;
    }
}
