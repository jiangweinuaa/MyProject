package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderShopQueryRes extends JsonRes {
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
	return datas;
	}

	public void setDatas(List<level1Elm> datas) {
	this.datas = datas;
	}
	
	public class level1Elm
	{
		private String shopId;
		private String shopName;
		private String telephone;
		private String address;
		private String minDeliveryPrice;
		private String agentFee;
		private String openTime;
		private String status;
		private String[] orderType;
			

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
				return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getMinDeliveryPrice() {
			return minDeliveryPrice;
		}
		public void setMinDeliveryPrice(String minDeliveryPrice) {
			this.minDeliveryPrice = minDeliveryPrice;
		}
		public String getAgentFee() {
			return agentFee;
		}
		public void setAgentFee(String agentFee) {
			this.agentFee = agentFee;
		}
		public String getOpenTime() {
			return openTime;
		}
		public void setOpenTime(String openTime) {
			this.openTime = openTime;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String[] getOrderType() {
			return orderType;
		}
		public void setOrderType(String[] orderType) {
			this.orderType = orderType;
		}
			
		
		
	}

}
