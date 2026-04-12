package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 电商订单导入格式删除
 * @author yuanyy 2019-03-08
 *
 */
public class DCP_OrderECFormatDeleteReq extends JsonBasicReq {
	private String orderFormatNo;

	public String getOrderFormatNo() {
		return orderFormatNo;
	}

	public void setOrderFormatNo(String orderFormatNo) {
		this.orderFormatNo = orderFormatNo;
	}
	
	
}
