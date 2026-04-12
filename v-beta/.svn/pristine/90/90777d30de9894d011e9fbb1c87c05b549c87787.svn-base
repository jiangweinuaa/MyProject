package com.dsc.spos.ninetyone.response;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * 26. 訂單查詢
 * /V2/SalesOrder/Get
 * /V1/Order/GetOrder
 */
public class NOSalesOrderDetailReq extends NOBasicReq {

	private SalesOrderDetailData Data;

	public SalesOrderDetailData getData() {
		return Data;
	}

	public void setData(SalesOrderDetailData data) {
		Data = data;
	}
	
	public static class SalesOrderDetailData {
		private int TotalCount;//本次查询结果总笔数
		private List<SalesOrderDetail> List;
		public int getTotalCount() {
			return TotalCount;
		}
		public void setTotalCount(int totalCount) {
			TotalCount = totalCount;
		}
		public List<SalesOrderDetail> getList() {
			return List;
		}
		public void setList(List<SalesOrderDetail> list) {
			List = list;
		}
	}
	
	public static class SalesOrderDetail {
		private String TGCode;//购物车编号
		private String TMCode;//主单编号
		private String TSCode;//订单编号
		//付款方式
		//-信用卡一次付款： CreditCardOnce
		//-信用卡分期付款： CreditCardInstallment
		//-超商取货付款： StorePay
		//-ATM 付款： ATM
		//-货到付款： CashOnDelivery
		//- LINE Pay： LinePay
		//- 街口支付： JKOPay
		private String OrderPayType;
		private String OrderShippingTypeId;//商店物流方式编号
		//配送方式
		//-宅配(含离岛宅配) :Home
		//-超商取货付款 : StoreCashOnDelivery
		//-付款后超商取货 :StorePickup
		//-付款后门市自取 : LocationPickup
		//-货到付款： CashOnDelivery
		//-海外宅配： Oversea
		private String OrderDeliverType;
		//通路商
		//-全家： Family
		//-7-11： SevenEleven
		private String DistributorDef;
		//温层类别
		//-常温： Normal
		//-冷藏： Refrigerator
		//-冷冻： Freezer
		private String TemperatureTypeDef;
		//主单(TM)运费
		//-此为消费者自行负担的运费金额
		//-若同次结帐(1 笔主单)有多张订单(TS)，消费者只会支付 1 笔运费金额
		//-查询时，若一笔主单有多笔订单，则每张订单都会列出主单的运费金额
		private String TMShippingFee;
		private String OrderDateTime;//订单转单日  如:2013-08-30T17:30:00  "yyyy-MM-dd'T'HH:mm:ss"
		//订单状态
		//- 已取消： Cancel
		//- 已完成： Finish
		//- 付款确认中： WaitingToCreditCheck
		//- 已成立： WaitingToShipping
		//- 已确认待出货： ConfirmedToShipping
		//- 待付款： WaitingToPay
		private String OrderStatus;
		private String OrderStatusUpdatedDateTime;//订单状态日 2013-08-30T17:30:00 "yyyy-MM-dd'T'HH:mm:ss"
		private String SalesOrderConfirmDateTime;//订单确认日期 2013-09-01T17:30:00 "yyyy-MM-dd'T'HH:mm:ss"
		//订单交期别
		//- 一般 : 1
		//-预购(指定出货日) : 2
		//- 订製 : 3
		//- 客约 : 4
		//- 预购(指定工作天)： 6
		private String OrderShippingType;
		private String OrderExpectShippingDate;//订单预计出货日 2013-09-04T17:30:00  "yyyy-MM-dd'T'HH:mm:ss"
		private String OrderSource;//订单来源 如:"iOSApp"、"官网(PC)"
		private String MemberCode;//会员编号
		private String VipMemberId;//91APP VIP 会员序号
		private String OuterMemberCode;//客户会员编号 (贵公司的会员编号)    如未使用会员模组，此栏位会回覆 null
		private String OrderReceiverName;//收件人姓名
		private String OrderReceiverMobile;//收件人电话
		private String OrderReceiverZipCode;//收件人邮递区号
		private String OrderReceiverCity;//收件人县市
		private String OrderReceiverDistrict;//收件人乡镇市区
		//收件人地址
		//*若为 超商取货的订单，则为地址为超商门市的地址
		private String OrderReceiverAddress;
		private String OrderReceiverStoreId;//超商取货的门市编号
		private String OrderReceiverStoreName;//超商取货的门市名称
		private String ShippingOrderCode;//货运单配送编号
		//消费者备注
		//*请注意： 商店须设定使用订单备注，订购流程中才会显示消费者填写的栏位
		private String OrderMemo;
		private String OrderSupplierNote;//客户备注 "2014/1/8 已叫货"
		private String SkuId;//商品选项(SKU)编号
		private String SkuName;//商品选项名称
		//商品料号
		//-若为空白则显示 null
		private String OuterId;
		//用于显示一般商品的 TS 单是否有带活动赠品或商品赠品，任一条件符合有即显示 true，无则显示 false，
		//如为活动赠品或商品赠品的 TS则显示 false
		private String HasGift;
		private String Price;//商品单价
		private String Qty;//商品数量
		private String TotalPrice;//商品总金额(单价*数量)
		private String TotalDiscount;//订单总折扣金额
		private String TotalPayment;//订单实际付款金额
		private String[] PromotionIdList;//折扣活动序号
		private String PromotionDiscount;//折扣活动折扣金额
		private String ECouponId;//折价券活动序号
		private String ECouponDiscount;//折价券折扣金额
		//赠品属性，若为满额 is 赠会显示(活动赠品)，
		//若为买就送会显示(商品赠品)，一般商品则显示”“
		private String ProductAttribute;
		//买就送(商品赠品关连代码)，用于辩识同一张
		//TM 单 TS 主订单与商品赠品订单的群组关係。
		private String SalePageGiftGroupCode;
		//温层商品空间保留编号
		//-若无资料则显示""
		//-此为超商温层配送时的空间保留编号
		//-未开放(请忽略此栏位)  不确定V2接口是否开放
		private String TemperatureReservedNo;
		private String VipMemberFullName;//购买人姓名
		//取消代号:
		//1 价格比较贵
		//2 衝动购买
		//3 不想等太久
		//4 其他
		//5 商店取消
		//6 消费者未取货
		//7 门市闭店
		//8 订单缴费过期
		private String CancelOrderSlaveCauseDef;
		private String CancelOrderSlaveCauseDefDesc;//取消原因
		private String ShippingOrderSlaveStatusCasue;//验退原因说明 如:"门市关转"
		private String IsExtra;//是否为加价购商品		true:是 false:否		(注：此栏位目前皆先回传为 false)
		private String OrderReceiverCountryCode;//收件人电话国码
		private String OrderReceiverCountry;//收件国家/地区
		private String LoyaltyPointDiscount;//点数折抵金额
		private String LoyaltyPoint;//折抵点数
		//订单推荐门市序号
		//-若为空白则显示 null
		private String RefereeLocationId;
		//订单推荐门市店号
		//-若为空白则显示 null
		private String RefereeLocationCode;
		//订单推荐门市名称
		//-若为空白则显示 null
		private String RefereeLocationName;
		//订单推荐人店员序号
		//-若为空白则显示 null
		private String RefereeEmployeeId;
		//订单推荐人店员编号
		//-若为空白则显示 null
		private String RefereeEmployeeCode;
		//订单推荐人名称
		//-若为空白则显示 null
		private String RefereeEmployeeName;
		public String getCancelOrderSlaveCauseDefDesc() {
			return CancelOrderSlaveCauseDefDesc;
		}
		public void setCancelOrderSlaveCauseDefDesc(String cancelOrderSlaveCauseDefDesc) {
			CancelOrderSlaveCauseDefDesc = cancelOrderSlaveCauseDefDesc;
		}
		public String getTGCode() {
			return TGCode;
		}
		public void setTGCode(String tGCode) {
			TGCode = tGCode;
		}
		public String getTMCode() {
			return TMCode;
		}
		public void setTMCode(String tMCode) {
			TMCode = tMCode;
		}
		public String getTSCode() {
			return TSCode;
		}
		public void setTSCode(String tSCode) {
			TSCode = tSCode;
		}
		public String getOrderPayType() {
			return OrderPayType;
		}
		public void setOrderPayType(String orderPayType) {
			OrderPayType = orderPayType;
		}
		public String getOrderShippingTypeId() {
			return OrderShippingTypeId;
		}
		public void setOrderShippingTypeId(String orderShippingTypeId) {
			OrderShippingTypeId = orderShippingTypeId;
		}
		public String getOrderDeliverType() {
			return OrderDeliverType;
		}
		public void setOrderDeliverType(String orderDeliverType) {
			OrderDeliverType = orderDeliverType;
		}
		public String getDistributorDef() {
			return DistributorDef;
		}
		public void setDistributorDef(String distributorDef) {
			DistributorDef = distributorDef;
		}
		public String getTemperatureTypeDef() {
			return TemperatureTypeDef;
		}
		public void setTemperatureTypeDef(String temperatureTypeDef) {
			TemperatureTypeDef = temperatureTypeDef;
		}
		public String getTMShippingFee() {
			return TMShippingFee;
		}
		public void setTMShippingFee(String tMShippingFee) {
			TMShippingFee = tMShippingFee;
		}
		public String getOrderDateTime() {
			return OrderDateTime;
		}
		public void setOrderDateTime(String orderDateTime) {
			OrderDateTime = orderDateTime;
		}
		public String getOrderStatus() {
			return OrderStatus;
		}
		public void setOrderStatus(String orderStatus) {
			OrderStatus = orderStatus;
		}
		public String getOrderStatusUpdatedDateTime() {
			return OrderStatusUpdatedDateTime;
		}
		public void setOrderStatusUpdatedDateTime(String orderStatusUpdatedDateTime) {
			try{
				SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				orderStatusUpdatedDateTime=sdf5.format(sdf6.parse(orderStatusUpdatedDateTime));
			}catch(Exception e){
				
			}
			OrderStatusUpdatedDateTime = orderStatusUpdatedDateTime;
		}
		public String getSalesOrderConfirmDateTime() {
			return SalesOrderConfirmDateTime;
		}
		public void setSalesOrderConfirmDateTime(String salesOrderConfirmDateTime) {
			SalesOrderConfirmDateTime = salesOrderConfirmDateTime;
		}
		public String getOrderShippingType() {
			return OrderShippingType;
		}
		public void setOrderShippingType(String orderShippingType) {
			OrderShippingType = orderShippingType;
		}
		public String getOrderExpectShippingDate() {
			return OrderExpectShippingDate;
		}
		public void setOrderExpectShippingDate(String orderExpectShippingDate) {
			OrderExpectShippingDate = orderExpectShippingDate;
		}
		public String getOrderSource() {
			return OrderSource;
		}
		public void setOrderSource(String orderSource) {
			OrderSource = orderSource;
		}
		public String getMemberCode() {
			return MemberCode;
		}
		public void setMemberCode(String memberCode) {
			MemberCode = memberCode;
		}
		public String getVipMemberId() {
			return VipMemberId;
		}
		public void setVipMemberId(String vipMemberId) {
			VipMemberId = vipMemberId;
		}
		public String getOuterMemberCode() {
			return OuterMemberCode;
		}
		public void setOuterMemberCode(String outerMemberCode) {
			OuterMemberCode = outerMemberCode;
		}
		public String getOrderReceiverName() {
			return OrderReceiverName;
		}
		public void setOrderReceiverName(String orderReceiverName) {
			OrderReceiverName = orderReceiverName;
		}
		public String getOrderReceiverMobile() {
			return OrderReceiverMobile;
		}
		public void setOrderReceiverMobile(String orderReceiverMobile) {
			OrderReceiverMobile = orderReceiverMobile;
		}
		public String getOrderReceiverZipCode() {
			return OrderReceiverZipCode;
		}
		public void setOrderReceiverZipCode(String orderReceiverZipCode) {
			OrderReceiverZipCode = orderReceiverZipCode;
		}
		public String getOrderReceiverCity() {
			return OrderReceiverCity;
		}
		public void setOrderReceiverCity(String orderReceiverCity) {
			OrderReceiverCity = orderReceiverCity;
		}
		public String getOrderReceiverDistrict() {
			return OrderReceiverDistrict;
		}
		public void setOrderReceiverDistrict(String orderReceiverDistrict) {
			OrderReceiverDistrict = orderReceiverDistrict;
		}
		public String getOrderReceiverAddress() {
			return OrderReceiverAddress;
		}
		public void setOrderReceiverAddress(String orderReceiverAddress) {
			OrderReceiverAddress = orderReceiverAddress;
		}
		public String getOrderReceiverStoreId() {
			return OrderReceiverStoreId;
		}
		public void setOrderReceiverStoreId(String orderReceiverStoreId) {
			OrderReceiverStoreId = orderReceiverStoreId;
		}
		public String getOrderReceiverStoreName() {
			return OrderReceiverStoreName;
		}
		public void setOrderReceiverStoreName(String orderReceiverStoreName) {
			OrderReceiverStoreName = orderReceiverStoreName;
		}
		public String getShippingOrderCode() {
			return ShippingOrderCode;
		}
		public void setShippingOrderCode(String shippingOrderCode) {
			ShippingOrderCode = shippingOrderCode;
		}
		public String getOrderMemo() {
			return OrderMemo;
		}
		public void setOrderMemo(String orderMemo) {
			OrderMemo = orderMemo;
		}
		public String getOrderSupplierNote() {
			return OrderSupplierNote;
		}
		public void setOrderSupplierNote(String orderSupplierNote) {
			OrderSupplierNote = orderSupplierNote;
		}
		public String getSkuId() {
			return SkuId;
		}
		public void setSkuId(String skuId) {
			SkuId = skuId;
		}
		public String getSkuName() {
			return SkuName;
		}
		public void setSkuName(String skuName) {
			SkuName = skuName;
		}
		public String getOuterId() {
			return OuterId;
		}
		public void setOuterId(String outerId) {
			OuterId = outerId;
		}
		public String getHasGift() {
			return HasGift;
		}
		public void setHasGift(String hasGift) {
			HasGift = hasGift;
		}
		public String getPrice() {
			return Price;
		}
		public void setPrice(String price) {
			Price = price;
		}
		public String getQty() {
			return Qty;
		}
		public void setQty(String qty) {
			Qty = qty;
		}
		public String getTotalPrice() {
			return TotalPrice;
		}
		public void setTotalPrice(String totalPrice) {
			TotalPrice = totalPrice;
		}
		public String getTotalDiscount() {
			return TotalDiscount;
		}
		public void setTotalDiscount(String totalDiscount) {
			TotalDiscount = totalDiscount;
		}
		public String getTotalPayment() {
			return TotalPayment;
		}
		public void setTotalPayment(String totalPayment) {
			TotalPayment = totalPayment;
		}
		public String[] getPromotionIdList() {
			return PromotionIdList;
		}
		public void setPromotionIdList(String[] promotionIdList) {
			PromotionIdList = promotionIdList;
		}
		public String getPromotionDiscount() {
			return PromotionDiscount;
		}
		public void setPromotionDiscount(String promotionDiscount) {
			PromotionDiscount = promotionDiscount;
		}
		public String getECouponId() {
			return ECouponId;
		}
		public void setECouponId(String eCouponId) {
			ECouponId = eCouponId;
		}
		public String getECouponDiscount() {
			return ECouponDiscount;
		}
		public void setECouponDiscount(String eCouponDiscount) {
			ECouponDiscount = eCouponDiscount;
		}
		public String getProductAttribute() {
			return ProductAttribute;
		}
		public void setProductAttribute(String productAttribute) {
			ProductAttribute = productAttribute;
		}
		public String getSalePageGiftGroupCode() {
			return SalePageGiftGroupCode;
		}
		public void setSalePageGiftGroupCode(String salePageGiftGroupCode) {
			SalePageGiftGroupCode = salePageGiftGroupCode;
		}
		public String getTemperatureReservedNo() {
			return TemperatureReservedNo;
		}
		public void setTemperatureReservedNo(String temperatureReservedNo) {
			TemperatureReservedNo = temperatureReservedNo;
		}
		public String getVipMemberFullName() {
			return VipMemberFullName;
		}
		public void setVipMemberFullName(String vipMemberFullName) {
			VipMemberFullName = vipMemberFullName;
		}
		public String getCancelOrderSlaveCauseDef() {
			return CancelOrderSlaveCauseDef;
		}
		public void setCancelOrderSlaveCauseDef(String cancelOrderSlaveCauseDef) {
			CancelOrderSlaveCauseDef = cancelOrderSlaveCauseDef;
		}
		public String getShippingOrderSlaveStatusCasue() {
			return ShippingOrderSlaveStatusCasue;
		}
		public void setShippingOrderSlaveStatusCasue(String shippingOrderSlaveStatusCasue) {
			ShippingOrderSlaveStatusCasue = shippingOrderSlaveStatusCasue;
		}
		public String getIsExtra() {
			return IsExtra;
		}
		public void setIsExtra(String isExtra) {
			IsExtra = isExtra;
		}
		public String getOrderReceiverCountryCode() {
			return OrderReceiverCountryCode;
		}
		public void setOrderReceiverCountryCode(String orderReceiverCountryCode) {
			OrderReceiverCountryCode = orderReceiverCountryCode;
		}
		public String getOrderReceiverCountry() {
			return OrderReceiverCountry;
		}
		public void setOrderReceiverCountry(String orderReceiverCountry) {
			OrderReceiverCountry = orderReceiverCountry;
		}
		public String getLoyaltyPointDiscount() {
			return LoyaltyPointDiscount;
		}
		public void setLoyaltyPointDiscount(String loyaltyPointDiscount) {
			LoyaltyPointDiscount = loyaltyPointDiscount;
		}
		public String getLoyaltyPoint() {
			return LoyaltyPoint;
		}
		public void setLoyaltyPoint(String loyaltyPoint) {
			LoyaltyPoint = loyaltyPoint;
		}
		public String getRefereeLocationId() {
			return RefereeLocationId;
		}
		public void setRefereeLocationId(String refereeLocationId) {
			RefereeLocationId = refereeLocationId;
		}
		public String getRefereeLocationCode() {
			return RefereeLocationCode;
		}
		public void setRefereeLocationCode(String refereeLocationCode) {
			RefereeLocationCode = refereeLocationCode;
		}
		public String getRefereeLocationName() {
			return RefereeLocationName;
		}
		public void setRefereeLocationName(String refereeLocationName) {
			RefereeLocationName = refereeLocationName;
		}
		public String getRefereeEmployeeId() {
			return RefereeEmployeeId;
		}
		public void setRefereeEmployeeId(String refereeEmployeeId) {
			RefereeEmployeeId = refereeEmployeeId;
		}
		public String getRefereeEmployeeCode() {
			return RefereeEmployeeCode;
		}
		public void setRefereeEmployeeCode(String refereeEmployeeCode) {
			RefereeEmployeeCode = refereeEmployeeCode;
		}
		public String getRefereeEmployeeName() {
			return RefereeEmployeeName;
		}
		public void setRefereeEmployeeName(String refereeEmployeeName) {
			RefereeEmployeeName = refereeEmployeeName;
		}
		
		
		
	}

}
