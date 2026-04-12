package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_MonitorDetailQuery
 * 服务说明：监控明细查询
 * @author jinzma 
 * @since  2020-03-18
 */
public class DCP_MonitorDetailQueryRes extends JsonRes {
	private String contact;
	private String telephone;
	private String dcpConnInfo;
	private String dcpUrl;
	private String lastTrantime;

	private List<level1ElmWsErp> wsErp;
	private List<level1ElmWsDcp> wsDcp;
	private List<level1ElmService> service;
	private List<level1ElmJob> job;
	private List<level1ElmShopEDate> shopEDate;

	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getDcpConnInfo() {
		return dcpConnInfo;
	}
	public void setDcpConnInfo(String dcpConnInfo) {
		this.dcpConnInfo = dcpConnInfo;
	}
	public String getDcpUrl() {
		return dcpUrl;
	}
	public void setDcpUrl(String dcpUrl) {
		this.dcpUrl = dcpUrl;
	}
	public String getLastTrantime() {
		return lastTrantime;
	}
	public void setLastTrantime(String lastTrantime) {
		this.lastTrantime = lastTrantime;
	}
	public List<level1ElmWsErp> getWsErp() {
		return wsErp;
	}
	public void setWsErp(List<level1ElmWsErp> wsErp) {
		this.wsErp = wsErp;
	}
	public List<level1ElmWsDcp> getWsDcp() {
		return wsDcp;
	}
	public void setWsDcp(List<level1ElmWsDcp> wsDcp) {
		this.wsDcp = wsDcp;
	}
	public List<level1ElmService> getService() {
		return service;
	}
	public void setService(List<level1ElmService> service) {
		this.service = service;
	}
	public List<level1ElmJob> getJob() {
		return job;
	}
	public void setJob(List<level1ElmJob> job) {
		this.job = job;
	}
	public List<level1ElmShopEDate> getShopEDate() {
		return shopEDate;
	}
	public void setShopEDate(List<level1ElmShopEDate> shopEDate) {
		this.shopEDate = shopEDate;
	}


	public class level1ElmWsErp
	{
		private String item;
		private String wsName;
		private String wsExplain;
		private String status;
		private String transQty;
		private String notTransQty;
		private List<level2ElmWsErpError>error;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
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
		public List<level2ElmWsErpError> getError() {
			return error;
		}
		public void setError(List<level2ElmWsErpError> error) {
			this.error = error;
		}
	}
	public class level1ElmWsDcp
	{
		private String item;
		private String wsName;
		private String wsExplain;
		private String status;
		private String transQty;
		private String notTransQty;
		private List<level2ElmWsDcpError>error;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
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
		public List<level2ElmWsDcpError> getError() {
			return error;
		}
		public void setError(List<level2ElmWsDcpError> error) {
			this.error = error;
		}

	}	
	public class level1ElmService
	{
		private String item;
		private String serviceId;
		private String serviceName;
		private String concurrencyQty;
		private String errorMsg;
		private String status;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
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
	public class level1ElmJob
	{
		private String item;
		private String jobName;
		private String jobDiscretion;
		private String errorMsg;
		private String status;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
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
	public class level1ElmShopEDate
	{
		private String item;
		private String shopId;
		private String shopName;
		private String eDate;
		private String errorMsg;
		private String status;
		
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
	public class level2ElmWsErpError
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
	public class level2ElmWsDcpError
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
