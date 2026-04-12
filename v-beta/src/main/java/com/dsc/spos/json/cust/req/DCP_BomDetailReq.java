package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务接口：BomGet 建立于2018-08-07
 * 
 * @author 24480
 *
 */
public class DCP_BomDetailReq extends JsonBasicReq
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
		private String bomNo;

		public String getBomNo()
		{
			return bomNo;
		}

		public void setBomNo(String bomNo)
		{
			this.bomNo = bomNo;
		}
		

	}

}
