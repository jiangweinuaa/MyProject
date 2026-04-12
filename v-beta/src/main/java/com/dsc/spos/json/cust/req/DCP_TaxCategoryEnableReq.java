package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TaxCategoryEnableReq extends JsonBasicReq
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
		private	List<level1Elm> taxCodeList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		public List<level1Elm> getTaxCodeList() {
			return taxCodeList;
		}
		public void setTaxCodeList(List<level1Elm> taxCodeList) {
			this.taxCodeList = taxCodeList;
		}
		
			
	}
	
	public class level1Elm
	{
		private String taxCode;
        private String taxArea;
        public String getTaxArea() {
            return taxArea;
        }
        public void setTaxArea(String taxArea) {
            this.taxArea = taxArea;
        }
		public String getTaxCode() {
			return taxCode;
		}
	
		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}
		

					
	}
	
}
