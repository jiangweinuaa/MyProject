package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_PinPeiCreate
 * 服务说明：拼胚修改
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiUpdateReq extends JsonBasicReq{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String pinPeiNo;
		private String bDate;
		private String stockInWearehouse;
		private String stockOutWearehouse;
		private String stockInBsNo;
		private String stockOutBsNo;
		private String memo;
		private String totPqty;
		private String totAmt;
		private String totDistriAmt;
		private String totCqty;
		private List<level1Elm> stockInPluList;
		private List<level1Elm> stockOutPluList;
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getStockInWearehouse() {
			return stockInWearehouse;
		}
		public void setStockInWearehouse(String stockInWearehouse) {
			this.stockInWearehouse = stockInWearehouse;
		}
		public String getStockOutWearehouse() {
			return stockOutWearehouse;
		}
		public void setStockOutWearehouse(String stockOutWearehouse) {
			this.stockOutWearehouse = stockOutWearehouse;
		}
		public String getStockInBsNo() {
			return stockInBsNo;
		}
		public void setStockInBsNo(String stockInBsNo) {
			this.stockInBsNo = stockInBsNo;
		}
		public String getStockOutBsNo() {
			return stockOutBsNo;
		}
		public void setStockOutBsNo(String stockOutBsNo) {
			this.stockOutBsNo = stockOutBsNo;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public List<level1Elm> getStockInPluList() {
			return stockInPluList;
		}
		public void setStockInPluList(List<level1Elm> stockInPluList) {
			this.stockInPluList = stockInPluList;
		}
		public List<level1Elm> getStockOutPluList() {
			return stockOutPluList;
		}
		public void setStockOutPluList(List<level1Elm> stockOutPluList) {
			this.stockOutPluList = stockOutPluList;
		}
		public String getPinPeiNo() {
			return pinPeiNo;
		}
		public void setPinPeiNo(String pinPeiNo) {
			this.pinPeiNo = pinPeiNo;
		}
		public String getTotPqty() {
			return totPqty;
		}
		public void setTotPqty(String totPqty) {
			this.totPqty = totPqty;
		}
		public String getTotAmt() {
			return totAmt;
		}
		public void setTotAmt(String totAmt) {
			this.totAmt = totAmt;
		}
		public String getTotDistriAmt() {
			return totDistriAmt;
		}
		public void setTotDistriAmt(String totDistriAmt) {
			this.totDistriAmt = totDistriAmt;
		}
		public String getTotCqty() {
			return totCqty;
		}
		public void setTotCqty(String totCqty) {
			this.totCqty = totCqty;
		}
	}
	public class level1Elm{
		private String pluNo;
		private String featureNo;
		private String punit;
		private String baseUnit;
		private String unitRatio;
		private String pqty;
		private String baseQty;
		private String batchNo;
		private String prodDate;
		private String price;
		private String distriPrice;
		private String amt;
		private String distriAmt;
		private String pluMemo;
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPunit() {
			return punit;
		}
		public void setPunit(String punit) {
			this.punit = punit;
		}
		public String getUnitRatio() {
			return unitRatio;
		}
		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
		}
		public String getPqty() {
			return pqty;
		}
		public void setPqty(String pqty) {
			this.pqty = pqty;
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
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getDistriPrice() {
			return distriPrice;
		}
		public void setDistriPrice(String distriPrice) {
			this.distriPrice = distriPrice;
		}
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
		public String getDistriAmt() {
			return distriAmt;
		}
		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
		}
		public String getPluMemo() {
			return pluMemo;
		}
		public void setPluMemo(String pluMemo) {
			this.pluMemo = pluMemo;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
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
	}

}
