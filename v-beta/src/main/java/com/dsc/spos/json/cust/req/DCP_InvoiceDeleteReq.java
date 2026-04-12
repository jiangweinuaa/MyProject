package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：InvoiceDeleteDCP
 * 服务说明：发票簿删除
 * @author jinzma 
 * @since  2019-03-1
 */
public class DCP_InvoiceDeleteReq  extends JsonBasicReq{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String sellerGuiNo;
		private String year;
		private String startMonth;
		private String endMonth;
		private String invStartNo;
		private String invEndNo;
		private String invType;

		public String getSellerGuiNo() {
			return sellerGuiNo;
		}
		public void setSellerGuiNo(String sellerGuiNo) {
			this.sellerGuiNo = sellerGuiNo;
		}
		public String getInvStartNo() {
			return invStartNo;
		}
		public void setInvStartNo(String invStartNo) {
			this.invStartNo = invStartNo;
		}
		public String getInvEndNo() {
			return invEndNo;
		}
		public void setInvEndNo(String invEndNo) {
			this.invEndNo = invEndNo;
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
		public String getInvType() {
			return invType;
		}
		public void setInvType(String invType) {
			this.invType = invType;
		}
	}
}
