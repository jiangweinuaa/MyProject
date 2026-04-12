package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


public class DCP_GoodsShelfShopQueryRes  extends JsonRes {
		
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm
	{
		private String channelId;
		private String channelName;
//		private String status;//状态：100上架0-下架	
		private String shopId;
		private String shopName;
		
		//2020-11-05 SA-武小凤增加 以下两个字段
		private String status_channel; //渠道上架状态：100上架0-下架
		private String status_shop; //门店上架状态：100上架0-下架

        private String onShelfAuto; // 是否自动上架0-否1-是
        private String onShelfDate; // 自动上架日期YYYY-MM-DD
        private String onShelfTime; // 自动上架时间HH:MM:SS
        private String offShelfAuto; // 是否自动下架0-否1-是
        private String offShelfDate; // 自动下架日期YYYY-MM-DD
        private String offShelfTime; //自动下架时间HH:MM:SS

		//private List<shopInfo> shopList;
		public String getChannelId()
		{
			return channelId;
		}
		public void setChannelId(String channelId)
		{
			this.channelId = channelId;
		}
		public String getChannelName()
		{
			return channelName;
		}
		public void setChannelName(String channelName)
		{
			this.channelName = channelName;
		}
		
		public String getStatus_channel() {
			return status_channel;
		}
		public String getStatus_shop() {
			return status_shop;
		}
		public void setStatus_channel(String status_channel) {
			this.status_channel = status_channel;
		}
		public void setStatus_shop(String status_shop) {
			this.status_shop = status_shop;
		}

		public String getOnShelfAuto() {
			return onShelfAuto;
		}

		public void setOnShelfAuto(String onShelfAuto) {
			this.onShelfAuto = onShelfAuto;
		}

		public String getOnShelfDate() {
			return onShelfDate;
		}

		public void setOnShelfDate(String onShelfDate) {
			this.onShelfDate = onShelfDate;
		}

		public String getOnShelfTime() {
			return onShelfTime;
		}

		public void setOnShelfTime(String onShelfTime) {
			this.onShelfTime = onShelfTime;
		}

		public String getOffShelfAuto() {
			return offShelfAuto;
		}

		public void setOffShelfAuto(String offShelfAuto) {
			this.offShelfAuto = offShelfAuto;
		}

		public String getOffShelfDate() {
			return offShelfDate;
		}

		public void setOffShelfDate(String offShelfDate) {
			this.offShelfDate = offShelfDate;
		}

		public String getOffShelfTime() {
			return offShelfTime;
		}

		public void setOffShelfTime(String offShelfTime) {
			this.offShelfTime = offShelfTime;
		}

		//		public String getStatus()
//		{
//			return status;
//		}
//		public void setStatus(String status)
//		{
//			this.status = status;
//		}
		/*public List<shopInfo> getShopList()
		{
			return shopList;
		}
		public void setShopList(List<shopInfo> shopList)
		{
			this.shopList = shopList;
		}*/
		public String getShopId()
		{
			return shopId;
		}
		public void setShopId(String shopId)
		{
			this.shopId = shopId;
		}
		public String getShopName()
		{
			return shopName;
		}
		public void setShopName(String shopName)
		{
			this.shopName = shopName;
		}
			
	
	}
	
	public class shopInfo
	{
		private String shopId;
		private String shopName;
		private String status;
		public String getShopId()
		{
			return shopId;
		}
		public void setShopId(String shopId)
		{
			this.shopId = shopId;
		}
		public String getShopName()
		{
			return shopName;
		}
		public void setShopName(String shopName)
		{
			this.shopName = shopName;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		
			
	}
	
	
	
}
