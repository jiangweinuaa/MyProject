package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_OrderStatusUpdate
 * 服务说明：订单状态修改
 * @author jinzma 
 * @since  2020-10-30
 */
public class DCP_OrderStatusUpdateJhReq extends JsonBasicReq {
	
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
		private String modiType;
		private String status;
		public String getOrderNo()
		{
			return orderNo;
		}
		public String geteId()
		{
			return eId;
		}
		public void seteId(String eId)
		{
			this.eId = eId;
		}
		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}
		public String getModiType()
		{
			return modiType;
		}
		public void setModiType(String modiType)
		{
			this.modiType = modiType;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		
		

	}
}
