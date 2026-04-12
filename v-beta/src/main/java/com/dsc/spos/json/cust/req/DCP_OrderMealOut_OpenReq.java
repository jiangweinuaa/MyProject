package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderMealOut_OpenReq extends JsonBasicReq
{
	
	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public  class level1Elm
	{
		private String eId;
		private String shopId;
		private String opNo;
		private String opName;
		private String orderNo;
		private String loadDocType;

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

		public String getOpNo() {
			return opNo;
		}

		public void setOpNo(String opNo) {
			this.opNo = opNo;
		}

		public String getOpName() {
			return opName;
		}

		public void setOpName(String opName) {
			this.opName = opName;
		}

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		public String getLoadDocType() {
			return loadDocType;
		}

		public void setLoadDocType(String loadDocType) {
			this.loadDocType = loadDocType;
		}
	}


}
