package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_FlavorQueryRes extends JsonRes
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
		private List<levelFlavorName> flavorName_lang;//口味名称多语言
		private String groupId;//分组编码
		private String groupName;//f分组名称
		private String sortId;//显示顺序
		private String subCategoryCount;//下属商品分类数量
		private String subGoodsCount;//下属商品数量


		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
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
		public List<levelFlavorName> getFlavorName_lang() {
			return flavorName_lang;
		}
		public void setFlavorName_lang(List<levelFlavorName> flavorName_lang) {
			this.flavorName_lang = flavorName_lang;
		}
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public String getSubCategoryCount() {
			return subCategoryCount;
		}
		public void setSubCategoryCount(String subCategoryCount) {
			this.subCategoryCount = subCategoryCount;
		}
		public String getSubGoodsCount() {
			return subGoodsCount;
		}
		public void setSubGoodsCount(String subGoodsCount) {
			this.subGoodsCount = subGoodsCount;
		}		
	}

	public class levelFlavorName
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

	}

}
