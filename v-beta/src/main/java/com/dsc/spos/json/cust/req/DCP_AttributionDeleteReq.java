package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_AttributionDeleteReq extends JsonBasicReq {

 private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> attrIdList;

		public List<level1Elm> getAttrIdList() {
			return attrIdList;
		}
	
		public void setAttrIdList(List<level1Elm> attrIdList) {
			this.attrIdList = attrIdList;
		}
		
	}
	
	public class level1Elm
	{
		private String attrId;

		public String getAttrId() {
			return attrId;
		}
	
		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}

		
		
	}
	
	
}
