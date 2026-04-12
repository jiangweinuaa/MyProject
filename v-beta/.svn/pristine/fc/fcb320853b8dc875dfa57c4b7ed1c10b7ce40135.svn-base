package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;


public class DCP_OrderRefundReq extends JsonBasicReq 
{

	private levelRequest request;

	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String eId;
		private String shopId;
		private String opNo;		
		private String opName;
		
		/**
		 * 退单类型 0：全退（全部商品（未出货和已出货都退）),1：仅退未出货商品（POS操作退订）,2：仅退已出货商品（POS操作退销售单）
		 */
		private String refundType;
		private String orderNo;
		private String loadDocType;
        private String partnerMember; // digiwin  鼎捷    qimai企迈 ，空为鼎捷
        private String channelId;
		private String pickGoodsRefundType;
		private String refundReasonNo;
		private String refundReasonName;
		private String refundReason;
		private String invOperateType;
		private String refundBdate;
		private String refundDatetime;
		private String tot_amt;
        private String machineNo;
        private String squadNo;
        private String workNo;
        private String refundOrderNo;
        private String deliveryType;
        private String deliveryNo;
        private String isRefundDeliverAmt;//是否退运费 0否1是
		
		private List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> goods;
		private List<levelPay> pay;
		public String getIsRefundDeliverAmt() {
			return isRefundDeliverAmt;
		}
		public void setIsRefundDeliverAmt(String isRefundDeliverAmt) {
			this.isRefundDeliverAmt = isRefundDeliverAmt;
		}
		public String geteId()
		{
			return eId;
		}
		public void seteId(String eId)
		{
			this.eId = eId;
		}
		public String getShopId()
		{
			return shopId;
		}
		public void setShopId(String shopId)
		{
			this.shopId = shopId;
		}
		public String getOpNo()
		{
			return opNo;
		}
		public void setOpNo(String opNo)
		{
			this.opNo = opNo;
		}
		public String getOpName()
		{
			return opName;
		}
		public void setOpName(String opName)
		{
			this.opName = opName;
		}
		public String getRefundType()
		{
			return refundType;
		}
		public void setRefundType(String refundType)
		{
			this.refundType = refundType;
		}
		public String getOrderNo()
		{
			return orderNo;
		}
		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}
		public String getLoadDocType()
		{
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType)
		{
			this.loadDocType = loadDocType;
		}
		public String getPartnerMember() {
            return partnerMember;
        }
        public void setPartnerMember(String partnerMember) {
            this.partnerMember = partnerMember;
        }
        public String getChannelId()
		{
			return channelId;
		}
		public void setChannelId(String channelId)
		{
			this.channelId = channelId;
		}
		public String getPickGoodsRefundType()
		{
			return pickGoodsRefundType;
		}
		public void setPickGoodsRefundType(String pickGoodsRefundType)
		{
			this.pickGoodsRefundType = pickGoodsRefundType;
		}
		public String getRefundReasonNo()
		{
			return refundReasonNo;
		}
		public void setRefundReasonNo(String refundReasonNo)
		{
			this.refundReasonNo = refundReasonNo;
		}
		public String getRefundReasonName()
		{
			return refundReasonName;
		}
		public void setRefundReasonName(String refundReasonName)
		{
			this.refundReasonName = refundReasonName;
		}
		public String getRefundReason()
		{
			return refundReason;
		}
		public void setRefundReason(String refundReason)
		{
			this.refundReason = refundReason;
		}
		public String getInvOperateType()
		{
			return invOperateType;
		}
		public void setInvOperateType(String invOperateType)
		{
			this.invOperateType = invOperateType;
		}
		public String getRefundBdate()
		{
			return refundBdate;
		}
		public void setRefundBdate(String refundBdate)
		{
			this.refundBdate = refundBdate;
		}
		public String getRefundDatetime()
		{
			return refundDatetime;
		}
		public void setRefundDatetime(String refundDatetime)
		{
			this.refundDatetime = refundDatetime;
		}
		public String getTot_amt()
		{
			return tot_amt;
		}
		public void setTot_amt(String tot_amt)
		{
			this.tot_amt = tot_amt;
		}
		public List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> getGoods() {
			return goods;
		}
		public void setGoods(List<DCP_OrderRefundAgreeOrRejectReq.levelGoods> goods) {
			this.goods = goods;
		}
		public List<levelPay> getPay()
		{
			return pay;
		}
		public void setPay(List<levelPay> pay)
		{
			this.pay = pay;
		}

        public String getMachineNo() {
            return machineNo;
        }

        public void setMachineNo(String machineNo) {
            this.machineNo = machineNo;
        }

        public String getSquadNo() {
            return squadNo;
        }

        public void setSquadNo(String squadNo) {
            this.squadNo = squadNo;
        }

        public String getWorkNo() {
            return workNo;
        }

        public void setWorkNo(String workNo) {
            this.workNo = workNo;
        }

        public String getRefundOrderNo() {
            return refundOrderNo;
        }

        public void setRefundOrderNo(String refundOrderNo) {
            this.refundOrderNo = refundOrderNo;
        }

        public String getDeliveryType() {
            return deliveryType;
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = deliveryType;
        }

        public String getDeliveryNo() {
            return deliveryNo;
        }

        public void setDeliveryNo(String deliveryNo) {
            this.deliveryNo = deliveryNo;
        }
    }
	
	public class levelGoods{
		
	}
	
	public class levelPay
	{
		private String item;
		private String payCode;
		private String payCodeerp;		
		private String payName;
		private String cardNo;
		private String ctType;
		private String payType;
		private String pay_channel;
		private String payChannelCode;
		
		/**
		 * 支付订单号
		 */
		private String paySerNum;
		private String serialNo;
		private String refNo;
		private String teriminalNo;
		private double descore;
		private double pay;
		private double extra;
		private double changed;
		private double couponQty;
		
		/**
		 * 收银营业日期(日期格式yyyyMMdd)
		 */
		private String bdate;
		
		/**
		 * 是否订金 Y-是,N-否
		 */
		private String isOrderpay;
		
		/**
		 * 是否平台支付 Y-是,N-否
		 */
		private String isOnlinePay;
		
		/**
		 * 平台支付编码
		 */
		private String order_PayCode;
		
		/**
		 * 付款门店
		 */
		private String payShop;
		
		/**
		 * 付款来源平台类型
		 */
		private String payLoadDocType;
		
		private String funcNo;//功能编码		
		private String paydoctype;//POS=4处理卡的扣款类型
        private String password;// 禄品卡录入密码

		//【ID1037176】【阿哆诺斯】券退卡功能，订单退单也需要券退卡----服务  by jinzma 20231129
		private String refundType;   //订单使用券支付时，退券券如何处理（0-原券激活 1-券退到卡上 ）
		private String refundCardNo; //券退卡时传入卡号
		private String refundAmt;    //券退卡时传入金额
		
		private String gainChannel;
		private String gainChannelName;
		
		
		public String getGainChannel() {
			return gainChannel;
		}

		public void setGainChannel(String gainChannel) {
			this.gainChannel = gainChannel;
		}

		public String getGainChannelName() {
			return gainChannelName;
		}

		public void setGainChannelName(String gainChannelName) {
			this.gainChannelName = gainChannelName;
		}

		public String getPayType() {
			return payType;
		}

		public void setPayType(String payType) {
			this.payType = payType;
		}

		public String getPay_channel() {
			return pay_channel;
		}

		public void setPay_channel(String pay_channel) {
			this.pay_channel = pay_channel;
		}

		public String getPayChannelCode() {
			return payChannelCode;
		}

		public void setPayChannelCode(String payChannelCode) {
			this.payChannelCode = payChannelCode;
		}

		public String getItem()
		{
			return item;
		}

		public void setItem(String item)
		{
			this.item = item;
		}

		public String getPayCode()
		{
			return payCode;
		}

		public void setPayCode(String payCode)
		{
			this.payCode = payCode;
		}

		public String getPayCodeerp()
		{
			return payCodeerp;
		}

		public void setPayCodeerp(String payCodeerp)
		{
			this.payCodeerp = payCodeerp;
		}

		public String getPayName()
		{
			return payName;
		}

		public void setPayName(String payName)
		{
			this.payName = payName;
		}

		public String getCardNo()
		{
			return cardNo;
		}

		public void setCardNo(String cardNo)
		{
			this.cardNo = cardNo;
		}

		public String getCtType()
		{
			return ctType;
		}

		public void setCtType(String ctType)
		{
			this.ctType = ctType;
		}
		
		public String getPaySerNum() {
			return paySerNum;
		}

		public void setPaySerNum(String paySerNum) {
			this.paySerNum = paySerNum;
		}

		public String getSerialNo()
		{
			return serialNo;
		}

		public void setSerialNo(String serialNo)
		{
			this.serialNo = serialNo;
		}

		public String getRefNo()
		{
			return refNo;
		}

		public void setRefNo(String refNo)
		{
			this.refNo = refNo;
		}

		public String getTeriminalNo()
		{
			return teriminalNo;
		}

		public void setTeriminalNo(String teriminalNo)
		{
			this.teriminalNo = teriminalNo;
		}

		public double getDescore()
		{
			return descore;
		}

		public void setDescore(double descore)
		{
			this.descore = descore;
		}

		public double getPay()
		{
			return pay;
		}

		public void setPay(double pay)
		{
			this.pay = pay;
		}

		public double getExtra()
		{
			return extra;
		}

		public void setExtra(double extra)
		{
			this.extra = extra;
		}

		public double getChanged()
		{
			return changed;
		}

		public void setChanged(double changed)
		{
			this.changed = changed;
		}
		
		public double getCouponQty()
		{
			return couponQty;
		}

		public void setCouponQty(double couponQty)
		{
			this.couponQty = couponQty;
		}

		public String getBdate()
		{
			return bdate;
		}

		public void setBdate(String bdate)
		{
			this.bdate = bdate;
		}

		public String getIsOrderpay()
		{
			return isOrderpay;
		}

		public void setIsOrderpay(String isOrderpay)
		{
			this.isOrderpay = isOrderpay;
		}

		public String getIsOnlinePay()
		{
			return isOnlinePay;
		}

		public void setIsOnlinePay(String isOnlinePay)
		{
			this.isOnlinePay = isOnlinePay;
		}

		public String getOrder_PayCode()
		{
			return order_PayCode;
		}

		public void setOrder_PayCode(String order_PayCode)
		{
			this.order_PayCode = order_PayCode;
		}

		public String getPayShop()
		{
			return payShop;
		}

		public void setPayShop(String payShop)
		{
			this.payShop = payShop;
		}

		public String getPayLoadDocType()
		{
			return payLoadDocType;
		}

		public void setPayLoadDocType(String payLoadDocType)
		{
			this.payLoadDocType = payLoadDocType;
		}

		public String getFuncNo()
		{
			return funcNo;
		}

		public void setFuncNo(String funcNo)
		{
			this.funcNo = funcNo;
		}

		public String getPaydoctype()
		{
			return paydoctype;
		}

		public void setPaydoctype(String paydoctype)
		{
			this.paydoctype = paydoctype;
		}

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }
		
		public String getRefundType() {
			return refundType;
		}
		
		public void setRefundType(String refundType) {
			this.refundType = refundType;
		}
		
		public String getRefundCardNo() {
			return refundCardNo;
		}
		
		public void setRefundCardNo(String refundCardNo) {
			this.refundCardNo = refundCardNo;
		}
		
		public String getRefundAmt() {
			return refundAmt;
		}
		
		public void setRefundAmt(String refundAmt) {
			this.refundAmt = refundAmt;
		}
	}
}
