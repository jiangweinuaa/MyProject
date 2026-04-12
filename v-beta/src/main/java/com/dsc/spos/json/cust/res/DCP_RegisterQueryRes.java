package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_RegisterQueryRes extends JsonBasicRes 
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
		private String SNumber;
		private String SName;
		private String TOT_Count;
		private String CanUseCount;
		private List<level2Elm> datas;
		
	public String getSNumber() {
		return SNumber;
	}
	public void setSNumber(String sNumber) {
		SNumber = sNumber;
	}
	public String getSName() {
		return SName;
	}
	public void setSName(String sName) {
		SName = sName;
	}
	public String getTOT_Count() {
		return TOT_Count;
	}
	public void setTOT_Count(String tOT_Count) {
		TOT_Count = tOT_Count;
	}
	public String getCanUseCount() {
		return CanUseCount;
	}
	public void setCanUseCount(String canUseCount) {
		CanUseCount = canUseCount;
	}
	public List<level2Elm> getDatas() {
	return datas;
	}
	public void setDatas(List<level2Elm> datas) {
	this.datas = datas;
	}
		
	}
	
	public class level2Elm
	{
		private String RegisterType;
		private String SCount;
		private String BDate;
		private String EDate;
		private String SDateTime;
		private String TerminalLicence;
		private String MachineName;
		private String MotherboardSN;
		private String HardDiskSN;
		private String CPUSerial;
		private String SMac;
		private String IsFirst;
		private String IsRegister;
	public String getRegisterType() {
		return RegisterType;
	}
	public void setRegisterType(String registerType) {
		RegisterType = registerType;
	}
	public String getSCount() {
		return SCount;
	}
	public void setSCount(String sCount) {
		SCount = sCount;
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
	public String getSDateTime() {
		return SDateTime;
	}
	public void setSDateTime(String sDateTime) {
		SDateTime = sDateTime;
	}
	public String getTerminalLicence() {
		return TerminalLicence;
	}
	public void setTerminalLicence(String terminalLicence) {
		TerminalLicence = terminalLicence;
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
	public String getIsFirst() {
		return IsFirst;
	}
	public void setIsFirst(String isFirst) {
		IsFirst = isFirst;
	}
	public String getIsRegister() {
		return IsRegister;
	}
	public void setIsRegister(String isRegister) {
		IsRegister = isRegister;
	}
	}
	
}
