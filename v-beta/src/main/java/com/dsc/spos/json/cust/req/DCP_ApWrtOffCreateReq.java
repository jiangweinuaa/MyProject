package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ApWrtOffCreateReq extends JsonBasicReq {

    private RequestLevel request;

    @NoArgsConstructor
    @Data
    public  class RequestLevel {
        private String taskId;
        private String accountId;
        private String apType;
        private String corp;
        private String wrtOffNo;
        private String bDate;
        private String organizationNo;
        private String accEmployeeNo;
        private String bizPartnerNo;
        private String receiver;
        private String fCYDRTATAmt;
        private String fCYCRTATAmt;
        private String lCYDRTATAmt;
        private String lCYCRTATAmt;
        private String sourceNo;
        private String glNo;
        private List<ApWFListLevel> apWFList;
        private List<PmtListLevel> pmtList;
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
    public  class PmtListLevel {
        private String corp;
        private String accountID;
        private String wrtOffNo;
        private String item;
        private String organizationNo;
        private String sourceOrg;
        private String taskId;
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
        private String wrtOffDirection;
        private String wrtOffSubject;
        private String payDueDate;
        private String receiver;
        private String salerAccount;
        private String salerAccountCode;
        private String saleName;
        @SerializedName("FreeChars1")
        private String freeChars1;
        @SerializedName("FreeChars2")
        private String freeChars2;
        @SerializedName("FreeChars3")
        private String freeChars3;
        @SerializedName("FreeChars4")
        private String freeChars4;
        @SerializedName("FreeChars5")
        private String freeChars5;
        private String currency;
        private String exRate;
        private String fCYRevAmt;
        private String lCYRevAmt;
    }

    @NoArgsConstructor
    @Data
    public  class ApWFListLevel {
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
        private String employeeNo;
        private String departId;
        private String cateGory;
        private String secRefNo;
        private String glNo;
        private String payDueDate;
        private String bizPartnerNo;
        private String receiver;
        @SerializedName("FreeChars1")
        private String freeChars1;
        @SerializedName("FreeChars2")
        private String freeChars2;
        @SerializedName("FreeChars3")
        private String freeChars3;
        @SerializedName("FreeChars4")
        private String freeChars4;
        @SerializedName("FreeChars5")
        private String freeChars5;
        private String currency;
        private String exRate;
        private String fCYRevAmt;
        private String lCYRevAmt;
        private String fCYBTaxWrtOffAmt;
        private String lCYBTaxWrtOffAmt;
        private String invoiceNumber;
        private String invoiceCode;
    }

}
