package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_SalePriceTemplateDeleteReq extends JsonBasicReq
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
		private List<levelTemplate> templateList;//

		public List<levelTemplate> getTemplateList() {
			return templateList;
		}

		public void setTemplateList(List<levelTemplate> templateList) {
			this.templateList = templateList;
		}		
	}
	
	public class levelTemplate
	{
		private String templateId;//模板编码

		public String getTemplateId() {
			return templateId;
		}

		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}		
	}
	
	
}
