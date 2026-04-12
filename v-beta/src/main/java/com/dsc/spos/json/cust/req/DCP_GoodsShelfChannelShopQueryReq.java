package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * V3-商品上下架渠道下属门店查询
 * http://183.233.190.204:10004/project/144/interface/api/3230
 */
public class DCP_GoodsShelfChannelShopQueryReq extends JsonBasicReq {
	public levReq request;

	public levReq getRequest() {
		return request;
	}

	public void setRequest(levReq request) {
		this.request = request;
	}
	
	
	public class levReq{
		
		private String pluNo;
		private String keyTxt;
		private String channelId;
		private String restrictShop;
		
		public String getPluNo() {
			return pluNo;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public String getChannelId() {
			return channelId;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		
	}
	
	
}
