package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_WarningMsgTemplateQueryRes extends JsonRes {

	private level1Elm datas;
	
	public level1Elm getDatas() {
		return datas;
	}

	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		
		private String templateType;//orderTem：零售单,pointTem：会员积分,cardTem：储值卡
		private String templateTypeTitle;
		private String msgBegin;
		private String msgMiddle;
		private String msgEnd;
		private String linkUrl;
		private String batchNo;
		
		public String getTemplateType() {
			return templateType;
		}
		public void setTemplateType(String templateType) {
			this.templateType = templateType;
		}
		public String getTemplateTypeTitle() {
			return templateTypeTitle;
		}
		public void setTemplateTypeTitle(String templateTypeTitle) {
			this.templateTypeTitle = templateTypeTitle;
		}
		public String getMsgBegin() {
			return msgBegin;
		}
		public void setMsgBegin(String msgBegin) {
			this.msgBegin = msgBegin;
		}
		public String getMsgMiddle() {
			return msgMiddle;
		}
		public void setMsgMiddle(String msgMiddle) {
			this.msgMiddle = msgMiddle;
		}
		public String getMsgEnd() {
			return msgEnd;
		}
		public void setMsgEnd(String msgEnd) {
			this.msgEnd = msgEnd;
		}
		public String getLinkUrl() {
			return linkUrl;
		}
		public void setLinkUrl(String linkUrl) {
			this.linkUrl = linkUrl;
		}
		public String getBatchNo() {
			return batchNo;
		}
		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}
	
		
		
		
	}
	
	
}
