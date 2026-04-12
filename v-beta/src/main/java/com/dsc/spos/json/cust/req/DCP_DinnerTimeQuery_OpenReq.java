package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 餐段查询
 * @author yuanyy 2019-09-17
 *
 */
public class DCP_DinnerTimeQuery_OpenReq extends JsonBasicReq {

	private level1Elm request;

	public class level1Elm{

		private String status;
		private String keyTxt;

		private String oEId;
		private String shopId;
		private String pageSize;
		private String pageNumber;

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
		public String getoEId() {
			return oEId;
		}
		public void setoEId(String oEId) {
			this.oEId = oEId;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getPageSize() {
			return pageSize;
		}
		public String getPageNumber() {
			return pageNumber;
		}
		public void setPageSize(String pageSize) {
			this.pageSize = pageSize;
		}
		public void setPageNumber(String pageNumber) {
			this.pageNumber = pageNumber;
		}
	}

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}



}
