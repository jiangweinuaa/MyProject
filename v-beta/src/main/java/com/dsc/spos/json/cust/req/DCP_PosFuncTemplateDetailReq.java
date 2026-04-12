package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PosFuncTemplateDetailReq extends JsonBasicReq
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
		private String templateId ;

		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}		
	}
	
	
}
