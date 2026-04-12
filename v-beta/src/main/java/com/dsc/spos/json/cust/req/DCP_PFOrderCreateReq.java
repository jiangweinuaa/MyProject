package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 计划报单新增 （主要目的是手动报单 和  修改数量）
 * @author yuanyy 
 * 
 */
public class DCP_PFOrderCreateReq extends JsonBasicReq{
//	private String timeStamp;
	private level1Elm request;
//	public String getTimeStamp() {
//		return timeStamp;
//	}
//	public void setTimeStamp(String timeStamp) {
//		this.timeStamp = timeStamp;
//	}
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
	public class level1Elm{
		
		private String pfId;
		private String bDate;
		private String rDate;
		private String pfOrderType;
		private String dateReferType;
		private String memo;
		private String totCQty;
		private String totPQty;
		private String totAmt;
			
		private List<level2Elm> pluList;

		public String getPfId() {
			return pfId;
		}

		public void setPfId(String pfId) {
			this.pfId = pfId;
		}

		public String getbDate() {
			return bDate;
		}

		public void setbDate(String bDate) {
			this.bDate = bDate;
		}

		public String getrDate() {
			return rDate;
		}

		public void setrDate(String rDate) {
			this.rDate = rDate;
		}

		public String getPfOrderType() {
			return pfOrderType;
		}

		public void setPfOrderType(String pfOrderType) {
			this.pfOrderType = pfOrderType;
		}

		public String getDateReferType() {
			return dateReferType;
		}

		public void setDateReferType(String dateReferType) {
			this.dateReferType = dateReferType;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getTotCQty() {
			return totCQty;
		}

		public void setTotCQty(String totCQty) {
			this.totCQty = totCQty;
		}

		public String getTotPQty() {
			return totPQty;
		}

		public void setTotPQty(String totPQty) {
			this.totPQty = totPQty;
		}

		public String getTotAmt() {
			return totAmt;
		}

		public void setTotAmt(String totAmt) {
			this.totAmt = totAmt;
		}

		public List<level2Elm> getPluList() {
			return pluList;
		}

		public void setPluList(List<level2Elm> pluList) {
			this.pluList = pluList;
		}
	}
	
	public class level2Elm{
		
		private String featureNo;//特征码
		
		private String item;
		private String pluNo;
		private String pluName;
		private String pUnit; //要货单位
		private String guQingType;
		private String kQty;
		private String kAmt;
		private String price;
		private String preSaleQty;
		private String qty;
		private String kAdjAmt;
		
		private String sUnit;//销售单位（计划报单页面显示的单位）
		private String punitUdLength; //要货单位长度
		
		private List<level3Elm> mealData;
		
		
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
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
		public String getpUnit() {
			return pUnit;
		}
		public void setpUnit(String pUnit) {
			this.pUnit = pUnit;
		}
		public String getGuQingType() {
			return guQingType;
		}
		public void setGuQingType(String guQingType) {
			this.guQingType = guQingType;
		}
		public String getkQty() {
			return kQty;
		}
		public void setkQty(String kQty) {
			this.kQty = kQty;
		}
		public String getkAmt() {
			return kAmt;
		}
		public void setkAmt(String kAmt) {
			this.kAmt = kAmt;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getPreSaleQty() {
			return preSaleQty;
		}
		public void setPreSaleQty(String preSaleQty) {
			this.preSaleQty = preSaleQty;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getsUnit() {
			return sUnit;
		}
		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}
		public String getPunitUdLength() {
			return punitUdLength;
		}
		public void setPunitUdLength(String punitUdLength) {
			this.punitUdLength = punitUdLength;
		}
		public String getkAdjAmt() {
			return kAdjAmt;
		}
		public void setkAdjAmt(String kAdjAmt) {
			this.kAdjAmt = kAdjAmt;
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
		private String lastSaleTime;
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
		public String getkQty() {
			return kQty;
		}
		public void setkQty(String kQty) {
			this.kQty = kQty;
		}
		public String getLastSaleTime() {
			return lastSaleTime;
		}
		public void setLastSaleTime(String lastSaleTime) {
			this.lastSaleTime = lastSaleTime;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		
	}
	
}
