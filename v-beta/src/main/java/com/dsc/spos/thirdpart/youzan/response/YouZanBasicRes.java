package com.dsc.spos.thirdpart.youzan.response;

/**
 * 呼叫有赞接口返参(基础)
 * @author LN 08546
 */
public class YouZanBasicRes {

	public YouZanBasicRes() {
		
	}

	private String success;
	private String errorCode;
	private String errorMsg;
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
