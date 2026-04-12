package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：TransferPOrderGetDCP
 * 服务说明：调拨要货单转入查询
 * @author JZMA
 * @since  2019-07-05
 */
public class DCP_TransferPOrderQueryRes  extends JsonRes {
	
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String porderShopNo;
		private String porderShopName;
		private String porderInCostWarehouse;
		private String porderNo;
		private String bDate;
		private String rDate;
		private String rTime;
		private String isUrgentOrder;
		private String memo;
		private String totPqty;
		private String totCqty;
		private String totAmt;
		private String totDistriAmt;
		private String ptemplateNo;
		private String ptemplateName;
		private List<level2Elm> datas;
		
		public String getPtemplateNo() {
			return ptemplateNo;
		}
		public void setPtemplateNo(String ptemplateNo) {
			this.ptemplateNo = ptemplateNo;
		}
		public String getPtemplateName() {
			return ptemplateName;
		}
		public void setPtemplateName(String ptemplateName) {
			this.ptemplateName = ptemplateName;
		}
		public String getPorderShopName() {
			return porderShopName;
		}
		public void setPorderShopName(String porderShopName) {
			this.porderShopName = porderShopName;
		}
		public String getrDate() {
			return rDate;
		}
		public void setrDate(String rDate) {
			this.rDate = rDate;
		}
		public String getrTime() {
			return rTime;
		}
		public void setrTime(String rTime) {
			this.rTime = rTime;
		}
		public String getIsUrgentOrder() {
			return isUrgentOrder;
		}
		public void setIsUrgentOrder(String isUrgentOrder) {
			this.isUrgentOrder = isUrgentOrder;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
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
		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getPorderInCostWarehouse() {
			return porderInCostWarehouse;
		}
		public void setPorderInCostWarehouse(String porderInCostWarehouse) {
			this.porderInCostWarehouse = porderInCostWarehouse;
		}
		public String getTotDistriAmt() {
			return totDistriAmt;
		}
		public void setTotDistriAmt(String totDistriAmt) {
			this.totDistriAmt = totDistriAmt;
		}
		public String getPorderShopNo() {
			return porderShopNo;
		}
		public void setPorderShopNo(String porderShopNo) {
			this.porderShopNo = porderShopNo;
		}
		public String getPorderNo() {
			return porderNo;
		}
		public void setPorderNo(String porderNo) {
			this.porderNo = porderNo;
		}
		
		
	}
	public class level2Elm{
		private String item;
		private String pluNo;
		private String pluName;
		private String punit;
		private String punitName;
		private String baseUnit;
		private String baseUnitName;
		private String listImage;
		private String pqty;
		private String baseQty;
		private String price;
		private String amt;
		private String unitRatio;
		private String spec;
		private String distriPrice;
		private String isBatch;
		private String distriAmt;
		private String punitUdLength;
		private String featureNo;
		private String featureName;
		private String stockManageType;
		private String Stockqty;
		private String baseUnitUdLength;
		
		
		public String getStockManageType()
		{
			return stockManageType;
		}
		public void setStockManageType(String stockManageType)
		{
			this.stockManageType = stockManageType;
		}
		public String getStockqty()
		{
			return Stockqty;
		}
		public void setStockqty(String stockqty)
		{
			Stockqty = stockqty;
		}
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
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
		public String getPqty() {
			return pqty;
		}
		public void setPqty(String pqty) {
			this.pqty = pqty;
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
		public String getUnitRatio() {
			return unitRatio;
		}
		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
		}
		public String getSpec() {
			return spec;
		}
		public void setSpec(String spec) {
			this.spec = spec;
		}
		public String getDistriPrice() {
			return distriPrice;
		}
		public void setDistriPrice(String distriPrice) {
			this.distriPrice = distriPrice;
		}
		public String getIsBatch() {
			return isBatch;
		}
		public void setIsBatch(String isBatch) {
			this.isBatch = isBatch;
		}
		public String getDistriAmt() {
			return distriAmt;
		}
		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
		}
		public String getPunitUdLength() {
			return punitUdLength;
		}
		public void setPunitUdLength(String punitUdLength) {
			this.punitUdLength = punitUdLength;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
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
		public String getListImage() {
			return listImage;
		}
		public void setListImage(String listImage) {
			this.listImage = listImage;
		}
		public String getBaseQty() {
			return baseQty;
		}
		public void setBaseQty(String baseQty) {
			this.baseQty = baseQty;
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
		public String getBaseUnitUdLength() {
			return baseUnitUdLength;
		}
		public void setBaseUnitUdLength(String baseUnitUdLength) {
			this.baseUnitUdLength = baseUnitUdLength;
		}
	}
	
	
	
}
