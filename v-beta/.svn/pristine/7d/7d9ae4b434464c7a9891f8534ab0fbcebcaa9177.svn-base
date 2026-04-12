package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PeriodEnableReq extends JsonBasicReq
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
		private	List<level1Elm> periodList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		public List<level1Elm> getPeriodList() {
			return periodList;
		}
		public void setPeriodList(List<level1Elm> periodList) {
			this.periodList = periodList;
		}
						
	}
	
	public class level1Elm
	{
		private String periodNo;
		public String getPeriodNo() {
			return periodNo;
		}	
		public void setPeriodNo(String periodNo) {
			this.periodNo = periodNo;
		}
									
	}
	
}
