package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ApBillDetailQueryReq  extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_ApBillDetailQueryReq.Request request;

    @Data
    public class Request {
        private String status;
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String taskId;
        //@JSONFieldRequired
        private String bizPartnerNo;
        @JSONFieldRequired
        private String apNo;

    }

}
