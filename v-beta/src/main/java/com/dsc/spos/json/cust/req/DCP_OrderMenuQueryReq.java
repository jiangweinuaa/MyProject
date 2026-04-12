package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 外卖菜单查询
 * @author Typingly
 *
 */
public class DCP_OrderMenuQueryReq extends JsonBasicReq 
{
	private String keyTxt;
	
	public String getKeyTxt() {
		return keyTxt;
	}
	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}
}
