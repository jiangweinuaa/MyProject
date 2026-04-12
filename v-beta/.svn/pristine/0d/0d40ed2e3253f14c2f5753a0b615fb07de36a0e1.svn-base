package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_AVGSaleAMTQueryReq  extends JsonBasicReq {
	
	//【ID1026725】【河北建投/鲜鲜坊】1001门店做要货申请使用要货模板主食类，点击千元用量报错 by jinzma 20220621
	//增加request
	private levelElm request;
	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	
	public class levelElm{
		private String beginDate;
		private String endDate;
		private String isReferenceWeather;//是否参考天气,默认为N
		
		public String getBeginDate() {
			return beginDate;
		}
		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public String getIsReferenceWeather() {
			return isReferenceWeather;
		}
		public void setIsReferenceWeather(String isReferenceWeather) {
			this.isReferenceWeather = isReferenceWeather;
		}
	}
	
	
	
	
}
