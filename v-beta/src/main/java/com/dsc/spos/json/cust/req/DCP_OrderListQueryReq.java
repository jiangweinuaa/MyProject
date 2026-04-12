package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderListQueryReq extends JsonBasicReq
{
	private String oEId;

	public String getoEId() {
		return oEId;
	}

	public void setoEId(String oEId) {
		this.oEId = oEId;
	}



}
