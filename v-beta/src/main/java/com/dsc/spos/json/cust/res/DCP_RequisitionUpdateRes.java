package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_RequisitionUpdateRes extends JsonBasicRes
{

	private String doc_no; //门店管理对应的单号
	private String org_no; //门店管理对应的门店
	public String getDoc_no() {
		return doc_no;
	}
	public void setDoc_no(String doc_no) {
		this.doc_no = doc_no;
	}
	public String getOrg_no() {
		return org_no;
	}
	public void setOrg_no(String org_no) {
		this.org_no = org_no;
	}
	
	
	
}
