package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * POS班次记录查询
 * @author yuanyy 2019-05-20
 *
 */
public class DCP_SquadQueryReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String endSquad;
		private String bDate;
		private String eDate;
		private String keyTxt;

		public String getEndSquad() {
			return endSquad;
		}
		public void setEndSquad(String endSquad) {
			this.endSquad = endSquad;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String geteDate() {
			return eDate;
		}
		public void seteDate(String eDate) {
			this.eDate = eDate;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
	}

}
