package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 外卖基础设置删除
 * @author yuanyy 2020-03-16
 *
 */
public class DCP_TakeOutOrderBaseSetDeleteReq extends JsonBasicReq {
	
	private level1Elm request;
//	private String timestamp;
	
	public level1Elm getRequest() {
		return request;
	}
	
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
//	public String getTimestamp() {
//		return timestamp;
//	}
//	
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}
	
	public class level1Elm{
		private String baseSetNo;

		public String getBaseSetNo() {
			return baseSetNo;
		}

		public void setBaseSetNo(String baseSetNo) {
			this.baseSetNo = baseSetNo;
		}
		
	}
	
	
}
