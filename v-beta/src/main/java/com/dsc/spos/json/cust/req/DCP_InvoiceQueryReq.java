package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：InvoiceGetDCP
 * 服务说明： 发票簿查询
 * @author jinzma
 * @since  2019-03-1
 */
public class DCP_InvoiceQueryReq extends JsonBasicReq  {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String year;
		private String startMonth;
		private String endMonth;	
		private String sellerGuiNo;
		private String isUsed;
		private String invLoad;
		private String oShopId;

		public String getSellerGuiNo() {
			return sellerGuiNo;
		}
		public void setSellerGuiNo(String sellerGuiNo) {
			this.sellerGuiNo = sellerGuiNo;
		}
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		public String getStartMonth() {
			return startMonth;
		}
		public void setStartMonth(String startMonth) {
			this.startMonth = startMonth;
		}
		public String getEndMonth() {
			return endMonth;
		}
		public void setEndMonth(String endMonth) {
			this.endMonth = endMonth;
		}
		public String getIsUsed() {
			return isUsed;
		}
		public void setIsUsed(String isUsed) {
			this.isUsed = isUsed;
		}
		public String getInvLoad() {
			return invLoad;
		}
		public void setInvLoad(String invLoad) {
			this.invLoad = invLoad;
		}
		public String getoShopId() {
			return oShopId;
		}
		public void setoShopId(String oShopId) {
			this.oShopId = oShopId;
		}
	}

}
