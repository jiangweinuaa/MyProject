package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ARDocCreateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String accountId;
        private String arType;
        private String corp;
        private String pDate;
        private String organizationNo;
        private String accEmployeeNo;
        private String bizPartnerNo;
        private String receiver;
        private String taskId;
        private String payDateNo;
        private String payDueDate;
        private String taxCode;
        private String taxRate;
        private String inclTax;
        private String applicant;
        private String employeeNo;
        private String departId;
        private String sourceType;
        private String sourceNo;
        private String pendOffsetNo;
        private String feeSubjectId;
        private String apSubjectId;
        private String glNo;
        private String grpPmtNo;
        private String memo;
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

        private List<ArBillList> arBillList;
        private List<ArBillSumList> arBillSumList;
        private List<ArPerdList> arPerdList;
        private List<ArWFList> arWFList;
    }

    @NoArgsConstructor
    @Data
    public class ArBillList {
        private String accountId;
        private String organizationNo;
        private String arNo;
        private String item;
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
        private String price;
        private String fee;
        private String oofNo;
        private String ooItem;
        private String departId;
        private String cateGory;
        private String isGift;
        private String bsNo;
        private String taxRate;
        private String feeSubjectId;
        private String direction;
        private String isRevEst;
        private String apSubjectId;
        private String payDateNo;

        private String freeChars1;
        private String freeChars2;
        private String freeChars3;
        private String freeChars4;
        private String freeChars5;
        private String memo;
        private String currency;
        private String billPrice;
        private String exRate;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYTATAmt;
        private String iCYPrice;
        private String lCYBTAmt;
        private String lCYTAmt;
        private String lCYTATAmt;
        private String purOrderNo;
    }

    @NoArgsConstructor
    @Data
    public class ArBillSumList {
        private String accountID;
        private String organizationNo;
        private String apNo;
        private String item;
        private String bizPartnerNo;
        private String receiver;
        private String sourceType;
        private String sourceNo;
        private String sourceOrg;
        private String fee;
        private String departId;
        private String cateGory;
        private String isGift;
        private String taxRate;
        private String revSubjectId;
        private String arSubjectId;
        private String direction;
        private String isRevEst;
        private String freeChars1;
        private String freeChars2;
        private String freeChars3;
        private String freeChars4;
        private String freeChars5;
        private String currency;
        private String exRate;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYTATAmt;
        private String lCYBTAmt;
        private String lCYTAmt;
        private String lCYTATAmt;
        private String invCrncyBTAmt;
        private String invCrncyATAmt;
        private String classNo;
        private String advSubject;
        private String cardNo;
    }

    @NoArgsConstructor
    @Data
    public class ArPerdList {
        private String accountID;
        private String corp;
        private String sourceOrg;
        private String organizationNo;
        private String arNo;
        private String item;
        private String instPmtSeq;
        private String payType;
        private String payDueDate;
        private String billDueDate;
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
        private String pmtCategory;
        private String poNo;
        private String arSubjectId;
    }

    @NoArgsConstructor
    @Data
    public class ArWFList {
        private String corp;
        private String organizationNo;
        private String accountID;
        private String wrtOffNo;
        private String item;
        private String taskId;
        private String wrtOffType;
        private String sourceOrg;
        private String wrtOffBillNo;
        private String wrtOffBillitem;
        private String instPmtSeq;
        private String memo;
        private String bsNo;
        private String wrtOffDirection;
        private String apSubjectId;
        private String departId;
        private String cateGory;
        private String secRefNo;
        private String glNo;
        private String payDueDate;
        private String bizPartnerNo;
        private String receiver;
        private String freeChars1;
        private String freeChars2;
        private String freeChars3;
        private String freeChars4;
        private String freeChars5;
        private String currency;
        private String exRate;
        private String fCYRevAmt;
        private String lCYRevAmt;
        private String fCYBTaxWrtOffAmt;
        private String lCYBTaxWrtOffAmt;
        private String invoiceNumber;
        private String invoiceCode;
        private String billPrice;
        private String fee;
        private String direction;
        private String pendOffsetNo;
        private String apNo;
        private String advNo;
    }
}
