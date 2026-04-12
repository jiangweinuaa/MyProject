package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsSetShopDeleteReq extends JsonBasicReq
{
	
	 private String SELSHOP;
	 private String PLUNO;
	public String getSELSHOP() {
		return SELSHOP;
	}
	public void setSELSHOP(String sELSHOP) {
		SELSHOP = sELSHOP;
	}
	public String getPLUNO() {
		return PLUNO;
	}
	public void setPLUNO(String pLUNO) {
		PLUNO = pLUNO;
	}
	 
	 
	 

}
