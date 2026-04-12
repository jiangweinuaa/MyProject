package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 影响因素修改
 * @author yuanyy 20191021
 *
 */
public class DCP_ElementUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String e_Type;
		private String eNo;
		private String eName;
		private String eRatio;
		private String status;

		public String getE_Type() {
			return e_Type;
		}
		public void setE_Type(String e_Type) {
			this.e_Type = e_Type;
		}
		public String geteNo() {
			return eNo;
		}
		public void seteNo(String eNo) {
			this.eNo = eNo;
		}
		public String geteName() {
			return eName;
		}
		public void seteName(String eName) {
			this.eName = eName;
		}
		public String geteRatio() {
			return eRatio;
		}
		public void seteRatio(String eRatio) {
			this.eRatio = eRatio;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}


}
