package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StockOutDelete
 *    說明：出货单删除
 * 服务说明：出货单删除
 * @author panjing 
 * @since  2016-09-28
 */
public class DCP_StockOutDeleteReq extends JsonBasicReq {

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String stockOutNo;

		public String getStockOutNo() {
			return stockOutNo;
		}
		public void setStockOutNo(String stockOutNo) {
			this.stockOutNo = stockOutNo;
		}

	}


}

