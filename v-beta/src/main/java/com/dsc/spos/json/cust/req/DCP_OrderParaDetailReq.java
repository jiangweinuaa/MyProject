package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 门店订单参数查询
 * 
 * @author yuanyy 2019-11-22
 *
 */
public class DCP_OrderParaDetailReq extends JsonBasicReq 
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
		private String organizationNo;

		public String getOrganizationNo()
		{
			return organizationNo;
		}

		public void setOrganizationNo(String organizationNo)
		{
			this.organizationNo = organizationNo;
		}

		
	}

}
