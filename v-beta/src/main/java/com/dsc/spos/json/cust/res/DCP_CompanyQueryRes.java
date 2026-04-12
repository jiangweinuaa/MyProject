package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_CompanyQueryRes extends JsonRes
{
	
	private List<level1Elm> datas;
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}





	public class level1Elm
	{
		private String companyId;//公司id
		private String companyName;//公司全称
		private String companySName;//公司简称
		private String status;//
		public String getCompanyId() {
			return companyId;
		}
		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}
		public String getCompanyName() {
			return companyName;
		}
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}
		public String getCompanySName() {
			return companySName;
		}
		public void setCompanySName(String companySName) {
			this.companySName = companySName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
		
		
		
	}
}
