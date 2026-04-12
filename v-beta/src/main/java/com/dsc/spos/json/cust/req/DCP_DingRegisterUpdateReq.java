package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_DingRegisterUpdate
 * 服务说明：钉钉回调地址注册
 * @author jinzma 
 * @since  2019-12-20
 */
public class DCP_DingRegisterUpdateReq  extends JsonBasicReq{
	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm
	{
		private String appKey;
		private String appSecret;
		private String corpId;
		private String callBackUrl;
		private String registerType;
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
		public String getCorpId() {
			return corpId;
		}
		public void setCorpId(String corpId) {
			this.corpId = corpId;
		}
		public String getCallBackUrl() {
			return callBackUrl;
		}
		public void setCallBackUrl(String callBackUrl) {
			this.callBackUrl = callBackUrl;
		}
		public String getRegisterType() {
			return registerType;
		}
		public void setRegisterType(String registerType) {
			this.registerType = registerType;
		}


	}

}
