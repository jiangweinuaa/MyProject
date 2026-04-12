package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ScanOrderBaseSetDeleteReq extends JsonBasicReq
{

	private level1Elm request;
//	private String timestamp;
	
	
	
	
	public level1Elm getRequest() {
		return request;
	}




	public void setRequest(level1Elm request) {
		this.request = request;
	}



//
//	public String getTimestamp() {
//		return timestamp;
//	}
//
//
//
//
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}




	public class level1Elm
	{
		private String ruleNo;

		public String getRuleNo() {
			return ruleNo;
		}

		public void setRuleNo(String ruleNo) {
			this.ruleNo = ruleNo;
		}
		
	}
}
