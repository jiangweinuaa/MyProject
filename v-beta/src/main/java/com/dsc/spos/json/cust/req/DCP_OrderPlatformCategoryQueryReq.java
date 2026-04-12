package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderPlatformCategoryQueryReq extends JsonBasicReq 
{
	private String isOnline;//是否在线
	private String[] erpShopNO;
	private String loadDocType;
	
	public String[] getErpShopNO() {
		return erpShopNO;
	}
	public void setErpShopNO(String[] erpShopNO) {
		this.erpShopNO = erpShopNO;
	}
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

}
