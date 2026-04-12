package com.dsc.spos.thirdpart.youzan.response;

import java.util.List;

import com.dsc.spos.thirdpart.youzan.YouZanPayTypeEnum;

/**
 * 适用于同城配送发货（仅支持商家自配送）
 * https://doc.youzanyun.com/doc#/content/API/0/detail/api/0/712
 * /api/youzan.logistics.online.local.confirm/3.0.0
 * @author LN 08546
 */
public class YouZanOrderComputeRes extends YouZanBasicRes  {

	private Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public class Data{
		private String payment;//订单原价
		private String realPayment;//订单整单实付价格
		private String tid;//订单号
		private String nodeKdtId;//订单门店id
		private String created;//订单创建时间yyyy-MM-dd HH:mm:ss
		private String payTime;//订单支付时间
		private String offlineId;//网店id
		private String consignTime;//订单发货时间
		private String selffetchCode;//自提码
		public String getSelffetchCode() {
			return selffetchCode;
		}
		public void setSelffetchCode(String selffetchCode) {
			this.selffetchCode = selffetchCode;
		}
		private PayInfo payInfo;
		private DiscountInfo discountInfo;
		private List<Good> goods;
		public String getPayment() {
			return payment;
		}
		public void setPayment(String payment) {
			this.payment = payment;
		}
		public String getRealPayment() {
			return realPayment;
		}
		public void setRealPayment(String realPayment) {
			this.realPayment = realPayment;
		}
		public String getTid() {
			return tid;
		}
		public void setTid(String tid) {
			this.tid = tid;
		}
		public String getNodeKdtId() {
			return nodeKdtId;
		}
		public void setNodeKdtId(String nodeKdtId) {
			this.nodeKdtId = nodeKdtId;
		}
		public String getCreated() {
			return created;
		}
		public void setCreated(String created) {
			this.created = created;
		}
		public String getPayTime() {
			return payTime;
		}
		public void setPayTime(String payTime) {
			this.payTime = payTime;
		}
		public String getOfflineId() {
			return offlineId;
		}
		public void setOfflineId(String offlineId) {
			this.offlineId = offlineId;
		}
		public String getConsignTime() {
			return consignTime;
		}
		public void setConsignTime(String consignTime) {
			this.consignTime = consignTime;
		}
		public PayInfo getPayInfo() {
			return payInfo;
		}
		public void setPayInfo(PayInfo payInfo) {
			this.payInfo = payInfo;
		}
		public DiscountInfo getDiscountInfo() {
			return discountInfo;
		}
		public void setDiscountInfo(DiscountInfo discountInfo) {
			this.discountInfo = discountInfo;
		}
		public List<Good> getGoods() {
			return goods;
		}
		public void setGoods(List<Good> goods) {
			this.goods = goods;
		}
		
		
		public class PayInfo{
			private String deductionPay;//deductionPay		储值卡支付金额
			private String deduction_real_pay;//deductionPay		除会员卡支付以外的支付金额(微信、支付宝、银行卡、有赞E卡、有赞零钱)
			private String pointsPay;//pointsPay		积分兑换金额
			private String couponPay;//couponPay		优惠卷支付金额
			private String pointDeductionPay;//pointDeductionPay		积分抵现支付金额
			private String tuanPay;//tuanPay		团购返现
			private String cashBackPay;//cashBackPay		订单返现
			private List<Payment> paymentsList;
			
			public List<Payment> getPaymentsList() {
				return paymentsList;
			}
			public void setPaymentsList(List<Payment> paymentsList) {
				this.paymentsList = paymentsList;
			}
			public String getDeduction_real_pay() {
				return deduction_real_pay;
			}
			public void setDeduction_real_pay(String deduction_real_pay) {
				this.deduction_real_pay = deduction_real_pay;
			}
			public String getDeductionPay() {
				return deductionPay;
			}
			public void setDeductionPay(String deductionPay) {
				this.deductionPay = deductionPay;
			}
			public String getPointsPay() {
				return pointsPay;
			}
			public void setPointsPay(String pointsPay) {
				this.pointsPay = pointsPay;
			}
			public String getCouponPay() {
				return couponPay;
			}
			public void setCouponPay(String couponPay) {
				this.couponPay = couponPay;
			}
			public String getPointDeductionPay() {
				return pointDeductionPay;
			}
			public void setPointDeductionPay(String pointDeductionPay) {
				this.pointDeductionPay = pointDeductionPay;
			}
			public String getTuanPay() {
				return tuanPay;
			}
			public void setTuanPay(String tuanPay) {
				this.tuanPay = tuanPay;
			}
			public String getCashBackPay() {
				return cashBackPay;
			}
			public void setCashBackPay(String cashBackPay) {
				this.cashBackPay = cashBackPay;
			}
			
			/**
			 * 除会员卡支付以外的支付方式  此处加总==deduction_real_pay栏位
			 */
			public class Payment{
				//31-有赞E卡
				//32-有赞零钱
				private String pay_type;//支付方式
				private String pay_type_name;//支付方式
				private String payment;
				public String getPay_type() {
					return pay_type;
				}
				public void setPay_type(String pay_type) {
					String payTypeName="";
					if(pay_type!=null&&pay_type.trim().length()>0){
						payTypeName=YouZanPayTypeEnum.getName(pay_type);
						if(payTypeName==null||payTypeName.trim().length()<1){
							payTypeName=pay_type;
						}
						this.pay_type_name=payTypeName;
					}
					this.pay_type = pay_type;
				}
				public String getPayment() {
					return payment;
				}
				public void setPayment(String payment) {
					this.payment = payment;
				}
				public String getPay_type_name() {
					return pay_type_name;
				}
				public void setPay_type_name(String pay_type_name) {
					this.pay_type_name = pay_type_name;
				}
			}
		}
		
		public class DiscountInfo{
			private String groupOnPromotion;//groupOnPromotion		多人拼团
			private String seckillPronmotion;//seckillPronmotion		秒杀
			private String presentExchangePromotion;//presentExchangePromotion		赠品
			private String customerDiscountPromotion;//customerDiscountPromotion		会员折扣金额
			private String timelimitedDiscountPromotion;//timelimitedDiscountPromotion		限时折扣金额
			private String meetReducePromotion;//meetReducePromotion		满减送金额
			public String getGroupOnPromotion() {
				return groupOnPromotion;
			}
			public void setGroupOnPromotion(String groupOnPromotion) {
				this.groupOnPromotion = groupOnPromotion;
			}
			public String getSeckillPronmotion() {
				return seckillPronmotion;
			}
			public void setSeckillPronmotion(String seckillPronmotion) {
				this.seckillPronmotion = seckillPronmotion;
			}
			public String getPresentExchangePromotion() {
				return presentExchangePromotion;
			}
			public void setPresentExchangePromotion(String presentExchangePromotion) {
				this.presentExchangePromotion = presentExchangePromotion;
			}
			public String getCustomerDiscountPromotion() {
				return customerDiscountPromotion;
			}
			public void setCustomerDiscountPromotion(String customerDiscountPromotion) {
				this.customerDiscountPromotion = customerDiscountPromotion;
			}
			public String getTimelimitedDiscountPromotion() {
				return timelimitedDiscountPromotion;
			}
			public void setTimelimitedDiscountPromotion(String timelimitedDiscountPromotion) {
				this.timelimitedDiscountPromotion = timelimitedDiscountPromotion;
			}
			public String getMeetReducePromotion() {
				return meetReducePromotion;
			}
			public void setMeetReducePromotion(String meetReducePromotion) {
				this.meetReducePromotion = meetReducePromotion;
			}
		}
		
		public class AddressInfo{
			private String deliveryCity;//deliveryCity		市
			private String selfFetchInfo;//selfFetchInfo		到店自提信息
			private String addressExtra;//addressExtra		字段为json格式，需要开发者自行解析 lng、lon（经纬度，百度坐标系）； checkOutTime（酒店退房时间）； recipients（入住人）； checkInTime（酒店入住时间）； idCardNumber（海淘身份证信息）； areaCode（行政编码）
			private String deliveryEndTime;//deliveryEndTime		同城送预计送达时间-结束时间 非同城送以及没有开启定时达的订单不返回
			private String deliveryProvince;//deliveryProvince		省
			private String receiverName;//receiverName		收货人姓名
			private String deliveryStartTime;//deliveryStartTime		同城送预计送达时间-开始时间 非同城送以及没有开启定时达的订单不返回
			private String receiverTel;//receiverTel		收货人手机号
			private String deliveryPostalCode;//deliveryPostalCode		
			private String deliveryAddress;//deliveryAddress		详细地址
			private String deliveryDistrict;//deliveryDistrict		区
			public String getDeliveryCity() {
				return deliveryCity;
			}
			public void setDeliveryCity(String deliveryCity) {
				this.deliveryCity = deliveryCity;
			}
			public String getSelfFetchInfo() {
				return selfFetchInfo;
			}
			public void setSelfFetchInfo(String selfFetchInfo) {
				this.selfFetchInfo = selfFetchInfo;
			}
			public String getAddressExtra() {
				return addressExtra;
			}
			public void setAddressExtra(String addressExtra) {
				this.addressExtra = addressExtra;
			}
			public String getDeliveryEndTime() {
				return deliveryEndTime;
			}
			public void setDeliveryEndTime(String deliveryEndTime) {
				this.deliveryEndTime = deliveryEndTime;
			}
			public String getDeliveryProvince() {
				return deliveryProvince;
			}
			public void setDeliveryProvince(String deliveryProvince) {
				this.deliveryProvince = deliveryProvince;
			}
			public String getReceiverName() {
				return receiverName;
			}
			public void setReceiverName(String receiverName) {
				this.receiverName = receiverName;
			}
			public String getDeliveryStartTime() {
				return deliveryStartTime;
			}
			public void setDeliveryStartTime(String deliveryStartTime) {
				this.deliveryStartTime = deliveryStartTime;
			}
			public String getReceiverTel() {
				return receiverTel;
			}
			public void setReceiverTel(String receiverTel) {
				this.receiverTel = receiverTel;
			}
			public String getDeliveryPostalCode() {
				return deliveryPostalCode;
			}
			public void setDeliveryPostalCode(String deliveryPostalCode) {
				this.deliveryPostalCode = deliveryPostalCode;
			}
			public String getDeliveryAddress() {
				return deliveryAddress;
			}
			public void setDeliveryAddress(String deliveryAddress) {
				this.deliveryAddress = deliveryAddress;
			}
			public String getDeliveryDistrict() {
				return deliveryDistrict;
			}
			public void setDeliveryDistrict(String deliveryDistrict) {
				this.deliveryDistrict = deliveryDistrict;
			}
		}
		
		public class Good{
			private String price;//price		商品原价
			private String title;//title		商品名称
			private String isPresent;//isPresent		是否为赠品
			private String totalFee;//totalFee		商品总价
			private String num;//num		商品数量
			private String picPath;//picPath		商品图片
			private String discountPrice;//discountPrice		单商品现价，减去了商品的优惠金额。单位：元
			private String pointsPrice;//pointsPrice		商品积分价，商品积分价（非积分商品则为0），如返回值是100则表示100积分。
			private String outerSkuId;
			private String outerItemId;
			private String skuPropertiesName;
			private String oid;
			public String getOid() {
				return oid;
			}
			public void setOid(String oid) {
				this.oid = oid;
			}
			public String getOuterSkuId() {
				return outerSkuId;
			}
			public void setOuterSkuId(String outerSkuId) {
				this.outerSkuId = outerSkuId;
			}
			public String getOuterItemId() {
				return outerItemId;
			}
			public void setOuterItemId(String outerItemId) {
				this.outerItemId = outerItemId;
			}
			public String getSkuPropertiesName() {
				return skuPropertiesName;
			}
			public void setSkuPropertiesName(String skuPropertiesName) {
				this.skuPropertiesName = skuPropertiesName;
			}
			public String getPrice() {
				return price;
			}
			public void setPrice(String price) {
				this.price = price;
			}
			public String getTitle() {
				return title;
			}
			public void setTitle(String title) {
				this.title = title;
			}
			public String getIsPresent() {
				return isPresent;
			}
			public void setIsPresent(String isPresent) {
				this.isPresent = isPresent;
			}
			public String getTotalFee() {
				return totalFee;
			}
			public void setTotalFee(String totalFee) {
				this.totalFee = totalFee;
			}
			public String getNum() {
				return num;
			}
			public void setNum(String num) {
				this.num = num;
			}
			public String getPicPath() {
				return picPath;
			}
			public void setPicPath(String picPath) {
				this.picPath = picPath;
			}
			public String getDiscountPrice() {
				return discountPrice;
			}
			public void setDiscountPrice(String discountPrice) {
				this.discountPrice = discountPrice;
			}
			public String getPointsPrice() {
				return pointsPrice;
			}
			public void setPointsPrice(String pointsPrice) {
				this.pointsPrice = pointsPrice;
			}
		}
	}
	
	
	
	
}
