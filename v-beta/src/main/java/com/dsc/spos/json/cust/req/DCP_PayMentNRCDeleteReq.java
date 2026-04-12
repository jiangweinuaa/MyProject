package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * CRM与云中台支付方式映射关系删除
 * @author yuanyy 2019-08-27
 *
 */
public class DCP_PayMentNRCDeleteReq extends JsonBasicReq {
	
	private String platformPayCode;
	private String crmPayCode;
	
	public String getPlatformPayCode() {
		return platformPayCode;
	}
	public void setPlatformPayCode(String platformPayCode) {
		this.platformPayCode = platformPayCode;
	}
	public String getCrmPayCode() {
		return crmPayCode;
	}
	public void setCrmPayCode(String crmPayCode) {
		this.crmPayCode = crmPayCode;
	}
	
	
}
