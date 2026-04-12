package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_CreditPayResultQueryRes extends JsonRes
{
	
	private String trade_no;
	private String totalAmount;
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	
	
}
