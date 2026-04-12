package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：PStockInProcess
 *    說明：完工入库单执行
 * 服务说明：完工入库单执行
 * @author luoln 
 * @since  2017-03-31
 */
public class DCP_PStockInRefundProcessReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String pStockInNo;
		 //完工入库红冲 用到 
		private String pStockInNo_origin;//原完工入库单号

        private String accountDate;

		public String getpStockInNo() {
			return pStockInNo;
		}
		public void setpStockInNo(String pStockInNo) {
			this.pStockInNo = pStockInNo;
		}
		public String getpStockInNo_origin() {
			return pStockInNo_origin;
		}
		public void setpStockInNo_origin(String pStockInNo_origin) {
			this.pStockInNo_origin = pStockInNo_origin;
		}

        public String getAccountDate() {
            return accountDate;
        }

        public void setAccountDate(String accountDate) {
            this.accountDate = accountDate;
        }
    }

}
