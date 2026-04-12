package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StockTakeProcessDCP
 *   說明：盘点单处理
 * 服务说明：盘点单处理
 * @author JZMA
 * @since  2018-11-21
 */
public class DCP_StockTakeProcessReq extends JsonBasicReq {
	private levelElm request;
	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	
	public class levelElm{
		private String stockTakeNo;
		private String status;
		private String ofNo;
		private String docType;
		private String taskWay;
		private String notGoodsMode;
		private String warehouse;
		private String bDate;
        private String opType;
		
		public String getStockTakeNo() {
			return stockTakeNo;
		}
		public void setStockTakeNo(String stockTakeNo) {
			this.stockTakeNo = stockTakeNo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getOfNo() {
			return ofNo;
		}
		public void setOfNo(String ofNo) {
			this.ofNo = ofNo;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getTaskWay() {
			return taskWay;
		}
		public void setTaskWay(String taskWay) {
			this.taskWay = taskWay;
		}
		public String getNotGoodsMode() {
			return notGoodsMode;
		}
		public void setNotGoodsMode(String notGoodsMode) {
			this.notGoodsMode = notGoodsMode;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}

        public String getOpType() {
            return opType;
        }

        public void setOpType(String opType) {
            this.opType = opType;
        }
    }
	
}

