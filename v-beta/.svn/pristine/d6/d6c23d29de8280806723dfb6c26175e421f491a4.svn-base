package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PosFuncTemplateEnableReq extends JsonBasicReq
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
		private String oprType;//操作类型：1-启用2-禁用
		private	List<level1Elm> templateList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		public List<level1Elm> getTemplateList() {
			return templateList;
		}
		public void setTemplateList(List<level1Elm> templateList) {
			this.templateList = templateList;
		}		
			
	}
	
	public class level1Elm
	{
		private String templateId;

		public String getTemplateId() {
			return templateId;
		}
	
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}

		
		
							
	}
	
}
