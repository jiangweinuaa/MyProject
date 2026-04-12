package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_TaxCategoryCreateReq.level1Elm;

/**
 * 税别信息修改
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_TaxCategoryUpdateReq extends JsonBasicReq {
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String taxCode;
		private String taxType;
		private String taxRate;
		private String inclTax;
		private String status;
        private String taxArea;
        private String taxProp;
        private String taxCalType;
		private List<level1Elm> taxName_lang;
		public String getTaxCode() {
			return taxCode;
		}
		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}
		
		public String getTaxRate() {
			return taxRate;
		}
		public void setTaxRate(String taxRate) {
			this.taxRate = taxRate;
		}
		public String getInclTax() {
			return inclTax;
		}
		public void setInclTax(String inclTax) {
			this.inclTax = inclTax;
		}
		public String getTaxType() {
			return taxType;
		}
		public void setTaxType(String taxType) {
			this.taxType = taxType;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
        public void setTaxArea(String taxArea){this.taxArea=taxArea;}
        public String getTaxArea(){return taxArea;}
        public void setTaxProp(String taxProp){this.taxProp=taxProp;}
        public String getTaxProp(){return taxProp;}
		public List<level1Elm> getTaxName_lang() {
			return taxName_lang;
		}
		public void setTaxName_lang(List<level1Elm> taxName_lang) {
			this.taxName_lang = taxName_lang;
		}


        public String getTaxCalType() {
            return taxCalType;
        }

        public void setTaxCalType(String taxCalType) {
            this.taxCalType = taxCalType;
        }
    }
	
	public class level1Elm {
		private String langType;
		private String name;
		
		public String getLangType() {
			return langType;
		}

		public void setLangType(String langType) {
			this.langType = langType;
		}

		public String getName() {
			return name;
		}
	
		public void setName(String name) {
			this.name = name;
		}

		

	}
	
}
