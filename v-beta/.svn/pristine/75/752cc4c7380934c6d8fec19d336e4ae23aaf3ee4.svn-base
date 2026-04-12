package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderCategoryUpdateRes extends JsonRes 
{
	private String orderStatus;
	private List<level1Elm> datas;
	
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String category;
		private String categoryName;
		private List<level2Elm> datas;
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}	  						
	}
	
	public class level2Elm
	{
		private String shopId;
		private String resultCode;
		private String description;
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getResultCode() {
			return resultCode;
		}
		public void setResultCode(String resultCode) {
			this.resultCode = resultCode;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
						
	}


	
}
