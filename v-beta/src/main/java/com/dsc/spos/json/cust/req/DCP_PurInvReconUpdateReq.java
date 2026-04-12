package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PurInvReconUpdateReq extends JsonBasicReq {


    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String organizationNo;
        private String organizationName;
        private String corp;
        private String corpName;
        private String bdate;
        private String purInvNo;
        private String invoiceType;
        private String bizPartnerNo;
        private String invProperty;
        private String currency;
        private String payDateNo;
        private String payDueDate;
        private String apNo;
        private List<InvList> invList;
        private List<ReconList> reconList;
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
    }

    @NoArgsConstructor
    @Data
    public class InvList {
        private String purInvNo;
        private String item;
        private String invoiceType;
        private String invType;
        private String invoiceNumber;
        private String invoiceCode;
        private String invoiceDate;
        private String taxCode;
        private String inclTax;
        private String taxRate;
        private String currency;
        private String exRate;
        private String payerName;
        private String payerTaxNo;
        private String payerAddress;
        private String payerTel;
        private String payerAccount;
        private String payerAccountCode;
        private String receiver;
        private String bizPartnerNo;
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
        private String isEInvoice;
        private String apNo;
        private String payDateNo;
        private String payDueDate;
        private String diff;
        private String diffAmt;
    }

    @NoArgsConstructor
    @Data
    public class ReconList {
        private String organizationNo;
        private String organizationName;
        private String corp;
        private String corpName;
        private String purInvNo;
        private String item;
        private String sourceType;
        private String sourceNo;
        private String sourceOrg;
        private String sourceNoSeq;
        private String rDate;
        private String fee;
        private String feeName;
        private String pluNo;
        private String pluName;
        private String spec;
        private String priceUnit;
        private String currency;
        private String taxRate;
        private String referenceNo;
        private String referenceItem;
        private String direction;
        private String billQty;
        private String billPrice;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYATAmt;
        private String exRate;
        private String invCrncyBTAmt;
        private String invCrncyTAmt;
        private String invCrncyATAmt;
        private String curInvoiceAmt;

    }
}
