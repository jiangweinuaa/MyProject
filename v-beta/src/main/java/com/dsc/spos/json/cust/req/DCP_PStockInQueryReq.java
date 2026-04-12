package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：PStockInGet 說明：完工入库查询 服务说明：完工入库查询
 * 
 * @author luoln
 * @since 2017-04-01
 */
public class DCP_PStockInQueryReq extends JsonBasicReq {

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
		// 2018-08-07 添加docType
		private String docType;
		// 2018-11-09 yyy 新增beginDate 和 endDate
		private String beginDate;
		private String endDate;
        private String departId;

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
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
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

        public String getDepartId() {
            return departId;
        }

        public void setDepartId(String departId) {
            this.departId = departId;
        }
    }


}
