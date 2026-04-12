package com.dsc.spos.waimai.jddj;

public class OStoreInfoResult {

  private OStoreInfo result;
  private String detail;
  private String code;
  private boolean success;
  private String msg;
  
	public OStoreInfo getResult() {
		return result;
	}
	public void setResult(OStoreInfo result) {
		this.result = result;
	}
	public void setDetail(String detail) {
       this.detail = detail;
   }
   public String getDetail() {
       return detail;
   }

  public void setCode(String code) {
       this.code = code;
   }
   public String getCode() {
       return code;
   }

  public void setSuccess(boolean success) {
       this.success = success;
   }
   public boolean getSuccess() {
       return success;
   }

  public void setMsg(String msg) {
       this.msg = msg;
   }
   public String getMsg() {
       return msg;
   }

}
