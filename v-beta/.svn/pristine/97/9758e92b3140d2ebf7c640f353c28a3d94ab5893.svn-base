package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurOrderDetailQueryRes extends JsonRes {

    private List<DCP_PurOrderDetailQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{

        private String purOrderNo;
        private String purOrgNo;
        private String purOrgName;
        private String bDate;
        private String supplier;
        private String supplierName;
        private String receiveOrgNo;
        private String receiveOrgName;
        private String expireDate;
        private String purType;
        private String purEmpID;
        private String purEmpName;
        private String purDeptID;
        private String purDeptName;
        private String payType;
        private String payOrgNo;
        private String payOrgName;
        private String billdateNo;
        private String billdateDesc;
        private String paydateNo;
        private String paydateDesc;
        private String invoiceCode;
        private String invoiceName;
        private String currency;
        private String currencyName;
        private String receiveStatus;
        private String totcQty;
        private String totpQty;
        private String totrQty;
        private String totsQty;
        private String totnQty;
        private String tot_purAmt;
        private String tot_preTaxAmt;
        private String tot_TaxAmt;
        private String rate;
        private String status;
        private String memo;

        private String distriCenter;
        private String distriCenterName;
        private String address;
        private String contact;
        private String contactName;
        private String telephone;
        private String sourceType;
        private String sourceBillNo;
        private String isPrePay;
        private String version;
        private String taxCode;
        private String taxRate;
        private String taxName;

        private String ownerID;
        private String ownerName;
        private String ownDeptID;
        private String ownDeptName;
        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;
        private String confirmID;
        private String confirmName;
        private String confirm_datetime;
        private String cancelBy;
        private String cancelByName;
        private String cancel_datetime;
        private String closeBy;
        private String closeByName;
        private String closeBy_datetime;
        private String payee;
        private String payeeName;

        private String corp;
        private String corpName;
        private String receiptCorp;
        private String receiptCorpName;

        private String receiptOrgNo;
        private String receiptOrgName;

        private String taxPayerType;
        private String inputTaxCode;
        private String inputTaxRate;

        private List<Detail> detail;
        private List<PayList> payList;

    }

    @Data
    public class Detail{

        private String item;
        private String pluNo;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String pluBarCode;
        private String category;
        private String categoryName;
        private String purUnit;
        private String purUnitName;
        private String purQty;
        private String purPrice;
        private String purAmt;
        private String preTaxAmt;
        private String taxAmt;
        private String taxCode;
        private String taxName;
        private String taxRate;
        private String inclTax;
        private String arrivalDate;
        private String multiDate;
        private List<MultiDetail> multiDateDetail;
        private String is_qualityCheck;
        private String stockQty;
        private String nonArrivalQty;
        private String closeStatus;
        private String wunit;
        private String wunitName;
        private String wunitQty;
        private String image;
        private String mulPurQty;
        private String taxCalType;
        private String purTemplateNo;
        private String baseUnit;
        private String baseUnitName;
        private String unitRatio;
        private String baseQty;
        private String isGift;
        private String priceType;
        private List<PurPriceList> purPriceList;

        //receivePrice,receiveAmt,supPrice,supAmt;
        private String receivePrice;
        private String receiveAmt;
        private String supPrice;
        private String supAmt;
    }

    @Data
    public class PurPriceList{
        private String beginQty;
        private String endQty;
        private String purPrice;
    }

    @Data
    public class MultiDetail{

        private String item2;
        private String purQty;
        private String arrivalDate;
    }

    @Data
    public class purOrderInfo{
        private String purOrderNo;
        private String receiveStatus;
        private String sh;
        private String rk;
        private String dhl;
    }

    @Data
    public class PayList{
        private String item;
        private String payType;
        private String purAmt;
        private String payBillNo;
        private String payAmt;

    }
}
