package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsCategoryEnableReq extends JsonBasicReq
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
		private	List<level1Elm> categoryList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		public List<level1Elm> getCategoryList() {
			return categoryList;
		}
		public void setCategoryList(List<level1Elm> categoryList) {
			this.categoryList = categoryList;
		}
		
		
			
	}
	
	public class level1Elm
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
