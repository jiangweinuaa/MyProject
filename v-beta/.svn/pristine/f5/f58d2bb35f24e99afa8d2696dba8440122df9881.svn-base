package com.dsc.spos.ninetyone.response;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * 28. 退貨單查詢
 * /V2/ReturnGoodsOrder/Get
 * /V1/Order/GetReturnGood
 */
public class NOReturnOrderDetailReq extends NOBasicReq {

	private ReturnOrderDetailData Data;
	
	public ReturnOrderDetailData getData() {
		return Data;
	}

	public void setData(ReturnOrderDetailData data) {
		Data = data;
	}

	public static class ReturnOrderDetailData {
		private int TotalCount;//本次查询结果总笔数
		private List<ReturnOrderDetail> List;
		public int getTotalCount() {
			return TotalCount;
		}
		public void setTotalCount(int totalCount) {
			TotalCount = totalCount;
		}
		public List<ReturnOrderDetail> getList() {
			return List;
		}
		public void setList(List<ReturnOrderDetail> list) {
			List = list;
		}
	}
	
	public static class ReturnOrderDetail {
		private String ReturnGoodDetailId;//退貨單序號
		private String TSCode;//來源訂單編號
		//退貨單狀態
		//-退貨尚未處理 : WaitingToReturnGoods
		//-退貨已取消—不需要退貨 : ReturnGoodsCancel
		//-退貨已取消—不接受退貨 : ReturnGoodsFail
		//-退貨完成 : Finish
		//-等待消費者寄件 : WaitingToSend
		//-消費者已到店寄件 : StoreReceived
		//-物流中心開始驗收 : VerifySuccess
		//-物流中心驗收完成 : SupplierReceived
		//-物流中心驗收失敗_商品遺失 : VerifyFailLost
		//-逾期未寄件_配送編號失效 : VerifyFailInvalidCode
		private String ReturnGoodStatus;
		private String ReturnGoodDateTime;//datetime 2014-06-17T09:45:03.1 退貨單成立日
		private String ReturnGoodStatusUpdatedDateTime;//datetime 2014-06-17T09:51:38.687 退貨單狀態日
		private String ReturnGoodPickupName;//string(50) 趙曉明 取件人姓名
		private String ReturnGoodPickupPhone;//string(20) 0912345678 取件人電話
		private String ReturnGoodPickupZipCode;//string(6) 取件人郵遞區號 -若為空白則回傳空字串
		private String ReturnGoodPickupCity;//string(10) 取件人縣市 -若為空白則回傳空字串
		private String ReturnGoodPickupDistrict;//string(10) 取件人鄉鎮市區 -若為空白則回傳空字串
		private String ReturnGoodPickupAddress;//string(200) 取件人地址 -若為空白則回傳空字串
		private String SkuId;//long 34666 商品選項(SKU)編號
		private String OuterId;//string(100) 201406160007 商店料號 -若為空白則回傳空字串
		private String ReturnGoodPrice;//decimal 199 商品單價
		private String ReturnGoodQty;//int 1 退貨數量
		private String ReturnGoodTotalDiscount;//decimal -59 退貨總折扣金額
		private String ReturnGoodTotalPayment;//decimal 140 退貨實際金額
		private String ReturnGoodCause;//int 4 退貨原因 -規格不符 : 1 -商品品質不佳 : 2 -價格比較貴 : 3 -其他 : 4
		private String ReturnGoodCauseDesc;//string(200) 太小了 退貨描述
		//long 123456 91AVIP 會員序號  
		//此次说明有问题-與{91 VIP 會員編號 / 商店會員編號} 需擇一傳入
		private String VipMemberId;
		//string(255) AA531238 客戶會員編號 (貴公司的會員編號) 
		//此次说明有问题-與{91 VIP 會員編號 / 商店會員編號} 需擇一傳入
		private String OuterMemberCode;
		private String MemberCode;//string(20) "MemberCode": "5100437436" 91APP會員編號 (存入外部資料庫使用)
		private String LoyaltyPointDiscount;//decimal 10 點數折抵金額
		private String LoyaltyPoint;//decimal 100 折抵點數
		public String getReturnGoodDetailId() {
			return ReturnGoodDetailId;
		}
		public void setReturnGoodDetailId(String returnGoodDetailId) {
			ReturnGoodDetailId = returnGoodDetailId;
		}
		public String getTSCode() {
			return TSCode;
		}
		public void setTSCode(String tSCode) {
			TSCode = tSCode;
		}
		public String getReturnGoodStatus() {
			return ReturnGoodStatus;
		}
		public void setReturnGoodStatus(String returnGoodStatus) {
			ReturnGoodStatus = returnGoodStatus;
		}
		public String getReturnGoodDateTime() {
			return ReturnGoodDateTime;
		}
		public void setReturnGoodDateTime(String returnGoodDateTime) {
			ReturnGoodDateTime = returnGoodDateTime;
		}
		public String getReturnGoodStatusUpdatedDateTime() {
			return ReturnGoodStatusUpdatedDateTime;
		}
		public void setReturnGoodStatusUpdatedDateTime(String returnGoodStatusUpdatedDateTime) {
			try{
				SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				returnGoodStatusUpdatedDateTime=sdf5.format(sdf6.parse(returnGoodStatusUpdatedDateTime));
			}catch(Exception e){
				
			}
			ReturnGoodStatusUpdatedDateTime = returnGoodStatusUpdatedDateTime;
		}
		public String getReturnGoodPickupName() {
			return ReturnGoodPickupName;
		}
		public void setReturnGoodPickupName(String returnGoodPickupName) {
			ReturnGoodPickupName = returnGoodPickupName;
		}
		public String getReturnGoodPickupPhone() {
			return ReturnGoodPickupPhone;
		}
		public void setReturnGoodPickupPhone(String returnGoodPickupPhone) {
			ReturnGoodPickupPhone = returnGoodPickupPhone;
		}
		public String getReturnGoodPickupZipCode() {
			return ReturnGoodPickupZipCode;
		}
		public void setReturnGoodPickupZipCode(String returnGoodPickupZipCode) {
			ReturnGoodPickupZipCode = returnGoodPickupZipCode;
		}
		public String getReturnGoodPickupCity() {
			return ReturnGoodPickupCity;
		}
		public void setReturnGoodPickupCity(String returnGoodPickupCity) {
			ReturnGoodPickupCity = returnGoodPickupCity;
		}
		public String getReturnGoodPickupDistrict() {
			return ReturnGoodPickupDistrict;
		}
		public void setReturnGoodPickupDistrict(String returnGoodPickupDistrict) {
			ReturnGoodPickupDistrict = returnGoodPickupDistrict;
		}
		public String getReturnGoodPickupAddress() {
			return ReturnGoodPickupAddress;
		}
		public void setReturnGoodPickupAddress(String returnGoodPickupAddress) {
			ReturnGoodPickupAddress = returnGoodPickupAddress;
		}
		public String getSkuId() {
			return SkuId;
		}
		public void setSkuId(String skuId) {
			SkuId = skuId;
		}
		public String getOuterId() {
			return OuterId;
		}
		public void setOuterId(String outerId) {
			OuterId = outerId;
		}
		public String getReturnGoodPrice() {
			return ReturnGoodPrice;
		}
		public void setReturnGoodPrice(String returnGoodPrice) {
			ReturnGoodPrice = returnGoodPrice;
		}
		public String getReturnGoodQty() {
			return ReturnGoodQty;
		}
		public void setReturnGoodQty(String returnGoodQty) {
			ReturnGoodQty = returnGoodQty;
		}
		public String getReturnGoodTotalDiscount() {
			return ReturnGoodTotalDiscount;
		}
		public void setReturnGoodTotalDiscount(String returnGoodTotalDiscount) {
			ReturnGoodTotalDiscount = returnGoodTotalDiscount;
		}
		public String getReturnGoodTotalPayment() {
			return ReturnGoodTotalPayment;
		}
		public void setReturnGoodTotalPayment(String returnGoodTotalPayment) {
			ReturnGoodTotalPayment = returnGoodTotalPayment;
		}
		public String getReturnGoodCause() {
			return ReturnGoodCause;
		}
		public void setReturnGoodCause(String returnGoodCause) {
			ReturnGoodCause = returnGoodCause;
		}
		public String getReturnGoodCauseDesc() {
			return ReturnGoodCauseDesc;
		}
		public void setReturnGoodCauseDesc(String returnGoodCauseDesc) {
			ReturnGoodCauseDesc = returnGoodCauseDesc;
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
		public String getMemberCode() {
			return MemberCode;
		}
		public void setMemberCode(String memberCode) {
			MemberCode = memberCode;
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
		
	}

}
