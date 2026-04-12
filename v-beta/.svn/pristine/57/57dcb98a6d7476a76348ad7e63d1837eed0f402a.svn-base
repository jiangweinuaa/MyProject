package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_RegisterCreateReq extends JsonBasicReq
{
	private String customerNo;
	private String memo;
	private List<level1Elm> datas;
	
	public  class level1Elm
	{
		private String registerType;
		private String producttype;
		private String scount;
		private String bdate;
		private String edate;
		private String memo;
		private List<level2Elm> SDetailinfo;
		
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
	public List<level2Elm> getSDetailinfo() {
		return SDetailinfo;
	}
	public void setSDetailinfo(List<level2Elm> sDetailinfo) {
		SDetailinfo = sDetailinfo;
	}
	}

	public String getCustomerNo() {
	return customerNo;
	}

	public void setCustomerNo(String customerNo) {
	this.customerNo = customerNo;
	}

	public List<level1Elm> getDatas() {
	return datas;
	}

	public void setDatas(List<level1Elm> datas) {
	this.datas = datas;
	}

	public String getMemo() {
	return memo;
	}

	public void setMemo(String memo) {
	this.memo = memo;
	}
	
	public  class level2Elm
	{
		private String SDetailType;
		private String SDetailmodular;
		private String scount;
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
	}
}
