package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务OrderStatementGet ,对账查询
 * @author 08546
 */
public class DCP_OrderStatementQueryRes extends JsonRes {
	/**
	 * 
	 */
	private List<Data> datas;

	public List<Data> getDatas() {
		return datas;
	}

	public void setDatas(List<Data> datas) {
		this.datas = datas;
	}

	public class Data
	{
		private String eId;
		private String shopId;
		private String shopName;
		private String thirdShop;
		private String thirdShopName;
		private String orderNo;
		private String orderType;
		private String status;
		//退单交易号
		private String orderNo_refund;
		//POS单号
		private String posSaleNo;
		//交易类型
		private String typeName;
		private String thirdCreateTime;
		private String thirdCompleteTime;
		private String thirdStatementDate;
		private String thirdSettlementDate;
		private String thirdType;
		private String thirdOrderAmt;
		private String thirdPromotionAmt;
		private String thirdPaidAmt;
		private String thirdCommissionAmt;
		private String thirdSettlementAmt;
		private String thirdRefundAmt;
		private String thirdSettlementNo;
		private String thirdPaymentNo;
		private String thirdAccountDate;
		private String orderSettlementAmt;
		private String posSettlementAmt;
		private String dataCreatedBy;
		private String dataCreatedDate;
		private String dataModifiedBy;
		private String lastModifiedDate;
		private String diversityType1;
		private String diversityType2;
		private String diversityAmt1;
		private String diversityAmt2;
		private String diversityReason;
		private String accountStatus;
		private String customField1;
		private String customField2;
		private String customField3;
		private String customField4;
		private String customField5;
		private String customField6;
		private String customField7;
		private String customField8;
		private String customField9;
		private String customField10;

