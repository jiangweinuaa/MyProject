package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_FlavorCategoryAddReq extends JsonBasicReq
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
		private String flavorId ;//口味编码	
		private String groupId ;//
		private List<levelCategory> categoryList ;//
		
		
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getFlavorId() {
			return flavorId;
		}
		public void setFlavorId(String flavorId) {
			this.flavorId = flavorId;
		}
		public List<levelCategory> getCategoryList() {
			return categoryList;
		}
		public void setCategoryList(List<levelCategory> categoryList) {
			this.categoryList = categoryList;
		}		
	}
	
	public class levelCategory
	{
		private String category ;//商品分类编码	

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}
		
	}
	
	
}
