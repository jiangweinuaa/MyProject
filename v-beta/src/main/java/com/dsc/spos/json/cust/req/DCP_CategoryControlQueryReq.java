package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CategoryControlQueryReq extends JsonBasicReq {
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String category;		
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		
		
	}

}
