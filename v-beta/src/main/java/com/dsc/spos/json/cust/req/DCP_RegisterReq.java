package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_RegisterReq extends JsonBasicReq
{
	//客户代码
	private String SNumber;
  //机器名称
	private String MachineName;
	
	public String getSNumber() {
		return SNumber;
	}

	public void setSNumber(String sNumber) {
		SNumber = sNumber;
	}

	public String getMachineName() {
		return MachineName;
	}

	public void setMachineName(String machineName) {
		MachineName = machineName;
	}

	public String getMotherboardSN() {
		return MotherboardSN;
	}

	public void setMotherboardSN(String motherboardSN) {
		MotherboardSN = motherboardSN;
	}

	public String getHardDiskSN() {
		return HardDiskSN;
	}

	public void setHardDiskSN(String hardDiskSN) {
		HardDiskSN = hardDiskSN;
	}

	public String getCPUSerial() {
		return CPUSerial;
	}

	public void setCPUSerial(String cPUSerial) {
		CPUSerial = cPUSerial;
	}

	public String getSMac() {
		return SMac;
	}

	public void setSMac(String sMac) {
		SMac = sMac;
	}

	public String getTerminalLicence() {
		return TerminalLicence;
	}

	public void setTerminalLicence(String terminalLicence) {
		TerminalLicence = terminalLicence;
	}

	public String getRegisterType() {
		return RegisterType;
	}

	public void setRegisterType(String registerType) {
		RegisterType = registerType;
	}

	public String getBDate() {
		return BDate;
	}

	public void setBDate(String bDate) {
		BDate = bDate;
	}

	public String getEDate() {
		return EDate;
	}

	public void setEDate(String eDate) {
		EDate = eDate;
	}

	public String getISFirst() {
		return ISFirst;
	}

	public void setISFirst(String iSFirst) {
		ISFirst = iSFirst;
	}

	public String getFRType() {
		return FRType;
	}

	public void setFRType(String fRType) {
		FRType = fRType;
	}

	public String getSCount() {
	return SCount;
	}

	public void setSCount(String sCount) {
	SCount = sCount;
	}

	//主板序列号
	private String MotherboardSN;
	
	//硬盘序列号
	private String HardDiskSN;
	
	//CPU序列号
	private String CPUSerial;
	
	//网卡MAC地址
	private String SMac;
	
  //注册号码
	private String TerminalLicence;
	
	//注册类型0正式   1借货
	private String RegisterType;
	
  //注册开始时间
	private String BDate;
		
	//注册结束时间
	private String EDate;
	
  //是否为第一次注册
	private String ISFirst;
	
	//0接收前台网页的注册服务  1接受来自公司注册服务器的注册 2 作为公司的注册服务器接受客户服务器的注册3服务器接受网页前端的申请
	
	private String FRType;
	
  //注册数量,作为服务方才会有数量
  private String SCount;
  
}
