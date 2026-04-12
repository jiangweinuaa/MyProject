package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：TransferCreateReq
 *   說明：调拨通知单新增
 * 服务说明：调拨通知单新增
 * @author luoln
 * @since  2017-12-15
 */
public class DCP_TransferCreateReq extends JsonBasicReq{
	
	private String transferShop;
	private String loadDocType;
	private String loadDocNo;
	private String memo;
	private List<level1Elm> datas;
	
	//【ID1030137】【3.0】中台和erp之间增加调拨通知单服务 by jinzma 20221207
	//private String createBy;
	//private String warehouse;
	//private String transferWarehouse;
	//private String totPQty;
	//private String totAmt;
	//private String totCQty;
	
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getLoadDocType() {
		return loadDocType;
	}
	public void setLoadDocType(String loadDocType) {
		this.loadDocType = loadDocType;
	}
	public String getTransferShop() {
		return transferShop;
	}
	public void setTransferShop(String transferShop) {
		this.transferShop = transferShop;
	}
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	public String getLoadDocNo() {
		return loadDocNo;
	}
	public void setLoadDocNo(String loadDocNo) {
		this.loadDocNo = loadDocNo;
	}
	
	public  class level1Elm {
		private String item;
		private String pluNo;
		private String featureNo;
		private String batchNo;
		private String prodDate;
		private String punit;
		private String pqty;
		private String baseUnit;
		private String baseQty;
		private String unitRatio;
		private String price;
		private String amt;
		private String distriPrice;
		private String distriAmt;
		
		//【ID1030137】【3.0】中台和erp之间增加调拨通知单服务 by jinzma 20221207
		//private String oItem;
		//private String warehouse;
		//private String pluBarcode;
		//private String wunit;
		//private String wqty;
		
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getPunit() {
			return punit;
		}
		public void setPunit(String punit) {
			this.punit = punit;
		}
		public String getPqty() {
			return pqty;
		}
		public void setPqty(String pqty) {
			this.pqty = pqty;
		}
		public String getUnitRatio() {
			return unitRatio;
		}
		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public String getBatchNo() {
			return batchNo;
		}
		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}
		public String getProdDate() {
			return prodDate;
		}
		public void setProdDate(String prodDate) {
			this.prodDate = prodDate;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		public String getBaseQty() {
			return baseQty;
		}
		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
		}
		public String getDistriPrice() {
			return distriPrice;
		}
		public void setDistriPrice(String distriPrice) {
			this.distriPrice = distriPrice;
		}
		public String getDistriAmt() {
			return distriAmt;
		}
		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
		}
	}
}
