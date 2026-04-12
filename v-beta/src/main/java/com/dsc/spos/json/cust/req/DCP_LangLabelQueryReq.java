package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/***
 * 多语言标签查询服务
 * @author 袁云洋 2019-01-17 
 *
 */
public class DCP_LangLabelQueryReq extends JsonBasicReq {

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
