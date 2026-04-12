package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 
 */
public class DCP_ShopQueryReq extends JsonBasicReq {
	
	private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}
	
	
	
	
	public class levelRequest
	{
		private String keyTxt;
		private String range;  //门店范围 0:全部门店 1:登入公司的所属门店 2:用户权限范围内门店(作废)  3:用户权限范围内门店
		private String status;//-1未启用0.禁用100.已启用
		private String getType;
		
		
		public String getRange() {
			return range;
		}
		public void setRange(String range) {
			this.range = range;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getGetType() {
			return getType;
		}
		public void setGetType(String getType) {
			this.getType = getType;
		}
		
	}
	
}
