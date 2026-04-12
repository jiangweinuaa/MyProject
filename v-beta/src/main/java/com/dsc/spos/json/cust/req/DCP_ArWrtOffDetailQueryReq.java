package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_ArWrtOffDetailQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public  class Request {
        private String status;
        private String accountId;
        private String taskId;
        private String bizPartnerNo;
        @JSONFieldRequired
        private String arNo;
    }
}
