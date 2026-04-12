package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CRegisterCreateReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String terminalLicence;
		private String producttype;
		private List<level1Elm> datas;

		public String getProducttype() {
			return producttype;
		}

		public void setProducttype(String producttype) {
			this.producttype = producttype;
		}

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}

		public String getTerminalLicence() {
			return terminalLicence;
		}

		public void setTerminalLicence(String terminalLicence) {
			this.terminalLicence = terminalLicence;
		}
	}
	public  class level1Elm
	{
		private String rEId;
		private String rShopId;
		private String rmachine;

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
	}

}
