package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服务函数：DCP_FuncGet
 * 服务说明：基本功能查询
 * @author jinzma 
 * @since  2019-12-17
 */
public class DCP_FuncQueryReq extends JsonBasicReq{

	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm
	{
		private String keyTxt;
		private String funcType;
		private String approve;

		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getFuncType() {
			return funcType;
		}
		public void setFuncType(String funcType) {
			this.funcType = funcType;
		}
		public String getApprove() {
			return approve;
		}
		public void setApprove(String approve) {
			this.approve = approve;
		}
	
	}
}
