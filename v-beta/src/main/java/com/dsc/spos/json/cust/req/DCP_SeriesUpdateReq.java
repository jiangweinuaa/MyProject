package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;


/**
 * 系列新增 2018-09-19	
 * @author yuanyy
 *
 */
public final class DCP_SeriesUpdateReq extends JsonBasicReq {
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String seriesNo; 
		private String seriesName;
		private String status;
		private List<level1Elm> seriesName_lang;
		
		public String getSeriesNo() {
			return seriesNo;
		}
		public void setSeriesNo(String seriesNo) {
			this.seriesNo = seriesNo;
		}
		public String getSeriesName() {
			return seriesName;
		}
		public void setSeriesName(String seriesName) {
			this.seriesName = seriesName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public List<level1Elm> getSeriesName_lang() {
			return seriesName_lang;
		}
		public void setSeriesName_lang(List<level1Elm> seriesName_lang) {
			this.seriesName_lang = seriesName_lang;
		}
		
		
	}

	public  class level1Elm {
		private String name;
		private String langType;	
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLangType() {
				return langType;
			}
		public void setLangType(String langType) {
			this.langType = langType;
		}
	
		
	}
	
}