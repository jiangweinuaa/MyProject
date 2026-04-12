package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：LStockOutProcess
 *    說明：报损单处理
 * 服务说明：报损单处理
 * @author luoln 
 * @since  2017-03-27
 */
public class DCP_LStockOutProcessReq extends JsonBasicReq {

	private levelElm request;	

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String lStockOutNo;
		private String shopId;

        private String opType;
		
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getlStockOutNo() {
			return lStockOutNo;
		}
		public void setlStockOutNo(String lStockOutNo) {
			this.lStockOutNo = lStockOutNo;
		}


		public String getOpType() {
			return opType;
		}

		public void setOpType(String opType) {
			this.opType = opType;
		}
	}
}
