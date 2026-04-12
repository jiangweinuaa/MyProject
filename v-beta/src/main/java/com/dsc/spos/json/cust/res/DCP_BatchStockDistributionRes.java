package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_BatchStockDistributionRes extends JsonBasicRes {

    private DatasLevel datas;

    @NoArgsConstructor
    @Data
    public  class DatasLevel {
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String batchNo;
        private String wUnit;
        private String wUnitName;
        private String baseUnit;
        private String baseUnitName;
        private String unitRatio;
        private String totStockQty;
        private String top5StockQtyPercent;
        private String orgCnt;
        private String warehouseCnt;
        private List<DetailLevel> detail;


    }

    @NoArgsConstructor
    @Data
    public  class DetailLevel {
        private String organizationNo;
        private String organizationName;
        private String qty;
        private String lockQty;
        private String onLineQty;
        private String availableQty;
        private List<ChildrenLevel1> children;


    }


    @NoArgsConstructor
    @Data
    public  class ChildrenLevel1 {
        private String warehouseNo;
        private String warehouseName;
        private String qty;
        private String lockQty;
        private String onLineQty;
        private String availableQty;
        private List<ChildrenLevel2> children;


    }

    @NoArgsConstructor
    @Data
    public  class ChildrenLevel2 {
        private String location;
        private String locationName;
        private String qty;
        private String lockQty;
        private String onLineQty;
        private String availableQty;
    }

}
