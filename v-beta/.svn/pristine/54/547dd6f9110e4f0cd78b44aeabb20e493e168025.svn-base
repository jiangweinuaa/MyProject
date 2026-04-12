package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PStockInMaterialBatchRefreshReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PStockInMaterialBatchRefreshReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String docType;
        @JSONFieldRequired
        private String pStockInNo;
    }

    @Data
    public class StockInfo{
        private String pluNo;
        private String featureNo;
        private String warehouse;
        private String batchNo;
        private String location;
        private String baseUnit;
        private String qty;
        private String lockQty;
        private String prodDate;
        private String validDate;
    }

}