package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.JsonBasicRes;

public class DCP_GoodsSetCategoryTreeQueryRes extends JsonBasicRes {
	private List<level1Elm> datas;
 
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
 
	public class level1Elm {
		private String CATEGORY;
		private String CATEGORY_NAME;
		private String UP_CATEGORY;
		private String CATEGORYLEVEL;
		private String eId;
		private String status;
        private String isAllowSelfBuilt;
        private String taxCode;
        private String taxName;
        private String taxRate;
        private List<level1Elm> children;

		public String getCATEGORY() {
			return CATEGORY;
		}
		public void setCATEGORY(String cATEGORY) {
			CATEGORY = cATEGORY;
		}
		public String getCATEGORY_NAME() {
			return CATEGORY_NAME;
		}
		public void setCATEGORY_NAME(String cATEGORY_NAME) {
			CATEGORY_NAME = cATEGORY_NAME;
		}
		public String getUP_CATEGORY() {
			return UP_CATEGORY;
		}
		public void setUP_CATEGORY(String uP_CATEGORY) {
			UP_CATEGORY = uP_CATEGORY;
		}
		public String getCATEGORYLEVEL() {
			return CATEGORYLEVEL;
		}
		public void setCATEGORYLEVEL(String cATEGORYLEVEL) {
			CATEGORYLEVEL = cATEGORYLEVEL;
		}
		public List<level1Elm> getChildren() {
			return children;
		}
		public void setChildren(List<level1Elm> children) {
			this.children = children;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
        public String getIsAllowSelfBuilt() {
            return isAllowSelfBuilt;
        }
        public void setIsAllowSelfBuilt(String isAllowSelfBuilt) {
            this.isAllowSelfBuilt = isAllowSelfBuilt;
        }

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

        public String getTaxRate() {
            return taxRate;
        }

        public void setTaxRate(String taxRate) {
            this.taxRate = taxRate;
        }
    }




}
