package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_DinnerQueryReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String status;
		private String keyTxt;
		private String dinnerGroup;
		private List<String> shopList;

		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getDinnerGroup() {
			return dinnerGroup;
		}
		public void setDinnerGroup(String dinnerGroup) {
			this.dinnerGroup = dinnerGroup;
		}

        public List<String> getShopList() {
            return shopList;
        }

        public void setShopList(List<String> shopList) {
            this.shopList = shopList;
        }
    }

}
