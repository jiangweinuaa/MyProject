package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_AcctBankAccountQryRes extends JsonRes {


    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String accountCode;
        private String accName;
        private String accountName;
        private String organizationNo;
        private String orgName;
        private String bankaccount;
        private String bankName;
        private String relateOrg;
        private String status;
        private String currency;
        private String subjectId;
        private String subjectName;

        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;

        private AccountNameLang accountName_lang;
    }

    @NoArgsConstructor
    @Data
    public class AccountNameLang {

        private String langType;
        private String name;
    }

}
