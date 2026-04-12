package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.cust.JsonReq;

/**
 * 同意/拒绝买家取消订单
 * @author yuanyy 2019-07-01
 *
 */
public class DCP_OrderECAgreeOrRejectBuyerCancelReq extends JsonReq {
	
	/**
	 * {	
		   "serviceId": "OrderECAgreeOrRejectBuyerCancelDCP",	必传，服务名
		   "token": "f14ee75ff5b220177ac0dc538bdea08c",	必传且非空，访问令牌
		    "ecPlatformNo": ""	电商平台代码
		    "ecOrderNo": "",	订单号
		    "opType": "",	操作类型 1:同意 2：拒绝
		}	

	 */
	private String ecPlatformNo;
	private String ecOrderNo;
	private String opType;
	public String getEcPlatformNo() {
		return ecPlatformNo;
	}
	public void setEcPlatformNo(String ecPlatformNo) {
		this.ecPlatformNo = ecPlatformNo;
	}
	public String getEcOrderNo() {
		return ecOrderNo;
	}
	public void setEcOrderNo(String ecOrderNo) {
		this.ecOrderNo = ecOrderNo;
	}
	public String getOpType() {
		return opType;
	}
	public void setOpType(String opType) {
		this.opType = opType;
	}
	
}
