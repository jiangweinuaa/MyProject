package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.cust.JsonReq;

public class DCP_OrderECSaleReq extends JsonReq
{

	private level1Elm request;

	public class level1Elm
	{
		private String eId;
		private String shopId;
		private String docType;
		private String orderNO;

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
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getOrderNO() {
			return orderNO;
		}
		public void setOrderNO(String orderNO) {
			this.orderNO = orderNO;
		}
	}

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

}
