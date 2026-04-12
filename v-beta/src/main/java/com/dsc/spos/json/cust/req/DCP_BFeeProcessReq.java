package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：BFeeProcess
 *    說明：门店费用处理
 * 服务说明：门店费用处理
 * @author luoln 
 * @since  2017-07-18
 */
public class DCP_BFeeProcessReq extends JsonBasicReq{

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String bFeeNo;
		private String status;
		public String getbFeeNo() {
			return bFeeNo;
		}
		public void setbFeeNo(String bFeeNo) {
			this.bFeeNo = bFeeNo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}


}
