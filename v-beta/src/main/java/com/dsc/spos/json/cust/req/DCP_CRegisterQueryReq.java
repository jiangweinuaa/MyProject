package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CRegisterQueryReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String producttype;
		private String keyTxt;

		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getProducttype() {
			return producttype;
		}
		public void setProducttype(String producttype) {
			this.producttype = producttype;
		}
	}
}


