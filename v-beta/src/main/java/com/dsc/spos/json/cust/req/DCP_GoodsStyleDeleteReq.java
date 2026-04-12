package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 款式品类删除 2018-10-12
 * @author yuanyy
 *
 */
public class DCP_GoodsStyleDeleteReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String styleNo;

		public String getStyleNo() {
			return styleNo;
		}

		public void setStyleNo(String styleNo) {
			this.styleNo = styleNo;
		}
	}

}
