package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
 

import lombok.Getter;
import lombok.Setter;

/**
 *  
 * 
 * @date 2024-11-19
 * @author 01029
 */
@Getter
@Setter
public class DCP_StockOutNoticeUpdateReq extends JsonBasicReq {
    @JSONFieldRequired
	private levelRequest request;

	@Getter
	@Setter
	public class levelRequest {
		@JSONFieldRequired (display = "制单日期")
		private String bDate;
		@JSONFieldRequired (display = "申请人员ID")
		private String employeeID;
		@JSONFieldRequired (display = "申请部门ID")
		private String departID;
		@JSONFieldRequired (display = "单据类型")
		private String billType;
		@JSONFieldRequired (display = "出货对象类型")
		private String objectType;
		//@JSONFieldRequired (display = "出货对象编号")
		private String billNo;
		private String objectId;
		private String payType;
		private String payOrgNo;
		private String billDateNo;
		private String payDateNo;
		private String invoiceCode;
		private String currency;
		private String deliverOrgNo;
		private String wareHouse;
		private String returnType;
		private String rDate;
		private String sourceType;
		private String sourceBillNo;
		private String memo;
		private String deliveryAddress;
		private String deliveryDate;
		private String receiptWarehouse;
		private String invWarehouse;
		private String isTranInConfirm;
		private String totCqty;
		private String totPqty;
		private String totAmt;
		private String totPreTaxAmt;
		private String totTaxAmt;
		private String totRetailAmt;
		private List<Detail1> dataList;
		private String templateNo;

		private String payee;
		private String payer;

		private String corp;
		private String deliveryCorp;
		private String receiptCorp;


	}

	@Getter
	@Setter
	public class Detail1 {
		@JSONFieldRequired (display = "项次")
		private String item;
		@JSONFieldRequired (display = "品号")
		private String pluNo;
		@JSONFieldRequired (display = "特征码")
		private String featureNo;
		@JSONFieldRequired (display = "条码")
		private String pluBarcode;
		@JSONFieldRequired (display = "交易单价")
		private String price;
		@JSONFieldRequired (display = "交易单位")
		private String pUnit;
		@JSONFieldRequired (display = "交易数量")
		private String pQty;
		@JSONFieldRequired (display = "交易含税金额")
		private String amount;
		private String bsNo;
		private String sourceType;
		private String sourceBillNo;
		private String oItem;
		private String templateNo;
		private String memo;
		private String baseUnit;
		private String wUnit;
		private String retailPrice;
		private String retailAmt;
	 
		private String baseQty;
		private String unitRatio;  
		private String preTaxAmt;
		private String taxAmt;
		private String taxCode;  
		private String taxRate;
		private String inclTax;
		private String taxCalType;
		private String objectType;
		private String objectId;
		private String isGift;
		private String noQty;
		private String poQty;

		private String receivePrice;
		private String receiveAmt;

		private String refPurPrice;
		private String supPrice;
		private String supAmt;
		private String category;

		private String receiptCorp;
	}
 

}
