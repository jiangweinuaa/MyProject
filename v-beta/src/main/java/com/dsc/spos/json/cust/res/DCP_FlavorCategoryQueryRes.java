package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_FlavorCategoryQueryRes extends JsonRes
{
	
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	
	public class level1Elm
	{
		private String flavorId;//口味编码
		private String flavorName;//口味名称
		private String category;//商品分类编码
		private String categoryName;//商品分类名称
		private String status;//资料状态：-1未启用 100-已启用 0-已禁用
		public String getFlavorId() {
			return flavorId;
		}
		public void setFlavorId(String flavorId) {
			this.flavorId = flavorId;
		}
		public String getFlavorName() {
			return flavorName;
		}
		public void setFlavorName(String flavorName) {
			this.flavorName = flavorName;
		}
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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
		
	}
	
	
	
	
}
