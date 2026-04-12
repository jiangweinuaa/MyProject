package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderShopStatusQueryRes extends JsonRes
{
	private String elmStatus;
	private String jbpStatus;
	private String jdStatus;
	private String gwStatus;
	
	public String getElmStatus() {
		return elmStatus;
	}
	public void setElmStatus(String elmStatus) {
		this.elmStatus = elmStatus;
	}
	public String getJbpStatus() {
		return jbpStatus;
	}
	public void setJbpStatus(String jbpStatus) {
		this.jbpStatus = jbpStatus;
	}
	public String getJdStatus() {
		return jdStatus;
	}
	public void setJdStatus(String jdStatus) {
		this.jdStatus = jdStatus;
	}
	public String getGwStatus() {
		return gwStatus;
	}
	public void setGwStatus(String gwStatus) {
		this.gwStatus = gwStatus;
	}
	
}

