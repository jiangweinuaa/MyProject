package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 渠道库存分配增加减少
 * @author 2020-06-05
 *
 */
public class DCP_StockChannelUpdateReq extends JsonBasicReq {
	
	private levelReq request;

	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}
	
	public class levelReq{
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
		private String organizationNo;
		private String pluNo;
		private String featureNo;
		private String sUnit;
		private String warehouse;
		private String direction;
		private String sQty;
		
		private String baseUnit;
		
		public String getChannelId() {
			return channelId;
		}
		public String getOrganizationNo() {
			return organizationNo;
		}
		public String getPluNo() {
			return pluNo;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public String getsUnit() {
			return sUnit;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public String getDirection() {
			return direction;
		}
		public String getsQty() {
			return sQty;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public void setDirection(String direction) {
			this.direction = direction;
		}
		public void setsQty(String sQty) {
			this.sQty = sQty;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		
	}
	
	
}
