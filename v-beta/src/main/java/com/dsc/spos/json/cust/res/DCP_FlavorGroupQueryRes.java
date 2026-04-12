package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_FlavorGroupQueryRes extends JsonRes
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
		private String groupId;//
		private String groupName;//
		private List<levelGroup> groupName_lang;//
		private String sortId;//
		private String exclusived;//
		private List<levelGroupFlavor> subFlavorList;//
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public List<levelGroup> getGroupName_lang() {
			return groupName_lang;
		}
		public void setGroupName_lang(List<levelGroup> groupName_lang) {
			this.groupName_lang = groupName_lang;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public String getExclusived() {
			return exclusived;
		}
		public void setExclusived(String exclusived) {
			this.exclusived = exclusived;
		}
		public List<levelGroupFlavor> getSubFlavorList() {
			return subFlavorList;
		}
		public void setSubFlavorList(List<levelGroupFlavor> subFlavorList) {
			this.subFlavorList = subFlavorList;
		}
		
		
		
	}
	
	public class levelGroup
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
	
	public class levelGroupFlavor
	{
		private String flavorId;//
		private String flavorName;//
		private String sortId;//
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
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		
		
	}
	
	
	
	
}
