package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_PurOrderBookingListQueryRes extends JsonRes {

    private List<Data> datas;


    @Getter
    @Setter
    public class Data {
        private String purOrderNo;
        private String item;
        private String item2;
        private String listImage;
        private String pluNo;
        private String pluName;
        private String spec;
        private String pluBarcode;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUnitName;
        private String purQty;
        private String bookQty;
        private String canBookQty;
        private String refPurPrice;
        private String refPurAmt;
        private String purType;
        private String supplier;
        private String supplierName;
        private String distriCenter;
        private String distriCenterName;
        private String receiptOrgNo;
        private String receiptOrgName;
        private String bDate;
        private String receivePrice;
        private String receiveAmt;
        private String category;
        private String categoryName;

        private String taxCode;
        private String taxRate;
        private String inclTax;
        private String taxCalType;

    }


}
