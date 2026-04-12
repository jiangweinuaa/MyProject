package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_VersionQueryRes extends JsonBasicRes
{
	
	private List<levelversion> datas;
	
	
	public class levelversion
	{
		private String version;
		private String updateDate;
		
		private List<levelmemo> datas;
		
		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getUpdateDate() {
			return updateDate;
		}

		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}
		
		public List<levelmemo> getDatas() {
			return datas;
		}

		public void setDatas(List<levelmemo> datas) {
			this.datas = datas;
		}
		
	}
	
	public class levelmemo
	{
		private String memo;

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}		
	}

	
	
	public List<levelversion> getDatas() {
		return datas;
	}

	public void setDatas(List<levelversion> datas) {
		this.datas = datas;
	}
	
	
	

}
