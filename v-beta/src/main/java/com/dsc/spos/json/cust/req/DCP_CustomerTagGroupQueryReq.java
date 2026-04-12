package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 客户标签
 * @author yuanyy
 * 
 */
public class DCP_CustomerTagGroupQueryReq extends JsonBasicReq {
//	private String timeStamp;
	private level1Elm request;
//	public String getTimeStamp() {
//		return timeStamp;
//	}
//	public void setTimeStamp(String timeStamp) {
//		this.timeStamp = timeStamp;
//	}
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
	public class level1Elm{
		private String keyTxt;

		public String getKeyTxt() {
			return keyTxt;
		}

		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		
	}
	
	
	
}
