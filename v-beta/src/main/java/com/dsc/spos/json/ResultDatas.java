package com.dsc.spos.json;

import com.dsc.spos.model.ApiUser;

public class ResultDatas {
	
	private String status; // SUCCESS： 成功；   FAILED： 失败
	private String eId;
	private String desctiption; // 返回信息
	private ApiUser apiUser;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String geteId() {
		return eId;
	}
	public void seteId(String eId) {
		this.eId = eId;
	}
	public String getDesctiption() {
		return desctiption;
	}
	public void setDesctiption(String desctiption) {
		this.desctiption = desctiption;
	}
	public ApiUser getApiUser() {
		return apiUser;
	}
	public void setApiUser(ApiUser apiUser) {
		this.apiUser = apiUser;
	}
	
	
}
