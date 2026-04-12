package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_OrderTransferErpSetQuery
 * 服务说明：订单上传ERP白名单查询
 * @author jinzma 
 * @since  2020-12-03
 */
public class DCP_OrderTransferErpSetQueryReq extends JsonBasicReq{
	private levelElm request;
	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String keyTxt;
		
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
	}
}
