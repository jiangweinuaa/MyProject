package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockOutNoticePendingDetailQueryRes extends JsonRes {

    private List<DCP_StockOutNoticePendingDetailQueryRes.DataDetail> datas;

    @Data
    public  class DataDetail {
        private String noticeNo;
        private String noticeItem;
        private String objectId;
        private String objectName;
        private String templateNo;
        private String templateName;
        private String pluNo;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String pluBarcode;
        private String warehouse;
        private String warehouseName;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private String stockOutQty;
        private String canStockOutQty;

        private String taxCode;
        private String taxRate;
        private String inclTax;
        private String taxCalType;


        private String baseUnitName;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String ofNo;
        private String oItem;
        private String price;
        private String amt;
        private String retailPrice;
        private String retailAmt;
    }



}
