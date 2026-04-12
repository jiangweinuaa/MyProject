package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CustomerRegeisterQueryReq extends JsonBasicReq
{
	private level1Elm request;
	public class level1Elm
	{
		private String rFuncNo;
		private String companyId;

		public String getrFuncNo() {
			return rFuncNo;
		}

		public void setrFuncNo(String rFuncNo) {
			this.rFuncNo = rFuncNo;
		}

		public String getCompanyId() {
			return companyId;
		}

		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}
	}
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
}
