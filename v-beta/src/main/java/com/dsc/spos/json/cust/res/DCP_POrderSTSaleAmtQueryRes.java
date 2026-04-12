package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_POrderSTSaleAmtQueryRes extends JsonRes {
	
	private String beginDate;
	private String endDate;
	private String totAmt;
	
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getTotAmt() {
		return totAmt;
	}
	public void setTotAmt(String totAmt) {
		this.totAmt = totAmt;
	}
	
}
