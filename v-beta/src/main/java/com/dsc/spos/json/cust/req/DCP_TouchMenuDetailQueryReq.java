package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TouchMenuDetailQueryReq   extends JsonBasicReq{
	/*
    "menuNO":"CPCD201801010001"	必传且非空，菜单编号
  */
	
	private String menuNO;

	public String getMenuNO() {
	return menuNO;
	}

	public void setMenuNO(String menuNO) {
	this.menuNO = menuNO;
	}
	
}
