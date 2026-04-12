package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 大智通配送流水新增
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECDZTBookCreateReq extends JsonBasicReq {
    private String dztNo;
    private String dztDescription;
    private String status;
    private String startNo;
    private String endNo;
    private String lastNo;
    private String inputDate;
    
	public String getDztNo() {
		return dztNo;
	}
	public void setDztNo(String dztNo) {
		this.dztNo = dztNo;
	}
	public String getDztDescription() {
		return dztDescription;
	}
	public void setDztDescription(String dztDescription) {
		this.dztDescription = dztDescription;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartNo() {
		return startNo;
	}
	public void setStartNo(String startNo) {
		this.startNo = startNo;
	}
	public String getEndNo() {
		return endNo;
	}
	public void setEndNo(String endNo) {
		this.endNo = endNo;
	}
	public String getLastNo() {
		return lastNo;
	}
	public void setLastNo(String lastNo) {
		this.lastNo = lastNo;
	}
	public String getInputDate() {
		return inputDate;
	}
	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	
}
