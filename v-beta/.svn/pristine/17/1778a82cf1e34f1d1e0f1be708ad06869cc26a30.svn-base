package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/*
 * 服务函数:SStockOutGet
 * 服务说明:自采退货查询
 * @author JZMA
 * @since  2018-11-20
 */
public class DCP_SStockOutQueryReq extends JsonBasicReq {
	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String status;
		private String keyTxt;
		private String supplier;
		private String beginDate;
		private String endDate;
        private String sStockOutType;
        private String customer;

		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getSupplier() {
			return supplier;
		}
		public void setSupplier(String supplier) {
			this.supplier = supplier;
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


		public String getCustomer() {
			return customer;
		}

		public void setCustomer(String customer) {
			this.customer = customer;
		}

        public String getsStockOutType() {
            return sStockOutType;
        }

        public void setsStockOutType(String sStockOutType) {
            this.sStockOutType = sStockOutType;
        }
    }
}
