package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_WarningUpdateReq extends JsonBasicReq {

	private level1Elm request;
	public class level1Elm
	{
		private String billDate;
		private String billNo;
		private String companyId;
		private String shopId;
		private String channelId;
		private String employeeId;
		private String departId;
		private String billName;
		private String warningType;
		private String warningItem;//order_inTime：每日%点后的零售单,order_value：每日单笔金额超过%元的零售单,order_period：每日%点至%点的零售单,point_sun：同一会员的每日交易单超过%笔,point_over：同一会员的每日总积分超过%分,point_close：每日闭店后的会员积分,card_sum：同一储值卡的每日交易数超过%笔,card_close：每日闭店后发生的储值卡交易
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
		private String status;//枚举: -1：草稿,0：已撤审 （禁用）,100：已审核（启用）
		private String lastmodiopid;//备注: YYYY-MM-DD HH:MM:SS
		private String lastmoditime;
		public String getBillDate() {
			return billDate;
		}
		public void setBillDate(String billDate) {
			this.billDate = billDate;
		}
		
		public String getCompanyId() {
			return companyId;
		}
		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}
		public String getShopId() {
				return shopId;
			}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getChannelId() {
			return channelId;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getEmployeeId() {
			return employeeId;
		}
		public void setEmployeeId(String employeeId) {
			this.employeeId = employeeId;
		}
		public String getDepartId() {
			return departId;
		}
		public void setDepartId(String departId) {
			this.departId = departId;
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
		public List<level2Shop> getShopList() {
		return shopList;
		}
		public void setShopList(List<level2Shop> shopList) {
			this.shopList = shopList;
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
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getLastmodiopid() {
			return lastmodiopid;
		}
		public void setLastmodiopid(String lastmodiopid) {
			this.lastmodiopid = lastmodiopid;
		}
		public String getLastmoditime() {
			return lastmoditime;
		}
		public void setLastmoditime(String lastmoditime) {
			this.lastmoditime = lastmoditime;
		}
		
		
	}
	
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	public class level2PushMan
	{
		private String serialNo;
		private String opNo;
		private String mobilePhone;
		private String email;
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
				
	}
	
	public class level3PushWay
	{
		private String serialNo;
		private String pushWay;//枚举: PHONE：手机,EMAIL：邮箱,DING：钉钉
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
		
		
	}
	
}
