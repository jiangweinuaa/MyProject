package com.dsc.spos.json;

import java.beans.Transient;
import java.util.List;
import java.util.Map;

public class JsonBasicRes extends JsonBasic {
	private boolean success = Boolean.FALSE;   //服务执行是否成功
	private String serviceStatus;             //服务状态码
    private String serviceDescription;        //服務描述

    private String oriJson;

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getServiceStatus() {
		return serviceStatus;
	}
	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}


	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@Transient
	public String getOriJson() {
		return oriJson;
	}

	public void setOriJson(String oriJson) {
		this.oriJson = oriJson;
	}
}
