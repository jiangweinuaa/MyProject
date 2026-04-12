package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_AcctStockMthQueryRes extends JsonRes {

    private Datas datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String accountID;
        private String accountName;
        private String year;
        private String period;
        private String isDiffQty;
        private String totCostQty;
        private String totStockQty;
        private String totDiffQty;
        private List<QtyList> qtyList;
    }

    @NoArgsConstructor
    @Data
    public class QtyList {
        private String costDomainId;
        private String costDomainIdName;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String spec;
        private String baseUnit;
        private String baseUnitName;
        private String costQty;
        private String stockQty;
        private String diffQty;
    }

}

