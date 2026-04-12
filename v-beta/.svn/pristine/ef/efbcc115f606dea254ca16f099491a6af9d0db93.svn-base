package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_WarningQueryRes extends JsonRes {

	private List<level1Elm> datas;
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm
	{
		private String billNo;
		private String billDate;
		private String billName;
		/*private String companyId;
		private String shopId;
		private String channelId;
		private String employeeId;
		private String departId;*/
		
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
		private String warningStatus;//status=0 inValid：暂停中  status=100 valid：执行中
		private String restrictShop;//枚举: limit：限用,noLimit：不限用
		private String templateType;// orderTem：零售单,pointTem：会员积分,cardTem：储值卡
		private String status;//枚举: -1：草稿,0：已撤审 （禁用）,100：已审核（启用）
		private String createopid;//备注: YYYY-MM-DD HH:MM:SS
		private String createTime;
		private String lastmodiopid;
		private String lastmoditime;//YYYY-MM-DD HH:MM:SS
		
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
		public String getWarningStatus() {
			return warningStatus;
		}
		public void setWarningStatus(String warningStatus) {
			this.warningStatus = warningStatus;
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
		public String getCreateopid() {
			return createopid;
		}
		public void setCreateopid(String createopid) {
			this.createopid = createopid;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
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
	
	public class level2PushMan
	{
		private String serialNo;
		private String opNo;
		private String opName;
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
		
		
		
	}
	
	
	
	
	
}
