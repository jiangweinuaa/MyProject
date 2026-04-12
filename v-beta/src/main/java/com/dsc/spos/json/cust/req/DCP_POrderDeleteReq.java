package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：POrderDelete
 *    說明：要货单删除
 * 服务说明：要货单删除
 * @author panjing 
 * @since  2016-10-08
 */
public class DCP_POrderDeleteReq extends JsonBasicReq {

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm {
		private String porderNo;

		public String getPorderNo() {
			return porderNo;
		}
		public void setPorderNo(String porderNo) {
			this.porderNo = porderNo;
		}

	}

}
