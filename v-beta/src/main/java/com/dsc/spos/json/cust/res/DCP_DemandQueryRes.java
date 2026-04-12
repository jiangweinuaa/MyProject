package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_DemandQueryRes extends JsonRes
{
    private List<DCP_DemandQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm{
        private String objectType;
        private String objectId;
        private String objectName;
        private String orderNo;
        private String orderItem;
        private String pluBarcode;
        private String pluNo;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String category;
        private String categoryName;
        private String oUnit;
        private String oUnitName;
        private String oQty;
        private String toPoQty;
        private String canPoQty;
        private String purQty;
        private String purUnit;
        private String purUnitName;
        private String purUnitUdLength;
        private String purUnitRoundType;
        private String baseUnit;
        private String baseUnitName;
        private String pUnitRatio;
        private String oUnitRatio;
        private String minPurQty;
        private String mulPurQty;
        private String purPrice;
        private String taxCode;
        private String taxName;
        private String taxRate;
        private String inclTax;
        private String taxCalType;
        private String supplier;
        private String supplierName;
        private String beginDate;
        private String endDate;
        private String preDays;
        private String purTemplateNo;
        private String purType;
        private String purCenter;
        private String purCenterName;
        private String purCenterCorp;
        private String purCenterCorpName;
        private String payType;
        private String payCenter;
        private String payCenterName;
        private String billDateNo;
        private String billDateDesc;
        private String payDateNo;
        private String payDateDesc;
        private String invoiceCode;
        private String invoiceName;
        private String currency;
        private String currencyName;
        private String distriOrgNo;
        private String distriOrgName;
        private String distriOrgAddress;
        private String distriOrgContact;
        private String distriOrgTelePhone;
        private String receiptAddress;
        private String receiptContact;
        private String receiptTelephone;

    }
}
