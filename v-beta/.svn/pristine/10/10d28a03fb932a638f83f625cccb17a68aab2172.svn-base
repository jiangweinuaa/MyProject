package com.dsc.spos.hll.api.response;

import java.util.List;

/**
 * 账单主信息
 * @author LN 08546
 */
public class HllOrderDetail {

	public HllOrderDetail() {
		
	}
	
	/**
	 * 账单中菜品信息详情
	 */
	private List<HllFoodDetail> foodDetail;
	
	/**
	 * 账单中支付信息详情
	 */
	private List<HllPayDetail> payDetail;
	
	private List<HllPromotionDetail> promotionDetail;

	/**
	 * 记录修改时间【yyyyMMddHHmmss，默认值0】
	 */
	private String actionTime;
	
	/**
	 * 账单异常操作标识
	 */
	private String alertFlagLst;
	
	/**
	 * 所属区域名称
	 */
	private String areaName;
	
	/**
	 * 品牌ID
	 */
	private String brandID;
	
	/**
	 * 会员卡Key
	 */
	private String cardKey;
	
	/**
	 * 会员卡号
	 */
	private String cardNo;
	
	
	/**
	 * 会员卡交易后资金信息
	 */
	private String cardTransAfterRemark;
	
	/**
	 * 会员卡交易ID
	 */
	private String cardTransID;
	
	/**
	 * 渠道Key
	 */
	private String channelKey;
	
	
	/**
	 * 订单来源渠道名称
	 */
	private String channelName;
	
	/**
	 * 渠道订单Key
	 */
	private String channelOrderKey;
	
	/**
	 * 第三方订单号
	 */
	private String channelOrderKeyTP;
	
	/**
	 * 渠道订单号
	 */
	private String channelOrderNo;
	
	/**
	 * 渠道订单时间【yyyyMMddHHmm，默认值空字符串】
	 */
	private String channelOrderTime;
	
	/**
	 * 渠道用户ID
	 */
	private String channelUserID;
	
	/**
	 * 渠道用户头像路径
	 */
	private String channelUserImage;
	
	/**
	 * 渠道用户Key
	 */
	private String channelUserKey;
	
	/**
	 * 结账人员
	 */
	private String checkoutBy;
	
	/**
	 * 结账时间（yyyyMMddHHssnn)【yyyyMMddHHmmss，默认值0】
	 */
	private String checkoutTime;
	
	/**
	 * 创建人员（开台人员）
	 */
	private String createBy;
	
	/**
	 * 记录创建时间【yyyyMMddHHmmss，默认值0】
	 */
	private String createTime;
	
	/**
	 * 会员姓名
	 */
	private String customerName;
	
	/**
	 * 设备编号
	 */
	private String deviceCode;
	
	/**
	 * 设备所属的站点分组(档口)ID
	 */
	private String deviceGroupID;
	
	/**
	 * 设备所属的站点分组(档口)名称
	 */
	private String deviceGroupName;
	
	/**
	 * 设备Key
	 */
	private String deviceKey;
	
	/**
	 * 设备名称
	 */
	private String deviceName;
	
	/**
	 * 账单打折人员
	 */
	private String discountBy;
	
	/**
	 * 打折范围 0：部分打折 1：全单打折 2：分类折扣
	 */
	private String discountRange;
	
	/**
	 * 折扣率。原始数据为Decimal，为保证数据精度，以String传输。
	 */
	private String discountRate;
	
	/**
	 * 折扣方案Key
	 */
	private String discountWayKey;
	
	/**
	 * 账单折扣优惠方案名
	 */
	private String discountWayName;
	
	/**
	 * 反结账次数
	 */
	private String FJZCount;
	
	/**
	 * 账单菜品明细警惕
	 */
	private String foodAlert;
	
	/**
	 * 菜品金额合计。原始数据为Decimal，为保证数据精度，以String传输
	 */
	private String foodAmount;
	
	/**
	 * 菜品条目数
	 */
	private String foodCount;
	
	/**
	 * 账单对应菜品名称，多个间用空格隔开
	 */
	private String foodNameDetail;
	
	/**
	 * 店铺所属集团ID
	 */
	private String groupID;
	
	/**
	 * 开票金额
	 */
	private String invoiceAmount;
		
