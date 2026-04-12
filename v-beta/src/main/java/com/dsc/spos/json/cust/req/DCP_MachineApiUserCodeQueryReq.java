package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_MachineApiUserCodeQueryReq extends JsonBasicReq
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

		private String shopId;
		private String appType;
		
		public String getShopId()
		{
			return shopId;
		}
		public void setShopId(String shopId)
		{
			this.shopId = shopId;
		}
		public String getAppType()
		{
			return appType;
		}
		public void setAppType(String appType)
		{
			this.appType = appType;
		}		
	}


}
