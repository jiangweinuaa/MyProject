package com.dsc.spos.ninetyone.response;

/**
 * 基础response
 * @author LN 08546
 */
public class NOBasicReq {

	public NOBasicReq() {
		
	}

	private String ErrorId;
	
	private String Status;//執行結果 (Success:成功； Failure:失敗)
	
	private String ErrorMessage;//錯誤訊息
	
	private String TimeStamp;

	public String getErrorId() {
		return ErrorId;
	}

	public void setErrorId(String errorId) {
		ErrorId = errorId;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status.trim().toLowerCase();
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}

	public String getTimeStamp() {
		return TimeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		TimeStamp = timeStamp;
	}

	

}
