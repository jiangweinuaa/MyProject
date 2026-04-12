package com.dsc.spos.hll.api.response;

/**
 * 账单支付信息
 * @author LN 08546
 */
public class HllPayDetail {
	
	
	/**
	 * 记录修改时间【yyyyMMddHHmmss，默认值0】
	 */
	private String actionTime;
	
	/**
	 * 品牌ID
	 */
	private String brandID;
	
	/**
	 * 结账人员
	 */
	private String checkoutBy;
	
	/**
	 * 创建人员
	 */
	private String createBy;
	
	/**
	 * 记录创建时间【yyyyMMddHHmmss，默认值0】
	 */
	private String createTime;
	
	/**
	 * 贷方发生额，例如海鲜收入、酒水收入
	 */
	private String creditAmount;
	
	/**
	 * 券支付面值金额。原始数据为Decimal
	 */
	private String debitAmount;
	
	/**
	 * 借方发生额，账单结算支付，例如人民币、支票
	 */
	private String debitAmountGiftTotal;
	
	/**
	 * 设备名称
	 */
	private String deviceName;
	
	/**
	 * 代金券券号列表，多个间用逗号隔开
	 */
	private String giftItemNoLst;
	
	/**
	 * 所属集团ID
	 */
	private String groupID;
	
	/**
	 * 费用是否计入实收
	 */
	private String isFeeJoinReceived;
	
	/**
	 * 是否参与积分计算
	 */
	private String isIncludeScore;
	
	/**
	 * 是否记入实收
	 */
	private String isJoinReceived;
	
	/**
	 * 是否实物凭证
	 */
	private String isPhysicalEvidence;
	
	/**
	 * 涉及到会员相关的科目，记录会员卡ID
	 */
	private String memberCardID;
	
	/**
	 * 30：作废 40：已完成
	 */
	private String orderStatus;
	
	/**
	 * 备注
	 */
	private String payRemark;
	
	/**
	 * 总的优惠: paySubjectDiscountAmount + paySubjectFeeAmount(如果费用计入优惠)
	 */
	private String paySubjectAllDiscountAmount;
	
	/**
	 * 支付科目编码，对应基本档-获取店铺科目列表返回的subjectCode
	 */
	private String paySubjectCode;
	
	/**
	 * 记录支付类型的次数,现金与银联卡多次支付按一次统计;微信,支付宝等其他线上支付按实际支付次数统计 账单内多条同一种支付方式 支付次数记录在第一条
	 */
	private String paySubjectCount;
	
	/**
	 * 支付折扣对应金额
	 */
	private String paySubjectDiscountAmount;
	
	/**
	 * 支付费用对应金额
	 */
	private String paySubjectFeeAmount;
	
	/**
	 * 支付科目分组名称，未定义分组名称，则此处为科目名称
	 */
	private String paySubjectGroupName;
	
	/**
	 * 支付科目Key，对应基本档-获取店铺科目列表返回的subjectKey
	 */
	private String paySubjectKey;
	
	/**
	 * 支付科目名，对应基本档-获取店铺科目列表返回的subjectName
	 */
	private String paySubjectName;
	
	/**
	 * 支付科目费率百分比，支持银联POS、O2O支付费率、团购佣金等使用情况
	 */
	private String paySubjectRate;
	
	/**
	 * 借贷方发生的实收金额，从相关菜品实收金额进行汇总
	 */
	private String paySubjectRealAmount;
		
	/**
	 * 支付实得对应金额
	 */
	private String paySubjectReceivedAmount;
	
	/**
	 * 支付交易关联单号
	 */
	private String payTransNo;
	
	/**
	 * 营销类型：10基础营销，20会员，30券信息
	 */
	private String programType;
	
	/**
	 * 设计营销活动的科目，记录营销活动ID
	 */
	private String promotionID;
	
	/**
	 * 报表统计日期
	 */
	private String reportDate;
	
	/**
	 * 餐饮系统订单Key
	 */
	private String saasOrderKey;
	
	/**
	 * 服务器MAC地址
	 */
	private String serverMAC;
	
	/**
	 * 店铺ID
	 */
	private String shopID;
	
	/**
	 * 店铺名称
	 */
	private String shopName;
		
	/**
	 * 营销编码
	 */
	private String promotionCode;
	
	/**
	 * 用户自定义科目编码	
	 */
	private String subjectnumber;

