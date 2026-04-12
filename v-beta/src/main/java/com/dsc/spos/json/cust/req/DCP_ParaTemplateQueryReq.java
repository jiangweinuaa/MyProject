package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 模板参数设置列表查询
 * @author 2020-05-29
 *
 */
public class DCP_ParaTemplateQueryReq extends JsonBasicReq {
	
	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String paraShop;
		private String paraMachine;
		
		private String modularId;
		private String paraItem;
		public String getParaShop() {
			return paraShop;
		}
		public String getParaMachine() {
			return paraMachine;
		}
		public String getModularId() {
			return modularId;
		}
		public String getParaItem() {
			return paraItem;
		}
		public void setParaShop(String paraShop) {
			this.paraShop = paraShop;
		}
		public void setParaMachine(String paraMachine) {
			this.paraMachine = paraMachine;
		}
		public void setModularId(String modularId) {
			this.modularId = modularId;
		}
		public void setParaItem(String paraItem) {
			this.paraItem = paraItem;
		}
		
		
	}
}
