package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_MonitorCreate_Open
 * 服务说明：监控新增
 * @author jinzma 
 * @since  2020-03-20
 */
public class DCP_MonitorCreate_OpenReq extends JsonBasicReq {

	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String customerId;
		private String customerName;
		private String dcpVersion;
		private String shopQty;
		private String status;
		private String eId;

		private List<level2ElmWsErp> wsErp;
		private List<level2ElmWsDcp> wsDcp;
		private List<level2ElmService> service;
		private List<level2ElmJob> job;
		private List<level2ElmShopEDate> shopEDate;

		public String getCustomerId() {
			return customerId;
		}
		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}
		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}
		public String getDcpVersion() {
			return dcpVersion;
		}
		public void setDcpVersion(String dcpVersion) {
			this.dcpVersion = dcpVersion;
		}
		public String getShopQty() {
			return shopQty;
		}
		public void setShopQty(String shopQty) {
			this.shopQty = shopQty;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public List<level2ElmWsErp> getWsErp() {
			return wsErp;
		}
		public void setWsErp(List<level2ElmWsErp> wsErp) {
			this.wsErp = wsErp;
		}
		public List<level2ElmWsDcp> getWsDcp() {
			return wsDcp;
		}
		public void setWsDcp(List<level2ElmWsDcp> wsDcp) {
			this.wsDcp = wsDcp;
		}
		public List<level2ElmService> getService() {
			return service;
		}
		public void setService(List<level2ElmService> service) {
			this.service = service;
		}
		public List<level2ElmJob> getJob() {
			return job;
		}
		public void setJob(List<level2ElmJob> job) {
			this.job = job;
		}
		public List<level2ElmShopEDate> getShopEDate() {
			return shopEDate;
		}
		public void setShopEDate(List<level2ElmShopEDate> shopEDate) {
			this.shopEDate = shopEDate;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}

	}
	public class level2ElmWsErp
	{
		private String wsName;
		private String wsExplain;
		private String status;
		private String transQty;
		private String notTransQty;
		private List<level3ElmWsErpError>error;

		public String getWsName() {
			return wsName;
		}
		public void setWsName(String wsName) {
			this.wsName = wsName;
		}
		public String getWsExplain() {
			return wsExplain;
		}
		public void setWsExplain(String wsExplain) {
			this.wsExplain = wsExplain;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getTransQty() {
			return transQty;
		}
		public void setTransQty(String transQty) {
			this.transQty = transQty;
		}
		public String getNotTransQty() {
			return notTransQty;
		}
		public void setNotTransQty(String notTransQty) {
			this.notTransQty = notTransQty;
		}
		public List<level3ElmWsErpError> getError() {
			return error;
		}
		public void setError(List<level3ElmWsErpError> error) {
			this.error = error;
		}
	}
	public class level2ElmWsDcp
	{
		private String wsName;
		private String wsExplain;
		private String status;
		private String transQty;
		private String notTransQty;
		private List<level3ElmWsDcpError>error;

		public String getWsName() {
			return wsName;
		}
		public void setWsName(String wsName) {
			this.wsName = wsName;
		}
		public String getWsExplain() {
			return wsExplain;
		}
		public void setWsExplain(String wsExplain) {
			this.wsExplain = wsExplain;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getTransQty() {
			return transQty;
		}
		public void setTransQty(String transQty) {
			this.transQty = transQty;
		}
		public String getNotTransQty() {
			return notTransQty;
		}
		public void setNotTransQty(String notTransQty) {
			this.notTransQty = notTransQty;
		}
		public List<level3ElmWsDcpError> getError() {
			return error;
		}
		public void setError(List<level3ElmWsDcpError> error) {
			this.error = error;
		}

	}	
	public class level2ElmService
	{
		private String serviceId;
		private String serviceName;
		private String concurrencyQty;
		private String errorMsg;
		private String status;

		public String getServiceId() {
			return serviceId;
		}
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		public String getConcurrencyQty() {
			return concurrencyQty;
		}
		public void setConcurrencyQty(String concurrencyQty) {
			this.concurrencyQty = concurrencyQty;
		}
		public String getErrorMsg() {
			return errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	}
	public class level2ElmJob
	{
		private String jobName;
		private String jobDiscretion;
		private String errorMsg;
		private String status;

		public String getJobName() {
			return jobName;
		}
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}
		public String getJobDiscretion() {
			return jobDiscretion;
		}
		public void setJobDiscretion(String jobDiscretion) {
			this.jobDiscretion = jobDiscretion;
		}
		public String getErrorMsg() {
			return errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	}
	public class level2ElmShopEDate
	{
		private String shopId;
		private String shopName;
		private String eDate;
		private String errorMsg;
		private String status;

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String geteDate() {
			return eDate;
		}
		public void seteDate(String eDate) {
			this.eDate = eDate;
		}
		public String getErrorMsg() {
			return errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	}
	public class level3ElmWsErpError
	{
		private String item;
		private String shopId;
		private String shopName;
		private String docNo;
		private String errorCode;
		private String errorMsg;
		private String request;
		private String response;
		private String modifyDate;
		private String modifyTime;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getDocNo() {
			return docNo;
		}
		public void setDocNo(String docNo) {
			this.docNo = docNo;
		}
		public String getErrorCode() {
			return errorCode;
		}
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}
		public String getErrorMsg() {
			return errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public String getRequest() {
			return request;
		}
		public void setRequest(String request) {
			this.request = request;
		}
		public String getResponse() {
			return response;
		}
		public void setResponse(String response) {
			this.response = response;
		}
		public String getModifyDate() {
			return modifyDate;
		}
		public void setModifyDate(String modifyDate) {
			this.modifyDate = modifyDate;
		}
		public String getModifyTime() {
			return modifyTime;
		}
		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}


	}
	public class level3ElmWsDcpError
	{
		private String item;
		private String shopId;
		private String shopName;
		private String docNo;
		private String errorCode;
		private String errorMsg;
		private String request;
		private String response;
		private String modifyDate;
		private String modifyTime;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getDocNo() {
			return docNo;
		}
		public void setDocNo(String docNo) {
			this.docNo = docNo;
		}
		public String getErrorCode() {
			return errorCode;
		}
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}
		public String getErrorMsg() {
			return errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public String getRequest() {
			return request;
		}
		public void setRequest(String request) {
			this.request = request;
		}
		public String getResponse() {
			return response;
		}
		public void setResponse(String response) {
			this.response = response;
		}
		public String getModifyDate() {
			return modifyDate;
		}
		public void setModifyDate(String modifyDate) {
			this.modifyDate = modifyDate;
		}
		public String getModifyTime() {
			return modifyTime;
		}
		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}

	}



}
