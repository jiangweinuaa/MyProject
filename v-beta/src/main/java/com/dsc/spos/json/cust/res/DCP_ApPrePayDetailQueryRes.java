package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ApPrePayDetailQueryRes extends JsonRes {

    private DCP_ApPrePayDetailQueryRes.Datas datas;

    @Data
    public class Datas{

        private String accountId;
        private String account;
        private String apType;
        private String corp;
        private String corpName;
        private String organizationNo;
        private String organizationName;
        private String pDate;
        private String accEmployeeNo;
        private String accEmployeeName;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String receiver;//应该是bizpartner
        private String receiverName;
        private String taskId;
        private String payDateNo;
        private String payDueDate;
        private String taxCode;
        private String taxName;
        private String taxRate;
        private String inclTax;
        private String applicant;
        private String employeeNo;
        private String employeeName;
        private String departId;
        private String departName;
        private String sourceType;
        private String sourceNo;
        private String pendOffsetNo;
        private String feeSubjectId;
        private String feeSubjectName;
        private String apSubjectId;
        private String apSubjectName;
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
        private String lCYPmtAmt;
        private String fCYPmtAmt;

        private List<ApPredList> apPredList;
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
    public class ApPredList{
        private String accountID;
        private String accountName;
        private String corp;
        private String corpName;
        private String sourceOrg;
        private String sourceOrgName;
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
        private String apSubjectName;
        private String invoiceNumber;
        private String invoiceCode;
        private String invoiceDate;


    }


}
