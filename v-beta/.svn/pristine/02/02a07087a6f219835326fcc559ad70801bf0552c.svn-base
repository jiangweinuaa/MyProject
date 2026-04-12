package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 税别信息删除
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_TaxCategoryDeleteReq extends JsonBasicReq {
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> taxCodeList;

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


        public void setTaxArea(String taxArea){this.taxArea=taxArea;}
        public String getTaxArea(){return taxArea;}
		public String getTaxCode() {
			return taxCode;
		}
		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}
		
	}


	
	
}
