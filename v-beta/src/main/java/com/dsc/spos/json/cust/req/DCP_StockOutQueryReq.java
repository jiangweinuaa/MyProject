package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：StockOutUpdate
 *   說明：出库单查询
 * 服务说明：出库单查询
 * @author panjing 
 * @since  2016-09-22
 */

public class DCP_StockOutQueryReq extends JsonBasicReq{

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String status;
		private String[] docType;
		private String keyTxt;
		//2018-11-09 yyy 添加 beginData 和 endDate 
		private String beginDate;
		private String endDate;
		private String sourceMenu;

        private String dateType;

        private String[] receiptOrg;

		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String[] getDocType() {
			return docType;
		}
		public void setDocType(String[] docType) {
			this.docType = docType;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
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
		public String getSourceMenu() {
			return sourceMenu;
		}
		public void setSourceMenu(String sourceMenu) {
			this.sourceMenu = sourceMenu;
		}

        public String[] getReceiptOrg() {
            return receiptOrg;
        }

        public void setReceiptOrg(String[] receiptOrg) {
            this.receiptOrg = receiptOrg;
        }

        public String getDateType() {
            return dateType;
        }

        public void setDateType(String dateType) {
            this.dateType = dateType;
        }
    }


}