	/**
	 * 开票人
	 */
	private String invoiceBy;
	
	/**
	 * 税额
	 */
	private String invoiceTaxAmount;
	
	/**
	 * 纳税人识别码（税务登记证上15或者18或者29位的数字）
	 */
	private String invoiceTaxpayerIdentCode;
	
	/**
	 * 税率 通常为 0.03 或者 0.06 或者 0.17
	 */
	private String invoiceTaxRate;
	
	/**
	 * 发票抬头
	 */
	private String invoiceTitle;
	
	/**
	 * 是否由登录者创建的订单
	 */
	private String isCreatedByLoginUser;
	
	/**
	 * 是否是测试账单 0：不是（默认） 1：是
	 */
	private String isTestOrder;
	
	/**
	 * 是否执行会员价 0：非会员价 1：会员价
	 */
	private String isVipPrice;
	
	/**
	 * 帐单已锁（如果此字段为空，则没有锁定，如果不为空表示帐单为锁定，通常这里是锁定的人员工号和姓名），锁定的账单不能除锁定人外，别人不能进行菜品及结账相关操作。
	 */
	private String lockedFlag;
	
	/**
	 * 修改订单日志
	 */
	private String modifyOrderLog;
	
	/**
	 * 抹零规则 0:不抹零 1:四舍五入到角 2:向上抹零到角 3:向下抹零到角 4:四舍五入到元 5:向上抹零到元 6:向下抹零到元
	 */
	private String moneyWipeZeroType;
		
	/**
	 * 账单数据签名字符串订单类型标识 WS_YD：网上预订 WS_SC：网上闪吃 WS_WM：网上外卖 WS_ZT：网上自提 WS_DN：堂食自助
	 */
	private String netOrderTypeCode;
	
	/**
	 * 账单数据签名字符串
	 */
	private String orderMD5;
	
	/**
	 * 订单状态 10：存单 20：已落单 40：已结账 30：废单,废单的相关菜品及结算明细不会出现在统计报表中。另外单独提供废单列表、存单列表，可以查看详情
	 */
	private String orderStatus;
	
	/**
	 * 订单类型（0：堂食 20：外卖 21：自提）
	 */
	private String orderSubType;
	
	/**
	 * 订单实收金额
	 */
	private String paidAmount;
	
	/**
	 * 账单支付科目明细
	 */
	private String payAlert;
	
	/**
	 * 消费人数
	 */
	private String person;
	
	/**
	 * 优惠金额
	 */
	private String promotionAmount;
	
	/**
	 * 优惠描述
	 */
	private String promotionDesc;
	
	/**
	 * 报表统计日期，门店增加数据清机结转时间设定，该时间为次日小时数（1~12），默认为1，即次日凌晨1点【yyyyMMdd，默认值0】
	 */
	private String reportDate;
	
	/**
	 * 账单审核人
	 */
	private String reviewBy;
	
	/**
	 * 账单审核时间【yyyyMMddHHmmss，默认值0】
	 */
	private String reviewTime;
	
	/**
	 * 按站点设备的单号DeviceCode+设备流水号
	 */
	private String saasDeviceOrderNo;
	
	/**
	 * 餐饮系统订单Key(YYYYMMDD-ID-日流水号）
	 */
	private String saasOrderKey;
	
	/**
	 * 日流水号
	 */
	private String saasOrderNo;
	
	/**
	 * 账单备注（通常在开台时输入）
	 */
	private String saasOrderRemark;
	
	/**
	 * 赠券金额
	 */
	private String sendCouponAmount;
	
	/**
	 * 赠券说明
	 */
	private String sendCouponRemark;
	
	/**
	 * 赠菜金额合计
	 */
	private String sendFoodAmount;
	
	/**
	 * 服务器MAC地址
	 */
	private String serverMAC;
	
	/**
	 * 收银班次
	 */
	private String shiftName;
	
	/**
	 * 交班时间（yyyyMMddHHmmss）【yyyyMMddHHmmss，默认值空字符串，通常无值为0】
	 */
	private String shiftTime;
	
	/**
	 * 店铺ID
	 */
	private String shopID;
	
	/**
	 * 店铺名称
	 */
	private String shopName;
	
