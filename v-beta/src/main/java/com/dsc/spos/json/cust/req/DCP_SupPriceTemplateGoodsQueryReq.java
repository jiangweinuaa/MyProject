package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_SupPriceTemplateGoodsQueryReq extends JsonBasicReq {
	
	private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}
	
	public class levelRequest {
		private String templateId;
		private String category;         //商品分类
		private String sortType;         //显示顺序：1-添加顺序降序2-商品编码升序
		private String keyTxt;           //编码/名称模糊搜索
		private String effectDate;
		private String effectStatus;
		
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getSortType() {
			return sortType;
		}
		public void setSortType(String sortType) {
			this.sortType = sortType;
		}
		public String getEffectDate() {
			return effectDate;
		}
		public void setEffectDate(String effectDate) {
			this.effectDate = effectDate;
		}
		public String getEffectStatus() {
			return effectStatus;
		}
		public void setEffectStatus(String effectStatus) {
			this.effectStatus = effectStatus;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
	}
}
