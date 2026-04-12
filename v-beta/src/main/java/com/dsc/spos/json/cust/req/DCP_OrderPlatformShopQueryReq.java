package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderPlatformShopQueryReq  extends JsonBasicReq 
{
	private String isOnline;//是否在线
	private String loadDocType;//平台类型
	private String keyTxt;//关键字
	public String getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}
	public String getLoadDocType() {
		return loadDocType;
	}
	public void setLoadDocType(String loadDocType) {
		this.loadDocType = loadDocType;
	}
	public String getKeyTxt() {
		return keyTxt;
	}
	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}
	
	
	

}
