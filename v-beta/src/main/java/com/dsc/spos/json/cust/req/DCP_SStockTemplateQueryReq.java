package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：PTemplateGet
 *    說明：要货模板查询
 * 服务说明：要货模板查询
 * @author jinzma
 * @since  2017-03-09
 */
public class DCP_SStockTemplateQueryReq extends JsonBasicReq {
	private levelRequest request;
	
	public levelRequest getRequest()
	{
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}
	
	public class levelRequest {
		private String keyTxt;
		private String status;
		private String beginDate;
		private String endDate;
		
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
	}
	
	
	
}
