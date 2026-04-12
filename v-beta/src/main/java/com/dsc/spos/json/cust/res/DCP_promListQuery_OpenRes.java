package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 促销查询
 * @author Huawei
 *
 */
public class DCP_promListQuery_OpenRes extends JsonBasicRes {
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		
		private String PromNo;
		private String PromCategory;
		private String PromName;
		
		private String PromDiscrep;
		private String StartDate;
		private String EndDate;
		public String getPromNo() {
			return PromNo;
		}
		public String getPromCategory() {
			return PromCategory;
		}
		public String getPromName() {
			return PromName;
		}
		public String getPromDiscrep() {
			return PromDiscrep;
		}
		public String getStartDate() {
			return StartDate;
		}
		public String getEndDate() {
			return EndDate;
		}
		public void setPromNo(String promNo) {
			PromNo = promNo;
		}
		public void setPromCategory(String promCategory) {
			PromCategory = promCategory;
		}
		public void setPromName(String promName) {
			PromName = promName;
		}
		public void setPromDiscrep(String promDiscrep) {
			PromDiscrep = promDiscrep;
		}
		public void setStartDate(String startDate) {
			StartDate = startDate;
		}
		public void setEndDate(String endDate) {
			EndDate = endDate;
		}
		
	}
	
}
