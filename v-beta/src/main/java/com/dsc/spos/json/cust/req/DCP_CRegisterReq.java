package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CRegisterReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String terminalLicence;
		private String stype;

		public String getTerminalLicence() {
			return terminalLicence;
		}

		public void setTerminalLicence(String terminalLicence) {
			this.terminalLicence = terminalLicence;
		}

		public String getStype() {
			return stype;
		}

		public void setStype(String stype) {
			this.stype = stype;
		}
	}

}
