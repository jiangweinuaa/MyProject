package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 沽清计划数调整
 * @author yuanyy 2020-03-01
 * 
 */
public class DCP_PlanQtyUpdateReq extends JsonBasicReq {
	private level1Elm request;
//	private String timestamp;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
//	public String getTimestamp() {
//		return timestamp;
//	}
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

	public class level1Elm
	{
		private String oEId;
		private String oShopId;
		private String guQingNo;
		private String pluNo;
		private String pUnit;
		private String guQingType;
		private String price;
		private String pfNo;
		private String pfOrderType;
		private String modifyBy;
		private String modifyByName;

		private List<level2Elm> mealData;


		public String getoEId() {
			return oEId;
		}
		public void setoEId(String oEId) {
			this.oEId = oEId;
		}
		public String getoShopId() {
			return oShopId;
		}
		public void setoShopId(String oShopId) {
			this.oShopId = oShopId;
		}
		public String getGuQingNo() {
			return guQingNo;
		}
		public void setGuQingNo(String guQingNo) {
			this.guQingNo = guQingNo;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
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
		public String getModifyBy() {
			return modifyBy;
		}
		public void setModifyBy(String modifyBy) {
			this.modifyBy = modifyBy;
		}
		public String getModifyByName() {
			return modifyByName;
		}
		public void setModifyByName(String modifyByName) {
			this.modifyByName = modifyByName;
		}
		public List<level2Elm> getMealData() {
			return mealData;
		}
		public void setMealData(List<level2Elm> mealData) {
			this.mealData = mealData;
		}

	}

	public class level2Elm{

		private String dtNo;
		private String dtName;
		private String qty;
		private String saleQty;
		private String restQty;
		private String updateType; // initial：初始化,guQing：沽清,backEnd：后台,pos：POS端
		private String beginTime;
		private String endTime;

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
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getSaleQty() {
			return saleQty;
		}
		public String getRestQty() {
			return restQty;
		}
		public void setSaleQty(String saleQty) {
			this.saleQty = saleQty;
		}
		public void setRestQty(String restQty) {
			this.restQty = restQty;
		}
		public String getUpdateType() {
			return updateType;
		}
		public void setUpdateType(String updateType) {
			this.updateType = updateType;
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

	}



}
