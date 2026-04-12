package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 营业预估：解冻班次查询 2019-10-10
 * @author yuanyy
 * 
 */
public class DCP_PlanWorkQueryReq extends JsonBasicReq{

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
		private String status;
		private String keyTxt ;

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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
	}
}
