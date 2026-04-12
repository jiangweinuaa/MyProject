package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DCP_invClosingDateProcessReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Data
    public static class Request {
        private String corp;
        private String corp_Name;
        private String accountID;
        private String account;
        private String cost_Calculation;
        private String year;
        private String period;
        private String invClosingDate;
    }
}
