package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 渠道库存分配增加减少
 * @author 2020-06-05
 *
 */
public class DCP_StockChannelDetailReq extends JsonBasicReq {
	
	private levelReq request;

	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}
	
	public class levelReq{
		private String channelId;
		private String pluNo;
		private String featureNo;
		private String sUnit;
		private String warehouse;
		private String keyTxt;
		
		private List<OrganizationList> organizationList;

		public String getChannelId() {
			return channelId;
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

		public String getKeyTxt() {
			return keyTxt;
		}

		public List<OrganizationList> getOrganizationList() {
			return organizationList;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
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

		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}

		public void setOrganizationList(List<OrganizationList> organizationList) {
			this.organizationList = organizationList;
		}
		
	}
	
	public class OrganizationList{
		private String organizationNo;

		public String getOrganizationNo() {
			return organizationNo;
		}

		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		
	}
	
}
