package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_WarningDeleteReq extends JsonBasicReq  {

  private level1Elm request;
		
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm
	{
		private String billNo;
		private String billType;
		
		
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getBillType() {
			return billType;
		}
		public void setBillType(String billType) {
			this.billType = billType;
		}
			
	}
}
