package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_AttrGroupEnableReq extends JsonBasicReq
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
		private	List<level1Elm> attrGroupIdList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		public List<level1Elm> getAttrGroupIdList() {
			return attrGroupIdList;
		}
		public void setAttrGroupIdList(List<level1Elm> attrGroupIdList) {
			this.attrGroupIdList = attrGroupIdList;
		}
		
			
	}
	
	public class level1Elm
	{
		private String attrGroupId;
		public String getAttrGroupId() {
			return attrGroupId;
		}
	
		public void setAttrGroupId(String attrGroupId) {
			this.attrGroupId = attrGroupId;
		}

					
	}
	
}
