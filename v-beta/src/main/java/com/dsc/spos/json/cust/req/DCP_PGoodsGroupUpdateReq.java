package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PGoodsGroupUpdateReq extends JsonBasicReq 
{

	private String pluNO;
	private String pclassNO;
	private String invoWay;
	private String condCount;
	private String priority; 
	public String getPluNO() {
		return pluNO;
	}
	public void setPluNO(String pluNO) {
		this.pluNO = pluNO;
	}
	public String getPclassNO() {
		return pclassNO;
	}
	public void setPclassNO(String pclassNO) {
		this.pclassNO = pclassNO;
	}
	public String getInvoWay() {
		return invoWay;
	}
	public void setInvoWay(String invoWay) {
		this.invoWay = invoWay;
	}
	public String getCondCount() {
		return condCount;
	}
	public void setCondCount(String condCount) {
		this.condCount = condCount;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	
}
