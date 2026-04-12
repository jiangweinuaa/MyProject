package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 报价单
 * @author yuanyy
 *
 */
public class DCP_QuotationDeleteReq extends JsonBasicReq {
	

	private level1Elm request;
//	private String timestamp;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
//	public String getTimestamp() {
//		return timestamp;
//	}
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

	public class level1Elm
	{
		private String quotationRecordNo;
		private String shopId;

		public String getQuotationRecordNo() {
			return quotationRecordNo;
		}

		public void setQuotationRecordNo(String quotationRecordNo) {
			this.quotationRecordNo = quotationRecordNo;
		}

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		
	}
	
}
