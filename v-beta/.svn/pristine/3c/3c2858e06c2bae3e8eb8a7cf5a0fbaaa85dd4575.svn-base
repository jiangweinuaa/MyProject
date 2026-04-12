package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_TaxSubjectQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_TaxSubjectQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        private String status;
        private String accountId;
    }

}
