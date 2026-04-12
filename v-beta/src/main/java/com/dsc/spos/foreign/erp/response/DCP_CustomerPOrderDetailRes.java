package com.dsc.spos.foreign.erp.response;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;


public class DCP_CustomerPOrderDetailRes extends JsonBasicRes {

	private level1Elm datas;
	
	
	public level1Elm getDatas() {
		return datas;
	}


	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		List<level2Elm> dataList;

		public List<level2Elm> getDataList() {
			return dataList;
		}

		public void setDataList(List<level2Elm> dataList) {
			this.dataList = dataList;
		}
		
	}
	public class level2Elm
	{
		private String pluNo;
		private String pluName;
		private String featureNo;
		private String qty;
		private String price;
		private String oPrice;
		private String discRate;
		private String amt;
		private String unit;
		private String unitName;
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
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getoPrice() {
			return oPrice;
		}
		public void setoPrice(String oPrice) {
			this.oPrice = oPrice;
		}
		public String getDiscRate() {
			return discRate;
		}
		public void setDiscRate(String discRate) {
			this.discRate = discRate;
		}
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		
	}
	

	
	
	
}
