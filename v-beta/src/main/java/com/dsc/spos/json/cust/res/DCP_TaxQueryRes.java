package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_TaxQueryRes extends JsonRes
{
	private List<leve1> datas;
	
	public class  leve1
	{
		private String taxCode;
		private String taxRate;
		private String taxName;
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
	public String getTaxName() {
		return taxName;
	}
	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}
	}

	public List<leve1> getDatas() {
	return datas;
	}

	public void setDatas(List<leve1> datas) {
	this.datas = datas;
	}
}
