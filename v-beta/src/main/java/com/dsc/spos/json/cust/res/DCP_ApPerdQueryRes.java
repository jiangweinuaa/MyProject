package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ApPerdQueryRes extends JsonRes
{
    private List<DCP_ApPerdQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{
        private String accountId;
        private String apNo;
        private String accountName;
        private String apType;
        private String corp;
        private String corpName;
        private String pDate;
        private String organizationNo;
        private String organizationName;
        private String accEmployeeNo;
        private String accEmployeeName;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String receiver;
        private String taskId;
        private String payDateNo;
        private String payDueDate;
        private String taxCode;
        private String taxName;
        private String taxRate;
        private String inclTax;
        private String employeeNo;
        private String employeeName;
        private String departId;
        private String departName;
        private String sourceType;
        private String sourceNo;
        private String pendOffsetNo;
        private String feeSubjectId;
        private String apSubjectId;
        private String glNo;
        private String grpPmtNo;
        private String memo;
        private String payList;
        private String currency;
        private String currencyName;
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
        //private List<ApBillList> apBillList;
        private List<ApRerdList> apPerdList;
        //private List<ApPayList> apPayList;
        //private List<PmtList> pmtList;
        //private List<EstList> estList;

        private String status;
        private String createBy;
        private String createByName;
        private String create_Date;
        private String create_Time;
        private String modifyBy;
        private String modifyByName;
        private String modify_Date;
        private String modify_Time;
        private String confirmBy;
        private String confirmByName;
        private String confirm_Date;
        private String confirm_Time;
        private String cancelBy;
        private String cancelByName;
        private String cancel_Date;
        private String cancel_Time;

    }

    @Data
    public class ApBillList{
        private String accountId;
        private String accountName;
        private String organizationNo;
        private String organizationName;
        private String apNo;
        private String item;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String receiver;
        private String sourceType;
        private String sourceNo;
        private String sourceItem;
        private String sourceOrg;
        private String pluNo;
        private String pluName;
        private String spec;
        private String priceUnit;
        private String priceUnitName;
        private String qty;
        private String fCYPrice;
        private String fee;
        private String oofNo;
        private String ooItem;
        private String departId;
        private String departName;
        private String cateGory;
        private String cateGoryName;
        private String isGift;
        private String bsNo;
        private String taxCode;
        private String taxName;
        private String feeSubjectId;
        private String direction;
        private String isRevEst;
        private String apSubjectId;
        private String payDateNo;
        private String employeeNo;
        private String employeeName;
        private String freeChars1;
        private String freeChars2;
        private String freeChars3;
        private String freeChars4;
        private String freeChars5;
        private String memo;
        private String currency;
        private String currencyName;
        private String lCYPrice;
        private String billPrice;
        private String exRate;
        private String fCYBTAmt;
        private String fCYTAmt;
        private String fCYTATAmt;
        private String fCYStdCostAmt;
        private String fCYActCostAmt;
        private String iCYPrice;
        private String lCYBTAmt;
        private String lCYTAmt;
        private String lCYTATAmt;
        private String lCYStdCostAmt;
        private String lCYActCostAmt;
        private String purOrderNo;

    }

    @Data
    public class ApRerdList{
        private String accountID;
        private String accountName;
        private String corp;
        private String corpName;
        private String sourceOrg;
        private String organizationNo;
        private String organizationName;
        private String apNo;
        private String item;
        private String instPmtSeq;
        private String payType;
        private String payDueDate;
        private String billDueDate;
        private String direction;
        private String fCYReqAmt;
        private String currency;
        private String currencyName;
        private String exRate;
        private String fCYRevsedRate;
        private String fCYTATAmt;
        private String fCYPmtRevAmt;
        private String revalAdjNum;
        private String lCYTATAmt;
        private String lCYPmtRevAmt;
        private String payDateNo;
        private String pmtCategory;
        private String purOrderNo;
        private String apSubjectId;
        private String invoiceNumber;
        private String invoiceCode;
        private String invoiceDate;

    }

    @Data
    public class ApPayList{
        private String corp;
        private String corpName;
        private String organizationNo;
        private String organizationName;
        private String accountID;
        private String accountName;
        private String wrtOffNo;
        private String item;
        private String taskId;
        private String writeOffType;
        private String sourceOrg;
        private String wrtOffBillNo;
        private String wrtOffBillitem;
        private String instPmtSeq;
        private String memo;
        private String bsNo;
        private String wrtOffDirection;
        private String apSubjectId;
        private String employeeNo;
        private String employeeName;
        private String departId;
        private String cateGory;
        private String cateGoryName;
        private String secRefNo;
        private String glNo;
        private String payDueDate;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String receiver;
        private String FreeChars1;
        private String FreeChars2;
        private String FreeChars3;
        private String FreeChars4;
        private String FreeChars5;
        private String currency;
        private String currencyName;
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
        private String corp;
        private String corpName;
        private String accountID;
        private String accountName;
        private String wrtOffNo;
        private String item;
        private String organizationNo;
        private String organizationName;
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
        private String FreeChars1;
        private String FreeChars2;
        private String FreeChars3;
        private String FreeChars4;
        private String FreeChars5;
        private String currency;
        private String currencyName;
        private String exRate;
        private String fCYRevAmt;
        private String lCYRevAmt;
    }

    @Data
    public class EstList{
        private String accountID;
        private String accountName;
        private String sourceOrg;
        private String apNo;
        private String item;
        private String glItem;
        private String taskId;
        private String wrtOffType;
        private String wrtOffQty;
        private String estBillNo;
        private String estBillItem;
        private String period;
        private String taxCode;
        private String taxName;
        private String WrtOffAPSubject;
        private String WrtOffBTaxSubject;
        private String WrtOffTaxSubject;
        private String wrtOffPrcDiffSubject;
        private String wrtOffExDiffSubject;
        private String departId;
        private String departName;
        private String tradeCustomer;
        private String pmtCustomer;
        private String cateGory;
        private String cateGoryName;
        private String employeeNo;
        private String employeeName;
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
