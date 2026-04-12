package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_APDocCreateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_APDocCreateReq.level1Elm request;

    @Data
    public class level1Elm{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String apType;
        @JSONFieldRequired
        private String corp;
        private String pDate;
        @JSONFieldRequired
        private String organizationNo;
        @JSONFieldRequired
        private String accEmployeeNo;
        @JSONFieldRequired
        private String bizPartnerNo;
        @JSONFieldRequired
        private String receiver;
        @JSONFieldRequired
        private String taskId;
        @JSONFieldRequired
        private String payDateNo;
        @JSONFieldRequired
        private String payDueDate;
        @JSONFieldRequired
        private String taxCode;
        @JSONFieldRequired
        private String taxRate;
        private String inclTax;
        private String applicant;
        private String apDepart;
        private String employeeNo;
        private String departId;
        @JSONFieldRequired
        private String sourceType;
        private String sourceNo;
        private String pendOffsetNo;
        @JSONFieldRequired
        private String feeSubjectId;
        @JSONFieldRequired
        private String apSubjectId;
        private String glNo;
        private String grpPmtNo;
        private String memo;
        private String payList;
        //@JSONFieldRequired
        private String currency;
        private String exRate;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYRevAmt;
        private String fCYTATAmt;
        private String lCYBTAmt;
        private String lCYTAmt;
        private String lCYRevAmt;
        private String lCYTATAmt;
        private String fCYPmtAmt;
        private String lCYPmtAmt;

        private String iCYCurrency;
        private String fCYCurrency;

        @JSONFieldRequired
        private List<ApBillList> apBillList;
        //apBillSumList
        //@JSONFieldRequired
        private List<ApBillSumList> apBillSumList;
        //apPerdList
        //@JSONFieldRequired
        private List<ApPerdList> apPerdList;
        //apWFList
        //@JSONFieldRequired
        private List<ApWFList> apWFList;
        //pmtList
        //@JSONFieldRequired
        private List<PmtList> pmtList;
        //estList
        //@JSONFieldRequired
        private List<EstList> estList;
        private String status;


    }

    @Data
    public class ApBillList{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String organizationNo;
        //@JSONFieldRequired
        private String apNo;
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String bizPartnerNo;
        private String receiver;
        private String sourceType;
        private String sourceNo;
        private String sourceItem;
        private String sourceOrg;
        private String pluNo;
        private String spec;
        private String priceUnit;
        private String qty;
        private String billPrice;
        private String fee;
        private String oofNo;
        private String ooItem;
        private String departId;
        private String cateGory;
        private String isGift;
        private String bsNo;
        private String taxCode;
        private String taxRate;
        private String feeSubjectId;
        private String taxSubjectId;
        @JSONFieldRequired
        private String direction;
        private String isRevEst;
        private String apSubjectId;
        private String payDateNo;
        private String employeeNo;
        private String freeChars1;
        private String freeChars2;
        private String freeChars3;
        private String freeChars4;
        private String freeChars5;
        private String memo;
        private String currency;
        private String fCYPrice;
        private String exRate;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYTATAmt;
        private String fCYStdCostAmt;
        private String fCYActCostAmt;
        private String lCYPrice;
        private String lCYBTAmt;
        private String lCYTAmt;
        private String lCYTATAmt;
        private String lCYStdCostAmt;
        private String lCYActCostAmt;
        private String purOrderNo;
    }

    @Data
    public class ApBillSumList{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String organizationNo;
        //@JSONFieldRequired
        private String apNo;
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String bizPartnerNo;
        private String receiver;
        @JSONFieldRequired
        private String sourceType;
        @JSONFieldRequired
        private String sourceNo;
        @JSONFieldRequired
        private String sourceOrg;
        private String fee;
        private String departId;
        private String cateGory;
        private String isGift;
        private String taxRate;
        private String feeSubjectId;
        private String taxSubjectId;
        @JSONFieldRequired
        private String direction;
        private String isRevEst;
        private String apSubjectId;
        private String employeeNo;
        private String FreeChars1;
        private String FreeChars2;
        private String FreeChars3;
        private String FreeChars4;
        private String FreeChars5;
        private String currency;
        private String exRate;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYTATAmt;
        private String fCYStdCostAmt;
        private String fCYActCostAmt;
        private String lCYBTAmt;
        private String lCYTAmt;
        private String lCYTATAmt;
        private String lCYStdCostAmt;
        private String lCYActCostAmt;

    }

    @Data
    public class ApPerdList{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String corp;
        @JSONFieldRequired
        private String sourceOrg;
        @JSONFieldRequired
        private String organizationNo;
        //@JSONFieldRequired
        private String apNo;
        @JSONFieldRequired
        private String item;
        private String instPmtSeq;
        private String payType;
        private String payDueDate;
        private String billDueDate;
        @JSONFieldRequired
        private String direction;
        private String fCYReqAmt;
        private String currency;
        private String exRate;
        private String fCYRevsedRate;
        private String fCYTATAmt;
        private String fCYPmtRevAmt;
        private String revalAdjNum;
        private String lCYTATAmt;
        private String lCYPmtRevAmt;
        private String payDateNo;
        @JSONFieldRequired
        private String pmtCategory;
        private String purOrderNo;
        private String apSubjectId;
        private String invoiceNumber;
        private String invoiceCode;
        private String invoiceDate;

    }

    @Data
    public class ApWFList{
        @JSONFieldRequired
        private String corp;
        @JSONFieldRequired
        private String organizationNo;
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String wrtOffNo;
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String taskId;
        @JSONFieldRequired
        private String wrtOffType;
        private String sourceOrg;
        private String wrtOffBillNo;
        private String wrtOffBillitem;
        private String instPmtSeq;
        private String memo;
        private String bsNo;
        @JSONFieldRequired
        private String wrtOffDirection;
        private String apSubjectId;
        private String employeeNo;
        private String departId;
        private String cateGory;
        private String secRefNo;
        private String glNo;
        private String payDueDate;
        private String bizPartnerNo;
        private String receiver;
        private String FreeChars1;
        private String FreeChars2;
        private String FreeChars3;
        private String FreeChars4;
        private String FreeChars5;
        private String currency;
        private String exRate;
        private String fCYRevAmt;
        private String lCYRevAmt;
        private String fCYBTaxWrtOffAmt;
        private String lCYBTaxWrtOffAmt;
        private String invoiceNumber;
        private String invoiceCode;


    }

    @Data
    public class PmtList{
        @JSONFieldRequired
        private String corp;
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String wrtOffNo;
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String organizationNo;
        @JSONFieldRequired
        private String sourceOrg;
        @JSONFieldRequired
        private String taskId;
        @JSONFieldRequired
        private String wrtOffPmtType;
        private String paidBillNo;
        private String wrtOffItem;
        private String pmtCode;
        private String accountBillNo;
        private String transferredData;
        private String memo;
        private String bnkDepWdrawCode;
        private String cashChgCode;
        private String transInCustCode;
        private String transInPmtBillNo;
        @JSONFieldRequired
        private String wrtOffDirection;
        private String wrtOffSubject;
        private String payDueDate;
        private String receiver;
        private String salerAccount;
        private String salerAccountCode;
        private String saleName;
        private String FreeChars1;
        private String FreeChars2;
        private String FreeChars3;
        private String FreeChars4;
        private String FreeChars5;
        private String currency;
        private String exRate;
        private String fCYRevAmt;
        private String lCYRevAmt;
    }

    @Data
    public class EstList {
        @JSONFieldRequired
        private String corp;
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String sourceOrg;
        //@JSONFieldRequired
        private String apNo;
        @JSONFieldRequired
        private String item;
        private String glItem;
        private String taskId;
        private String wrtOffType;
        private String wrtOffQty;
        private String estBillNo;
        private String estBillItem;
        private String period;
        private String taxRate;
        private String WrtOffAPSubject;
        private String WrtOffBTaxSubject;
        private String WrtOffTaxSubject;
        private String wrtOffPrcDiffSubject;
        private String wrtOffExDiffSubject;
        private String departId;
        private String tradeCustomer;
        private String pmtCustomer;
        private String cateGory;
        private String employeeNo;
        private String FreeChars1;
        private String FreeChars2;
        private String FreeChars3;
        private String FreeChars4;
        private String FreeChars5;
        private String memo;
        private String fCYBillPrice;
        private String exRate;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYTATAmt;
        private String fCYWrtOffDiffAmt;
        private String lCYBillPrice;
        private String lCYBTAmt;
        private String lCYTAmt;
        private String lCYTATAmt;
        private String lCYPrcDiffAmt;
        private String lCYExDiffAmt;
    }

}
