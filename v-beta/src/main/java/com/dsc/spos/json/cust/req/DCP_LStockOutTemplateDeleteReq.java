package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 删除 报损模板 2019-01-08
 * @author yuanyy
 *
 */
public class DCP_LStockOutTemplateDeleteReq extends JsonBasicReq 
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
		private String templateNo;

		public String getTemplateNo()
		{
			return templateNo;
		}

		public void setTemplateNo(String templateNo)
		{
			this.templateNo = templateNo;
		}
		
	}
	

	
	
}	
