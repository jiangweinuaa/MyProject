package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PendingPOrderDetailRes extends JsonBasicRes {


    private List<DatasLevel> datas;

    @NoArgsConstructor
    @Data
    public  class DatasLevel {
        private String pOrderNo;
        private String item;
        private String pluBarcode;
        private String pluNo;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String category;
        private String categoryName;
        private String pUnit;
        private String pUnitName;
        private String baseUnit;
        private String unitRatio;
        private String poQty;
        private String pQty;
        private String noQty;
        private String stockOutQty;
        private String distriPrice;
        private String price;
    }
}
