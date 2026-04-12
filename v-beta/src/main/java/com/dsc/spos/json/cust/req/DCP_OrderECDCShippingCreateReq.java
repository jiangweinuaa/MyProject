package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 大物流中心配送新增
 * @author yuanyy 2019-03-27
 *
 */
public class DCP_OrderECDCShippingCreateReq extends JsonBasicReq {
	
	private String shipmentNo;
	private String subShipmentNo[];
	private String lgPlatformNo;
	private String lgPlatformName;
	private String dcNo;
	private String dcName;
	private String status;
	
	
	public String getShipmentNo() {
		return shipmentNo;
	}
	public void setShipmentNo(String shipmentNo) {
		this.shipmentNo = shipmentNo;
	}
	public String[] getSubShipmentNo() {
		return subShipmentNo;
	}
	public void setSubShipmentNo(String[] subShipmentNo) {
		this.subShipmentNo = subShipmentNo;
	}
	public String getLgPlatformNo() {
		return lgPlatformNo;
	}
	public void setLgPlatformNo(String lgPlatformNo) {
		this.lgPlatformNo = lgPlatformNo;
	}
	public String getLgPlatformName() {
		return lgPlatformName;
	}
	public void setLgPlatformName(String lgPlatformName) {
		this.lgPlatformName = lgPlatformName;
	}
	public String getDcNo() {
		return dcNo;
	}
	public void setDcNo(String dcNo) {
		this.dcNo = dcNo;
	}
	public String getDcName() {
		return dcName;
	}
	public void setDcName(String dcName) {
		this.dcName = dcName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
}
