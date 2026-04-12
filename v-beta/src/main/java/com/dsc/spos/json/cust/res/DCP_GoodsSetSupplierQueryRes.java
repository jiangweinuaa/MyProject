package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_GoodsSetSupplierQueryRes extends JsonRes
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
		private String SUPPLIER;
		private String SUPPLIER_NAME;
		private String ABBR;
		private String MOBILE;
		private String ADDRESS;
		private String eId;
		private String status;
		private String stockInAllowType;
		private String stockOutAllowType;
		private String taxCode;//税别编码
		private String taxName;//税别名称

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
		public String getSUPPLIER() {
			return SUPPLIER;
		}
		public void setSUPPLIER(String sUPPLIER) {
			SUPPLIER = sUPPLIER;
		}
		public String getSUPPLIER_NAME() {
			return SUPPLIER_NAME;
		}
		public void setSUPPLIER_NAME(String sUPPLIER_NAME) {
			SUPPLIER_NAME = sUPPLIER_NAME;
		}
		public String getABBR() {
			return ABBR;
		}
		public void setABBR(String aBBR) {
			ABBR = aBBR;
		}
		public String getMOBILE() {
			return MOBILE;
		}
		public void setMOBILE(String mOBILE) {
			MOBILE = mOBILE;
		}
		public String getADDRESS() {
			return ADDRESS;
		}
		public void setADDRESS(String aDDRESS) {
			ADDRESS = aDDRESS;
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
		public String getStockInAllowType() {
			return stockInAllowType;
		}
		public void setStockInAllowType(String stockInAllowType) {
			this.stockInAllowType = stockInAllowType;
		}
		public String getStockOutAllowType() {
			return stockOutAllowType;
		}
		public void setStockOutAllowType(String stockOutAllowType) {
			this.stockOutAllowType = stockOutAllowType;
		}



	}
}
