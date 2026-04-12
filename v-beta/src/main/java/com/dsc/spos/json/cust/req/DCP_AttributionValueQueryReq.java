package com.dsc.spos.json.cust.req;


import com.dsc.spos.json.JsonBasicReq;

public class DCP_AttributionValueQueryReq extends JsonBasicReq {
	
  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String attrId;//属性编码
		private String keyTxt;
		private String status;
		
		public String getAttrId() {
		return attrId;
		}
		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}
		public String getKeyTxt() {
				return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	
	}
	
	

}
