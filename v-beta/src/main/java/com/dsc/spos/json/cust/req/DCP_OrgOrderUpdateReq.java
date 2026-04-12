package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrgOrderUpdateReq extends JsonBasicReq
{
	private String enterprise_no;
	private String oShopId;
	private String front_no;
	private String operation_type;
	private String operation_opno;
	private String operation_opname;
	private String operation_date;
	private String operation_time;
	public String getEnterprise_no() {
		return enterprise_no;
	}
	public void setEnterprise_no(String enterprise_no) {
		this.enterprise_no = enterprise_no;
	}
	public String getoShopId() {
		return oShopId;
	}
	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}
	public String getFront_no() {
		return front_no;
	}
	public void setFront_no(String front_no) {
		this.front_no = front_no;
	}
	public String getOperation_type() {
		return operation_type;
	}
	public void setOperation_type(String operation_type) {
		this.operation_type = operation_type;
	}
	public String getOperation_opno() {
		return operation_opno;
	}
	public void setOperation_opno(String operation_opno) {
		this.operation_opno = operation_opno;
	}
	public String getOperation_opname() {
		return operation_opname;
	}
	public void setOperation_opname(String operation_opname) {
		this.operation_opname = operation_opname;
	}
	public String getOperation_date() {
		return operation_date;
	}
	public void setOperation_date(String operation_date) {
		this.operation_date = operation_date;
	}
	public String getOperation_time() {
		return operation_time;
	}
	public void setOperation_time(String operation_time) {
		this.operation_time = operation_time;
	}
	
}
