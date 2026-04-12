package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 超商取货门店查询
 * @author yuanyy 2019-05-17
 *
 */
public class DCP_OrderECGETSHOPQueryReq extends JsonBasicReq {
	private String ecOrderNo;
	
	private String timeNo;

	public String getEcOrderNo() {
		return ecOrderNo;
	}

	public void setEcOrderNo(String ecOrderNo) {
		this.ecOrderNo = ecOrderNo;
	}

	public String getTimeNo() {
		return timeNo;
	}

	public void setTimeNo(String timeNo) {
		this.timeNo = timeNo;
	}
	
	
}
