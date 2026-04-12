package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrgDeleteReq extends JsonBasicReq
{
	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String eId;
		private String organizationID;

		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getOrganizationID() {
			return organizationID;
		}
		public void setOrganizationID(String organizationID) {
			this.organizationID = organizationID;
		}
	}
}
