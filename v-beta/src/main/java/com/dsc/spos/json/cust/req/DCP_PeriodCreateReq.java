package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PeriodCreateReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String periodNo;//时段编码
		private String beginTime;//开始时间HH:MM:SS
		private String endTime;//截止时间HH:MM:SS
		private String memo;
		private String status;
		
		private String restrictShop;//适用门店：0-所有门店1-指定门店
		private List<periodName> periodName_lang;
		private List<range> rangeList;
		private List<String> timeList;
		
		public String getPeriodNo() {
			return periodNo;
		}
		public void setPeriodNo(String periodNo) {
			this.periodNo = periodNo;
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
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		public List<periodName> getPeriodName_lang() {
			return periodName_lang;
		}
		public void setPeriodName_lang(List<periodName> periodName_lang) {
			this.periodName_lang = periodName_lang;
		}
		public List<range> getRangeList() {
			return rangeList;
		}
		public void setRangeList(List<range> rangeList) {
			this.rangeList = rangeList;
		}
		public List<String> getTimeList() {
			return timeList;
		}
		public void setTimeList(List<String> timeList) {
			this.timeList = timeList;
		}
		
										
	}
	
	public class periodName 
	{
		private String langType;
		private String name;
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}		
		
	}
		
	public class range 
	{
		private String shopId;
		public String getShopId() {
			return shopId;
		}
	
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}		
	}
	
	

}
