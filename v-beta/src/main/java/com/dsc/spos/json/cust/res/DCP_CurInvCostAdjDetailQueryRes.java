package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_CurInvCostAdjDetailQueryRes extends JsonBasicRes {

    private String status;
    private String accountID;
    private String account;
    private String year;
    private String period;
    private String cost_Calculation;
    private String dataSource;
    private String referenceNo;

    private List<InvList> invList;

    @NoArgsConstructor
    @Data
    public class InvList {
        private String item;
        private String costDomainId;
        private String costDomainDis;
        private String pulNo;
        private String pluName;
        private String featureNo;
        private String baseUnitName;
        private String bsName;
        private String category;
        private String categoryName;
        private String qty;
        private String totPretaxAmt;
        private String pretaxMaterial;
        private String pretaxLabor;
        private String pretaxOem;
        private String pretaxExp1;
        private String pretaxExp2;
        private String pretaxExp3;
        private String pretaxExp4;
        private String pretaxExp5;
        private String memo;
    }

}
