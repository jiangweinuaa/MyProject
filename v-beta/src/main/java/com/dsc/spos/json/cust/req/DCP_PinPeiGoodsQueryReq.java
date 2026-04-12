package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_PinPeiGoodsQuery
 * 服务说明：拼胚商品查询
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiGoodsQueryReq extends JsonBasicReq{
	private levelElm request;	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String status;
		private String keyTxt;
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
	}
}
