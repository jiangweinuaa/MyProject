package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

public class DCP_SupplierQueryRes extends JsonRes {
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm {
		private String supplier;
		private String supplierName;
		private String abbr;
		private String mobile;
		private String stockInAllowType;
		private String stockOutAllowType;
		private String taxCode;
		private String taxCodeName;
		private String rangeType;
		private String memo;
		private String status;
		private String selfBuiltShopId;
		
		public String getSupplier() {
			return supplier;
		}
		public void setSupplier(String supplier) {
			this.supplier = supplier;
		}
		public String getSupplierName() {
			return supplierName;
		}
		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}
		public String getAbbr() {
			return abbr;
		}
		public void setAbbr(String abbr) {
			this.abbr = abbr;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
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
		public String getTaxCode() {
			return taxCode;
		}
		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}
		public String getTaxCodeName() {
			return taxCodeName;
		}
		public void setTaxCodeName(String taxCodeName) {
			this.taxCodeName = taxCodeName;
		}
		public String getRangeType() {
			return rangeType;
		}
		public void setRangeType(String rangeType) {
			this.rangeType = rangeType;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getSelfBuiltShopId() {
			return selfBuiltShopId;
		}
		public void setSelfBuiltShopId(String selfBuiltShopId) {
			this.selfBuiltShopId = selfBuiltShopId;
		}
	}
	
}
