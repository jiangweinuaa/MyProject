package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.cust.JsonReq;

public class DCP_PlanCheckReq extends JsonReq {
	
	private String bDate; //营业日期
	private String docType; // 单据类型，用于判断查哪张表。   0：营业预估   1：生产计划

	public String getbDate() {
		return bDate;
	}

	public void setbDate(String bDate) {
		this.bDate = bDate;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}
	
	
}
