package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_AttributionValueDeleteReq extends JsonBasicReq {

private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> attrValueIdList;

		public List<level1Elm> getAttrValueIdList() {
			return attrValueIdList;
		}	
		public void setAttrValueIdList(List<level1Elm> attrValueIdList) {
			this.attrValueIdList = attrValueIdList;
		}
					
	}
	
	public class level1Elm
	{
		private String attrId;
		private String attrValueId;

		public String getAttrValueId() {
			return attrValueId;
		}
	
		public void setAttrValueId(String attrValueId) {
			this.attrValueId = attrValueId;
		}

		public String getAttrId()
		{
			return attrId;
		}

		public void setAttrId(String attrId)
		{
			this.attrId = attrId;
		}

		
		
	}
	
	
}
