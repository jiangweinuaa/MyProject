package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsImageQueryReq extends JsonBasicReq {
	
	private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}
	
	public class levelRequest {
		private String appType;//应用类型：ALL-所有应用 POS_SELF-自助收银 MALL-商城 ORDER_SCAN扫码点餐 ORDER_WAIMAI-外卖点餐 POS-POS
		private String keyTxt;//编码/名称模糊搜索
		private String searchScope;
		private String selfBuiltShopId;
		
		public String getAppType() {
			return appType;
		}
		public void setAppType(String appType) {
			this.appType = appType;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getSearchScope() {
			return searchScope;
		}
		public void setSearchScope(String searchScope) {
			this.searchScope = searchScope;
		}
		public String getSelfBuiltShopId() {
			return selfBuiltShopId;
		}
		public void setSelfBuiltShopId(String selfBuiltShopId) {
			this.selfBuiltShopId = selfBuiltShopId;
		}
	}
	
}
