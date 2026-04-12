package com.dsc.spos.thirdpart.youzan.response;

/**
 * 适用于同城配送发货（仅支持商家自配送）
 * https://doc.youzanyun.com/doc#/content/API/0/detail/api/0/712
 * /api/youzan.logistics.online.local.confirm/3.0.0
 * @author LN 08546
 */
public class YouZanLocalConfirmRes extends YouZanBasicRes  {

	private Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public static class Data{
		private String is_success;
		public String getIs_success() {
			return is_success;
		}
		public void setIs_success(String is_success) {
			this.is_success = is_success;
		}
	}
}
