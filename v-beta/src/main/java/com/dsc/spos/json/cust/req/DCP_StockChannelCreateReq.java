package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 渠道库存分配新增
 *
 */
public class DCP_StockChannelCreateReq extends JsonBasicReq {
	
	private levReq request;

	public levReq getRequest() {
		return request;
	}

	public void setRequest(levReq request) {
		this.request = request;
	}
	
	public class levReq{
		
		private String channelId ;
		private String sQty;
		private List<PluList> pluList;
		private List<OrganizationList> organizationList;
		public String getChannelId() {
			return channelId;
		}
		public String getsQty() {
			return sQty;
		}
		public List<PluList> getPluList() {
			return pluList;
		}
		public List<OrganizationList> getOrganizationList() {
			return organizationList;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setsQty(String sQty) {
			this.sQty = sQty;
		}
		public void setPluList(List<PluList> pluList) {
			this.pluList = pluList;
		}
		public void setOrganizationList(List<OrganizationList> organizationList) {
			this.organizationList = organizationList;
		}
		
	}
	
	public class PluList{
		private String pluNo;
		private String baseUnit;
		private String sUnit;
		private String featureNo;
		public String getPluNo() {
			return pluNo;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public String getsUnit() {
			return sUnit;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		
		
	}

	public class OrganizationList{
		private String organizationNo;
		
		private List<WarehouseList> warehouseList;

		public String getOrganizationNo() {
			return organizationNo;
		}

		public List<WarehouseList> getWarehouseList() {
			return warehouseList;
		}

		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}

		public void setWarehouseList(List<WarehouseList> warehouseList) {
			this.warehouseList = warehouseList;
		}
		
	}
	
	public class WarehouseList{
		private String warehouse;

		public String getWarehouse() {
			return warehouse;
		}

		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		
	}
	
}
