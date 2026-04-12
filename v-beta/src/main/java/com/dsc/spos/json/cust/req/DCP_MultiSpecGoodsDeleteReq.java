package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_MultiSpecGoodsDeleteReq extends JsonBasicReq {

  private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> masterPluNoList;
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
