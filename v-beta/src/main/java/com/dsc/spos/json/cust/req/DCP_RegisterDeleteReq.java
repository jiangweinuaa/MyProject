package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_RegisterDeleteReq extends JsonBasicReq
{
	
	private String customerNo;
	private String terminalLicence;

	public String getTerminalLicence() {
	return terminalLicence;
	}

	public void setTerminalLicence(String terminalLicence) {
	this.terminalLicence = terminalLicence;
	}

	public String getCustomerNo() {
	return customerNo;
	}

	public void setCustomerNo(String customerNo) {
	this.customerNo = customerNo;
	}
}
