package com.dsc.spos.waimai.jddj;

import java.util.List;

public class OShopCategoryResult {

	private String detail;
  private String code;
  private boolean success;
  private String msg;
  List<OShopCategory> result;
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
	public List<OShopCategory> getResult() {
		return result;
	}
	public void setResult(List<OShopCategory> result) {
		this.result = result;
	}
  
  
  
}
