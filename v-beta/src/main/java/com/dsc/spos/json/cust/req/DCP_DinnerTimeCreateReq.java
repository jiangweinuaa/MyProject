package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 餐段新增
 * @author yuanyy 2019-09-18
 *
 */
public class DCP_DinnerTimeCreateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{

		private String dtNo;
		private String dtName;
		private String beginTime;
		private String endTime;
		private String status;
		private String workNo;

		private String priority;

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
		public String getWorkNo() {
			return workNo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void setWorkNo(String workNo) {
			this.workNo = workNo;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
	}

}
