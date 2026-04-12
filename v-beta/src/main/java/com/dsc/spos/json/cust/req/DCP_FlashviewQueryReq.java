package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 轮播广告查询
 * @author yuanyy
 *
 */
public class DCP_FlashviewQueryReq extends JsonBasicReq {
//	private String timeStamp;
	private level1Elm request;
//	public String getTimeStamp() {
//		return timeStamp;
//	}
//	public void setTimeStamp(String timeStamp) {
//		this.timeStamp = timeStamp;
//	}
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}


	public class level1Elm{
		private String keyTxt;
		private String shopId;
		private String status;

		public String getKeyTxt() {
			return keyTxt;
		}

		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}


	}

}
