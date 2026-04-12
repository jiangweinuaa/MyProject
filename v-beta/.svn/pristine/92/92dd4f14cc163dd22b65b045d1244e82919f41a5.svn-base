package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PayTypeEnableReq extends JsonBasicReq
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
		private String oprType;//操作类型：1-启用2-禁用
		private	List<level1Elm> payTypeList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
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
