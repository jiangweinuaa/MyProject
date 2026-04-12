package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_POrderRevokeReq extends JsonBasicReq
{
	private level1Elm request;
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm
	{
		private String porderNo;

		public String getPorderNo() {
			return porderNo;
		}
		public void setPorderNo(String porderNo) {
			this.porderNo = porderNo;
		}

	}



}
