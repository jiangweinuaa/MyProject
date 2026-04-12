package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 商品口味修改 2018-09-20
 * @author yuanyy
 *
 */
public class DCP_GoodsFlavorUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String flavorNo;
		private String flavorName;
		private String priority;
		private String status;
		private String toFlavorNo;
		private String toPriority;

		public String getFlavorNo() {
			return flavorNo;
		}
		public void setFlavorNo(String flavorNo) {
			this.flavorNo = flavorNo;
		}
		public String getToFlavorNo() {
			return toFlavorNo;
		}
		public void setToFlavorNo(String toFlavorNo) {
			this.toFlavorNo = toFlavorNo;
		}
		public String getToPriority() {
			return toPriority;
		}
		public void setToPriority(String toPriority) {
			this.toPriority = toPriority;
		}
		public String getFlavorName() {
			return flavorName;
		}
		public void setFlavorName(String flavorName) {
			this.flavorName = flavorName;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}
}
