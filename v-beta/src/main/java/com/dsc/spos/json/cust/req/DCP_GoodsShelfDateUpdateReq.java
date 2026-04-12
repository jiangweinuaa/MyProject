package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * V3-商品渠道自动上下架设置
 * http://183.233.190.204:10004/project/144/interface/api/3232
 */
public class DCP_GoodsShelfDateUpdateReq extends JsonBasicReq {
	
	private levReq request ;

	public levReq getRequest() {
		return request;
	}

	public void setRequest(levReq request) {
		this.request = request;
	}
	
	public class levReq{
		
		private String channelId;

        private String onOffType; // 自动上下架模式 0-按渠道 1-按门店 不传为按渠道
        private String shopId; // 门店号，onOffType为1时，必传

		private String onShelfAuto;
		
		private String onShelfDate;
		private String onShelfTime;
		
		private String offShelfAuto;
		private String offShelfDate;
		
		private String offShelfTime;
		private List<PluList> pluList;

        public String getOnOffType() {
            return onOffType;
        }

        public void setOnOffType(String onOffType) {
            this.onOffType = onOffType;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getChannelId() {
			return channelId;
		}
		public String getOnShelfAuto() {
			return onShelfAuto;
		}
		public String getOnShelfDate() {
			return onShelfDate;
		}
		public String getOnShelfTime() {
			return onShelfTime;
		}
		public String getOffShelfAuto() {
			return offShelfAuto;
		}
		public String getOffShelfDate() {
			return offShelfDate;
		}
		public String getOffShelfTime() {
			return offShelfTime;
		}
		public List<PluList> getPluList() {
			return pluList;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setOnShelfAuto(String onShelfAuto) {
			this.onShelfAuto = onShelfAuto;
		}
		public void setOnShelfDate(String onShelfDate) {
			this.onShelfDate = onShelfDate;
		}
		public void setOnShelfTime(String onShelfTime) {
			this.onShelfTime = onShelfTime;
		}
		public void setOffShelfAuto(String offShelfAuto) {
			this.offShelfAuto = offShelfAuto;
		}
		public void setOffShelfDate(String offShelfDate) {
			this.offShelfDate = offShelfDate;
		}
		public void setOffShelfTime(String offShelfTime) {
			this.offShelfTime = offShelfTime;
		}
		public void setPluList(List<PluList> pluList) {
			this.pluList = pluList;
		}
		
		
	}
	
	
	public class PluList{
		private String pluNo;
		private String pluType;

		public String getPluType() {
			return pluType;
		}

		public void setPluType(String pluType) {
			this.pluType = pluType;
		}

		public String getPluNo() {
			return pluNo;
		}

		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		
	}
	
	
}
