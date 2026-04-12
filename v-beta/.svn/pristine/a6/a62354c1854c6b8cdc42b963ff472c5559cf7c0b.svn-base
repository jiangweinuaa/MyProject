package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 删除商品品类 2018-10-18
 * @author yuanyy
 *
 */
public class DCP_GoodsCategoryDeleteReq extends JsonBasicReq {
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private	List<level1Elm> categoryList;

		public List<level1Elm> getCategoryList()
		{
			return categoryList;
		}

		public void setCategoryList(List<level1Elm> categoryList)
		{
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
