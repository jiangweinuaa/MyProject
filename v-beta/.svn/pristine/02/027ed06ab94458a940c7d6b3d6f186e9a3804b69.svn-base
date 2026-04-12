package com.dsc.spos.json.cust.res;


import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_DosageOldQueryRes extends JsonBasicRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String pluNo;
		private String pluName;
		private String pUnit;
		private String pUnitName;
		private String price;
		private String isClear;
		private String lastSaleTime;
		private String kQty;
		private String preSaleQty;
		private String kAmt;
		
		private String avgQty;//平均销量  （期间内商品总销量 / 实际售卖天数）
		private String avgPrice;//均价   （期间内该商品实际销售总额  / 该商品总销量）
		private String avgAmt; // 平均销售额  = 平均销量 * 均价 
		
		private List<level2Elm> datas;
		
		
		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}

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
		
		public String getpUnit() {
			return pUnit;
		}
		public void setpUnit(String pUnit) {
			this.pUnit = pUnit;
		}
		public String getpUnitName() {
			return pUnitName;
		}
		public void setpUnitName(String pUnitName) {
			this.pUnitName = pUnitName;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getIsClear() {
			return isClear;
		}
		public void setIsClear(String isClear) {
			this.isClear = isClear;
		}
		public String getLastSaleTime() {
			return lastSaleTime;
		}
		public void setLastSaleTime(String lastSaleTime) {
			this.lastSaleTime = lastSaleTime;
		}
		public String getkQty() {
			return kQty;
		}
		public void setkQty(String kQty) {
			this.kQty = kQty;
		}
		
		public String getPreSaleQty() {
			return preSaleQty;
		}
		public void setPreSaleQty(String preSaleQty) {
			this.preSaleQty = preSaleQty;
		}
		public String getkAmt() {
			return kAmt;
		}
		public void setkAmt(String kAmt) {
			this.kAmt = kAmt;
		}
		public String getAvgQty() {
			return avgQty;
		}
		public String getAvgPrice() {
			return avgPrice;
		}
		public void setAvgQty(String avgQty) {
			this.avgQty = avgQty;
		}
		public void setAvgPrice(String avgPrice) {
			this.avgPrice = avgPrice;
		}
		public String getAvgAmt() {
			return avgAmt;
		}
		public void setAvgAmt(String avgAmt) {
			this.avgAmt = avgAmt;
		}
		
		
	}
	
	public class level2Elm{
		private String dtNo;
		private String dtName;
		private String beginTime;
		private String endTime;
		private String qty;
		public String getDtNo() {
			return dtNo;
		}
		public void setDtNo(String dtNo) {
			this.dtNo = dtNo;
		}
		public String getDtName() {
			return dtName;
		}
		public void setDtName(String dtName) {
			this.dtName = dtName;
		}
		public String getBeginTime() {
			return beginTime;
		}
		public void setBeginTime(String beginTime) {
			this.beginTime = beginTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		
	}
	
	
	
}
