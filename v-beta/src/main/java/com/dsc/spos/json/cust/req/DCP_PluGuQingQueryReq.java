package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 商品沽清查询
 * 
 */
public class DCP_PluGuQingQueryReq extends JsonBasicReq {
	private level1Elm request;
//	private String timestamp;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
//	public String getTimestamp() {
//		return timestamp;
//	}
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

	public class level1Elm
	{
		private String rDate;
		private String o_companyNo;
		private String o_shop;
		private List<level2Elm> pluList;
		
		public String getrDate() {
			return rDate;
		}
		public void setrDate(String rDate) {
			this.rDate = rDate;
		}
		public String getO_companyNo() {
			return o_companyNo;
		}
		public void setO_companyNo(String o_companyNo) {
			this.o_companyNo = o_companyNo;
		}
		public String getO_shop() {
			return o_shop;
		}
		public void setO_shop(String o_shop) {
			this.o_shop = o_shop;
		}
		public List<level2Elm> getPluList() {
			return pluList;
		}
		public void setPluList(List<level2Elm> pluList) {
			this.pluList = pluList;
		}
		
	}
	
	public class level2Elm{
		
		private String pluNo;
		private String barCode;
		private String pUnit;
		
		public String getPluNo() {
			return pluNo;
		}
		public String getBarCode() {
			return barCode;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setBarCode(String barCode) {
			this.barCode = barCode;
		}
		public String getpUnit() {
			return pUnit;
		}
		public void setpUnit(String pUnit) {
			this.pUnit = pUnit;
		}
		
	}
	
	
}
