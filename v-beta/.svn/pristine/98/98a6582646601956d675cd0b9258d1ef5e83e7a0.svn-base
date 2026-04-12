package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：MainErrorInfoGetDCP
 * 服务说明：首页异常信息查询
 * @author jinzma 
 * @since  2019-05-20
 */
public class DCP_MainErrorInfoQueryReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String dataType ;
		private String jobName ;
		private String wsType ;
		private String serviceName;

		public String getDataType() {
			return dataType;
		}
		public void setDataType(String dataType) {
			this.dataType = dataType;
		}
		public String getWsType() {
			return wsType;
		}
		public void setWsType(String wsType) {
			this.wsType = wsType;
		}
		public String getJobName() {
			return jobName;
		}
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
	}
}
