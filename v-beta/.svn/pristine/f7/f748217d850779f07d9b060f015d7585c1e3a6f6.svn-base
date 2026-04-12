package com.dsc.spos.ninetyone.response;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 31. 貨運單查詢
 * /V2/ShippingOrder/Get
 * /V1/Order/GetShippingOrder
 */
public class NOShippingOrderReq extends NOBasicReq {

	private Data Data;

	public Data getData() {
		return Data;
	}

	public void setData(Data data) {
		Data = data;
	}
	
	public static class Data {
		private List<ShippingOrderData> List;

		public List<ShippingOrderData> getList() {
			return List;
		}

		public void setList(List<ShippingOrderData> list) {
			List = list;
		}
	}

	public static class ShippingOrderData {
		private String TGCode;//string(20) TG151026100006 購物車編號
		private String TMCode;//string(20) TM140617100010 主單編號
		private String TSCode;//string(20) TS1406171000023 訂單編號
		//OrderDeliverType string(30) Home 配送方式
		//-宅配(含離島宅配) : Home    -超商取貨付款 : StoreCashOnDelivery
		//-付款後超商取貨 : StorePickup -付款後門市自取 : LocationPickup
		//-貨到付款： CashOnDelivery
		private String OrderDeliverType;
		private String DistributorDef;//DistributorDef string(30) Family  通路商  -全家 :Family  -7-11 :SevenEleven
		//TemperatureTypeDef string(30) Normal 溫層類別 
		//-常溫 :Normal -冷藏 :Refrigerator -冷凍 :Freezer
		private String TemperatureTypeDef;
		private String ShippingOrderCode;//ShippingOrderCode string(20) N000123 貨運單配送編號
		private String GoodsToLogisticCenterDate;//GoodsToLogisticCenterDate datetime 2013-11-04T17:30:00 貨到物流中心日
		private String SuggestGoodsArrivalDate;//SuggestGoodsArrivalDate datetime 2013-11-14T17:30:00 建議貨到期限
		//ShippingOrderStatus string(30) Finish 出貨單狀態
		//-Finish : 已出貨至消費者  
		//**超商付款取貨 / 超商純取貨專用貨態
		//-AllocatedCode : 已配號                                                         -VerifySuccess : 超商驗收成功
		//-VerifyFailLost : 超商驗收失敗.商品遺失                              -VerifyFailAbnormalPackage :超商 驗收失敗.包裝異常
		//-VerifyFailRenovation : 超商驗收失敗.門市閉店/整修         -VerifyFailErrorCode : 超商驗收失敗.配送編號異常
		//-VerifyFailInvalIdCode : 超商驗收失敗.編號失效(未到貨) -ShippingProcessing : 出貨處理中
		//-ShippingFail : 消費者逾期未取，出貨失敗                              -ShippingArrived : 貨到門市
		//**門市自取專用貨態
		//-AllocatedCode : 已配號                                                          -ShippingProcessing : 出貨處理中
		//-ShippingFail : 消費者逾期未取，出貨失敗(逾期未取)      -ShippingArrived : 貨到門市
		//**貨到付款專用貨態
		//-AllocatedCode 已配號                                                              -CashOnDeliveryTransferring 宅配轉運中
		//-CashOnDeliveryNotAtHome 宅配不在家                                   -CashOnDeliveryDistributing 宅配已配送
		//-CashOnDeliveryFailDamage 宅配異常-損壞                            -CashOnDeliveryFailLost 宅配異常-遺失
		//-CashOnDeliveryFail 宅配出貨失敗                                          -CashOnDeliveryAddressError 宅配地址錯誤
		//-CashOnDeliveryForwarding 宅配轉寄配送中                           -ShippingProcessing 出貨處理中
		private String ShippingOrderStatus;
		private String ShippingOrderStatusUpdatedDateTime;//ShippingOrderStatusUpdatedDateTime  datetime 2013-11-11T17:30:00   出貨狀態最後更新日
		private String OrderReceiverName;//OrderReceiverName string(50) 趙曉明 收件人姓名
		private String OrderReceiverMobile;//OrderReceiverMobile string(20) 0912345678 收件人電話
		private String OrderReceiverZipCode;//OrderReceiverZipCode string(6) 116 收件人郵遞區號
		private String OrderReceiverCity;//OrderReceiverCity string(10) 台北市 收件人縣市
		private String OrderReceiverDistrict;//OrderReceiverDistrict string(10) 文山區 收件人鄉鎮市區
		private String OrderReceiverAddress;//OrderReceiverAddress string(200) 萬安街 200 號    收件人地址     *若為超商取貨的訂單，則為地址為超商門市的地址
		private String OrderReceiverStoreId;//OrderReceiverStoreId string(20) 002941 超商取貨的門市編號
		private String OrderReceiverStoreName;//OrderReceiverStoreName string(30) 全家太原店 超商取貨的門市名稱
		//StorePaymentAmount decimal 400 超商代收金額
		//-為各筆訂單(TS)的代收金額
		//-若此筆貨運單包含主單(TM)運費金額時，系統會自動納入其中一筆訂單(TS)的代收金額中
		private String StorePaymentAmount;
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
		public String getShippingOrderCode() {
			return ShippingOrderCode;
		}
		public void setShippingOrderCode(String shippingOrderCode) {
			ShippingOrderCode = shippingOrderCode;
		}
		public String getGoodsToLogisticCenterDate() {
			return GoodsToLogisticCenterDate;
		}
		public void setGoodsToLogisticCenterDate(String goodsToLogisticCenterDate) {
			GoodsToLogisticCenterDate = goodsToLogisticCenterDate;
		}
		public String getSuggestGoodsArrivalDate() {
			return SuggestGoodsArrivalDate;
		}
		public void setSuggestGoodsArrivalDate(String suggestGoodsArrivalDate) {
			SuggestGoodsArrivalDate = suggestGoodsArrivalDate;
		}
		public String getShippingOrderStatus() {
			return ShippingOrderStatus;
		}
		public void setShippingOrderStatus(String shippingOrderStatus) {
			ShippingOrderStatus = shippingOrderStatus;
		}
		public String getShippingOrderStatusUpdatedDateTime() {
			return ShippingOrderStatusUpdatedDateTime;
		}
		public void setShippingOrderStatusUpdatedDateTime(String shippingOrderStatusUpdatedDateTime) {
			try{
				SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				shippingOrderStatusUpdatedDateTime=sdf5.format(sdf6.parse(shippingOrderStatusUpdatedDateTime));
			}catch(Exception e){
				
			}
			ShippingOrderStatusUpdatedDateTime = shippingOrderStatusUpdatedDateTime;
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
		public String getStorePaymentAmount() {
			return StorePaymentAmount;
		}
		public void setStorePaymentAmount(String storePaymentAmount) {
			StorePaymentAmount = storePaymentAmount;
		}
		
	}
	
}
