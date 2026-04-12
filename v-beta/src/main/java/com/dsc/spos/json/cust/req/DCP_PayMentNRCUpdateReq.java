package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * CRM与云中台支付方式映射关系修改
 * @author yuanyy 2019-08-27
 *
 */
public class DCP_PayMentNRCUpdateReq extends JsonBasicReq {
	
	private String platformPayCode;
	private String platformPayName;
	private String crmPayCode;
	private String crmPayName;
	private String priority; 
	private String status;
	private String onSale;
	
	private String toPriority;
	
	public String getPlatformPayCode() {
		return platformPayCode;
	}
	public void setPlatformPayCode(String platformPayCode) {
		this.platformPayCode = platformPayCode;
	}
	public String getPlatformPayName() {
		return platformPayName;
	}
	public void setPlatformPayName(String platformPayName) {
		this.platformPayName = platformPayName;
	}
	public String getCrmPayCode() {
		return crmPayCode;
	}
	public void setCrmPayCode(String crmPayCode) {
		this.crmPayCode = crmPayCode;
	}
	public String getCrmPayName() {
		return crmPayName;
	}
	public void setCrmPayName(String crmPayName) {
		this.crmPayName = crmPayName;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToPriority() {
		return toPriority;
	}
	public void setToPriority(String toPriority) {
		this.toPriority = toPriority;
	}
	public String getOnSale() {
		return onSale;
	}
	public void setOnSale(String onSale) {
		this.onSale = onSale;
	}
	
}
