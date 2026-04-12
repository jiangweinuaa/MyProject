package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsSetQueryReq extends JsonBasicReq
{
	private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}
	
	public class levelRequest {
		private String keyTxt;
		private String status;
		private String brandNo;
		private String seriesNo;
		private String category;
		private String pluType;
		private String virtual;
		private String openPrice;
		private String isWeight;
		private String isBatch;
		private String redisUpdateSuccess;//同步缓存是否成功Y/N
		private String searchScope;
		private String selfBuiltShopId;
		
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getBrandNo() {
			return brandNo;
		}
		public void setBrandNo(String brandNo) {
			this.brandNo = brandNo;
		}
		public String getSeriesNo() {
			return seriesNo;
		}
		public void setSeriesNo(String seriesNo) {
			this.seriesNo = seriesNo;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getPluType() {
			return pluType;
		}
		public void setPluType(String pluType) {
			this.pluType = pluType;
		}
		public String getVirtual() {
			return virtual;
		}
		public void setVirtual(String virtual) {
			this.virtual = virtual;
		}
		public String getOpenPrice() {
			return openPrice;
		}
		public void setOpenPrice(String openPrice) {
			this.openPrice = openPrice;
		}
		public String getIsWeight() {
			return isWeight;
		}
		public void setIsWeight(String isWeight) {
			this.isWeight = isWeight;
		}
		public String getIsBatch() {
			return isBatch;
		}
		public void setIsBatch(String isBatch) {
			this.isBatch = isBatch;
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
