package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_CCBEnDecryptRes extends JsonBasicRes {

	private String ccbParam;

	public String getCcbParam() {
	return ccbParam;
	}

	public void setCcbParam(String ccbParam) {
	this.ccbParam = ccbParam;
	}
}
