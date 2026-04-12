package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_BankAccountQueryRes extends JsonRes {

    private List<DCP_BankAccountQueryRes.Account> datas;

    @Getter
    @Setter
    public class Account {

        private String accountCode;
        private String accountName;
        private String organizationNo;
        private String bankNo;
        private String bankName;
        private String bankAccount;
        private String relateOrg;
        private String status;
        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastModifyID;
        private String lastModifyName;
        private String lastModify_datetime;
        private String orgName;
        private String accName;

        private List<DCP_BankAccountQueryRes.AccountLang> accountName_lang;


    }

    @Getter
    @Setter
    public class AccountLang {
        private String langType;
        private String name;
    }
}
