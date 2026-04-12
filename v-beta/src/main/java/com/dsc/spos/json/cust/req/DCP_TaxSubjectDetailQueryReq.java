package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_TaxSubjectDetailQueryReq  extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_TaxSubjectDetailQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        private String status;
        @JSONFieldRequired
        private String accountId;
    }

}