package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_CRegisterRes extends JsonRes 
{
	private String customerNo;
	private String customerName;
	
	private List<level1Elm> registerHead;
	
	private List<level4Elm> countdatas;
	
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
	
	public List<level1Elm> getRegisterHead() {
		return registerHead;
	}
	public void setRegisterHead(List<level1Elm> registerHead) {
		this.registerHead = registerHead;
	}
	
	
	public List<level4Elm> getCountdatas() {
	return countdatas;
	}
	public void setCountdatas(List<level4Elm> countdatas) {
	this.countdatas = countdatas;
	}


	public class level1Elm 
	{
		private String terminalLicence;
		private String isRegister;
		private String memo;
		private String sDateTime;
		private List<level2Elm> registerDetail;
	public String getTerminalLicence() {
		return terminalLicence;
	}
	public void setTerminalLicence(String terminalLicence) {
		this.terminalLicence = terminalLicence;
	}
	public String getIsRegister() {
		return isRegister;
	}
	public void setIsRegister(String isRegister) {
		this.isRegister = isRegister;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getsDateTime() {
		return sDateTime;
	}
	public void setsDateTime(String sDateTime) {
		this.sDateTime = sDateTime;
	}
	public List<level2Elm> getRegisterDetail() {
		return registerDetail;
	}
	public void setRegisterDetail(List<level2Elm> registerDetail) {
		this.registerDetail = registerDetail;
	}
	}
	
	public class level2Elm 
	{
		private String registerType;
		private String producttype;
		private String scount;
		private String bdate;
		private String edate;
		private String memo;
		private String iscount;
		
		private List<level3Elm> registerMachine;
		
	public String getRegisterType() {
		return registerType;
	}
	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}
	public String getProducttype() {
		return producttype;
	}
	public void setProducttype(String producttype) {
		this.producttype = producttype;
	}
	public String getScount() {
		return scount;
	}
	public void setScount(String scount) {
		this.scount = scount;
	}
	public String getBdate() {
		return bdate;
	}
	public void setBdate(String bdate) {
		this.bdate = bdate;
	}
	public String getEdate() {
		return edate;
	}
	public void setEdate(String edate) {
		this.edate = edate;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public List<level3Elm> getRegisterMachine() {
	return registerMachine;
	}
	public void setRegisterMachine(List<level3Elm> registerMachine) {
	this.registerMachine = registerMachine;
	}
	public String getIscount() {
	return iscount;
	}
	public void setIscount(String iscount) {
	this.iscount = iscount;
	}
	}
	public class level3Elm 
	{
		private String producttype;
		private String machineCode;
		private String machineName;
		private String rEId;
		private String rShopId;
		
		private String rmachine;
		private String rshopName;
		private String rmachineName;
		
	public String getProducttype() {
		return producttype;
	}
	public void setProducttype(String producttype) {
		this.producttype = producttype;
	}
	public String getMachineCode() {
		return machineCode;
	}
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public String getrEId() {
		return rEId;
	}
	public void setrEId(String rEId) {
		this.rEId = rEId;
	}
	public String getrShopId() {
		return rShopId;
	}
	public void setrShopId(String rShopId) {
		this.rShopId = rShopId;
	}
	public String getRmachine() {
		return rmachine;
	}
	public void setRmachine(String rmachine) {
		this.rmachine = rmachine;
	}
	public String getRshopName() {
	return rshopName;
	}
	public void setRshopName(String rshopName) {
	this.rshopName = rshopName;
	}
	public String getRmachineName() {
	return rmachineName;
	}
	public void setRmachineName(String rmachineName) {
	this.rmachineName = rmachineName;
	}
		
	}
	
	public class level4Elm
	{
		private String producttype;
		private String scount;
		private String iscount;
	public String getProducttype() {
		return producttype;
	}
	public void setProducttype(String producttype) {
		this.producttype = producttype;
	}
	public String getScount() {
		return scount;
	}
	public void setScount(String scount) {
		this.scount = scount;
	}
	public String getIscount() {
		return iscount;
	}
	public void setIscount(String iscount) {
		this.iscount = iscount;
	}
	}
	
	
}

