package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurOrderCreateReq extends JsonBasicReq {

    private DCP_PurOrderCreateReq.levelElm request;

    @Data
    public class levelElm{

        private String purOrgNo;
        private String bdate;
        private String supplier;
        private String payType;
        private String payOrgNo;
        private String billdateNo;
        private String paydateNo;
        private String invoiceCode;
        private String currency;
        private String purEmpNo;
        private String purDeptNo;
        private String purType;
        private String sourceType;
        private String sourceBillNo;
        private String sourceOrgNo;
        private String receiptOrgNo;
        private String distriCenter;
        private String address;
        private String telephone;
        private String contact;
        private String expireDate;
        private String is_prePay;
        private String memo;
        private String version;
        private String taxCode;
        private String payee;

        private String corp;
        private String receiptCorp;

        private String taxPayerType;
        private String inputTaxCode;
        private String inputTaxRate;

        private List<DCP_PurOrderCreateReq.PayList> payList;
        private List<DCP_PurOrderCreateReq.GoodsList> goodsList;
        private List<DCP_PurOrderCreateReq.SourceBillList> sourceBillList;

    }


    @Data
    public class PayList{
        private String item;
        private String payType;
        private String amount;
    }

    @Data
    public class GoodsList{
        private String item;
        private String pluNo;
        private String featureNo;
        private String barcode;
        private String category;
        private String taxCode;
        private String taxRate;
        private String incltax;
        private String purPrice;
        private String purUnit;
        private String purQty;
        private String purAmt;
        private String preTaxAmt;
        private String taxAmt;
        private String arrivalDate;
        private String multiDate;
        private List<DCP_PurOrderCreateReq.MultiDatedetail> multiDateDetail;
        private String is_qualityCheck;
        private String purTemplateNo;
        private String baseUnit;
        private String unitRatio;
        private String taxCalType;
        private String isGift;

        private String receivePrice;
        private String receiveAmt;
        private String supPrice;
        private String supAmt;

    }

    @Data
    public class MultiDatedetail{
        //private String item2;
        private String purQty;
        private String arrivalDate;
    }

    @Data
    public class SourceBillList{
        private String item;
        private String sourceBillNo;
        private String oItem;
        private String pluNo;
        private String featureNo;
        private String oUnit;
        private String oQty;
        private String purUnit;
        private String convertPurQty;
        private String purQty;
    }

}
