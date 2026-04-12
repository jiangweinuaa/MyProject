package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_DinnerAreaDeleteReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String dinnerGroup;
		private String priority;

		public String getDinnerGroup() {
			return dinnerGroup;
		}

		public void setDinnerGroup(String dinnerGroup) {
			this.dinnerGroup = dinnerGroup;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}
	}

}