	public String getActionTime() {
		return actionTime;
	}

	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}

	public String getBrandID() {
		return brandID;
	}

	public void setBrandID(String brandID) {
		this.brandID = brandID;
	}

	public String getCheckoutBy() {
		return checkoutBy;
	}

	public void setCheckoutBy(String checkoutBy) {
		this.checkoutBy = checkoutBy;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(String debitAmount) {
		this.debitAmount = debitAmount;
	}

	public String getDebitAmountGiftTotal() {
		return debitAmountGiftTotal;
	}

	public void setDebitAmountGiftTotal(String debitAmountGiftTotal) {
		this.debitAmountGiftTotal = debitAmountGiftTotal;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getGiftItemNoLst() {
		return giftItemNoLst;
	}

	public void setGiftItemNoLst(String giftItemNoLst) {
		this.giftItemNoLst = giftItemNoLst;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getIsFeeJoinReceived() {
		return isFeeJoinReceived;
	}

	public void setIsFeeJoinReceived(String isFeeJoinReceived) {
		this.isFeeJoinReceived = isFeeJoinReceived;
	}

	public String getIsIncludeScore() {
		return isIncludeScore;
	}

	public void setIsIncludeScore(String isIncludeScore) {
		this.isIncludeScore = isIncludeScore;
	}

	public String getIsJoinReceived() {
		return isJoinReceived;
	}

	public void setIsJoinReceived(String isJoinReceived) {
		this.isJoinReceived = isJoinReceived;
	}

	public String getIsPhysicalEvidence() {
		return isPhysicalEvidence;
	}

	public void setIsPhysicalEvidence(String isPhysicalEvidence) {
		this.isPhysicalEvidence = isPhysicalEvidence;
	}

	public String getMemberCardID() {
		return memberCardID;
	}

	public void setMemberCardID(String memberCardID) {
		this.memberCardID = memberCardID;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPayRemark() {
		return payRemark;
	}

	public void setPayRemark(String payRemark) {
		this.payRemark = payRemark;
	}

	public String getPaySubjectAllDiscountAmount() {
		return paySubjectAllDiscountAmount;
	}

	public void setPaySubjectAllDiscountAmount(String paySubjectAllDiscountAmount) {
		this.paySubjectAllDiscountAmount = paySubjectAllDiscountAmount;
	}

	public String getPaySubjectCode() {
		return paySubjectCode;
	}

	public void setPaySubjectCode(String paySubjectCode) {
		this.paySubjectCode = paySubjectCode;
	}

	public String getPaySubjectCount() {
		return paySubjectCount;
	}

	public void setPaySubjectCount(String paySubjectCount) {
		this.paySubjectCount = paySubjectCount;
	}

	public String getPaySubjectDiscountAmount() {
		return paySubjectDiscountAmount;
	}

	public void setPaySubjectDiscountAmount(String paySubjectDiscountAmount) {
		this.paySubjectDiscountAmount = paySubjectDiscountAmount;
	}

	public String getPaySubjectFeeAmount() {
		return paySubjectFeeAmount;
	}

	public void setPaySubjectFeeAmount(String paySubjectFeeAmount) {
		this.paySubjectFeeAmount = paySubjectFeeAmount;
	}

	public String getPaySubjectGroupName() {
		return paySubjectGroupName;
	}

	public void setPaySubjectGroupName(String paySubjectGroupName) {
		this.paySubjectGroupName = paySubjectGroupName;
	}

	public String getPaySubjectKey() {
		return paySubjectKey;
	}

	public void setPaySubjectKey(String paySubjectKey) {
		this.paySubjectKey = paySubjectKey;
	}

	public String getPaySubjectName() {
		return paySubjectName;
	}

	public void setPaySubjectName(String paySubjectName) {
		this.paySubjectName = paySubjectName;
	}

	public String getPaySubjectRate() {
		return paySubjectRate;
	}

	public void setPaySubjectRate(String paySubjectRate) {
		this.paySubjectRate = paySubjectRate;
	}

	public String getPaySubjectRealAmount() {
		return paySubjectRealAmount;
	}

	public void setPaySubjectRealAmount(String paySubjectRealAmount) {
		this.paySubjectRealAmount = paySubjectRealAmount;
	}

	public String getPaySubjectReceivedAmount() {
		return paySubjectReceivedAmount;
	}

	public void setPaySubjectReceivedAmount(String paySubjectReceivedAmount) {
		this.paySubjectReceivedAmount = paySubjectReceivedAmount;
	}

	public String getPayTransNo() {
		return payTransNo;
	}

	public void setPayTransNo(String payTransNo) {
		this.payTransNo = payTransNo;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getPromotionID() {
		return promotionID;
	}

	public void setPromotionID(String promotionID) {
		this.promotionID = promotionID;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getSaasOrderKey() {
		return saasOrderKey;
	}

	public void setSaasOrderKey(String saasOrderKey) {
		this.saasOrderKey = saasOrderKey;
	}

	public String getServerMAC() {
		return serverMAC;
	}

	public void setServerMAC(String serverMAC) {
		this.serverMAC = serverMAC;
	}

	public String getShopID() {
		return shopID;
	}

	public void setShopID(String shopID) {
		this.shopID = shopID;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getSubjectnumber() {
		return subjectnumber;
	}

	public void setSubjectnumber(String subjectnumber) {
		this.subjectnumber = subjectnumber;
	}

}
