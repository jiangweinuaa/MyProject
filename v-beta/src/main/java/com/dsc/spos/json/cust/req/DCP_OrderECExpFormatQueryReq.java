package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 电商订单导出格式查询
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECExpFormatQueryReq extends JsonBasicReq {
	private String keyTxt;

	public String getKeyTxt() {
		return keyTxt;
	}

	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}
	
}
