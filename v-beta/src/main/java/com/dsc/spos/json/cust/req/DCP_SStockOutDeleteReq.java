package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：SStockOutDeleteDCP
 *    說明：自采出库单删除
 * 服务说明：自采出库单删除
 * @author JZMA 
 * @since  2018-11-20
 */
public class DCP_SStockOutDeleteReq extends JsonBasicReq {

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String sStockOutNo;

		public String getsStockOutNo() {
			return sStockOutNo;
		}
		public void setsStockOutNo(String sStockOutNo) {
			this.sStockOutNo = sStockOutNo;
		}
	}



}
