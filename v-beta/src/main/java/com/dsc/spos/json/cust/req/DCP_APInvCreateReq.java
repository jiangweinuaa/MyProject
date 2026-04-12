package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_APInvCreateReq extends JsonBasicReq {


    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String accountId;
        private String apType;
        private String corp;
        private String purInvNo;


        private String status;
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
        private List<ApInvList> apInvList;
    }

    @NoArgsConstructor
    @Data
    public class ApInvList {
        private String organizationNo;
        private String item;
        private String invSource;
        private String bizPartnerNo;
        private String invoiceType;
        private String invoiceNumber;
        private String invoiceCode;
        private String invoiceDate;
        private String taxCode;
        private String isAfterTax;
        private String taxRate;
        private String currency;
        private String exRate;
        private String payerName;
        private String payerTaxNo;
        private String payerAddress;
        private String payerTel;
        private String payerAccount;
        private String payerAccountCode;
        private String salerAccountNo;
        private String invFCYBTAmt;
        private String invFCYTAmt;
        private String invFCYATAmt;
        private String invLCYBTAmt;
        private String invLCYTAmt;
        private String invLCYATAmt;
        private String saleName;
        private String taxnum;
        private String salerAddress;
        private String salerTel;
        private String salerAccount;
        private String salerAccountCode;
        private String recType;
        private String dedctblNo;
        private String apNo;
        private String isEInvoice;
    }

}
