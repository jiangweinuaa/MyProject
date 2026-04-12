package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsSetExQueryReq extends JsonBasicReq
{
	private String oShopId;
	private String pluno;

	public String getoShopId() {
		return oShopId;
	}
	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}
	public String getPluno() {
		return pluno;
	}
	public void setPluno(String pluno) {
		this.pluno = pluno;
	}
}
