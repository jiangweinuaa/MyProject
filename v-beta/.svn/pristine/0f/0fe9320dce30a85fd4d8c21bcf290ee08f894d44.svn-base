package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PStockOutStatusUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PStockOutStatusUpdateReq.levelElm request;

    @Data
    public class levelElm{
        private String pStockInNo;

        private String ofNo;
        private String docType;
        private String opType;
        private String oType;

    }
}

