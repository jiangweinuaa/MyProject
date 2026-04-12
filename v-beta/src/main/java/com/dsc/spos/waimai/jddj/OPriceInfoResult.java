package com.dsc.spos.waimai.jddj;

import java.util.List;

public class OPriceInfoResult {
	private List<OPriceInfo> result;
  private String detail;
  private String code;
  private boolean success;
  private String msg;
	public List<OPriceInfo> getResult() {
		return result;
	}
	public void setResult(List<OPriceInfo> result) {
		this.result = result;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

  
}
