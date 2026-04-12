package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ScanOrderBaseSetQuery_OpenReq extends JsonBasicReq
{
	
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
//
//
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}



	public class level1Elm
	{
		private String keyTxt;
		private String shopId;
		private String eId;
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		
		
	}

}