	/**
	 * 特别统计分类菜品的折后金额
	 */
	private String specialStatAmount;
	
	/**
	 * 开台时间（yyyyMMddHHssnn)
	 */
	private String startTime;
	
	/**
	 * 桌台名称
	 */
	private String tableName;
	
	/**
	 * 初次结账时的餐段名称(早餐，午餐，下午茶，晚餐，夜宵)
	 */
	private String timeNameCheckout;
	
	/**
	 * 同步时间【yyyyMMddHHmmss，默认值0】
	 */
	private String timeNameStart;
	
	/**
	 * 用户地址
	 */
	private String userAddress;
	
	/**
	 * 用户电话
	 */
	private String userMobile;
	
	/**
	 * 顾客姓名
	 */
	private String userName;
	
	/**
	 * 0：女士 1：先生 2：未知
	 */
	private String userSex;
	
	/**
	 * 服务员
	 */
	private String waiterBy;
	
	/**
	 * 预结账次数
	 */
	private String YJZCount;
	
	/**
	 * 支付宝user_id 微信openid 微信appid 微信sub_openid 微信sub_appid
	 */
	private String userPayFlag;
	

	public String getActionTime() {
		return actionTime;
	}

	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}

	public String getAlertFlagLst() {
		return alertFlagLst;
	}

	public void setAlertFlagLst(String alertFlagLst) {
		this.alertFlagLst = alertFlagLst;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getBrandID() {
		return brandID;
	}

	public void setBrandID(String brandID) {
		this.brandID = brandID;
	}

	public String getCardKey() {
		return cardKey;
	}

	public void setCardKey(String cardKey) {
		this.cardKey = cardKey;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardTransAfterRemark() {
		return cardTransAfterRemark;
	}

	public void setCardTransAfterRemark(String cardTransAfterRemark) {
		this.cardTransAfterRemark = cardTransAfterRemark;
	}

	public String getCardTransID() {
		return cardTransID;
	}

	public void setCardTransID(String cardTransID) {
		this.cardTransID = cardTransID;
	}

	public String getChannelKey() {
		return channelKey;
	}

	public void setChannelKey(String channelKey) {
		this.channelKey = channelKey;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelOrderKey() {
		return channelOrderKey;
	}

	public void setChannelOrderKey(String channelOrderKey) {
		this.channelOrderKey = channelOrderKey;
	}

	public String getChannelOrderKeyTP() {
		return channelOrderKeyTP;
	}

	public void setChannelOrderKeyTP(String channelOrderKeyTP) {
		this.channelOrderKeyTP = channelOrderKeyTP;
	}

	public String getChannelOrderNo() {
		return channelOrderNo;
	}

	public void setChannelOrderNo(String channelOrderNo) {
		this.channelOrderNo = channelOrderNo;
	}

	public String getChannelOrderTime() {
		return channelOrderTime;
	}

	public void setChannelOrderTime(String channelOrderTime) {
		this.channelOrderTime = channelOrderTime;
	}

	public String getChannelUserID() {
		return channelUserID;
	}

	public void setChannelUserID(String channelUserID) {
		this.channelUserID = channelUserID;
	}

	public String getChannelUserImage() {
		return channelUserImage;
	}

	public void setChannelUserImage(String channelUserImage) {
		this.channelUserImage = channelUserImage;
	}

	public String getChannelUserKey() {
		return channelUserKey;
	}

	public void setChannelUserKey(String channelUserKey) {
		this.channelUserKey = channelUserKey;
	}

	public String getCheckoutBy() {
		return checkoutBy;
	}

	public void setCheckoutBy(String checkoutBy) {
		this.checkoutBy = checkoutBy;
	}

	public String getCheckoutTime() {
		return checkoutTime;
	}

	public void setCheckoutTime(String checkoutTime) {
		this.checkoutTime = checkoutTime;
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getDeviceGroupID() {
		return deviceGroupID;
	}

	public void setDeviceGroupID(String deviceGroupID) {
		this.deviceGroupID = deviceGroupID;
	}

	public String getDeviceGroupName() {
		return deviceGroupName;
	}

	public void setDeviceGroupName(String deviceGroupName) {
		this.deviceGroupName = deviceGroupName;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDiscountBy() {
		return discountBy;
	}

	public void setDiscountBy(String discountBy) {
		this.discountBy = discountBy;
	}

	public String getDiscountRange() {
		return discountRange;
	}

	public void setDiscountRange(String discountRange) {
		this.discountRange = discountRange;
	}

	public String getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(String discountRate) {
		this.discountRate = discountRate;
	}

	public String getDiscountWayKey() {
		return discountWayKey;
	}

	public void setDiscountWayKey(String discountWayKey) {
		this.discountWayKey = discountWayKey;
	}

	public String getDiscountWayName() {
		return discountWayName;
	}

	public void setDiscountWayName(String discountWayName) {
		this.discountWayName = discountWayName;
	}

	public String getFJZCount() {
		return FJZCount;
	}

	public void setFJZCount(String fJZCount) {
		FJZCount = fJZCount;
	}

	public String getFoodAlert() {
		return foodAlert;
	}

	public void setFoodAlert(String foodAlert) {
		this.foodAlert = foodAlert;
	}

	public String getFoodAmount() {
		return foodAmount;
	}

	public void setFoodAmount(String foodAmount) {
		this.foodAmount = foodAmount;
	}

	public String getFoodCount() {
		return foodCount;
	}

	public void setFoodCount(String foodCount) {
		this.foodCount = foodCount;
	}

	public String getFoodNameDetail() {
		return foodNameDetail;
	}

	public void setFoodNameDetail(String foodNameDetail) {
		this.foodNameDetail = foodNameDetail;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public String getInvoiceBy() {
		return invoiceBy;
	}

	public void setInvoiceBy(String invoiceBy) {
		this.invoiceBy = invoiceBy;
	}

	public String getInvoiceTaxAmount() {
		return invoiceTaxAmount;
	}

	public void setInvoiceTaxAmount(String invoiceTaxAmount) {
		this.invoiceTaxAmount = invoiceTaxAmount;
	}

	public String getInvoiceTaxpayerIdentCode() {
		return invoiceTaxpayerIdentCode;
	}

	public void setInvoiceTaxpayerIdentCode(String invoiceTaxpayerIdentCode) {
		this.invoiceTaxpayerIdentCode = invoiceTaxpayerIdentCode;
	}

	public String getInvoiceTaxRate() {
		return invoiceTaxRate;
	}

	public void setInvoiceTaxRate(String invoiceTaxRate) {
		this.invoiceTaxRate = invoiceTaxRate;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	public String getIsCreatedByLoginUser() {
		return isCreatedByLoginUser;
	}

	public void setIsCreatedByLoginUser(String isCreatedByLoginUser) {
		this.isCreatedByLoginUser = isCreatedByLoginUser;
	}

	public String getIsTestOrder() {
		return isTestOrder;
	}

	public void setIsTestOrder(String isTestOrder) {
		this.isTestOrder = isTestOrder;
	}

	public String getIsVipPrice() {
		return isVipPrice;
	}

	public void setIsVipPrice(String isVipPrice) {
		this.isVipPrice = isVipPrice;
	}

	public String getLockedFlag() {
		return lockedFlag;
	}

	public void setLockedFlag(String lockedFlag) {
		this.lockedFlag = lockedFlag;
	}

	public String getModifyOrderLog() {
		return modifyOrderLog;
	}

	public void setModifyOrderLog(String modifyOrderLog) {
		this.modifyOrderLog = modifyOrderLog;
	}

	public String getMoneyWipeZeroType() {
		return moneyWipeZeroType;
	}

	public void setMoneyWipeZeroType(String moneyWipeZeroType) {
		this.moneyWipeZeroType = moneyWipeZeroType;
	}

	public String getNetOrderTypeCode() {
		return netOrderTypeCode;
	}

	public void setNetOrderTypeCode(String netOrderTypeCode) {
		this.netOrderTypeCode = netOrderTypeCode;
	}

	public String getOrderMD5() {
		return orderMD5;
	}

	public void setOrderMD5(String orderMD5) {
		this.orderMD5 = orderMD5;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderSubType() {
		return orderSubType;
	}

	public void setOrderSubType(String orderSubType) {
		this.orderSubType = orderSubType;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getPayAlert() {
		return payAlert;
	}

	public void setPayAlert(String payAlert) {
		this.payAlert = payAlert;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getPromotionAmount() {
		return promotionAmount;
	}

	public void setPromotionAmount(String promotionAmount) {
		this.promotionAmount = promotionAmount;
	}

	public String getPromotionDesc() {
		return promotionDesc;
	}

	public void setPromotionDesc(String promotionDesc) {
		this.promotionDesc = promotionDesc;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getReviewBy() {
		return reviewBy;
	}

	public void setReviewBy(String reviewBy) {
		this.reviewBy = reviewBy;
	}

	public String getReviewTime() {
		return reviewTime;
	}

	public void setReviewTime(String reviewTime) {
		this.reviewTime = reviewTime;
	}

	public String getSaasDeviceOrderNo() {
		return saasDeviceOrderNo;
	}

	public void setSaasDeviceOrderNo(String saasDeviceOrderNo) {
		this.saasDeviceOrderNo = saasDeviceOrderNo;
	}

	public String getSaasOrderKey() {
		return saasOrderKey;
	}

	public void setSaasOrderKey(String saasOrderKey) {
		this.saasOrderKey = saasOrderKey;
	}

	public String getSaasOrderNo() {
		return saasOrderNo;
	}

	public void setSaasOrderNo(String saasOrderNo) {
		this.saasOrderNo = saasOrderNo;
	}

	public String getSaasOrderRemark() {
		return saasOrderRemark;
	}

	public void setSaasOrderRemark(String saasOrderRemark) {
		this.saasOrderRemark = saasOrderRemark;
	}

	public String getSendCouponAmount() {
		return sendCouponAmount;
	}

	public void setSendCouponAmount(String sendCouponAmount) {
		this.sendCouponAmount = sendCouponAmount;
	}

	public String getSendCouponRemark() {
		return sendCouponRemark;
	}

	public void setSendCouponRemark(String sendCouponRemark) {
		this.sendCouponRemark = sendCouponRemark;
	}

	public String getSendFoodAmount() {
		return sendFoodAmount;
	}

	public void setSendFoodAmount(String sendFoodAmount) {
		this.sendFoodAmount = sendFoodAmount;
	}

	public String getServerMAC() {
		return serverMAC;
	}

	public void setServerMAC(String serverMAC) {
		this.serverMAC = serverMAC;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public String getShiftTime() {
		return shiftTime;
	}

	public void setShiftTime(String shiftTime) {
		this.shiftTime = shiftTime;
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

	public String getSpecialStatAmount() {
		return specialStatAmount;
	}

	public void setSpecialStatAmount(String specialStatAmount) {
		this.specialStatAmount = specialStatAmount;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTimeNameCheckout() {
		return timeNameCheckout;
	}

	public void setTimeNameCheckout(String timeNameCheckout) {
		this.timeNameCheckout = timeNameCheckout;
	}

	public String getTimeNameStart() {
		return timeNameStart;
	}

	public void setTimeNameStart(String timeNameStart) {
		this.timeNameStart = timeNameStart;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserSex() {
		return userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	public String getWaiterBy() {
		return waiterBy;
	}

	public void setWaiterBy(String waiterBy) {
		this.waiterBy = waiterBy;
	}

	public String getYJZCount() {
		return YJZCount;
	}

	public void setYJZCount(String yJZCount) {
		YJZCount = yJZCount;
	}

	public String getUserPayFlag() {
		return userPayFlag;
	}

	public void setUserPayFlag(String userPayFlag) {
		this.userPayFlag = userPayFlag;
	}

	public List<HllFoodDetail> getFoodDetail() {
		return foodDetail;
	}

	public void setFoodDetail(List<HllFoodDetail> foodDetail) {
		this.foodDetail = foodDetail;
	}

	public List<HllPayDetail> getPayDetail() {
		return payDetail;
	}

	public void setPayDetail(List<HllPayDetail> payDetail) {
		this.payDetail = payDetail;
	}

	public List<HllPromotionDetail> getPromotionDetail() {
		return promotionDetail;
	}

	public void setPromotionDetail(List<HllPromotionDetail> promotionDetail) {
		this.promotionDetail = promotionDetail;
	}
		
}
