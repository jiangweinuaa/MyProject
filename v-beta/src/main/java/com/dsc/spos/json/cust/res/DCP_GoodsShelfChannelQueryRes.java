package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


public class DCP_GoodsShelfChannelQueryRes  extends JsonRes {
		
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
		private String status;//状态：100上架0-下架			
		
		private String onShelfAuto; // 是否自动上架0-否1-是	
		private String onShelfDate; // 自动上架日期YYYY-MM-DD	
		private String onShelfTime; // 自动上架时间HH:MM:SS	
		private String offShelfAuto;//是否自动下架0-否1-是	
		private String offShelfDate; //自动下架日期YYYY-MM-DD	
		private String offShelfTime; //自动下架日期YYYY-MM-DD
		private String restrictShop; //是否适用全部门店
		
		public String getChannelId() {
			return channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public String getStatus() {
			return status;
		}
		public String getOnShelfAuto() {
			return onShelfAuto;
		}
		public String getOnShelfDate() {
			return onShelfDate;
		}
		public String getOnShelfTime() {
			return onShelfTime;
		}
		public String getOffShelfAuto() {
			return offShelfAuto;
		}
		public String getOffShelfDate() {
			return offShelfDate;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void setOnShelfAuto(String onShelfAuto) {
			this.onShelfAuto = onShelfAuto;
		}
		public void setOnShelfDate(String onShelfDate) {
			this.onShelfDate = onShelfDate;
		}
		public void setOnShelfTime(String onShelfTime) {
			this.onShelfTime = onShelfTime;
		}
		public void setOffShelfAuto(String offShelfAuto) {
			this.offShelfAuto = offShelfAuto;
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
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		
		//2020-11-04  SA-武小凤规划, 去掉 shopList 
//		private List<shopInfo> shopList;
	
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
