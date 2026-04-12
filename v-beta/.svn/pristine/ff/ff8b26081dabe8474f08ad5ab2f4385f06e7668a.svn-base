package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_BankJournalQueryRes extends JsonRes {

    private Datas datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String accountCode;
        private String accName;
        private List<AccList> accList;
    }

    @NoArgsConstructor
    @Data
    public class AccList {
        private String beginAmt;
        private String item;
        private String organizationNo;
        private String orgName;
        private String sourceOrg;
        private String sourceOrgName;
        private String sourceNo;
        private String sourceNoSeq;
        private String bizPartnerNo;
        private String accountDate;
        private String direction;
        private String depWdrawCode;
        private String depWdrawName;
        private String taskId;
        private String exRate;
        private String currency;
        private String fCYAmt;
        private String payAmt;
        private String revAmt;
        private String glNo;
        private String endAmt;
        private String bizPartnerName;
    }
}
