package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TagGroupDeleteReq extends JsonBasicReq
{
	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}	
	
	public class level1Elm
	{
		private List<tagGroupInfo> tagGroupList;

		public List<tagGroupInfo> getTagGroupList()
		{
			return tagGroupList;
		}

		public void setTagGroupList(List<tagGroupInfo> tagGroupList)
		{
			this.tagGroupList = tagGroupList;
		}
		
		
	}
	public class tagGroupInfo
	{

		private String tagGroupNo;
		private String tagGroupType;
		public String getTagGroupNo() {
			return tagGroupNo;
		}
		public void setTagGroupNo(String tagGroupNo) {
			this.tagGroupNo = tagGroupNo;
		}
		public String getTagGroupType() {
			return tagGroupType;
		}
		public void setTagGroupType(String tagGroupType) {
			this.tagGroupType = tagGroupType;
		}	
		
	
		
	}
	
	
	
}
