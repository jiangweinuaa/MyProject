package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：ShopEDateGetDCP
 * 服务说明：日结查询DCP
 * @author jinzma 
 * @since  2019-05-10
 */

public class DCP_ShopEDateQueryReq extends JsonBasicReq  {

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
