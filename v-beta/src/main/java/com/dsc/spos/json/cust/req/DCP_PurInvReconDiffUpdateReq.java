package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_PurInvReconDiffUpdateReq extends JsonBasicReq {

    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String eId;
        private String purInvNo;
        private String diff;
    }
}
