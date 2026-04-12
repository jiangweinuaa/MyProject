package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StockTakeDelete
 *   說明：盘点单删除
 * 服务说明：盘点单删除
 * @author JZMA 
 * @since  2018-11-21
 */
public class DCP_StockTakeDeleteReq extends JsonBasicReq{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String stockTakeNo;

		public String getStockTakeNo() {
			return stockTakeNo;
		}

		public void setStockTakeNo(String stockTakeNo) {
			this.stockTakeNo = stockTakeNo;
		}
	}



}

