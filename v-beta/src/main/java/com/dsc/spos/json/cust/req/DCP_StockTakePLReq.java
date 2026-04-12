package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：StockTakePL
 *    說明：门店库存查询
 * 服务说明：门店库存查询
 * @author panjing 
 * @since  2016-11-18
 */
public class DCP_StockTakePLReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String ofNo;
		private String bDate;
		private String docType;
		private String plType;
		private String stockTakeNo;
		private String taskWay;
		private String notGoodsMode;
		private String warehouse;

		public String getOfNo() {
			return ofNo;
		}
		public void setOfNo(String ofNo) {
			this.ofNo = ofNo;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getPlType() {
			return plType;
		}
		public void setPlType(String plType) {
			this.plType = plType;
		}
		public String getStockTakeNo() {
			return stockTakeNo;
		}
		public void setStockTakeNo(String stockTakeNo) {
			this.stockTakeNo = stockTakeNo;
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
	}

}

