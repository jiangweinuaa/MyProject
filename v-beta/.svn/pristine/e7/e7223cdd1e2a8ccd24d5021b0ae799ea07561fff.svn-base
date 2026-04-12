package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_SeriesEnableReq extends JsonBasicReq
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
		private	List<level1Elm> seriesNoList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		public List<level1Elm> getSeriesNoList() {
			return seriesNoList;
		}
		public void setSeriesNoList(List<level1Elm> seriesNoList) {
			this.seriesNoList = seriesNoList;
		}
		
			
	}
	
	public class level1Elm
	{
		private String seriesNo;

		public String getSeriesNo() {
			return seriesNo;
		}
	
		public void setSeriesNo(String seriesNo) {
			this.seriesNo = seriesNo;
		}
					
	}
	
}
