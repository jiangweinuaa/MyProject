package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：LStockOutDelete
 *    說明：报损单删除
 * 服务说明：报损单删除
 * @author luoln 
 * @since  2017-03-27
 */
public class DCP_LStockOutDeleteReq extends JsonBasicReq {
	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String lStockOutNo;

		public String getlStockOutNo() {
			return lStockOutNo;
		}
		public void setlStockOutNo(String lStockOutNo) {
			this.lStockOutNo = lStockOutNo;
		}		
	}


}
