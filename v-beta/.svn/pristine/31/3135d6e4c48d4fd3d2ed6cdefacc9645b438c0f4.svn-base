package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_SalePriceTemplateQueryReq extends JsonBasicReq {
	
	private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}
	
	public class levelRequest {
		private String status;//-1未启用100已启用0已禁用
		private String keyTxt;//编码/名称模糊搜索
		private String shopId;//适用门店编号
		private String channelId;//适用渠道编号
		private String redisUpdateSuccess;//同步缓存是否成功Y/N
		private String searchScope;
        private String selfBuiltShopId;
		
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getChannelId() {
			return channelId;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getRedisUpdateSuccess() {
			return redisUpdateSuccess;
		}
		public void setRedisUpdateSuccess(String redisUpdateSuccess) {
			this.redisUpdateSuccess = redisUpdateSuccess;
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
