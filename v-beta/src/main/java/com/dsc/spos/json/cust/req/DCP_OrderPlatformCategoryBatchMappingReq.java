package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderPlatformCategoryBatchMappingReq extends JsonBasicReq {
	private String loadDocType;
	private String[] erpShopNO;
	public String getLoadDocType() {
		return loadDocType;
	}
	public void setLoadDocType(String loadDocType) {
		this.loadDocType = loadDocType;
	}
	public String[] getErpShopNO() {
		return erpShopNO;
	}
	public void setErpShopNO(String[] erpShopNO) {
		this.erpShopNO = erpShopNO;
	}

}
