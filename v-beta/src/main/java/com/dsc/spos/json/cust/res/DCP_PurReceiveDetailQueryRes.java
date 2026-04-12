package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_PurReceiveDetailQueryRes extends JsonRes {

    private List<Data> datas;


    @Getter
    @Setter
    public class Data {
        private String status;
        private String billNo;
        private String orgNo;
        private String orgName;
        private String bDate;
        private String supplier;
        private String supplierName;
        private String payType;
        private String payOrgNo;
        private String payOrgName;
        private String memo;
        private String billDateNo;
        private String billDateDesc;
        private String payDateNo;
        private String payDateDesc;
        private String invoiceCode;
        private String invoiceName;
        private String currency;
        private String currencyName;
        private String purOrgNo;
        private String purOrgName;
        private String purOrderNo;
        private String expireDate;
        private String receivingNo;
        private String receiptDate;
        private String wareHouse;
        private String wareHouseName;
        private String totCqty;
        private String totPqty;
        private String totPassQty;
        private String totStockInQty;
        private String totCanStockInQty	;
        private String totCanStockInCqty;
        private String totPurAmt;
        private String qcStatus;
        private String isLocation;
        private String purType;
        private String employeeID;
        private String employeeName;
        private String departID;
        private String departName;
        private String createBy;
        private String createByName;
        private String createDateTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyDateTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmDateTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelDateTime;
        private String ownOpId;
        private String ownOpName;
        private String ownDeptId;
        private String ownDeptName;
        private String payee;
        private String payeeName;
        private String corp;
        private String corpName;
        private String bizOrgNo;
        private String bizOrgName;
        private String bizCorp;
        private String bizCorpName;

        private String totReceiveAmt;
        private String totSupAmt;

        private String taxPayerType;
        private String inputTaxCode;
        private String inputTaxRate;

        private List<Detail> detail;

    }

    @Getter
    @Setter
    public class Detail{

        private String item;
        private String receivingNo;
        private String rItem;
        private String purOrderNo;
        private String poItem;
        private String poItem2;
        private String pluNo;
        private String pluName;
        private String pluBarcode;
        private String spec;
        private String featureNo;
        private String featureName;
        private String isBatch;
        private String category;
        private String categoryName;
        private String wareHouse;
        private String wareHouseName;
        private String pUnit;
        private String pUnitName;
        private String pQty;
        private String baseUnit;
        private String baseUnitName;
        private String purPrice;
        private String isGift;
        private String qcStatus;
        private String batchNo;
        private String prodDate;
        private String expDate;
        private String passQty;
        private String rejectQty;
        private String stockInQty;
        private String canStockInQty;
        private String purTemplateNo;
        private String memo;
        private String shelfLife;
        private String canReceiveQty;
        private String baseQty;
        private String unitRatio;
        private String taxCode;
        private String taxName;
        private String taxRate;
        private String inclTax;
        private String purAmt;
        private String taxCalType;
        private String receivePrice;
        private String receiveAmt;
        private String supPrice;
        private String supAmt;
        private String procRate;
    }

}
