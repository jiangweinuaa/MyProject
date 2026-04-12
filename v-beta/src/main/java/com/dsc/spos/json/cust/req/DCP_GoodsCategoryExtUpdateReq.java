package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/**
 * 修改商品品类 2018-10-18
 * 
 * @author yuanyy
 *
 */
public class DCP_GoodsCategoryExtUpdateReq extends JsonBasicReq {

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

		private String categoryImage;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getCategoryImage() {
			return categoryImage;
		}

		public void setCategoryImage(String categoryImage) {
			this.categoryImage = categoryImage;
		}
	}
	

	



}
