package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 虾皮官方托运单打印
 * @author yuanyy 2019-04-04
 *
 */
public class DCP_OrderECSitePrintQueryRes extends JsonRes {
	private String airway_billsUrl; // 托运单地址

	public String getAirway_billsUrl() {
		return airway_billsUrl;
	}

	public void setAirway_billsUrl(String airway_billsUrl) {
		this.airway_billsUrl = airway_billsUrl;
	}
	
}
