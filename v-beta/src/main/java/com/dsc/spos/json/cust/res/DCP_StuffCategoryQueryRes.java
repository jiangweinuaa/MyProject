package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_StuffCategoryQueryRes extends JsonRes
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
		private String stuffId;//口味编码
		private String stuffName;//口味名称	
		private String category;//商品分类编码
		private String categoryName;//商品分类名称
		private String status;//分组编码
		
		public String getStuffId()
		{
			return stuffId;
		}
		public void setStuffId(String stuffId)
		{
			this.stuffId = stuffId;
		}
		public String getStuffName()
		{
			return stuffName;
		}
		public void setStuffName(String stuffName)
		{
			this.stuffName = stuffName;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		public String getCategory()
		{
			return category;
		}
		public void setCategory(String category)
		{
			this.category = category;
		}
		public String getCategoryName()
		{
			return categoryName;
		}
		public void setCategoryName(String categoryName)
		{
			this.categoryName = categoryName;
		}
		
		

	
	}

	/*public class levelFlavorName
	{		
		private String langType;//
		private String name;//				

		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

	}*/

}
