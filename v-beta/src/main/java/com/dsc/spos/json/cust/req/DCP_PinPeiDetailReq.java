package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_PinPeiDetail
 * 服务说明：拼胚详情
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiDetailReq extends JsonBasicReq{
	private levelElm request;	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String pinPeiNo;
		public String getPinPeiNo() {
			return pinPeiNo;
		}
		public void setPinPeiNo(String pinPeiNo) {
			this.pinPeiNo = pinPeiNo;
		}
	}

}
