package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 库存解锁
 * @author 2020-06-18
 *
 */
public class DCP_StockUnlockReq extends JsonBasicReq {
	private levelReq request;

	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}
	
	public class levelReq{
		
		private List<PluList> pluList;
		private String billNo;
		private String billType;
		private String channelId;
		private String unLockType;
		private String bDate;
		
		public List<PluList> getPluList() {
			return pluList;
		}

		public void setPluList(List<PluList> pluList) {
			this.pluList = pluList;
		}

		public String getBillNo() {
			return billNo;
		}

		public String getBillType() {
			return billType;
		}

		public String getChannelId() {
			return channelId;
		}

		public String getUnLockType() {
			return unLockType;
		}

		public String getbDate() {
			return bDate;
		}

		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}

		public void setBillType(String billType) {
			this.billType = billType;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public void setUnLockType(String unLockType) {
			this.unLockType = unLockType;
		}

		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		
	}
	
	public class PluList{
		
		private String pluNo;
		private String featureNo;
		private String sUnit; // 交易单位
		private List<OrgList> organizationList;
		public String getPluNo() {
			return pluNo;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public String getsUnit() {
			return sUnit;
		}
		public List<OrgList> getOrganizationList() {
			return organizationList;
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
		public void setOrganizationList(List<OrgList> organizationList) {
			this.organizationList = organizationList;
		}
		
	}
	
	public class OrgList{
		
		private String organizationNo;
		private String warehouse;
		private String qty;
		
		public String getOrganizationNo() {
			return organizationNo;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public String getQty() {
			return qty;
		}
		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		
	}
	

}
