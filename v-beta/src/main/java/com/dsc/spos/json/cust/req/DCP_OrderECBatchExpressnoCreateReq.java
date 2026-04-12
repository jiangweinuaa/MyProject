package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderECBatchExpressnoCreateReq extends JsonBasicReq
{

	private String[] ecOrderNo;
	private String lgPlatformNo;
	private String lgPlatformName;
	
	
	public String[] getEcOrderNo() {
		return ecOrderNo;
	}
	public void setEcOrderNo(String[] ecOrderNo) {
		this.ecOrderNo = ecOrderNo;
	}
	public String getLgPlatformNo() {
		return lgPlatformNo;
	}
	public void setLgPlatformNo(String lgPlatformNo) {
		this.lgPlatformNo = lgPlatformNo;
	}
	public String getLgPlatformName() {
		return lgPlatformName;
	}
	public void setLgPlatformName(String lgPlatformName) {
		this.lgPlatformName = lgPlatformName;
	}
	
	
	
	
	
}
