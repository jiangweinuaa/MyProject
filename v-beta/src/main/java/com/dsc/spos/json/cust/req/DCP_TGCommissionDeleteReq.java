package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TGCommissionDeleteReq extends JsonBasicReq {

	private String travelNO ;
	private String tgCategoryNO ;

	public String getTravelNO() {
		return travelNO;
	}
	public void setTravelNO(String travelNO) {
		this.travelNO = travelNO;
	}
	public String getTgCategoryNO() {
		return tgCategoryNO;
	}
	public void setTgCategoryNO(String tgCategoryNO) {
		this.tgCategoryNO = tgCategoryNO;
	}

}
