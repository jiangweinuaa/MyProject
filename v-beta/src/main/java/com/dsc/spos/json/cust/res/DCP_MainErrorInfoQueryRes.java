package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：MainErrorInfoGetDCP
 * 服务说明：首页异常信息查询
 * @author jinzma 
 * @since  2019-05-20
 */
public class DCP_MainErrorInfoQueryRes extends JsonRes {

	private List<level1Elm_WsError> wsError;

	public List<level1Elm_WsError> getWsError() {
		return wsError;
	}

	public void setWsError(List<level1Elm_WsError> wsError) {
		this.wsError = wsError;
	}

	public class level1Elm_WsError
	{	
		private String item;
		private String shopId;
		private String shopName;
		private String docNo;
		private String errorMsg;
		private String requestXML;
		private String responseXML;
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
		public String getErrorMsg() {
			return errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public String getRequestXML() {
			return requestXML;
		}
		public void setRequestXML(String requestXML) {
			this.requestXML = requestXML;
		}
		public String getResponseXML() {
			return responseXML;
		}
		public void setResponseXML(String responseXML) {
			this.responseXML = responseXML;
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
