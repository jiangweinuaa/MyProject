package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StockInGet
 *   說明：收货单查询
 * 服务说明：收货单查询
 * @author panjing 
 * @since  2016-09-22
 */
public class DCP_StockInQueryReq extends JsonBasicReq{

	private levelElm request;
	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String status;
		private String docType;
		private String keyTxt;
		private String dateType;

		//2018-11-09 yyy 新增beginDate 和 endDate 
		private String beginDate;
		private String endDate;

		public String getBeginDate() {
			return beginDate;
		}
		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getDateType() {
			return dateType;
		}
		public void setDateType(String dateType) {
			this.dateType = dateType;
		}
	}
}