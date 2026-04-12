package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_WarningMonitoringExportReq extends JsonBasicReq {

	
	private level1Elm request;
	
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm
	{
		private String warningType;
		private String warningNo;
		private String orderNo;
		private String shopId;
		private String memberId;
		private String cardTypeId;
		private String cardNo;
		private String beginDate;
		private String endDate;
		public String getWarningType() {
			return warningType;
		}
		public void setWarningType(String warningType) {
			this.warningType = warningType;
		}
		public String getWarningNo() {
			return warningNo;
		}
		public void setWarningNo(String warningNo) {
			this.warningNo = warningNo;
		}
		public String getOrderNo() {
			return orderNo;
		}
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getMemberId() {
			return memberId;
		}
		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}
		public String getCardTypeId() {
			return cardTypeId;
		}
		public void setCardTypeId(String cardTypeId) {
			this.cardTypeId = cardTypeId;
		}
		public String getCardNo() {
			return cardNo;
		}
		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}
		public String getBeginDate() {
			return beginDate;
		}
		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
			
		
	}
	
	
	
}
