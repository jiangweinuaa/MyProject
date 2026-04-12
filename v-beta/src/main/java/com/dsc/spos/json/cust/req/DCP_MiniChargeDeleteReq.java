package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 低消信息删除
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_MiniChargeDeleteReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String miniChargeNo;

		public String getMiniChargeNo() {
			return miniChargeNo;
		}

		public void setMiniChargeNo(String miniChargeNo) {
			this.miniChargeNo = miniChargeNo;
		}
	}
}
