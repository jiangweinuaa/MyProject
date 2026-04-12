package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_PurInvReconStatusUpdateReq extends JsonBasicReq {

    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String purInvNo;
        private String status;
        private String opType;
    }
}
