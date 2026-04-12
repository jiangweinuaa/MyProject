package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_AppQueryReq extends JsonBasicReq
{
	private levelRequest request;	
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String status;//-1未启用100已启用0已禁用
		private String isThird;//是否第三方（Y，N）
		private String isOnline;//是否线上（Y，N）
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		public String getIsThird()
		{
			return isThird;
		}
		public void setIsThird(String isThird)
		{
			this.isThird = isThird;
		}
		public String getIsOnline()
		{
			return isOnline;
		}
		public void setIsOnline(String isOnline)
		{
			this.isOnline = isOnline;
		}
		
		
		
	}
	
}
