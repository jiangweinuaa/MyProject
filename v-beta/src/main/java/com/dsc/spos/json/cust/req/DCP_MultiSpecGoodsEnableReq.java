package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_MultiSpecGoodsEnableReq extends JsonBasicReq
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
		private	List<level1Elm> masterPluNoList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		public List<level1Elm> getMasterPluNoList() {
			return masterPluNoList;
		}
		public void setMasterPluNoList(List<level1Elm> masterPluNoList) {
			this.masterPluNoList = masterPluNoList;
		}
		
		
			
	}
	
	public class level1Elm
	{
		private String masterPluNo;
		public String getMasterPluNo() {
			return masterPluNo;
		}
	
		public void setMasterPluNo(String masterPluNo) {
			this.masterPluNo = masterPluNo;
		}
		

					
	}
	
}
