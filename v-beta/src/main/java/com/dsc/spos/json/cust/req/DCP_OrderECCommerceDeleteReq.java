package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 电商平台删除
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_OrderECCommerceDeleteReq extends JsonBasicReq {
	
	private String ecPlatformNo;
	private String PlatformID;

	public String getEcPlatformNo() {
		return ecPlatformNo;
	}

	public void setEcPlatformNo(String ecPlatformNo) {
		this.ecPlatformNo = ecPlatformNo;
	}

	public String getPlatformID() {
		return PlatformID;
	}

	public void setPlatformID(String platformID) {
		PlatformID = platformID;
	}

}
