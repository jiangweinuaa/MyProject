package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderStatusLogQueryReq extends JsonBasicReq 
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
		private String display;//1:对外给买家看的 否则写0
		public String geteId()
		{
			return eId;
		}
		public void seteId(String eId)
		{
			this.eId = eId;
		}
		public String getOrderNo()
		{
			return orderNo;
		}
		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}

		public String getDisplay()
		{
			return display;
		}

		public void setDisplay(String display)
		{
			this.display = display;
		}
	}
	

}
