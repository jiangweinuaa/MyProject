package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_BankPayCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String status;
        private String corp;
        private String cmNo;
        private String accountCode;
        private String depWdrawCode;
        private String bDate;
        private String organizationNo;
        private String employeeNo;
        private String departId;
        private String bizPartnerNo;
        private String payEmployee;
        private String isEnty;
        private String glNo;
        private String memo;
        private List<CmList> cmList;
    }

    @NoArgsConstructor
    @Data
    public class CmList {
        private String item;
        private String taskId;
        private String organizationNo;
        private String classType;
        private String classNo;
        private String depWdrawCode;
        private String cfCode;
        private String payBankCode;
        private String currency;
        private String exRate;
        private String fCYAmt;
        private String lCYAmt;
        private String sourceNo;
        private String sourceNoSeq;
        private String offsetSubject;
        private String subjectId;
        private String pendOffsetNo;
        private String memo;
    }

}
