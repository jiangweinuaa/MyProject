package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderMappingShopQuery_OpenReq extends JsonBasicReq
{
	private levelElm request;

	public levelElm getRequest()
	{
		return request;
	}

	public void setRequest(levelElm request)
	{
		this.request = request;
	}

	public class levelElm
	{				
		private String[] erpShopNo;
		
		private String[] orderShopNo;
		
		private String keyTxt;

		public String[] getErpShopNo()
		{
			return erpShopNo;
		}

		public void setErpShopNo(String[] erpShopNo)
		{
			this.erpShopNo = erpShopNo;
		}

		public String[] getOrderShopNo()
		{
			return orderShopNo;
		}

		public void setOrderShopNo(String[] orderShopNo)
		{
			this.orderShopNo = orderShopNo;
		}

		public String getKeyTxt()
		{
			return keyTxt;
		}

		public void setKeyTxt(String keyTxt)
		{
			this.keyTxt = keyTxt;
		}

		
								
	}

}
