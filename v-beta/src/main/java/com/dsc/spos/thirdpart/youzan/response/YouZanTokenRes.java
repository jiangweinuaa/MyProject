package com.dsc.spos.thirdpart.youzan.response;

/**
 * 呼叫有赞接口返参(获取token)
 * @author LN 08546
 */
public class YouZanTokenRes extends YouZanBasicRes  {

	private Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public static class Data{
		private String access_token;
		private String expires;//access_token 的过期时间，时间戳（单位：毫秒；过期时间：7天）
		private String authority_id;//授权主体id，即店铺id

		public String getExpires() {
			return expires;
		}

		public void setExpires(String expires) {
			this.expires = expires;
		}

		public String getAuthority_id() {
			return authority_id;
		}

		public void setAuthority_id(String authority_id) {
			this.authority_id = authority_id;
		}

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}
	}
}
