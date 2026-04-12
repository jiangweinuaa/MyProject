package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsSetCategoryTreeQueryReq extends JsonBasicReq {
	private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}
	
	public class levelRequest {
		private String selfBuiltSearch;
		
		public String getSelfBuiltSearch() {
			return selfBuiltSearch;
		}
		public void setSelfBuiltSearch(String selfBuiltSearch) {
			this.selfBuiltSearch = selfBuiltSearch;
		}
	}
	
}
