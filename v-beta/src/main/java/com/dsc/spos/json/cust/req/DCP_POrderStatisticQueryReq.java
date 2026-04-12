package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 要货品类统计查询    2019-01-10
 * @author yuanyy
 *
 */
public class DCP_POrderStatisticQueryReq extends JsonBasicReq {
	/**
	 * {					
		"serviceId": "POrderStatisticGetDCP",	必传且非空，服务名				
		"token": "f14ee75ff5b220177ac0dc538bdea08c",	必传且非空，访问令牌				
		"pOrderNO": "123123",	必传且非空，要货申请单号				
		}
	 */
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String porderNo;

		public String getPorderNo() {
			return porderNo;
		}
		public void setPorderNo(String porderNo) {
			this.porderNo = porderNo;
		}
	}

}
