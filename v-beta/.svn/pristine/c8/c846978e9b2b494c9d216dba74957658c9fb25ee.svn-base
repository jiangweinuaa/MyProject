package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务费修改
 * @author yuanyy
 *
 */
public class DCP_ServiceChargeUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String serviceChargeNo ;
		private String scType ;
		private String limitShop;
		private String status;

		private List<level1Elm> datas;


		public String getLimitShop() {
			return limitShop;
		}

		public void setLimitShop(String limitShop) {
			this.limitShop = limitShop;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getServiceChargeNo() {
			return serviceChargeNo;
		}

		public void setServiceChargeNo(String serviceChargeNo) {
			this.serviceChargeNo = serviceChargeNo;
		}

		public String getScType() {
			return scType;
		}

		public void setScType(String scType) {
			this.scType = scType;
		}

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}
	
	public  class level1Elm {
		private String spNo;
		private String scrate;

		public String getSpNo() {
			return spNo;
		}
		public void setSpNo(String spNo) {
			this.spNo = spNo;
		}
		public String getScrate() {
			return scrate;
		}
		public void setScrate(String scrate) {
			this.scrate = scrate;
		}


	}

}
