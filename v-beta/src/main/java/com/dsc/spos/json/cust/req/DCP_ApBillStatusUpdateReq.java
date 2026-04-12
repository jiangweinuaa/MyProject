package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ApBillStatusUpdateReq extends JsonBasicReq
{
    @JSONFieldRequired
    private DCP_ApBillStatusUpdateReq.levelRequest request;

    @Data
    public class levelRequest {
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String apNo;
        private String opType;
    }
}
