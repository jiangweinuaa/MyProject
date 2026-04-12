package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.cust.JsonReq;

/**
 * 营业预估单确定
 * @author yuanyy
 *	
 */
public class DCP_PFOrderProcessReq extends JsonReq {
private levReq request;
	
	public levReq getRequest() {
		return request;
	}

	public void setRequest(levReq request) {
		this.request = request;
	}

	public class levReq{
		private String pfId;
		private String pfNo;
		private String rDate;
		
		public String getPfId() {
			return pfId;
		}
		public void setPfId(String pfId) {
			this.pfId = pfId;
		}
		public String getPfNo() {
			return pfNo;
		}
		public void setPfNo(String pfNo) {
			this.pfNo = pfNo;
		}
		public String getrDate() {
			return rDate;
		}
		public void setrDate(String rDate) {
			this.rDate = rDate;
		}
	}
}
