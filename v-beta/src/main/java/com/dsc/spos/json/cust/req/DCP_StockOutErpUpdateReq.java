package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StockOutErpUpdate
 *   說明：T100串出库单处理
 * 服务说明：T100串出库单处理
 * @author panjing
 * @since  2016-11-29
 */
public class DCP_StockOutErpUpdateReq extends JsonBasicReq {
	
	private String stockOutNO;
	private String opType;
    private String reject_reason;
	private List<Return_Detail> return_detail;
	
	public String getOpType() {
		return opType;
	}
	public void setOpType(String opType) {
		this.opType = opType;
	}
	public String getStockOutNO() {
		return stockOutNO;
	}
	public void setStockOutNO(String stockOutNO) {
		this.stockOutNO = stockOutNO;
	}
	public List<Return_Detail> getReturn_detail() {
		return return_detail;
	}
	public void setReturn_detail(List<Return_Detail> return_detail) {
		this.return_detail = return_detail;
	}
	public String getReject_reason() {
		return reject_reason;
	}
	public void setReject_reason(String reject_reason) {
		this.reject_reason = reject_reason;
	}
	
	public class Return_Detail {
		private int seq;
		private String item_no;
		private String packing_unit;
		private String packing_qty;
		private String base_unit;
		private String base_qty;
		
		public int getSeq() {
			return seq;
		}
		public void setSeq(int seq) {
			this.seq = seq;
		}
		public String getItem_no() {
			return item_no;
		}
		public void setItem_no(String item_no) {
			this.item_no = item_no;
		}
		public String getPacking_unit() {
			return packing_unit;
		}
		public void setPacking_unit(String packing_unit) {
			this.packing_unit = packing_unit;
		}
		public String getPacking_qty() {
			return packing_qty;
		}
		public void setPacking_qty(String packing_qty) {
			this.packing_qty = packing_qty;
		}
		public String getBase_unit() {
			return base_unit;
		}
		public void setBase_unit(String base_unit) {
			this.base_unit = base_unit;
		}
		public String getBase_qty() {
			return base_qty;
		}
		public void setBase_qty(String base_qty) {
			this.base_qty = base_qty;
		}
	}
}



