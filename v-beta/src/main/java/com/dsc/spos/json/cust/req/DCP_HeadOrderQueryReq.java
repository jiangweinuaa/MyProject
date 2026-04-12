package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_HeadOrderQueryReq extends JsonBasicReq
{
	private levelRequest request;

	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}
	
	public class levelRequest
	{
		private String headOrderNo;

		public String getHeadOrderNo() {
			return headOrderNo;
		}

		public void setHeadOrderNo(String headOrderNo) {
			this.headOrderNo = headOrderNo;
		}
	}
}
