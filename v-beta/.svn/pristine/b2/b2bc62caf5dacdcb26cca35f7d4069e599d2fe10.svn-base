package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_WarehouseRangeDeleteReq extends JsonBasicReq {
    private DCP_WarehouseRangeDeleteReq.LevelElm request;

    @Data
    public class LevelElm{
        private String warehouseNo;
        private String organizationNo;
        private List<RangeList> rangeList;
    }

    @Data
    public class RangeList{
        private String code;
        private String type;
    }
}
