package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_RegisterCreateRes extends JsonBasicRes
{
	private String terminalLicence;

	public String getTerminalLicence() {
	return terminalLicence;
	}

	public void setTerminalLicence(String terminalLicence) {
	this.terminalLicence = terminalLicence;
	}
}
