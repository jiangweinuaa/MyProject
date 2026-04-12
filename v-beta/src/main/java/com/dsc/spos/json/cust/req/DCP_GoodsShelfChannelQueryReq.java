package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsShelfChannelQueryReq extends JsonBasicReq
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
		private String pluNo;
		private String keyTxt;

		public String getPluNo()
		{
			return pluNo;
		}

		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
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
