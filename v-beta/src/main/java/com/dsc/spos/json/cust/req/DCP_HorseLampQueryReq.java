package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 跑马灯列表查询
 * @author yuanyy 2019-01-07
 */
public class DCP_HorseLampQueryReq extends JsonBasicReq {
	
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
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

	public class level1Elm
	{
		private String status;
		private String validStatus;
		private String billNo;
		private String searchString;
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getValidStatus() {
			return validStatus;
		}
		public void setValidStatus(String validStatus) {
			this.validStatus = validStatus;
		}
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getSearchString() {
			return searchString;
		}
		public void setSearchString(String searchString) {
			this.searchString = searchString;
		}
		
	}
}
