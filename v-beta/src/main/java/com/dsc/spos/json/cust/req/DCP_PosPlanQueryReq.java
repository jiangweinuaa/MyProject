package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * POS 查询生产计划
 * @author yuanyy 2019-10-29
 *
 */
public class DCP_PosPlanQueryReq extends JsonBasicReq {

	private String oEId;
	private String oShopId;
	private String bDate;
	private String fType;
	private String fNo;

	public String getoEId() {
		return oEId;
	}
	public void setoEId(String oEId) {
		this.oEId = oEId;
	}
	public String getoShopId() {
		return oShopId;
	}
	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}
	public String getbDate() {
		return bDate;
	}
	public void setbDate(String bDate) {
		this.bDate = bDate;
	}
	public String getfType() {
		return fType;
	}
	public void setfType(String fType) {
		this.fType = fType;
	}
	public String getfNo() {
		return fNo;
	}
	public void setfNo(String fNo) {
		this.fNo = fNo;
	}


}
