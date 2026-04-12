package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_SRegisterRes extends JsonRes
{
	private String customerNo;
	private String customerName;
	
	private List<level1Elm> datas;
	
	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public List<level1Elm> getDatas() {
	return datas;
	}

	public void setDatas(List<level1Elm> datas) {
	this.datas = datas;
	}

	public class level1Elm
	{
		private String terminalLicence;
		private String machinecode;
		private String tlinfohead;
		private List<level2Elm> registerHead;

	public String getTlinfohead() {
		return tlinfohead;
	}
	public void setTlinfohead(String tlinfohead) {
		this.tlinfohead = tlinfohead;
	}
	
	
	public String getTerminalLicence() {
		return terminalLicence;
	}
	public void setTerminalLicence(String terminalLicence) {
		this.terminalLicence = terminalLicence;
	}
	
	public String getMachinecode() {
	return machinecode;
	}
	public void setMachinecode(String machinecode) {
	this.machinecode = machinecode;
	}
	public List<level2Elm> getRegisterHead() {
	return registerHead;
	}
	public void setRegisterHead(List<level2Elm> registerHead) {
	this.registerHead = registerHead;
	}
	}
	
	public class level2Elm
	{
		private String producttype;
		private String tlinfo;
	public String getProducttype() {
		return producttype;
	}
	public void setProducttype(String producttype) {
		this.producttype = producttype;
	}
	public String getTlinfo() {
	return tlinfo;
	}
	public void setTlinfo(String tlinfo) {
	this.tlinfo = tlinfo;
	}
		
	}
	
}
