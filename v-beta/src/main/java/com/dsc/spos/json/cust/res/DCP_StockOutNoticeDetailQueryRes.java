package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
 
 

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_StockOutNoticeDetailQueryRes extends JsonRes {

	@Getter
    @Setter
    private List<DataDetail> datas;

    @Getter
    @Setter
    public  class DataDetail {

    	private String status;
    	private String orgNo;
    	private String orgName;
    	private String billType;
    	private String billNo;
    	private String sourceType;
    	private String sourceBillNo;
    	private String deliverOrgNo;
    	private String deliverOrgName;
    	private String bDate;
    	private String rDate;
    	private String objectType;
    	private String objectId;
    	private String objectName;
    	private String payType;
    	private String payOrgNo;
    	private String payOrgName;
    	private String billDateNo;
    	private String billDateDesc;
    	private String payDateNo;
    	private String payDateDesc;
    	private String invoiceCode;
    	private String invoiceName;
    	private String currency;
    	private String currencyName;
    	private String returnType;
    	private String wareHouse;
    	private String wareHouseName;
    	private String memo;
    	private String totCqty;
    	private String totPqty;
    	private String totStockOutQty;
    	private String totAmt;
    	private String totPreTaxAmt;
    	private String totTaxAmt;
    	private String employeeID;
    	private String employeeName;
    	private String departID;
    	private String departName;
    	private String createOpID;
    	private String createOpName;
    	private String createDateTime;
    	private String lastModiOpID;
    	private String lastModiOpName;
    	private String modifyDateTime;
    	private String confirmBy;
    	private String confirmByName;
    	private String confirmDateTime;
    	private String cancelBy;
    	private String cancelByName;
    	private String cancelDateTime;
    	private String ownOpID;
    	private String ownOpName;
    	private String ownDeptID;
    	private String ownDeptName;
		private String isLocation;
		private String isBatchManage;
		private String deliveryAddress;
		private String totRetailAmt;
		private String receiptWarehouse;
		private String receiptWarehouseName;
		private String invWarehouse;
		private String invWarehouseName;
		private String isTranInConfirm;
		private String receiptWHIsLocation;//收货仓库启用库位
		
		private String createDeptId;
    	private String createDeptName;
    	private String closeBy;
    	private String closeByName;
    	private String closeTime;

		private String deliveryDate;
		private List<DetailList> dataList;
		private List<MultiList>  multilotsList;
		private String templateNo;
		private String templateName;

		private String payee;
		private String payer;
		private String payeeName;
		private String payerName;

		private String corp;
		private String corpName;
		private String receiptCorp;
		private String receiptCorpName;
		private String deliveryCorp;
		private String deliveryCorpName;

    }

    @Getter
	@Setter
	public class DetailList {
    	private String item;
    	private String sourceType;
    	private String sourceBillNo;
    	private String oItem;
    	private String pluNo;
    	private String pluName;
    	private String spec;
    	private String pluBarcode;
    	private String featureNo;
    	private String featureName;
    	private String pUnit;
    	private String pUnitName;
    	private String pQty;
    	private String canReturnQty;
		private String canDeliverQty;
    	private String price;
    	private String amount;
    	private String preTaxAmt;
    	private String taxAmt;
    	private String taxCode;
    	private String taxName;
    	private String taxRate;
    	private String stockOutQty;
    	private String baseUnit;
    	private String baseUnitName;
    	private String baseQty;
    	private String wUnit;
    	private String wUnitName;
    	private String wQty;
    	private String status;
    	private String bsNo;
    	private String bsName;
    	private String memo;
    	private String availableStockQty;
    	private String canStockOutQty;
    	private String listImage;
		private String isBatch;
		private String isGift;
		private String templateNo;
		private String unitRatio;
		private String taxCalType;
		private String baseUnitUdLength;
		private String inclTax;
    	private String pUnitUdLength;
		private String retailPrice;
		private String retailAmt;
		private String objectType;
		private String objectId;
		private String objectName;
		private String poQty;
		private String noQty;

		private String receivePrice;
		private String receiveAmt;
		private String supPrice;
		private String supAmt;
		private String refPurPrice;
		private String category;

	}
    @Getter
   	@Setter
   	public class MultiList {
    	private String item;
    	private String item2;
    	private String pluNo;
    	private String pluName;
    	private String spec;
    	private String featureNo;
    	private String featureName;
    	private String pUnit;
    	private String pUnitName;
    	private String pQty;
    	private String location;
    	private String locationName;
    	private String batchNo;
    	private String wUnit;
    	private String wUnitName;
    	private String prodDate;
    	private String expDate;
    	

    }
  

 
}
