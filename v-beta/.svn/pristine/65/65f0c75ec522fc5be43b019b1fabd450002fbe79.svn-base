package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderPickUpGoodsUpdateReq extends JsonBasicReq
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
		private List<order> orderList ;

		public List<order> getOrderList()
		{
			return orderList;
		}

		public void setOrderList(List<order> orderList)
		{
			this.orderList = orderList;
		}		
		
	}
	
	public class order
	{
		private String orderNo ;

		public String getOrderNo()
		{
			return orderNo;
		}

		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}		
	}
	
	
}
