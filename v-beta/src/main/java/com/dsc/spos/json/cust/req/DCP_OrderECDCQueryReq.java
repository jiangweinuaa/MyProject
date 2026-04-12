package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 大物流中心资料查询
 * @author yuanyy 2019-03-27
 *
 */
public class DCP_OrderECDCQueryReq extends JsonBasicReq {
	private String keyTxt ;
	
	private String lgPlatformNo;

	public String getKeyTxt() {
		return keyTxt;
	}

	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}

	public String getLgPlatformNo() {
		return lgPlatformNo;
	}

	public void setLgPlatformNo(String lgPlatformNo) {
		this.lgPlatformNo = lgPlatformNo;
	}
	
	
}
