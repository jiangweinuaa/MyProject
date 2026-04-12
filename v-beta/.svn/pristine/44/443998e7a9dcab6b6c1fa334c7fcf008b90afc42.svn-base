package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class DCP_PStockInDetailQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PStockInDetailQueryReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String docType;
        private String pStockInNo;
        private String taskNo;
    }
}
