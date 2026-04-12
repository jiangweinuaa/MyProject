package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.req.DCP_RegisterCreateReq.level2Elm;

public class DCP_RCustomerQueryRes extends JsonRes
{
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	public static class level1Elm
	{
		private String customerNo;
		private String customerName;
		private String memo;
		private List<level4Elm> countdatas;
		
		
		
	private List<level2Elm> registerHead;
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
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public List<level2Elm> getRegisterHead() {
		return registerHead;
	}
	public void setRegisterHead(List<level2Elm> registerHead) {
		this.registerHead = registerHead;
	}
	
	public List<level4Elm> getCountdatas() {
	return countdatas;
	}
	public void setCountdatas(List<level4Elm> countdatas) {
	this.countdatas = countdatas;
	}
	}
	public static class level2Elm
	{
		private String terminalLicence;
		private String isRegister;
		private String memo;
		private String sDateTime;
		private List<level3Elm> registerDetail;
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
	public List<level3Elm> getRegisterDetail() {
		return registerDetail;
	}
	public void setRegisterDetail(List<level3Elm> registerDetail) {
		this.registerDetail = registerDetail;
	}
	}
	public static class level3Elm
	{
		private String registerType;
		private String producttype;
		private String scount;
		private String bdate;
		private String edate;
		private String memo;
		private List<level5Elm> SDetailinfo;
		
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
	public List<level5Elm> getSDetailinfo() {
		return SDetailinfo;
	}
	public void setSDetailinfo(List<level5Elm> sDetailinfo) {
		SDetailinfo = sDetailinfo;
	}
	}
	
	public static class level4Elm
	{
		private String producttype;
		private String scount;
		private String iscount;
		private List<level5Elm> SDetailinfo;
		
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
	public List<level5Elm> getSDetailinfo() {
		return SDetailinfo;
	}
	public void setSDetailinfo(List<level5Elm> sDetailinfo) {
		SDetailinfo = sDetailinfo;
	}
	}
	
	public static class level5Elm
	{
		private String SDetailType;
		private String SDetailmodular;
		private String scount;
		private String iscount;
		
		public String getSDetailType() {
			return SDetailType;
		}
		public void setSDetailType(String sDetailType) {
			SDetailType = sDetailType;
		}
		public String getSDetailmodular() {
			return SDetailmodular;
		}
		public void setSDetailmodular(String sDetailmodular) {
			SDetailmodular = sDetailmodular;
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


