package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 根据经纬度 和 下单门店查配送费
 * @author yuanyy
 *
 */
public class DCP_FreightQuery_OpenReq extends JsonBasicReq {

	private level1Elm request;
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String oEId;
		private String baseSetNo;
		private String shopId;
		private String longitude;
		private String latitude;
		private String address;//配送地址


		public String getoEId() {
			return oEId;
		}
		public void setoEId(String oEId) {
			this.oEId = oEId;
		}
		public String getBaseSetNo() {
			return baseSetNo;
		}
		public String getShopId() {
			return shopId;
		}
		public String getLongitude() {
			return longitude;
		}
		public String getLatitude() {
			return latitude;
		}
		public void setBaseSetNo(String baseSetNo) {
			this.baseSetNo = baseSetNo;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}

	}

}
