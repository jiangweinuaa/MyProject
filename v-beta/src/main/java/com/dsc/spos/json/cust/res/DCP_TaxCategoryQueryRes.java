package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 税别信息查询
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_TaxCategoryQueryRes extends JsonRes {
	private List<level1Elm> datas;

	public class level1Elm{
		private String taxCode;
		private String taxName;
		private String taxType;
		private String taxRate;
		private String inclTax;
		private String status;
        private String taxArea;
        private String taxProp;
        private String taxCalType;

        private String creatorID;

        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;

		private List<level2Elm> taxName_lang;
		
		public String getTaxCode() {
			return taxCode;
		}
		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}
		public String getTaxName() {
			return taxName;
		}
		public void setTaxName(String taxName) {
			this.taxName = taxName;
		}
		public String getTaxType() {
			return taxType;
		}
		public void setTaxType(String taxType) {
			this.taxType = taxType;
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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
        public String getTaxArea() {
			return taxArea;
		}
        public void setTaxArea(String taxArea) {
			this.taxArea = taxArea;
		}
        public String getTaxProp() {
			return taxProp;
		}
        public void setTaxProp(String taxProp) {
			this.taxProp = taxProp;
		}
		public List<level2Elm> getTaxName_lang() {
			return taxName_lang;
		}
		public void setTaxName_lang(List<level2Elm> taxName_lang) {
			this.taxName_lang = taxName_lang;
		}


        public String getTaxCalType() {
            return taxCalType;
        }

        public void setTaxCalType(String taxCalType) {
            this.taxCalType = taxCalType;
        }

        public String getCreatorName() {
            return creatorName;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public String getCreatorDeptID() {
            return creatorDeptID;
        }

        public void setCreatorDeptID(String creatorDeptID) {
            this.creatorDeptID = creatorDeptID;
        }

        public String getCreatorDeptName() {
            return creatorDeptName;
        }

        public void setCreatorDeptName(String creatorDeptName) {
            this.creatorDeptName = creatorDeptName;
        }

        public String getCreate_datetime() {
            return create_datetime;
        }

        public void setCreate_datetime(String create_datetime) {
            this.create_datetime = create_datetime;
        }

        public String getLastmodifyID() {
            return lastmodifyID;
        }

        public void setLastmodifyID(String lastmodifyID) {
            this.lastmodifyID = lastmodifyID;
        }

        public String getLastmodifyName() {
            return lastmodifyName;
        }

        public void setLastmodifyName(String lastmodifyName) {
            this.lastmodifyName = lastmodifyName;
        }

        public String getLastmodify_datetime() {
            return lastmodify_datetime;
        }

        public void setLastmodify_datetime(String lastmodify_datetime) {
            this.lastmodify_datetime = lastmodify_datetime;
        }

		public String getCreatorID() {
			return creatorID;
		}

		public void setCreatorID(String creatorID) {
			this.creatorID = creatorID;
		}
	}
	
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level2Elm{
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
