package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 大物流中心资料新增
 * @author yuanyy 2019-03-27
 *
 */
public class DCP_OrderECDCDeleteReq extends JsonBasicReq {

	private String dcNo;

	public String getDcNo() {
		return dcNo;
	}

	public void setDcNo(String dcNo) {
		this.dcNo = dcNo;
	}
	
}
