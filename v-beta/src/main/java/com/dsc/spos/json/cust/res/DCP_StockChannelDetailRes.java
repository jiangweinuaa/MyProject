package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_StockChannelDetailRes extends JsonRes{
	
	private levelRes datas;
	
	public levelRes getDatas() {
		return datas;
	}

	public void setDatas(levelRes datas) {
		this.datas = datas;
	}

	public class levelRes{
		private List<PluList> pluList;

		public List<PluList> getPluList() {
			return pluList;
		}

		public void setPluList(List<PluList> pluList) {
			this.pluList = pluList;
		}
		
	}
	
	public class PluList{
		private String channelId;
		private String channelName;
		private String organizationNo;
		private String organizationName;
		private String pluNo;
		private String pluName;
		private String featureNo;
		private String featureName;
		private String sUnit; //交易单位
		private String sUnitName;
		private String warehouse;
		private String warehouseName;
		private String listImage; //图片名称
		private String avalibleQty; //可用数
		private String onlineQty; // 预留（上架）数
		private String lockQty; // 锁定数
		private String baseUnit; //基准单位
		private String unitRatio;
		private String sUnitLength;
		
		public String getChannelId() {
			return channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public String getOrganizationNo() {
			return organizationNo;
		}
		public String getOrganizationName() {
			return organizationName;
		}
		public String getPluNo() {
			return pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public String getsUnit() {
			return sUnit;
		}
		public String getsUnitName() {
			return sUnitName;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public String getWarehouseName() {
			return warehouseName;
		}
		public String getOnlineQty() {
			return onlineQty;
		}
		public String getLockQty() {
			return lockQty;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		public void setOrganizationName(String organizationName) {
			this.organizationName = organizationName;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}
		public void setsUnitName(String sUnitName) {
			this.sUnitName = sUnitName;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}
		public void setOnlineQty(String onlineQty) {
			this.onlineQty = onlineQty;
		}
		public void setLockQty(String lockQty) {
			this.lockQty = lockQty;
		}
		public String getListImage() {
			return listImage;
		}
		public void setListImage(String listImage) {
			this.listImage = listImage;
		}
		public String getFeatureName() {
			return featureName;
		}
		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}
		public String getAvalibleQty() {
			return avalibleQty;
		}
		public void setAvalibleQty(String avalibleQty) {
			this.avalibleQty = avalibleQty;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		public String getUnitRatio() {
			return unitRatio;
		}
		public String getsUnitLength() {
			return sUnitLength;
		}
		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
		}
		public void setsUnitLength(String sUnitLength) {
			this.sUnitLength = sUnitLength;
		}
		
	}
	
}
