package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_RoleDeleteReq extends JsonBasicReq {
	/**
		{	
			"serviceId": "RoleDelete",	必傳且非空，服務名
			"token": "f14ee75ff5b220177ac0dc538bdea08c",	必傳且非空，訪問令牌
			"opGroup": "10001",	必傳且非空，角色编码
		}	
	 **/
	private levelRequest request;

	public levelRequest getRequest()
	{
		return request;
	}

	public void setRequest(levelRequest request)
	{
		this.request = request;
	}

	public class levelRequest
	{
		private String opGroup;

		public String getOpGroup() {
			return opGroup;
		}
		public void setOpGroup(String opGroup) {
			this.opGroup = opGroup;
		}
	}
	
	
}
