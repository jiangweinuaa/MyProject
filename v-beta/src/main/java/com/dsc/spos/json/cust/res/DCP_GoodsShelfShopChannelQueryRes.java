package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_GoodsShelfShopChannelQueryRes extends JsonBasicRes {
	
	private List<ShopChannelList> datas;
	
	public List<ShopChannelList> getDatas() {
		return datas;
	}

	public void setDatas(List<ShopChannelList> datas) {
		this.datas = datas;
	}

	public class ShopChannelList{
		
		private String channelId;
		private String channelName;
		private String status_channel;
		private String status_shop;
		private String shopId;
		private String shopName;
		
		public String getChannelId() {
			return channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public String getStatus_channel() {
			return status_channel;
		}
		public String getStatus_shop() {
			return status_shop;
		}
		public String getShopId() {
			return shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		public void setStatus_channel(String status_channel) {
			this.status_channel = status_channel;
		}
		public void setStatus_shop(String status_shop) {
			this.status_shop = status_shop;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		
	}
 	
}
