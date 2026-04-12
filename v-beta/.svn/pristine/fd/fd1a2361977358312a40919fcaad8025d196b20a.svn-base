package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：ProcessTaskGet
 *    說明：加工任务查询
 * 服务说明：加工任务查询
 * @author luoln 
 * @since  2017-03-31
 */
public class DCP_ProcessTaskQueryReq extends JsonBasicReq{

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
		//2018-11-09 yyy 新增beginDate 和 endDate 
		private String beginDate;
		private String endDate;
        private String isPStockIn;//是否完工入库 空:全部；Y:已入库；N:未入库

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

        public String getIsPStockIn()
        {
            return isPStockIn;
        }

        public void setIsPStockIn(String isPStockIn)
        {
            this.isPStockIn = isPStockIn;
        }
    }
}
