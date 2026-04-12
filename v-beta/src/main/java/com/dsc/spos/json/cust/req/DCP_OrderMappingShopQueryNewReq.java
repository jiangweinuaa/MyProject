package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderMappingShopQueryNewReq extends JsonBasicReq 
{
	private String docType;
	private String account;
	private String keyTxt;
	private String businessId;
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getKeyTxt() {
		return keyTxt;
	}
	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	
	
}
