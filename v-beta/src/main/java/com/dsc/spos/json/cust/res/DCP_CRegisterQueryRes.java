package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_CRegisterQueryRes extends JsonRes
{
	private String sCount;
	private String isCount;
	private List<level1Elm> registerDetail;
	private List<level2Elm> registerMachine;
	private List<level3Elm> allShop;

	public String getsCount() {
		return sCount;
	}
	public void setsCount(String sCount) {
		this.sCount = sCount;
	}
	public String getIsCount() {
		return isCount;
	}
	public void setIsCount(String isCount) {
		this.isCount = isCount;
	}
	public List<level1Elm> getRegisterDetail() {
		return registerDetail;
	}
	public void setRegisterDetail(List<level1Elm> registerDetail) {
		this.registerDetail = registerDetail;
	}
	public List<level2Elm> getRegisterMachine() {
		return registerMachine;
	}
	public void setRegisterMachine(List<level2Elm> registerMachine) {
		this.registerMachine = registerMachine;
	}
	public List<level3Elm> getAllShop() {
		return allShop;
	}
	public void setAllShop(List<level3Elm> allShop) {
		this.allShop = allShop;
	}
	public class level1Elm
	{
		private String terminalLicence;
		private String registerType;
		private String producttype;
		private String scount;
		private String bdate;
		private String edate;
		private String memo;
		private String isCount;
		public String getTerminalLicence() {
			return terminalLicence;
		}
		public void setTerminalLicence(String terminalLicence) {
			this.terminalLicence = terminalLicence;
		}
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
		public String getIsCount() {
			return isCount;
		}
		public void setIsCount(String isCount) {
			this.isCount = isCount;
		}
	}

	public class level2Elm
	{
		private String producttype;
		private String registerType;
		private String bdate;
		private String edate;

		private String machineCode;
		private String machineName;
		private String rEId;
		private String rShopId;
		private String rshopname;
		private String rmachine;
		private String terminalLicence;

		public String getRegisterType() {
			return registerType;
		}
		public void setRegisterType(String registerType) {
			this.registerType = registerType;
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
		public String getTerminalLicence() {
			return terminalLicence;
		}
		public void setTerminalLicence(String terminalLicence) {
			this.terminalLicence = terminalLicence;
		}
		public String getRshopname() {
			return rshopname;
		}
		public void setRshopname(String rshopname) {
			this.rshopname = rshopname;
		}
	}
	public class level3Elm
	{
		private String rEId;
		private String rshop;
		private String rmachine;
		private String rshopname;
		private String rmachinename;

		public String getrEId() {
			return rEId;
		}
		public void setrEId(String rEId) {
			this.rEId = rEId;
		}
		public String getRshop() {
			return rshop;
		}
		public void setRshop(String rshop) {
			this.rshop = rshop;
		}
		public String getRmachine() {
			return rmachine;
		}
		public void setRmachine(String rmachine) {
			this.rmachine = rmachine;
		}
		public String getRshopname() {
			return rshopname;
		}
		public void setRshopname(String rshopname) {
			this.rshopname = rshopname;
		}
		public String getRmachinename() {
			return rmachinename;
		}
		public void setRmachinename(String rmachinename) {
			this.rmachinename = rmachinename;
		}

	}

}
