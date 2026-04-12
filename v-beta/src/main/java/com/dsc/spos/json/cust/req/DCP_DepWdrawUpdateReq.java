package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_DepWdrawUpdateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String item;
        @JSONFieldRequired
        private String depWdrawCode;
        private String depWdrawName;
        private String dwType;
        private String cfCode;
        private String cfName;
        private String subjectId;
        private String subjectName;
        private String acctSet;
        private String accountID;
        private String account;
        private String status;
    }
}
