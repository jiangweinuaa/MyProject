package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：MainErrorInfoDeleteDCP
 * 服务说明：首页异常资料清理
 * @author jinzma 
 * @since  2019-10-29
 */
public class DCP_MainErrorInfoDeleteReq extends JsonBasicReq {

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
		private String oShopId ;
		private String docNo ;
		private String docType;
		private String serviceName;

		public String getDataType() {
			return dataType;
		}
		public void setDataType(String dataType) {
			this.dataType = dataType;
		}
		public String getoShopId() {
			return oShopId;
		}
		public void setoShopId(String oShopId) {
			this.oShopId = oShopId;
		}
		public String getDocNo() {
			return docNo;
		}
		public void setDocNo(String docNo) {
			this.docNo = docNo;
		}
		public String getJobName() {
			return jobName;
		}
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
	}

}
