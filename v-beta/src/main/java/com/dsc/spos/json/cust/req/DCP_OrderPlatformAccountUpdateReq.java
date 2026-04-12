package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderPlatformAccountUpdateReq extends JsonBasicReq {
	private String oEId;
	private String loadDocType;
	private String appKey;// 开发者ID
	private String appSecret;// 开发者秘钥
	private String appName;// 应该名称
	private String isTest; // 是否测试环境
	private String appsignkey;// 商户ID
	private String gyShopCode;


	public String getoEId() {
		return oEId;
	}

	public void setoEId(String oEId) {
		this.oEId = oEId;
	}

	public String getLoadDocType() {
		return loadDocType;
	}

	public void setLoadDocType(String loadDocType) {
		this.loadDocType = loadDocType;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getIsTest() {
		return isTest;
	}

	public void setIsTest(String isTest) {
		this.isTest = isTest;
	}

	public String getAppsignkey() {
		return appsignkey;
	}

	public void setAppsignkey(String appsignkey) {
		this.appsignkey = appsignkey;
	}

	public String getGyShopCode() {
		return gyShopCode;
	}

	public void setGyShopCode(String gyShopCode) {
		this.gyShopCode = gyShopCode;
	}

}
