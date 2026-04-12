package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 销售业绩查询
 * @author yuanyy
 *
 */
public class DCP_SalesPerformanceQueryReq extends JsonBasicReq {
	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String opNO;
		private String shopId;
		private String beginDate;
		private String endDate;
		private String[] saleType;
		public String getOpNO() {
			return opNO;
		}
		public void setOpNO(String opNO) {
			this.opNO = opNO;
		}

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
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
		public String[] getSaleType() {
			return saleType;
		}
		public void setSaleType(String[] saleType) {
			this.saleType = saleType;
		}


	}


}
