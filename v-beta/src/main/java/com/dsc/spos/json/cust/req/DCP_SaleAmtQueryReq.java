package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 查询某一天，或期间内的销售信息
 * @author yuanyy
 * 
 */
public class DCP_SaleAmtQueryReq extends JsonBasicReq {
	
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
		private String bDate;
		private String rDate;
		private String dateReferType;
		private List<level2Elm> datas;
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
		
		public String getDateReferType() {
			return dateReferType;
		}
		public void setDateReferType(String dateReferType) {
			this.dateReferType = dateReferType;
		}
		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
	}
	
	public class level2Elm{
		private String beginDate;
		private String endDate;
		private String item;
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
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		
	}
	
	
	
}
