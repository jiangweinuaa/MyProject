package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_BankQueryRes extends JsonRes {

    @Getter
    @Setter
    private List<Bank> datas;

    @Getter
    @Setter
    public static class Bank {

        private String nation;

        private String bankcode;
        private String ebankCode;
        private String bankname_s; //简称
        private String bankname_f; //全称

        private String relatePartner;//关联交易对象数
        private String relateAccount;//关联银行账户数

        private String status;

        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;

        private List<BanksName> banksname;

        private List<BankfName> bankfname;

    }

    @Getter
    @Setter
    public static class BanksName{
        private String langType;
        private String name;
    }

    @Getter
    @Setter
    public static class BankfName{
        private String langType;
        private String name;
    }

}
