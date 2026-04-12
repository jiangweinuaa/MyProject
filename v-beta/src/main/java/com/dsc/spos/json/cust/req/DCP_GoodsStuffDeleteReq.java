package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 删除统一加料信息
 * 2018-09-21
 * @author yuanyy	
 *
 */
public class DCP_GoodsStuffDeleteReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String stuffNo;
		private String priority;

		public String getStuffNo() {
			return stuffNo;
		}
		public void setStuffNo(String stuffNo) {
			this.stuffNo = stuffNo;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
	}
}
