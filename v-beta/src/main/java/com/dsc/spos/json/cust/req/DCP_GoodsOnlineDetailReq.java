package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsOnlineDetailReq  extends JsonBasicReq{
	
	private levelRequest request;
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String pluNo;
		public String getPluNo() {
			return pluNo;
		}	
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}		
	}

}
