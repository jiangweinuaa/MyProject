package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 可用性库存数量查询
 * @author 2020-06-11
 *
 */
public class DCP_StockQtyQueryReq extends JsonBasicReq {
	
	private levelReq request;
	
	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}

	public class levelReq{
		private List<PluList> pluList;
		
		private String channelId;
		private String organizationNo;
		
		public List<PluList> getPluList() {
			return pluList;
		}

		public void setPluList(List<PluList> pluList) {
			this.pluList = pluList;
		}

		public String getChannelId() {
			return channelId;
		}

		public String getOrganizationNo() {
			return organizationNo;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		
		
	}
	
	public class PluList{
		private String pluNo;
		private String featureNo;
		private String sUnit;
		private String warehouse; // 仓位这个字段预留， 规格上没有。  如果以后规格允许查仓位库存，传值即可。 不传程序也有做兼容
//		private String qty;
		
		public String getPluNo() {
			return pluNo;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public String getsUnit() {
			return sUnit;
		}
		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		
		
	}
	
}
