package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：SStockInECSDCP
 * 服务说明：自采通知单结案
 * @author JINZMA 
 * @since  2019-07-03
 */
public class DCP_SStockInECSReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String ofNo;

		public String getOfNo() {
			return ofNo;
		}

		public void setOfNo(String ofNo) {
			this.ofNo = ofNo;
		}

	}




}
