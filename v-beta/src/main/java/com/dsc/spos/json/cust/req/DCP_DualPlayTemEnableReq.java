package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_DualPlayTemEnableReq extends JsonBasicReq
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
		private String templateNo;

		public String getTemplateNo() {
			return templateNo;
		}

		public void setTemplateNo(String templateNo) {
			this.templateNo = templateNo;
		}
	}
	
}
