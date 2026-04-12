package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderMappingCategoryQueryRes extends JsonRes  
{
	private String result;
	private String description;
	private List<level1Elm> datas;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm
	{
		private String orderCategory;
		private String orderCategoryName;
		private String category;
		public String getOrderCategory() {
			return orderCategory;
		}
		public void setOrderCategory(String orderCategory) {
			this.orderCategory = orderCategory;
		}
		public String getOrderCategoryName() {
			return orderCategoryName;
		}
		public void setOrderCategoryName(String orderCategoryName) {
			this.orderCategoryName = orderCategoryName;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
				 						
	}

}
