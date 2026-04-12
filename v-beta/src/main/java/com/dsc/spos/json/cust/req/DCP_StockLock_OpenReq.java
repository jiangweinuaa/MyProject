package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 库存锁定
 * @author 2020-06-11
 *
 */
public class DCP_StockLock_OpenReq extends JsonBasicReq {
	
	private levelReq request;
	
	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}

	public class levelReq{
		
		private String billNo;
		private String billType;
		private String channelId;
		private String bDate;
		private String organizationNo;
		
		private String deliveryType;
		private String province;
		private String city;
		private String address;
		
		private List<PluList> pluList;

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

		public String getbDate() {
			return bDate;
		}

		public String getOrganizationNo() {
			return organizationNo;
		}

		public String getDeliveryType() {
			return deliveryType;
		}

		public String getProvince() {
			return province;
		}

		public String getCity() {
			return city;
		}

		public String getAddress() {
			return address;
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

		public void setbDate(String bDate) {
			this.bDate = bDate;
		}

		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}

		public void setDeliveryType(String deliveryType) {
			this.deliveryType = deliveryType;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public void setAddress(String address) {
			this.address = address;
		}
		
	}
	
	public class PluList{
		
		private String pluNo;
		private String featureNo;
		private String sUnit;
//		private String warehouse;
		private String qty;
//		private String baseUnit;
//		private String bQty;
		public String getPluNo() {
			return pluNo;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public String getsUnit() {
			return sUnit;
		}
		public String getQty() {
			return qty;
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
		public void setQty(String qty) {
			this.qty = qty;
		}
//		public String getBaseUnit() {
//			return baseUnit;
//		}
//		public void setBaseUnit(String baseUnit) {
//			this.baseUnit = baseUnit;
//		}
		
		
	}
	
}
