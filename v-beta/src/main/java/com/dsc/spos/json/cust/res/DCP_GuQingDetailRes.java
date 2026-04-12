package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_GuQingDetailRes.level1Elm;

public class DCP_GuQingDetailRes extends JsonRes {
	
//	public static level1Elm level1Elm;
	private level1Elm datas;
	
	public level1Elm getDatas() {
		return datas;
	}

	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String guQingNo;
		private String rDate;
		private List<level2Elm> pluList;
		public String getGuQingNo() {
			return guQingNo;
		}
		public void setGuQingNo(String guQingNo) {
			this.guQingNo = guQingNo;
		}
		public String getrDate() {
			return rDate;
		}
		public void setrDate(String rDate) {
			this.rDate = rDate;
		}
		public List<level2Elm> getPluList() {
			return pluList;
		}
		public void setPluList(List<level2Elm> pluList) {
			this.pluList = pluList;
		}
		
	}
	
	public class level2Elm{
		private String item;
		private String pluNo;
		private String pluName;
		private String fileName;
		private String pUnit;
		private String pUnitName;
		private String udLength;
		private String guQingType;
		private String price;
		
		private String pfNo;
		private String pfOrderType;
		
		private List<level3Elm> mealData;
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
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
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
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
		public String getUdLength() {
			return udLength;
		}
		public void setUdLength(String udLength) {
			this.udLength = udLength;
		}
		public String getGuQingType() {
			return guQingType;
		}
		public void setGuQingType(String guQingType) {
			this.guQingType = guQingType;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getPfNo() {
			return pfNo;
		}
		public void setPfNo(String pfNo) {
			this.pfNo = pfNo;
		}
		public String getPfOrderType() {
			return pfOrderType;
		}
		public void setPfOrderType(String pfOrderType) {
			this.pfOrderType = pfOrderType;
		}
		public List<level3Elm> getMealData() {
			return mealData;
		}
		public void setMealData(List<level3Elm> mealData) {
			this.mealData = mealData;
		}
		
	}
	
	public class level3Elm{
		private String dtNo;
		private String dtName;
		private String beginTime;
		private String endTime;
		private String kQty;
		private String qty;
		private String saleQty;
		private String restQty;
		private String isClear;

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

		public String getkQty() {
			return kQty;
		}

		public void setkQty(String kQty) {
			this.kQty = kQty;
		}

		public String getQty() {
			return qty;
		}

		public void setQty(String qty) {
			this.qty = qty;
		}

		public String getSaleQty() {
			return saleQty;
		}

		public void setSaleQty(String saleQty) {
			this.saleQty = saleQty;
		}

		public String getRestQty() {
			return restQty;
		}

		public void setRestQty(String restQty) {
			this.restQty = restQty;
		}

		public String getIsClear() {
			return isClear;
		}

		public void setIsClear(String isClear) {
			this.isClear = isClear;
		}
		
	}
	
	
}
