package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
public class DCP_OrderShippingReq extends JsonBasicReq
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
		private String opType;// 1:发货 2:安排取件
		private String[] orderList;//订单号列表
		private String bdate;//营业日期
		
		public String getOpType()
		{
			return opType;
		}
		public void setOpType(String opType)
		{
			this.opType = opType;
		}
		public String[] getOrderList()
		{
			return orderList;
		}
		public void setOrderList(String[] orderList)
		{
			this.orderList = orderList;
		}
		public String getBdate()
		{
			return bdate;
		}
		public void setBdate(String bdate)
		{
			this.bdate = bdate;
		}

	}
}
