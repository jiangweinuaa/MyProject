package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：SupplierGet
 *    說明：供应商查询
 * 服务说明：供应商查询
 * @author xiaoli
 * @since  2017-03-02
 */
public class DCP_SupplierQueryReq extends JsonBasicReq {
	private levelElm request;
	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	
	public class levelElm{
		private String keyTxt;
		private String status;
		private String searchScope;
		private String selfBuiltShopId;
		
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getSearchScope() {
			return searchScope;
		}
		public void setSearchScope(String searchScope) {
			this.searchScope = searchScope;
		}
		public String getSelfBuiltShopId() {
			return selfBuiltShopId;
		}
		public void setSelfBuiltShopId(String selfBuiltShopId) {
			this.selfBuiltShopId = selfBuiltShopId;
		}
	}
	
	
}
