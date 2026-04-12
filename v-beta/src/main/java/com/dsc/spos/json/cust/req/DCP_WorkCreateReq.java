package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 班次新增 2018-10-10
 * @author yuanyy 
 *
 */
public class DCP_WorkCreateReq extends JsonBasicReq {

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String workNo;
		private String workName;
		private String bTime;
		private String eTime;
		private String status;

		public String getWorkNo() {
			return workNo;
		}
		public void setWorkNo(String workNo) {
			this.workNo = workNo;
		}
		public String getWorkName() {
			return workName;
		}
		public void setWorkName(String workName) {
			this.workName = workName;
		}
		public String getbTime() {
			return bTime;
		}
		public void setbTime(String bTime) {
			this.bTime = bTime;
		}
		public String geteTime() {
			return eTime;
		}
		public void seteTime(String eTime) {
			this.eTime = eTime;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}
}
