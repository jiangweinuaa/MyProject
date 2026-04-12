package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_WarningMsgTemplateQueryReq extends JsonBasicReq {

	private level1Elm request;
	public class level1Elm
	{		
		private String templateType;// orderTem：零售单,pointTem：会员积分,cardTem：储值卡

		public String getTemplateType() {
			return templateType;
		}
	
		public void setTemplateType(String templateType) {
			this.templateType = templateType;
		}
		
				
	}
	
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

}