		public String getPosSaleNo() {
			return posSaleNo;
		}
		public void setPosSaleNo(String posSaleNo) {
			this.posSaleNo = posSaleNo;
		}
		public String getTypeName() {
			return typeName;
		}
		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
		public String getThirdRefundAmt() {
			return thirdRefundAmt;
		}
		public void setThirdRefundAmt(String thirdRefundAmt) {
			this.thirdRefundAmt = thirdRefundAmt;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getThirdShop() {
			return thirdShop;
		}
		public void setThirdShop(String thirdShop) {
			this.thirdShop = thirdShop;
		}
		public String getThirdShopName() {
			return thirdShopName;
		}
		public void setThirdShopName(String thirdShopName) {
			this.thirdShopName = thirdShopName;
		}
		public String getOrderNo() {
			return orderNo;
		}
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		public String getOrderType() {
			return orderType;
		}
		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getThirdCreateTime() {
			return thirdCreateTime;
		}
		public void setThirdCreateTime(String thirdCreateTime) {
			this.thirdCreateTime = thirdCreateTime;
		}
		public String getThirdCompleteTime() {
			return thirdCompleteTime;
		}
		public void setThirdCompleteTime(String thirdCompleteTime) {
			this.thirdCompleteTime = thirdCompleteTime;
		}
		public String getThirdStatementDate() {
			return thirdStatementDate;
		}
		public void setThirdStatementDate(String thirdStatementDate) {
			this.thirdStatementDate = thirdStatementDate;
		}
		public String getThirdSettlementDate() {
			return thirdSettlementDate;
		}
		public void setThirdSettlementDate(String thirdSettlementDate) {
			this.thirdSettlementDate = thirdSettlementDate;
		}
		public String getThirdType() {
			return thirdType;
		}
		public void setThirdType(String thirdType) {
			this.thirdType = thirdType;
		}
		public String getThirdOrderAmt() {
			return thirdOrderAmt;
		}
		public void setThirdOrderAmt(String thirdOrderAmt) {
			this.thirdOrderAmt = thirdOrderAmt;
		}
		public String getThirdPromotionAmt() {
			return thirdPromotionAmt;
		}
		public void setThirdPromotionAmt(String thirdPromotionAmt) {
			this.thirdPromotionAmt = thirdPromotionAmt;
		}
		public String getThirdPaidAmt() {
			return thirdPaidAmt;
		}
		public void setThirdPaidAmt(String thirdPaidAmt) {
			this.thirdPaidAmt = thirdPaidAmt;
		}
		public String getThirdCommissionAmt() {
			return thirdCommissionAmt;
		}
		public void setThirdCommissionAmt(String thirdCommissionAmt) {
			this.thirdCommissionAmt = thirdCommissionAmt;
		}
		public String getThirdSettlementAmt() {
			return thirdSettlementAmt;
		}
		public void setThirdSettlementAmt(String thirdSettlementAmt) {
			this.thirdSettlementAmt = thirdSettlementAmt;
		}
		public String getThirdSettlementNo() {
			return thirdSettlementNo;
		}
		public void setThirdSettlementNo(String thirdSettlementNo) {
			this.thirdSettlementNo = thirdSettlementNo;
		}
		public String getThirdPaymentNo() {
			return thirdPaymentNo;
		}
		public void setThirdPaymentNo(String thirdPaymentNo) {
			this.thirdPaymentNo = thirdPaymentNo;
		}
		public String getThirdAccountDate() {
			return thirdAccountDate;
		}
		public void setThirdAccountDate(String thirdAccountDate) {
			this.thirdAccountDate = thirdAccountDate;
		}
		public String getOrderSettlementAmt() {
			return orderSettlementAmt;
		}
		public void setOrderSettlementAmt(String orderSettlementAmt) {
			this.orderSettlementAmt = orderSettlementAmt;
		}
		public String getPosSettlementAmt() {
			return posSettlementAmt;
		}
		public void setPosSettlementAmt(String posSettlementAmt) {
			this.posSettlementAmt = posSettlementAmt;
		}
		public String getDataCreatedBy() {
			return dataCreatedBy;
		}
		public void setDataCreatedBy(String dataCreatedBy) {
			this.dataCreatedBy = dataCreatedBy;
		}
		public String getDataCreatedDate() {
			return dataCreatedDate;
		}
		public void setDataCreatedDate(String dataCreatedDate) {
			this.dataCreatedDate = dataCreatedDate;
		}
		public String getDataModifiedBy() {
			return dataModifiedBy;
		}
		public void setDataModifiedBy(String dataModifiedBy) {
			this.dataModifiedBy = dataModifiedBy;
		}
		public String getLastModifiedDate() {
			return lastModifiedDate;
		}
		public void setLastModifiedDate(String lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
		}

		public String getDiversityType1() {
			return diversityType1;
		}
		public void setDiversityType1(String diversityType1) {
			this.diversityType1 = diversityType1;
		}
		public String getDiversityType2() {
			return diversityType2;
		}
		public void setDiversityType2(String diversityType2) {
			this.diversityType2 = diversityType2;
		}
		public String getDiversityAmt1() {
			return diversityAmt1;
		}
		public void setDiversityAmt1(String diversityAmt1) {
			this.diversityAmt1 = diversityAmt1;
		}
		public String getDiversityAmt2() {
			return diversityAmt2;
		}
		public void setDiversityAmt2(String diversityAmt2) {
			this.diversityAmt2 = diversityAmt2;
		}
		public String getDiversityReason() {
			return diversityReason;
		}
		public void setDiversityReason(String diversityReason) {
			this.diversityReason = diversityReason;
		}
		public String getAccountStatus() {
			return accountStatus;
		}
		public void setAccountStatus(String accountStatus) {
			this.accountStatus = accountStatus;
		}
		public String getCustomField1() {
			return customField1;
		}
		public void setCustomField1(String customField1) {
			this.customField1 = customField1;
		}
		public String getCustomField2() {
			return customField2;
		}
		public void setCustomField2(String customField2) {
			this.customField2 = customField2;
		}
		public String getCustomField3() {
			return customField3;
		}
		public void setCustomField3(String customField3) {
			this.customField3 = customField3;
		}
		public String getCustomField4() {
			return customField4;
		}
		public void setCustomField4(String customField4) {
			this.customField4 = customField4;
		}
		public String getCustomField5() {
			return customField5;
		}
		public void setCustomField5(String customField5) {
			this.customField5 = customField5;
		}
		public String getCustomField6() {
			return customField6;
		}
		public void setCustomField6(String customField6) {
			this.customField6 = customField6;
		}
		public String getCustomField7() {
			return customField7;
		}
		public void setCustomField7(String customField7) {
			this.customField7 = customField7;
		}
		public String getCustomField8() {
			return customField8;
		}
		public void setCustomField8(String customField8) {
			this.customField8 = customField8;
		}
		public String getCustomField9() {
			return customField9;
		}
		public void setCustomField9(String customField9) {
			this.customField9 = customField9;
		}
		public String getCustomField10() {
			return customField10;
		}
		public void setCustomField10(String customField10) {
			this.customField10 = customField10;
		}
		public String getOrderNo_refund() {
			return orderNo_refund;
		}
		public void setOrderNo_refund(String orderNo_refund) {
			this.orderNo_refund = orderNo_refund;
		}


	}


}
