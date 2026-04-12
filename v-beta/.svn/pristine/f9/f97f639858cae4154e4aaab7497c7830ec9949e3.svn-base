package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_BankAccountUpdateReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    @Getter
    @Setter
    public class levelRequest {
        private String accountCode;

        private String organizationNo;

        private String bankCode;
        private String bankAccount;
        private String accountName;
        private String status;

        private List<DCP_BankAccountUpdateReq.level1Elm> accountName_lang;

    }

    @Getter
    @Setter
    public class level1Elm {

        private String name;
        private String langType;

    }


}