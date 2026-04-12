package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ApBillQueryReq extends JsonBasicReq
{
    @JSONFieldRequired
    private DCP_ApBillQueryReq.levelRequest request;

    @Data
    public class levelRequest{
        private String status;
        @JSONFieldRequired
        private String accountId;
        private String taskId;
        private String beginDate;
        private String endDate;
        private String bizPartnerNo;
        private String isPmtOffset;

    }

}
