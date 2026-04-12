package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 模板参数设置详情查询
 * @author 2020-05-29
 *
 */
public class DCP_ParaTemplateRetrieveReq extends JsonBasicReq {
	
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
		private String templateId;
		public String getParaShop() {
			return paraShop;
		}
		public String getParaMachine() {
			return paraMachine;
		}
		public String getModularId() {
			return modularId;
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
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		
		
	}
}
