package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_BatchStockDistributionReq extends JsonBasicReq {

    @JSONFieldRequired
    private RequestLevel request;

    @NoArgsConstructor
    @Data
    public  class RequestLevel {
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired
        private String batchNo;
        private Boolean inStockOnly;
        private List<String> organizationNo;
        private List<String> warehouse;
        private List<String> location;
    }
}
