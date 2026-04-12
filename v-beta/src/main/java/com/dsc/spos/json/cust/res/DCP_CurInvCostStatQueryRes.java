package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;


@Data
public class DCP_CurInvCostStatQueryRes extends JsonRes {

    private List<ChkList> chkList;

    @Data
    public class ChkList {
        private String corp;
        private String corpName;
        private String year;
        private String period;
        private String accountID;
        private String account;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String baseUnit;
        private String baseUnitName;
        private String costDomainID;
        private String costDomainName;
        private String curAvgPrice;
        private String lastBalQty;
        private String lastBalAmt;
        private String curInQty;
        private String curInAmt;
        private String curOutQty;
        private String curOutAmt;
        private String endingBalQty;
        private String endingBalAmt;
    }
}
