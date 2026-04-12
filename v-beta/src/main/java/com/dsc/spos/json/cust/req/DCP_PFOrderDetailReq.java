package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 计划报单详情查询
 * @author yuanyy
 *
 */
public class DCP_PFOrderDetailReq extends JsonBasicReq {
	
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
		private String pfNo;

		public String getPfNo() {
			return pfNo;
		}

		public void setPfNo(String pfNo) {
			this.pfNo = pfNo;
		}
		
	}
}
