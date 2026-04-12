package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;


@Data
public class DCP_CurInvCostStatQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @Data
    public static class Request {
        private String corp;
        private String corp_Name;
        private String accountID;
        private String account;
        private String year;
        private String period;
        private String pluNo;
        private String costDomainID;
    }
}
