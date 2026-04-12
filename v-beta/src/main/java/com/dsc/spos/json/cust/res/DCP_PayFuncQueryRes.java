package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_PayFuncQueryRes extends JsonRes {
	
private List<level1Elm> datas;
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm{
		private String funcNo;
		private String funcName;
		private String status;	
		private String memo;
		private List<funcNameLang> funcName_lang;

        private String enableRecharge;//是否适用充值，Y/N，默认Y
        private String enableSaleCard;//是否适用售卡，Y/N，默认Y
        private String enableSaleTicket;//是否适用售券，Y/N，默认Y
        private String enableCustReturn;//是否适用客户回款，Y/N，默认Y
        private String enableTableRsv;//是否适用桌台预订，Y/N，默认Y


        public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}		
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getFuncNo() {
			return funcNo;
		}
		public void setFuncNo(String funcNo) {
			this.funcNo = funcNo;
		}
		public String getFuncName() {
			return funcName;
		}
		public void setFuncName(String funcName) {
			this.funcName = funcName;
		}
		public List<funcNameLang> getFuncName_lang() {
			return funcName_lang;
		}
		public void setFuncName_lang(List<funcNameLang> funcName_lang) {
			this.funcName_lang = funcName_lang;
		}

        public String getEnableRecharge()
        {
            return enableRecharge;
        }

        public void setEnableRecharge(String enableRecharge)
        {
            this.enableRecharge = enableRecharge;
        }

        public String getEnableSaleCard()
        {
            return enableSaleCard;
        }

        public void setEnableSaleCard(String enableSaleCard)
        {
            this.enableSaleCard = enableSaleCard;
        }

        public String getEnableSaleTicket()
        {
            return enableSaleTicket;
        }

        public void setEnableSaleTicket(String enableSaleTicket)
        {
            this.enableSaleTicket = enableSaleTicket;
        }

        public String getEnableCustReturn()
        {
            return enableCustReturn;
        }

        public void setEnableCustReturn(String enableCustReturn)
        {
            this.enableCustReturn = enableCustReturn;
        }

        public String getEnableTableRsv()
        {
            return enableTableRsv;
        }

        public void setEnableTableRsv(String enableTableRsv)
        {
            this.enableTableRsv = enableTableRsv;
        }
    }
	
	public class funcNameLang 
	{
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
