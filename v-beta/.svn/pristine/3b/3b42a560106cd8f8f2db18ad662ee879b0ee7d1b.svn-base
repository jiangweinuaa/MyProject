package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_SupplierGoodsOpenQryRes extends JsonRes {
    private List<DCP_SupplierGoodsOpenQryRes.level1Elm> datas;

    @Data
    public class level1Elm {
        private String purTemplateNo;
        private String preDays;
        private String supplier;
        private String supplierName;
        private String purType;
        private String pluBarcode;
        private String pluNo;
        private String pluName;
        private String spec;
        private String pluType;
        private String taxCode;
        private String taxName;
        private String taxRate;
        private String inclTax;
        private String purUnit;
        private String purUdLength;
        private String purUnitName;
        private String priceType;
        private String purPrice;
        private String refPurPriceBaseUnit;
        private String minpurQty;
        private String mulpurQty;
        private String status;
        private String wunit;
        private String wunitName;
        private String nonArrivalQty;
        private String stockQty;
        private String minRate;
        private String maxRate;
        private String taxCalType;
        private List<DCP_SupplierGoodsOpenQryRes.FeatureList> featureList;
        private List<PurPriceList> purPriceList;
        private String lastStockInPrice;
        private String lastStockInQty;
        private String baseUnit;
        private String baseUnitName;
        private String category;
        private String categoryName;

        private String unitRatio;
        private String baseUnitUdLength;

        private String shelfLife;
        private String isBatch;
        private String price;
        private String isQualityCheck;
    }

    @Data
    public class FeatureList{
        private String featureNo;
        private String featureName;
        private String nonArrivalQty;
        private String stockQty;
        private String lastStockInPrice;
        private String lastStockInQty;
    }

    @Data
    public class PurPriceList{
        private String beginQty;
        private String endQty;
        private String purPrice;

    }

}
