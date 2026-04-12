package com.dsc.spos.hll.api.response;

/**
 * 基础response
 * @author LN 08546
 */
public class HllResponse {

	public HllResponse() {
		
	}

	private String code;

	private Long groupID;
	
	private String message;
	
	private Long shopID;
	
	private String traceID;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getGroupID() {
		return groupID;
	}

	public void setGroupID(Long groupID) {
		this.groupID = groupID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getShopID() {
		return shopID;
	}

	public void setShopID(Long shopID) {
		this.shopID = shopID;
	}

	public String getTraceID() {
		return traceID;
	}

	public void setTraceID(String traceID) {
		this.traceID = traceID;
	}
	

}
