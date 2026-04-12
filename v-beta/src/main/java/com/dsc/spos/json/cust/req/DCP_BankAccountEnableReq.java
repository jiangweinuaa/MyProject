package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_BankAccountEnableReq extends JsonBasicReq {
    private levelRequest request;


    @Getter
    @Setter
    public class levelRequest {
        private String oprType;//操作类型：1-启用 2-禁用
        private List<Account> accountList;
    }

    @Getter
    @Setter
    public class Account {
        private String accountCode;
    }

}
