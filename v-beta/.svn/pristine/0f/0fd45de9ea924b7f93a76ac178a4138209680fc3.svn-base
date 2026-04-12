package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_StockTaskBatchCreateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_StockTaskBatchCreateReq.LevelElm request;

    @Data
    public class LevelElm{
        private String beginDate;
        private String endDate;
        private String warehouseType;
        private String confirmType;

    }
}
