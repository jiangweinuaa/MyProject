package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_NRCRestfulStatusQueryReq extends JsonBasicReq
{	
	
	private String disHistory;//是否显示历史记录

	public String getDisHistory() {
		return disHistory;
	}

	public void setDisHistory(String disHistory) {
		this.disHistory = disHistory;
	}
	
	
}
