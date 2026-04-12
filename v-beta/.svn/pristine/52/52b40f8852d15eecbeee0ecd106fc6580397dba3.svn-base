package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_WarningQueryReq extends JsonBasicReq {

	private level1Elm request;
	public class level1Elm
	{		
		private String warningType;// null：全部,order：零售单,point：会员积分,card：储值卡
		private String warningStatus;//null：全部,valid：执行中,inValid：暂停中
		private String billNo;
		private String searchString;//根据单据编号或名称模糊搜索
		
		public String getWarningType() {
			return warningType;
		}
		public void setWarningType(String warningType) {
			this.warningType = warningType;
		}
		public String getWarningStatus() {
			return warningStatus;
		}
		public void setWarningStatus(String warningStatus) {
			this.warningStatus = warningStatus;
		}
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getSearchString() {
			return searchString;
		}
		public void setSearchString(String searchString) {
			this.searchString = searchString;
		}
				
	}
	
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

}
