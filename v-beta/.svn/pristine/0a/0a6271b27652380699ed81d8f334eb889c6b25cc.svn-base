package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DCP_BatchUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_BatchUpdateReq.RequestLevel request;

    @NoArgsConstructor
    @Data
    public  class RequestLevel {
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired
        private String batchNo;
        private String productDate;
        private String loseDate;
        private String supplierType;
        private String supplierId;
        private String produceArea;
        private String manufacturer;
    }
}