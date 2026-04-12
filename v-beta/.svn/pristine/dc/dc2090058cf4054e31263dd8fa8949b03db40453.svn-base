package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_BatchDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private RequestLevel request;

    @NoArgsConstructor
    @Data
    public static class RequestLevel {
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired
        private String batchNo;
    }
}
