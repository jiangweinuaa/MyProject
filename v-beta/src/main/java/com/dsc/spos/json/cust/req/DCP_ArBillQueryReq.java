package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ArBillQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String status;
        @JSONFieldRequired
        private String accountId;
        private List<String> taskId;
        private String beginDate;
        private String endDate;
        private String bizPartnerNo;
        private String isPmtOffset;
    }
}
