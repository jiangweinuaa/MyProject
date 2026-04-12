package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.cust.JsonReq;

public class DCP_OrderInvoiceReq extends JsonReq
{
	private level1Elm request;
	
	public class level1Elm
	{

		private String orderNo;
		private String invOperateType;//操作类型  0：发票打印  1：发票作废，2：折让单打印

		public String getOrderNo()
		{
			return orderNo;
		}

		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}

		public String getInvOperateType()
		{
			return invOperateType;
		}

		public void setInvOperateType(String invOperateType)
		{
			this.invOperateType = invOperateType;
		}
	}

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}
}
