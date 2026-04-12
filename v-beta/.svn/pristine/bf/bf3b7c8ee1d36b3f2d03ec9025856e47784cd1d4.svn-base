package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_ApWrtOffQueryReq extends JsonBasicReq {

    private RequestLevel request;

    @NoArgsConstructor
    @Data
    public  class RequestLevel {
        private String status;
        private String accountId;
        private String taskId;
        private String wrtOffNo;
        private String bizPartnerNo;
        private String beginDate;
        private String endDate;
    }
}
