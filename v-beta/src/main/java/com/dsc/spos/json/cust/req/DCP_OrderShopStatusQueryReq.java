package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderShopStatusQueryReq extends JsonBasicReq
{
	private String oEId;
	private String oShopId;

	public String getoShopId() {
		return oShopId;
	}

	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}

	public String getoEId() {
		return oEId;
	}

	public void setoEId(String oEId) {
		this.oEId = oEId;
	}

}


