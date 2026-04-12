package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 删除班次信息 2018-10-10
 * @author yuanyy
 *
 */
public class DCP_WorkDeleteReq extends JsonBasicReq {
	private levelElm request;	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String[] workNo;

		public String[] getWorkNo() {
			return workNo;
		}

		public void setWorkNo(String[] workNo) {
			this.workNo = workNo;
		}
	}

}
