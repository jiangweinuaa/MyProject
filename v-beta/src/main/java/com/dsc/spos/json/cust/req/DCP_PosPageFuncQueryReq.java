package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PosPageFuncQueryReq extends JsonBasicReq
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
		private String pageType;
		private String funcGroup;

		public String getPageType() {
			return pageType;
		}
	
		public void setPageType(String pageType) {
			this.pageType = pageType;
		}

		public String getFuncGroup()
		{
			return funcGroup;
		}

		public void setFuncGroup(String funcGroup)
		{
			this.funcGroup = funcGroup;
		}
		
		
		
		
		
	}

}
