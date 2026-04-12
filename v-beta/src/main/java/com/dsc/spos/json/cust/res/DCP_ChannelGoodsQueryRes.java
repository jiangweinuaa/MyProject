package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_ChannelGoodsQueryRes extends JsonRes{
	
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
//		private String channelId;
//		private String channelName;
//		private String organizationNo;
//		private String organizationName;
		private String pluNo;
		private String pluName;
//		private String sUnit; //交易单位
//		private String sUnitName;
//		private String warehouse;
//		private String warehouseName;
		private String baseUnit;
		
		private List<FeatureList> featureList;
		private List<OrgList> organizationList;
		private List<UnitList> sUnitList;
		public String getPluNo() {
			return pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public List<FeatureList> getFeatureList() {
			return featureList;
		}
		public List<OrgList> getOrganizationList() {
			return organizationList;
		}
		public List<UnitList> getsUnitList() {
			return sUnitList;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		public void setFeatureList(List<FeatureList> featureList) {
			this.featureList = featureList;
		}
		public void setOrganizationList(List<OrgList> organizationList) {
			this.organizationList = organizationList;
		}
		public void setsUnitList(List<UnitList> sUnitList) {
			this.sUnitList = sUnitList;
		}
//		public String getChannelId() {
//			return channelId;
//		}
//		public void setChannelId(String channelId) {
//			this.channelId = channelId;
//		}
		
		
		
	}
	
	public class FeatureList{
		
		private String featureNo;
		private String featureName;
		
		public String getFeatureNo() {
			return featureNo;
		}
		public String getFeatureName() {
			return featureName;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}
		
	}
	
	public class OrgList{
		
		private String organizationNo;
		private String organizationName;
		
		private List<WarehouseList> warehouseList;

		public String getOrganizationNo() {
			return organizationNo;
		}

		public String getOrganizationName() {
			return organizationName;
		}

		public List<WarehouseList> getWarehouseList() {
			return warehouseList;
		}

		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}

		public void setOrganizationName(String organizationName) {
			this.organizationName = organizationName;
		}

		public void setWarehouseList(List<WarehouseList> warehouseList) {
			this.warehouseList = warehouseList;
		}
		
	}
	
	public class WarehouseList{
		
		private String warehouse;
		private String warehouseName;
		
		public String getWarehouse() {
			return warehouse;
		}
		public String getWarehouseName() {
			return warehouseName;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}
		
	}

	public class UnitList{
		
		private String sUnit;
		private String sUnitName;
		
		public String getsUnit() {
			return sUnit;
		}
		public String getsUnitName() {
			return sUnitName;
		}
		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}
		public void setsUnitName(String sUnitName) {
			this.sUnitName = sUnitName;
		}
		
	}
	
	
}
