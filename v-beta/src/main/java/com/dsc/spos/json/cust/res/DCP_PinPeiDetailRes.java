package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_PinPeiDetail
 * 服务说明：拼胚详情
 * @author jinzma
 * @since  2020-07-13
 */
public class DCP_PinPeiDetailRes extends JsonRes{
	private level1Elm datas;
	public level1Elm getDatas() {
		return datas;
	}
	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}
	public class level1Elm{
		private String createOpId;
		private String createOpName;
		private String createTime;
		private String lastModiOpId;
		private String lastModiOpName;
		private String lastModiTime;
		private String memo;
		private String pinPeiNo;
		private String bDate;
		private String stockInNo;
		private String stockOutNo;
		private String status;
		private String totPqty;
		private String totCqty;
		private String totAmt;
		private String totDistriAmt;
		private String stockInWearehouse;
		private String stockInWearehouseName;
		private String stockOutWearehouse;
		private String stockOutWearehouseName;
		private String stockInBsNo;
		private String stockInBsName;
		private String stockOutBsNo;
		private String stockOutBsName;
		private List<level2Elm> stockInPluList;
		private List<level2Elm> stockOutPluList;
		
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getPinPeiNo() {
			return pinPeiNo;
		}
		public void setPinPeiNo(String pinPeiNo) {
			this.pinPeiNo = pinPeiNo;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getStockInNo() {
			return stockInNo;
		}
		public void setStockInNo(String stockInNo) {
			this.stockInNo = stockInNo;
		}
		public String getStockOutNo() {
			return stockOutNo;
		}
		public void setStockOutNo(String stockOutNo) {
			this.stockOutNo = stockOutNo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getTotPqty() {
			return totPqty;
		}
		public void setTotPqty(String totPqty) {
			this.totPqty = totPqty;
		}
		public String getTotCqty() {
			return totCqty;
		}
		public void setTotCqty(String totCqty) {
			this.totCqty = totCqty;
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
		public String getStockInWearehouse() {
			return stockInWearehouse;
		}
		public void setStockInWearehouse(String stockInWearehouse) {
			this.stockInWearehouse = stockInWearehouse;
		}
		public String getStockInWearehouseName() {
			return stockInWearehouseName;
		}
		public void setStockInWearehouseName(String stockInWearehouseName) {
			this.stockInWearehouseName = stockInWearehouseName;
		}
		public String getStockOutWearehouse() {
			return stockOutWearehouse;
		}
		public void setStockOutWearehouse(String stockOutWearehouse) {
			this.stockOutWearehouse = stockOutWearehouse;
		}
		public String getStockOutWearehouseName() {
			return stockOutWearehouseName;
		}
		public void setStockOutWearehouseName(String stockOutWearehouseName) {
			this.stockOutWearehouseName = stockOutWearehouseName;
		}
		public String getStockInBsNo() {
			return stockInBsNo;
		}
		public void setStockInBsNo(String stockInBsNo) {
			this.stockInBsNo = stockInBsNo;
		}
		public String getStockInBsName() {
			return stockInBsName;
		}
		public void setStockInBsName(String stockInBsName) {
			this.stockInBsName = stockInBsName;
		}
		public String getStockOutBsNo() {
			return stockOutBsNo;
		}
		public void setStockOutBsNo(String stockOutBsNo) {
			this.stockOutBsNo = stockOutBsNo;
		}
		public String getStockOutBsName() {
			return stockOutBsName;
		}
		public void setStockOutBsName(String stockOutBsName) {
			this.stockOutBsName = stockOutBsName;
		}
		public List<level2Elm> getStockInPluList() {
			return stockInPluList;
		}
		public void setStockInPluList(List<level2Elm> stockInPluList) {
			this.stockInPluList = stockInPluList;
		}
		public List<level2Elm> getStockOutPluList() {
			return stockOutPluList;
		}
		public void setStockOutPluList(List<level2Elm> stockOutPluList) {
			this.stockOutPluList = stockOutPluList;
		}
		public String getCreateOpId() {
			return createOpId;
		}
		public void setCreateOpId(String createOpId) {
			this.createOpId = createOpId;
		}
		public String getCreateOpName() {
			return createOpName;
		}
		public void setCreateOpName(String createOpName) {
			this.createOpName = createOpName;
		}
		public String getLastModiOpId() {
			return lastModiOpId;
		}
		public void setLastModiOpId(String lastModiOpId) {
			this.lastModiOpId = lastModiOpId;
		}
		public String getLastModiOpName() {
			return lastModiOpName;
		}
		public void setLastModiOpName(String lastModiOpName) {
			this.lastModiOpName = lastModiOpName;
		}
		public String getLastModiTime() {
			return lastModiTime;
		}
		public void setLastModiTime(String lastModiTime) {
			this.lastModiTime = lastModiTime;
		}
	}
	public class level2Elm{
		private String pluNo;
		private String pluName;
		private String featureNo;
		private String featureName;
		private String listImage;
		private String spec;
		private String punit;
		private String punitName;
		private String baseUnit;
		private String baseUnitName;
		private String unitRatio;
		private String pqty;
		private String baseQty;
		private String batchNo;
		private String prodDate;
		private String price;
		private String distriPrice;
		private String amt;
		private String distriAmt;
		private String punitUdLength;
		private String pluMemo;
		private String baseUnitUdLength;
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getSpec() {
			return spec;
		}
		public void setSpec(String spec) {
			this.spec = spec;
		}
		public String getPunit() {
			return punit;
		}
		public void setPunit(String punit) {
			this.punit = punit;
		}
		public String getPunitName() {
			return punitName;
		}
		public void setPunitName(String punitName) {
			this.punitName = punitName;
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
		public String getFeatureName() {
			return featureName;
		}
		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}
		public String getListImage() {
			return listImage;
		}
		public void setListImage(String listImage) {
			this.listImage = listImage;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		public String getBaseUnitName() {
			return baseUnitName;
		}
		public void setBaseUnitName(String baseUnitName) {
			this.baseUnitName = baseUnitName;
		}
		public String getBaseQty() {
			return baseQty;
		}
		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
		}
		public String getPunitUdLength() {
			return punitUdLength;
		}
		public void setPunitUdLength(String punitUdLength) {
			this.punitUdLength = punitUdLength;
		}
		public String getBaseUnitUdLength() {
			return baseUnitUdLength;
		}
		public void setBaseUnitUdLength(String baseUnitUdLength) {
			this.baseUnitUdLength = baseUnitUdLength;
		}
	}
}
