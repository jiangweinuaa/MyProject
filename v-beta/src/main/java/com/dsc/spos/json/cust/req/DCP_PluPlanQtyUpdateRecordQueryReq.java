package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 商品计划量修改记录查询
 * @author yuanyy 
 *	
 */
public class DCP_PluPlanQtyUpdateRecordQueryReq extends JsonBasicReq {
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
		private String pluNo;
		private String guQingNo;
		private String beginDate;
		private String endDate;
		private String dtNo; // all(默认，查全部)
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getGuQingNo() {
			return guQingNo;
		}
		public void setGuQingNo(String guQingNo) {
			this.guQingNo = guQingNo;
		}
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
		public String getDtNo() {
			return dtNo;
		}
		public void setDtNo(String dtNo) {
			this.dtNo = dtNo;
		}
		
	}
	
	
	
	
	
}
