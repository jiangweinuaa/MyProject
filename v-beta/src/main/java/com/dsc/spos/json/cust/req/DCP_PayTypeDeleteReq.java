package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PayTypeDeleteReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> payTypeList;
		public List<level1Elm> getPayTypeList() {
			return payTypeList;
		}
	
		public void setPayTypeList(List<level1Elm> payTypeList) {
			this.payTypeList = payTypeList;
		}
		
		
	}
	
	public class level1Elm
	{		
		private String payType;
		public String getPayType() {
			return payType;
		}
	
		public void setPayType(String payType) {
			this.payType = payType;
		}		
		
	}
	
	

}
