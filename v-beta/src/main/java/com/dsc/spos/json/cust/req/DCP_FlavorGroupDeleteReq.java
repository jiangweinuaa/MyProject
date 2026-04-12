package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_FlavorGroupDeleteReq extends JsonBasicReq
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
		
		List<group> groupIdList;
	
		public List<group> getGroupIdList()
		{
			return groupIdList;
		}
	
		public void setGroupIdList(List<group> groupIdList)
		{
			this.groupIdList = groupIdList;
		}
		


	}
	
	public class group
	{		
		private String groupId;//

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}


	}

}
