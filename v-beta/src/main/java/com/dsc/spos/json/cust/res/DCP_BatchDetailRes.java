package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_BatchDetailRes extends JsonRes {


    private List<DatasLevel> datas;

    @NoArgsConstructor
    @Data
    public  class DatasLevel {
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String spec;
        private String category;
        private String categoryName;
        private String wUnit;
        private String wUnitName;
        private String batchNo;
        private String productDate;
        private String loseDate;
        private String supplierType;
        private String supplierId;
        private String supplierName;
        private String billType;
        private String billNo;
        private String produceArea;
        private String manufacturer;
        private String totStockQty;
        private String status;
        private String isShelfLifeCheck;
        private String shelfLife;
        private String remainShelfLife;
        private String remainShelfLifeRatio;
        private String batchRules;
        private String createOpId;
        private String createOpName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiName;
        private String lastModiTime;
    }
}
