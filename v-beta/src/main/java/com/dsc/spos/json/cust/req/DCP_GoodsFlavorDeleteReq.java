package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 删除商品口味信息 2018-09-20	
 * @author yuanyy
 *
 */
public class DCP_GoodsFlavorDeleteReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String pluNo;
		private String flavorNo;
		private String priority;

		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getFlavorNo() {
			return flavorNo;
		}
		public void setFlavorNo(String flavorNo) {
			this.flavorNo = flavorNo;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
	}
}
