package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.cust.JsonReq;
import com.dsc.spos.json.cust.req.DCP_OrderPickUpGoodsUpdateReq.order;

/**
 * 订单打印次数更新
 * @author yuanyy
 *
 */
public class DCP_OrderPrintUpdateReq extends JsonReq 
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
