package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 电商出货作业	
 * @author yuanyy 2019-03-19
 *
 */
public class DCP_OrderECShippingCreateReq extends JsonBasicReq {
	/**
	 * {
	    "serviceId": "OrderECShippingCreateDCP",
	    "token": "f14ee75ff5b220177ac0dc538bdea08c",
	    "shipmentNo": ["123","456"],
	    "ecPlatformNo": ""
	    "opType": "1"	1:发货 2:安排取件										
		}
	 */
	
	private String[] shipmentNo;
	private String ecPlatformNo;
	private String opType ;

	
	//这几个参数是JOB定时器 调用此服务用的
	private String eShopId;
	private String eEId;
	private String eOrganizationNO;
	private String Jobway;//1：JOB定时器调用的 2：中台前端页面调用的

	public String getOpType() {
		return opType;
	}
	public void setOpType(String opType) {
		this.opType = opType;
	}
	public String[] getShipmentNo() {
		return shipmentNo;
	}
	public void setShipmentNo(String[] shipmentNo) {
		this.shipmentNo = shipmentNo;
	}
	public String getEcPlatformNo() {
		return ecPlatformNo;
	}
	public void setEcPlatformNo(String ecPlatformNo) {
		this.ecPlatformNo = ecPlatformNo;
	}
	public String geteShopId() {
		return eShopId;
	}
	public void seteShopId(String eShopId) {
		this.eShopId = eShopId;
	}
	public String geteEId() {
		return eEId;
	}
	public void seteEId(String eEId) {
		this.eEId = eEId;
	}
	public String geteOrganizationNO() {
		return eOrganizationNO;
	}
	public void seteOrganizationNO(String eOrganizationNO) {
		this.eOrganizationNO = eOrganizationNO;
	}
	public String getJobway() {
		return Jobway;
	}
	public void setJobway(String jobway) {
		Jobway = jobway;
	}
}
