package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_SupPriceTemplateDeleteReq extends JsonBasicReq
{

	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
	public class level1Elm
	{
		private List<Template> templateList;//

		public List<Template> getTemplateList()
		{
			return templateList;
		}

		public void setTemplateList(List<Template> templateList)
		{
			this.templateList = templateList;
		}
		
	}
	public class Template
	{
		private String templateId;//

		public String getTemplateId()
		{
			return templateId;
		}

		public void setTemplateId(String templateId)
		{
			this.templateId = templateId;
		}
		
	}
	
}
