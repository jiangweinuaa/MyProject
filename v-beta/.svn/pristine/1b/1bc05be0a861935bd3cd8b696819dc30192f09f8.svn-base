package com.dsc.spos.service.utils;

public class MesageUtils {
	
	public enum MessageCommanType {
		SUCCESS, FAIL, EXECUTE_SUCCESS
		, LOGIN_FAIL, LOGIN_SUCCESS
		, NOT_FOUND_PERMISSION
		, NOT_FOUND_SHOP
		, NOT_FOUND_DATA
		, LOSE_TOKEN
	}
	
	private static MesageUtils mu;
	private MesageUtils() {}
	
	public static MesageUtils getInstance() {
		if (mu == null) {
			mu = new MesageUtils();
		}
		return mu;
	}
	
	public String getMessage(MessageCommanType type) {
		//FXIME 這邊還要再做 訊息多語系控制
		switch (type) {
		    case SUCCESS:
		    	return "Success!";
		    case EXECUTE_SUCCESS:
		    	return "執行成功!";
		    case FAIL:
		    	return "Fail!";
		    case NOT_FOUND_SHOP:
		    	return "查無商店資料";
		    case NOT_FOUND_PERMISSION:
		    	return "查無權限資料";
		    case LOGIN_FAIL:
		    	return "登入失敗";
		    case LOGIN_SUCCESS:
		    	return "登入成功";
		    case NOT_FOUND_DATA:
		    	return "查無資料";
		    case LOSE_TOKEN:
		    	return "time out！plz relogin.";
			default:
				return "";
		}
	}
	
	public String getErrorMessage(Exception e) {
		return e.getMessage();
	}
}
