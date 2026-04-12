package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PurInvReconCreateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String organizationNo;
        private String organizationName;
        @JSONFieldRequired(display = "结算法人")
        private String corp;
        private String corpName;
        @JSONFieldRequired(display = "单据日期")
        private String bDate;
//        @JSONFieldRequired(display = "收票单号")
        private String purInvNo;
        @JSONFieldRequired(display = "发票类型")
        private String invoiceType;
        @JSONFieldRequired(display = "交易对象")
        private String bizPartnerNo;
        @JSONFieldRequired(display = "发票性质")
        private String invProperty;
        @JSONFieldRequired(display = "币种")
        private String currency;
        @JSONFieldRequired(display = "付款条件")
        private String payDateNo;
        private String payDueDate;
        private String apNo;
        private String isDoc;

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
        @JSONFieldRequired
        private List<InvList> invList;

        private List<ReconList> reconList;
    }

    @NoArgsConstructor
    @Data
    public class InvList {
//        @JSONFieldRequired(display = "收票单号")
        private String purInvNo;
        private String item;
//        @JSONFieldRequired(display = "发票类型")
//        private String invoiceType;
//        @JSONFieldRequired(display = "发票性质")
//        private String invProperty;
        @JSONFieldRequired(display = "发票代码")
        private String invoiceNumber;
        @JSONFieldRequired(display = "发票号码")
        private String invoiceCode;
        @JSONFieldRequired(display = "发票日期")
        private String invoiceDate;
        private String taxCode;
        private String inclTax;
//        @JSONFieldRequired(display = "税别")
        private String taxRate;
        private String currency;
        private String exRate;
        private String payerName;
        private String payerTaxNo;
        private String payerAddress;
        private String receiver;
        private String payerTel;
        private String payerAccount;
        private String payerAccountCode;
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
//        @JSONFieldRequired(display = "可扣抵编号")
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
//        @JSONFieldRequired(display = "收票单号")
        private String purInvNo;
        private String item;
        private String sourceType;
        private String sourceNo;
        private String sourceNoSeq;
        private String sourceOrg;
        private String rDate;
        private String fee;
        private String feeName;
        private String pluNo;
        private String pluName;
        private String spec;
        private String priceUnit;
        private String currency;
        private String taxRate;
        private String taxCode;
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
