package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_SettleDataCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String organizationNo;
        @JSONFieldRequired
        private String bdate;
        @JSONFieldRequired
        private String bType;
        @JSONFieldRequired
        private String billNo;
        @JSONFieldRequired
        private String bizType;
        private List<SettleList> settleList;
    }

    @NoArgsConstructor
    @Data
    public class SettleList {
        private String item;
        private String bizPartnerNo;
        private String payOrgNo;
        private String billDateNo;
        private String payDate;
        private String payDateNo;
        private String invoiceCode;
        private String billDate;
        private String month;
        private String year;
        private String currency;
        private String taxCode;
        private String taxRate;
        private String direction;
        private String priceUnit;
        private String featureNo;
        private String billQty;
        private String billPrice;
        private String preTaxAmt;
        private String taxAmt;
        private String billAmt;
        private String unSettlAmt;
        private String settleAmt;
        private String unPaidAmt;
        private String paidAmt;
        private String departId;
        private String cateGory;
        private String status;
        private String fee;
        private String feeName;
        private String pluNo;
        private String pluName;
        private String apQty;
        private String apAmt;
        private String unApAmt;
    }
}
