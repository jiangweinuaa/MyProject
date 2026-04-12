package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsWeightPluQueryReq extends JsonBasicReq
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
		private String keyTxt;
		
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		
		
		
		
	}
	
}
