package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ARInvCreateReq extends JsonBasicReq {

    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String accountId;
        private String corp;
        private String arNo;
        private String pDate;
        private String status;

        private List<ArInvList> arInvList;

        private String createBy;
        private String create_Date;
        private String create_Time;
        private String modifyBy;
        private String modify_Date;
        private String modify_Time;
        private String confirmBy;
        private String confirm_Date;
        private String confirm_Time;
        private String cancelBy;
        private String cancel_Date;
        private String cancel_Time;
    }

    @NoArgsConstructor
    @Data
    public class ArInvList {
        private String organizationNo;
        private String item;
        private String invoiceType;
        private String isEInvoice;
        private String invoiceCode;
        private String invoiceNo;
        private String invoiceCopyNo;
        private String invAntiRadndCode;
        private String invoiceDate;
        private String invoiceTime;
        private String invClientFullNm;
        private String purTaxPayerID;
        private String purAddress;
        private String salesTaxPayerID;
        private String bizPartnerNo;
        private String invDiffStatus;
        private String taxCode;
        private String taxRate;
        private String isAfterTax;
        private String currency;
        private String exRate;
        private String invFCYBTAmt;
        private String invFCYTAmt;
        private String invFCYATAmt;
        private String invLCYBTAmt;
        private String invLCYTAmt;
        private String invLCYATAmt;
        private String dedctblNo;
        private String recType;

    }
}
