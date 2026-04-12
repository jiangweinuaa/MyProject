package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_BatchPStockInQuery
 * 說明：分批出数单身查询
 * @author JZMA 
 * @since  2020-07-07
 */
public class DCP_BatchPStockInDetailReq extends JsonBasicReq{
	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String pTemplateNo;
		private String ofNo;
		
		public String getpTemplateNo() {
			return pTemplateNo;
		}
		public void setpTemplateNo(String pTemplateNo) {
			this.pTemplateNo = pTemplateNo;
		}
		public String getOfNo() {
			return ofNo;
		}
		public void setOfNo(String ofNo) {
			this.ofNo = ofNo;
		}
	}

}
