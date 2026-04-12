package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ArPerdQueryRes extends JsonRes {

    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String accountId;
        private String apType;
        private String corp;
        private String pDate;
        private String organizationNo;
        private String bizPartnerNo;
        private String receiver;
        private String taskId;
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

        private List<ArPerdList> arPerdList;
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
        private String invoiceNumber;
        private String invoiceCode;
        private String invoiceDate;
    }

}
