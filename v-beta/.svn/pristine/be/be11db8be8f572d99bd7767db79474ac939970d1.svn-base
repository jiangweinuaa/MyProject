package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_CostExecuteQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Data
    public class Request {
        private String status;
        private String accountID;
        private String account;
        private String year;
        private String period;
        private String mainTaskId;
        private String subtaskId;
        private String type;
    }
}
