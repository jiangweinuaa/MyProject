package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 商品系列查询
 * @author yuanyy	
 *
 */
public class DCP_SeriesQueryRes extends JsonRes {
	
	
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String seriesNo ;
		private String seriesName;
		private String status;
		
		private List<level2Elm> seriesName_lang;

		public String getSeriesNo() {
			return seriesNo;
		}
	
		public void setSeriesNo(String seriesNo) {
			this.seriesNo = seriesNo;
		}
	
		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	
		public String getSeriesName() {
				return seriesName;
			}

		public void setSeriesName(String seriesName) {
			this.seriesName = seriesName;
		}

		public List<level2Elm> getSeriesName_lang() {
			return seriesName_lang;
		}
	
		public void setSeriesName_lang(List<level2Elm> seriesName_lang) {
			this.seriesName_lang = seriesName_lang;
		}
	
			

		

		
		
	}
	public class level2Elm
	{
		
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
