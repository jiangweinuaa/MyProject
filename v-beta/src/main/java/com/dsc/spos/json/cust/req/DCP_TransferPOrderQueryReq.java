package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：TransferPOrderGetDCP
 * 服务说明：调拨要货单转入查询
 * @author JZMA 
 * @since  2019-07-05
 */
public class DCP_TransferPOrderQueryReq extends JsonBasicReq{

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String shopId;  // 要货门店ID
		private String beginDate;
		private String endDate;
        /**
         * 是否查询库存量Y/N
         */
        private String queryStockqty;


        public String getQueryStockqty()
        {
            return queryStockqty;
        }

        public void setQueryStockqty(String queryStockqty)
        {
            this.queryStockqty = queryStockqty;
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

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		

	}



}
