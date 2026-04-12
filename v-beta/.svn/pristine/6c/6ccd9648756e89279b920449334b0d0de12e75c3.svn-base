package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class DCP_AfterCostCalChkExportReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String corp;
        private String corp_Name;
        private String accountID;
        private String account;
        private String year;
        private String period;
        private String bType;
    }
}
