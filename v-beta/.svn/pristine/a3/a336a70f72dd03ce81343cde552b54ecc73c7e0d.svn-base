package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_StuffCategoryDeleteReq extends JsonBasicReq
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
		private List<levelStuffCategory> categoryList ;//

		public List<levelStuffCategory> getCategoryList()
		{
			return categoryList;
		}

		public void setCategoryList(List<levelStuffCategory> categoryList)
		{
			this.categoryList = categoryList;
		}
		
	}
	
	public class levelStuffCategory
	{
		private String stuffId ;//商品编码	
		private String category ;//商品编码	

		public String getCategory()
		{
			return category;
		}

		public void setCategory(String category)
		{
			this.category = category;
		}

		public String getStuffId()
		{
			return stuffId;
		}

		public void setStuffId(String stuffId)
		{
			this.stuffId = stuffId;
		}
		
				
	}
	
	
}
