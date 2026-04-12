package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 系列删除 2018-09-19
 * @author yuanyy
 *
 */
public class DCP_SeriesDeleteReq extends JsonBasicReq {

private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> seriesNoList;

		public List<level1Elm> getSeriesNoList() {
			return seriesNoList;
		}
	
		public void setSeriesNoList(List<level1Elm> seriesNoList) {
			this.seriesNoList = seriesNoList;
		}
		
		
	}
	
	public class level1Elm
	{
		private String seriesNo ;

		public String getSeriesNo() {
			return seriesNo;
		}
	
		public void setSeriesNo(String seriesNo) {
			this.seriesNo = seriesNo;
		}
		
	}
	
	
}
