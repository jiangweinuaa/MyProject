package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CompanyQueryReq extends JsonBasicReq
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
		private String companyId;
		private String status;//-1：未启用 100：已生效 0：已失效
		private String searchString;
		private String allData;//是否查询全部公司数据	0:否1：是 如果传入参数allData=0或不传，则返回当前用户隶属公司或当前用户管控公司 如果传入参数allData=1，则返回全部公司。
		public String getCompanyId() {
			return companyId;
		}
		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getSearchString() {
			return searchString;
		}
		public void setSearchString(String searchString) {
			this.searchString = searchString;
		}
		public String getAllData() {
			return allData;
		}
		public void setAllData(String allData) {
			this.allData = allData;
		}
		
		
		
	}

}
