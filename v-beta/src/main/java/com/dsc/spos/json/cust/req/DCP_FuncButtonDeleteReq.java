package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_FuncButtonDeleteReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String ptemplateNo;
		public String getPtemplateNo() {
			return ptemplateNo;
		}
		public void setPtemplateNo(String ptemplateNo) {
			this.ptemplateNo = ptemplateNo;
		}

	}
}
