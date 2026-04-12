package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_InterSettBillDetailQueryRes extends JsonBasicRes {

    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String corp;
        private String billNo;
        private String bDate;
        private String bizpartner1;
        private String bizpartner2;
        private String settAccPeriod;
        private String startDate;
        private String endDate;
        private String employeeId;
        private String departId;
        private String amt;
        private String arNo;
        private String apNo;
        private List<Detail> detail;
    }

    @NoArgsConstructor
    @Data
    public class Detail {
        private String item;
        private String organizationNo;
        private String bType;
        private String interTradeType;
        private String sourceNo;
        private String sourceNoSeq;
        private String rDate;
        private String fee;
        private String pluNo;
        private String currency;
        private String taxRate;
        private String direction;
        private String preTaxAmt;
        private String amt;
        private String unsettAmt;
        private String settledAmt;
        private String curSettledAmt;
        private String invOrg;
        private String invCorp;
        private String warehouse;
        private String bizpartnerNo;
        private String intertradeOrg;
        private String intertradeCorp;
        private String settAccPeriod;
        private String year;
        private String period;
        private String accountOrg;
        private String corp;
        private String glType;
        private String postedAmt;
        private String oItem;
        private String inOutCode;
        private String qty;
        private String price;
        private String baseUnit;
    }
}

