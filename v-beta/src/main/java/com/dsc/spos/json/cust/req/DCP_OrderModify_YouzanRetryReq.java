package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_OrderModify_YouzanRetryReq extends JsonBasicReq
{

	private levelRequest request;

	public levelRequest getRequest()
	{
		return request;
	}

	public void setRequest(levelRequest request)
	{
		this.request = request;
	}

	public class levelRequest
	{
		private String eId;
		private String orderNo;
		private String beginDate;
		private String endDate;
		private String shipType;

		public String geteId() {
			return eId;
		}

		public void seteId(String eId) {
			this.eId = eId;
		}

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
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

		public String getShipType() {
			return shipType;
		}

		public void setShipType(String shipType) {
			this.shipType = shipType;
		}
	}
	

}
