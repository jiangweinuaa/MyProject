package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_WarehouseRangeEnableReq extends JsonBasicReq {
    private DCP_WarehouseRangeEnableReq.LevelElm request;

    @Data
    public class LevelElm{
        private String warehouseNo;
        private String organizationNo;
        private String opType;
        private List<DCP_WarehouseRangeEnableReq.RangeList> rangeList;
    }

    @Data
    public class RangeList{
        private String code;
        private String type;
    }
}
