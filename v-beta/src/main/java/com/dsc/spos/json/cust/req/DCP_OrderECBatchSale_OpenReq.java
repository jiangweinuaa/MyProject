package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.cust.JsonReq;

public class DCP_OrderECBatchSale_OpenReq extends JsonReq
{
	private String[] ecOrderNo;

	public String[] getEcOrderNo() {
		return ecOrderNo;
	}

	public void setEcOrderNo(String[] ecOrderNo) {
		this.ecOrderNo = ecOrderNo;
	}
	
}
