package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderCheckOverGoodsReq extends JsonBasicReq
{

	private levelRequest request;

	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}


	public  class levelRequest
	{		
		private List<Order> orderList;

		public List<Order> getOrderList()
		{
			return orderList;
		}

		public void setOrderList(List<Order> orderList)
		{
			this.orderList = orderList;
		}

	}

	public class Order
	{
		private String orderNo;
		private String packageNo;//包裹代号

		public String getOrderNo()
		{
			return orderNo;
		}
		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}
		public String getPackageNo()
		{
			return packageNo;
		}
		public void setPackageNo(String packageNo)
		{
			this.packageNo = packageNo;
		}

	}

}
