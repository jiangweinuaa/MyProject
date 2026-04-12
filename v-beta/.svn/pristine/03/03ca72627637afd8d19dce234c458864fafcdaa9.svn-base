package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 基础设置列表查询
 * @author yuanyy 2020-03-04
 *
 */
public class DCP_BasicSetDetailReq extends JsonBasicReq {
//	private String timeStamp;
	private level1Elm request;
//	public String getTimeStamp() {
//		return timeStamp;
//	}
//	public void setTimeStamp(String timeStamp) {
//		this.timeStamp = timeStamp;
//	}
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{

		private String oEId;//这个字段规格上没有，放在这里是以防万一，PAD 调用时没有 token，也没传 公司别

		private String templateNo;
		public String getTemplateNo() {
			return templateNo;
		}
		public void setTemplateNo(String templateNo) {
			this.templateNo = templateNo;
		}
		public String getoEId() {
			return oEId;
		}
		public void setoEId(String oEId) {
			this.oEId = oEId;
		}

	}
}
