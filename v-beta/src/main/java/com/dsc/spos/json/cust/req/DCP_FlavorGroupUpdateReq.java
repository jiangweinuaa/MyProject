package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_FlavorGroupUpdateReq extends JsonBasicReq
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
		private String groupId ;//分组编码
		private List<levelFlavorGroup> groupName_lang ;//分组名称多语言
		private String sortId ;//显示顺序
		private String exclusived ;//组内标签是否互斥
		private String memo ;//备注
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public List<levelFlavorGroup> getGroupName_lang() {
			return groupName_lang;
		}
		public void setGroupName_lang(List<levelFlavorGroup> groupName_lang) {
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
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}	

	}

	public class levelFlavorGroup
	{
		private String langType ;//
		private String name ;//
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
