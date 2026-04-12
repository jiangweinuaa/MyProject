package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：ParaDefineGet
 *    說明：参数定义查询
 * 服务说明：参数定义查询
 * @author jinzma 
 * @since  2017-02-27
 */
public class DCP_ParaDefineQueryReq extends JsonBasicReq {

	private levelRequest request;
		
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String keyTxt;
		private String paraType;
		private String modularId;//模块代号
		
		public String getKeyTxt() {
			return keyTxt;
		}
		public String getParaType() {
			return paraType;
		}
		public String getModularId() {
			return modularId;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public void setParaType(String paraType) {
			this.paraType = paraType;
		}
		public void setModularId(String modularId) {
			this.modularId = modularId;
		}
	}
}
