package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：POrderProcess
 *   說明：要货单处理
 * 服务说明：要货单处理
 * @author panjing 
 * @since  2016-10-08
 */
public class DCP_POrderProcessReq extends JsonBasicReq {

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm
	{
		private String porderNo;
		private String status;
		private String ISUrgentOrder;

		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getPorderNo() {
			return porderNo;
		}
		public void setPorderNo(String porderNo) {
			this.porderNo = porderNo;
		}
		public String getISUrgentOrder() {
			return ISUrgentOrder;
		}
		public void setISUrgentOrder(String iSUrgentOrder) {
			ISUrgentOrder = iSUrgentOrder;
		}	

	}
}

