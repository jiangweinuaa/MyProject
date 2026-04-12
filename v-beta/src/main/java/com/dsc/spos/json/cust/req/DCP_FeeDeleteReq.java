package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 删除费用项 2018-09-20	
 * @author yuan	
 *
 */
public class DCP_FeeDeleteReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{


		private String fee;

		public String getFee() {
			return fee;
		}

		public void setFee(String fee) {
			this.fee = fee;
		}
	}
}
