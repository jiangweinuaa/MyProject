package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_BrandEnableReq extends JsonBasicReq
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
		private	List<level1Elm> brandNoList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		public List<level1Elm> getBrandNoList() {
			return brandNoList;
		}
		public void setBrandNoList(List<level1Elm> brandNoList) {
			this.brandNoList = brandNoList;
		}
			
	}
	
	public class level1Elm
	{
		private String brandNo;
		public String getBrandNo() {
			return brandNo;
		}
	
		public void setBrandNo(String brandNo) {
			this.brandNo = brandNo;
		}

		
		
	}
	
}
