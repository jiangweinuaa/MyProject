package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 订单状态查询
 * @author Huawei
 *
 */
public class DCP_OrderStatusQuery_OpenReq extends JsonBasicReq {
	private level1Elm request;
//	private String timestamp;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
//	public String getTimestamp() {
//		return timestamp;
//	}
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

	public class level1Elm
	{
		private String eId;
		private String appId;
		private String outSelID;  // 外部查询ID
		private String docType;   //单据类型 1.饿了么 2.美团外卖 3.微商城 4.云POS 5.总部  90.商户平台 12、外卖点餐
		private String beginDate;
		private String endDate;
		public String getAppId() {
			return appId;
		}
		public String getOutSelID() {
			return outSelID;
		}
		public String getBeginDate() {
			return beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public void setOutSelID(String outSelID) {
			this.outSelID = outSelID;
		}
		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}

	}

}
