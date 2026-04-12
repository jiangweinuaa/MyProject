package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class DCP_CurInvCostAdjUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
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
        private String referenceNo;
        @JSONFieldRequired
        private String dataSource;
        @JSONFieldRequired
        private List<InvList> adjList;
    }

    @NoArgsConstructor
    @Data
    public class InvList {
        @JSONFieldRequired
        private String item;
        private String costDomainId;
        private String costDomainDis;
        @JSONFieldRequired
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String memo;
        private String qty;
        private String totAmt;
        private String material;
        private String labor;
        private String oem;
        private String exp1;
        private String exp2;
        private String exp3;
        private String exp4;
        private String exp5;
        private String totPretaxAmt;
        private String pretaxMaterial;
        private String pretaxLabor;
        private String pretaxOem;
        private String pretaxExp1;
        private String pretaxExp2;
        private String pretaxExp3;
        private String pretaxExp4;
        private String pretaxExp5;

    }
}
