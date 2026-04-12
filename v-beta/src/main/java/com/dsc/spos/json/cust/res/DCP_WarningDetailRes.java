package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;


public class DCP_WarningDetailRes extends JsonBasicRes {

	private level1Elm datas;
	
	
	public level1Elm getDatas() {
		return datas;
	}


	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}


	public class level1Elm
	{
		private String billNo;
		private String billDate;
		private String billName;
		private String billType;
		private String companyId;
		private String companyName;
		private String shopId;
		private String shopName;
		private String channelId;
		private String channelName;
		private String employeeId;
		private String employeeName;
		private String departId;
		private String departName;
		
		private String warningType;
		private String warningItem;//order_inTime：每日%点后的零售单,order_value：每日单笔金额超过%元的零售单,order_period：每日%点至%点的零售单,point_sun：同一会员的每日交易单超过%笔,point_over：同一会员的每日总积分超过%分,point_close：每日闭店后的会员积分,card_sum：同一储值卡的每日交易数超过%笔,card_close：每日闭店后发生的储值卡交易
		private String warningItemDescription;
		private String startTime;
		private String endTime;
		private String orderQty;
		private String orderAmt;
		private String pointQty;
		private String pushTimeType;
		private String pushTime;
		private List<level2PushMan> pushManList;
		private String restrictShop;//枚举: limit：限用,noLimit：不限用
		private List<level2Shop> shopList;
		private String templateType;// orderTem：零售单,pointTem：会员积分,cardTem：储值卡
		private String templateTypeTitle;
		private String msgBegin;
		private String msgMiddle;
		private String msgEnd;
		private String linkUrl;
		private String batchNo;
		private String status;//枚举: -1：草稿,0：已撤审 （禁用）,100：已审核（启用）
		
		
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getBillDate() {
			return billDate;
		}
		public void setBillDate(String billDate) {
			this.billDate = billDate;
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
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getOrderQty() {
			return orderQty;
		}
		public void setOrderQty(String orderQty) {
			this.orderQty = orderQty;
		}
		public String getOrderAmt() {
			return orderAmt;
		}
		public void setOrderAmt(String orderAmt) {
			this.orderAmt = orderAmt;
		}
		public String getPointQty() {
			return pointQty;
		}
		public void setPointQty(String pointQty) {
			this.pointQty = pointQty;
		}
		public String getPushTimeType() {
			return pushTimeType;
		}
		public void setPushTimeType(String pushTimeType) {
			this.pushTimeType = pushTimeType;
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
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		public String getTemplateType() {
			return templateType;
		}
		public void setTemplateType(String templateType) {
			this.templateType = templateType;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getBillType() {
			return billType;
		}
		public void setBillType(String billType) {
			this.billType = billType;
		}
		public String getCompanyId() {
			return companyId;
		}
		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}
		public String getCompanyName() {
			return companyName;
		}
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
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
		public String getChannelId() {
			return channelId;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		public String getEmployeeId() {
			return employeeId;
		}
		public void setEmployeeId(String employeeId) {
			this.employeeId = employeeId;
		}
		public String getEmployeeName() {
			return employeeName;
		}
		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}
		public String getDepartId() {
			return departId;
		}
		public void setDepartId(String departId) {
			this.departId = departId;
		}
		public String getDepartName() {
			return departName;
		}
		public void setDepartName(String departName) {
			this.departName = departName;
		}
		public List<level2Shop> getShopList() {
			return shopList;
		}
		public void setShopList(List<level2Shop> shopList) {
			this.shopList = shopList;
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
	
	public class level2PushMan
	{
		private String serialNo;
		private String opNo;
		private String opName;
		private String mobilePhone;
		private String email;
		private String userId;//钉钉用户id
		private String userName;//钉钉用户名称
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
		public String getMobilePhone() {
			return mobilePhone;
		}
		public void setMobilePhone(String mobilePhone) {
			this.mobilePhone = mobilePhone;
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
		public List<level3PushWay> getPushWayList() {
			return pushWayList;
		}
		public void setPushWayList(List<level3PushWay> pushWayList) {
			this.pushWayList = pushWayList;
		}
			
		
		
	}
	
	public class level2Shop
	{
		private String serialNo;
		private String shopId;
		private String shopName;
		public String getSerialNo() {
			return serialNo;
		}
		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
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
		
				
	}
	
	public class level3PushWay
	{
		private String serialNo;
		private String pushWay;//枚举: PHONE：手机,EMAIL：邮箱,DING：钉钉
		private String pushWayName;
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
		public String getPushWayName() {
			return pushWayName;
		}
		public void setPushWayName(String pushWayName) {
			this.pushWayName = pushWayName;
		}
		
		
	}
	
	
	
	
	
}
