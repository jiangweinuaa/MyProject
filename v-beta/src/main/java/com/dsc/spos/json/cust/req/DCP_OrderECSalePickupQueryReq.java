package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 订单/销售单点货查询
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_OrderECSalePickupQueryReq extends JsonBasicReq {
	private String ecType; // 1订单， 2 销售单
	private String ecOrderNo; // 单号
	private String keyTxt; //
	
	private String ecPlatformNo; //电商平台代码
	
	public String getEcType() {
		return ecType;
	}
	public void setEcType(String ecType) {
		this.ecType = ecType;
	}
	public String getEcOrderNo() {
		return ecOrderNo;
	}
	public void setEcOrderNo(String ecOrderNo) {
		this.ecOrderNo = ecOrderNo;
	}
	public String getEcPlatformNo() {
		return ecPlatformNo;
	}
	public void setEcPlatformNo(String ecPlatformNo) {
		this.ecPlatformNo = ecPlatformNo;
	}
	public String getKeyTxt() {
		return keyTxt;
	}
	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}
	
}
