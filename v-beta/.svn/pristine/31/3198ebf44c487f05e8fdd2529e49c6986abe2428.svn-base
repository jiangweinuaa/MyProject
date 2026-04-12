package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_CurInvCostAdjDeleteReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public static class Request {
        @JSONFieldRequired
        private String status;
        @JSONFieldRequired
        private String accountID;
        private String account;
        @JSONFieldRequired
        private String year;
        @JSONFieldRequired
        private String period;
        @JSONFieldRequired
        private String cost_Calculation;
        @JSONFieldRequired
        private String dataSource ;
        @JSONFieldRequired
        private String referenceNo ;
        @JSONFieldRequired
        private List<InvList> adjList;
    }

    @NoArgsConstructor
    @Data
    public class InvList {
        private String item;
        private String costDomainId;
        private String costDomainName;
        private String fee;
        private String feeName;
        private String pluName;
        private String pulNo;

    }

}
