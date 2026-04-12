package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DCP_ECStockGet
 * 服务说明：电商平台库存上下架查询
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockQueryReq extends JsonBasicReq {

	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm
	{
		private String beginDate;
		private String endDate;
		private String keyTxt;
		private String status;
		private String docType;
		
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
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
	}
}
