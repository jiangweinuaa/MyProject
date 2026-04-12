package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：
 * 服务说明：加工任务原材料查询
 * @author yuanyy 
 * @since  2019-12-12
 */
public class DCP_ProcessTaskMaterialQueryReq extends JsonBasicReq{
	
	private level1Elm request;
//	private String timestamp;
	
	public class level1Elm{
		
		private String processTaskNo;
		private String pTemplateNo;

		public String getProcessTaskNo() {
			return processTaskNo;
		}

		public void setProcessTaskNo(String processTaskNo) {
			this.processTaskNo = processTaskNo;
		}

		public String getpTemplateNo() {
			return pTemplateNo;
		}

		public void setpTemplateNo(String pTemplateNo) {
			this.pTemplateNo = pTemplateNo;
		}
		
	}
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

//	public String getTimestamp() {
//		return timestamp;
//	}
//
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}
	
}
