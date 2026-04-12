package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DCP_ApPrePayDetailQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ApPrePayDetailQueryReq.Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String status;
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String taskId;
        private String bizPartnerNo;
        @JSONFieldRequired
        private String apNo;
    }
}