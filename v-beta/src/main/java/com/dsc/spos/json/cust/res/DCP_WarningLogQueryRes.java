package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_WarningLogQueryRes extends JsonRes {

	private List<level1Elm> datas;
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm
	{
		private String batchNo;//主键ID
		private String billNo;
		private String billName;		
		private String warningType;//order：零售单,point：会员积分,card：储值卡
		private String warningItem;//order_inTime：每日%点后的零售单,order_value：每日单笔金额超过%元的零售单,order_period：每日%点至%点的零售单,point_sun：同一会员的每日交易单超过%笔,point_over：同一会员的每日总积分超过%分,point_close：每日闭店后的会员积分,card_sum：同一储值卡的每日交易数超过%笔,card_close：每日闭店后发生的储值卡交易
		private String warningItemDescription;			
		private String pushTimeType;
		private String pushTimeTypeDescription;
		private String pushTime;
		private String pushMan;
		private List<level2PushMan> pushManList;
		private String templateType;//
		private String templateTypeTitle;//
		private String msgBegin;//
		private String msgMiddle;//
		private String msgEnd;
		private String linkUrl;
		
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getBillName() {
			return billName;
		}
		public void setBillName(String billName) {
			this.billName = billName;
		}
		public String getWarningType() {
			return warningType;
		}
		public void setWarningType(String warningType) {
			this.warningType = warningType;
		}
		public String getWarningItem() {
			return warningItem;
		}
		public void setWarningItem(String warningItem) {
			this.warningItem = warningItem;
		}
		public String getWarningItemDescription() {
			return warningItemDescription;
		}
		public void setWarningItemDescription(String warningItemDescription) {
			this.warningItemDescription = warningItemDescription;
		}
		public String getBatchNo() {
			return batchNo;
		}
		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}
		public String getPushTimeType() {
			return pushTimeType;
		}
		public void setPushTimeType(String pushTimeType) {
			this.pushTimeType = pushTimeType;
		}
		public String getPushTimeTypeDescription() {
			return pushTimeTypeDescription;
		}
		public void setPushTimeTypeDescription(String pushTimeTypeDescription) {
			this.pushTimeTypeDescription = pushTimeTypeDescription;
		}
		public String getPushTime() {
			return pushTime;
		}
		public void setPushTime(String pushTime) {
			this.pushTime = pushTime;
		}
		public List<level2PushMan> getPushManList() {
			return pushManList;
		}
		public void setPushManList(List<level2PushMan> pushManList) {
			this.pushManList = pushManList;
		}
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
		public String getPushMan() {
			return pushMan;
		}
		public void setPushMan(String pushMan) {
			this.pushMan = pushMan;
		}
	
		
	}
	
	public class level2PushMan
	{
		private String serialNo;
		private String opNo;
		private String opName;
		private List<level3PushWay> pushWayList;
		public String getSerialNo() {
			return serialNo;
		}
		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}
		public String getOpNo() {
			return opNo;
		}
		public void setOpNo(String opNo) {
			this.opNo = opNo;
		}
		public String getOpName() {
			return opName;
		}
		public void setOpName(String opName) {
			this.opName = opName;
		}
		public List<level3PushWay> getPushWayList() {
			return pushWayList;
		}
		public void setPushWayList(List<level3PushWay> pushWayList) {
			this.pushWayList = pushWayList;
		}
		
		
		
	}
	
	
	
	public class level3PushWay
	{
		private String serialNo;
		private String pushWay;//枚举: PHONE：手机,EMAIL：邮箱,DING：钉钉
		private String email;
		private String userId;
		private String userName;
		private String successFlag;//true：成功,false：失败
		private String failReason;
		public String getSerialNo() {
			return serialNo;
		}
		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}
		public String getPushWay() {
			return pushWay;
		}
		public void setPushWay(String pushWay) {
			this.pushWay = pushWay;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getSuccessFlag() {
			return successFlag;
		}
		public void setSuccessFlag(String successFlag) {
			this.successFlag = successFlag;
		}
		public String getFailReason() {
			return failReason;
		}
		public void setFailReason(String failReason) {
			this.failReason = failReason;
		}
		
		
	}
	
}
