package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

public class DCP_OrderModify_PayAdd_OpenReq extends JsonBasicReq
{
	private level1Elm request;
	
	public level1Elm getRequest() {
		return request;
	}
	
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
	public  class level1Elm
	{
		private String orderNo;
		private String loadDocType;
		private String machineNo;
		private String opNo;
		private String squadNo;
		private String workNo;
		private String opName;
        private String shopId;//当前追加订金的操作门店
		private List<level2Pay> pay;
		private List<order.CouponChange> couponChangeList;
        private List<order.otherCoupnPay> otherCoupnPayList;


        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
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
		public String getMachineNo()
		{
			return machineNo;
		}
		public void setMachineNo(String machineNo)
		{
			this.machineNo = machineNo;
		}
		public String getOpNo()
		{
			return opNo;
		}
		public void setOpNo(String opNo)
		{
			this.opNo = opNo;
		}
		public String getSquadNo()
		{
			return squadNo;
		}
		public void setSquadNo(String squadNo)
		{
			this.squadNo = squadNo;
		}
		public String getWorkNo()
		{
			return workNo;
		}
		public void setWorkNo(String workNo)
		{
			this.workNo = workNo;
		}
		public String getOpName()
		{
			return opName;
		}
		public void setOpName(String opName)
		{
			this.opName = opName;
		}
		public List<level2Pay> getPay() {
			return pay;
		}
		public void setPay(List<level2Pay> pay) {
			this.pay = pay;
		}
		public List<order.CouponChange> getCouponChangeList() {
			return couponChangeList;
		}
		public void setCouponChangeList(List<order.CouponChange> couponChangeList) {
			this.couponChangeList = couponChangeList;
		}

        public List<order.otherCoupnPay> getOtherCoupnPayList()
        {
            return otherCoupnPayList;
        }

        public void setOtherCoupnPayList(List<order.otherCoupnPay> otherCoupnPayList)
        {
            this.otherCoupnPayList = otherCoupnPayList;
        }
    }
	
	public  class level2Pay
	{
		private String item;
		private String funcNo;
		private String payType;
		private String payCode;
		private String payCodeErp;
		private String payName;
		private String order_payCode;
		private String mobile;
		private String cardNo;
		private String ctType;
		private String paySerNum;
		private String serialNo;
		private String refNo;
		private String teriminalNo;
		private String authCode;
		private String descore;
		
		private String pay;
		private String extra;
		private String changed;
		
		private String isOrderPay;//是否订金（Y/N）
		private String isOnlinePay;
		private String couponQty;
		private String isVerification;
		private String canInvoice;
		private String bDate;
		
		private String merDiscount;//商户优惠金额，移动支付用，例如支付宝，微信等
		private String merReceive;//商家实收金额，移动支付用，例如支付宝，微信等
		private String thirdDiscount;//第三方优惠金额：移动支付用，例如支付宝，微信等
		private String custPayReal;//客户实付金额：移动支付用，例如支付宝，微信等
		private String couponMarketPrice;//券面值
		private String couponPrice;//券售价
		private String payChannelCode;
		private String ecardSign; //企迈 ecardSign字段，0实体卡，1电子卡
		private String gainChannel; //企迈券渠道编码
		private String gainChannelName;//企迈券渠道名称
		
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
		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}
		public String getFuncNo()
		{
			return funcNo;
		}
		public void setFuncNo(String funcNo)
		{
			this.funcNo = funcNo;
		}
		public String getPayType()
		{
			return payType;
		}
		public void setPayType(String payType)
		{
			this.payType = payType;
		}
		public String getPayCode()
		{
			return payCode;
		}
		public void setPayCode(String payCode)
		{
			this.payCode = payCode;
		}
		public String getPayCodeErp()
		{
			return payCodeErp;
		}
		public void setPayCodeErp(String payCodeErp)
		{
			this.payCodeErp = payCodeErp;
		}
		public String getPayName()
		{
			return payName;
		}
		public void setPayName(String payName)
		{
			this.payName = payName;
		}
		public String getOrder_payCode()
		{
			return order_payCode;
		}
		public void setOrder_payCode(String order_payCode)
		{
			this.order_payCode = order_payCode;
		}
		public String getMobile()
		{
			return mobile;
		}
		public void setMobile(String mobile)
		{
			this.mobile = mobile;
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
		public String getPaySerNum()
		{
			return paySerNum;
		}
		public void setPaySerNum(String paySerNum)
		{
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
		public String getAuthCode()
		{
			return authCode;
		}
		public void setAuthCode(String authCode)
		{
			this.authCode = authCode;
		}
		public String getDescore()
		{
			return descore;
		}
		public void setDescore(String descore)
		{
			this.descore = descore;
		}
		public String getPay()
		{
			return pay;
		}
		public void setPay(String pay)
		{
			this.pay = pay;
		}
		public String getExtra()
		{
			return extra;
		}
		public void setExtra(String extra)
		{
			this.extra = extra;
		}
		public String getChanged()
		{
			return changed;
		}
		public void setChanged(String changed)
		{
			this.changed = changed;
		}
		public String getIsOrderPay()
		{
			return isOrderPay;
		}
		public void setIsOrderPay(String isOrderPay)
		{
			this.isOrderPay = isOrderPay;
		}
		public String getIsOnlinePay()
		{
			return isOnlinePay;
		}
		public void setIsOnlinePay(String isOnlinePay)
		{
			this.isOnlinePay = isOnlinePay;
		}
		public String getCouponQty()
		{
			return couponQty;
		}
		public void setCouponQty(String couponQty)
		{
			this.couponQty = couponQty;
		}
		public String getIsVerification()
		{
			return isVerification;
		}
		public void setIsVerification(String isVerification)
		{
			this.isVerification = isVerification;
		}
		public String getCanInvoice()
		{
			return canInvoice;
		}
		public void setCanInvoice(String canInvoice)
		{
			this.canInvoice = canInvoice;
		}
		public String getbDate()
		{
			return bDate;
		}
		public void setbDate(String bDate)
		{
			this.bDate = bDate;
		}
		public String getMerDiscount()
		{
			return merDiscount;
		}
		public void setMerDiscount(String merDiscount)
		{
			this.merDiscount = merDiscount;
		}
		public String getMerReceive()
		{
			return merReceive;
		}
		public void setMerReceive(String merReceive)
		{
			this.merReceive = merReceive;
		}
		public String getThirdDiscount()
		{
			return thirdDiscount;
		}
		public void setThirdDiscount(String thirdDiscount)
		{
			this.thirdDiscount = thirdDiscount;
		}
		public String getCustPayReal()
		{
			return custPayReal;
		}
		public void setCustPayReal(String custPayReal)
		{
			this.custPayReal = custPayReal;
		}
		public String getCouponMarketPrice()
		{
			return couponMarketPrice;
		}
		public void setCouponMarketPrice(String couponMarketPrice)
		{
			this.couponMarketPrice = couponMarketPrice;
		}
		public String getCouponPrice()
		{
			return couponPrice;
		}
		public void setCouponPrice(String couponPrice)
		{
			this.couponPrice = couponPrice;
		}
		public String getPayChannelCode() {
			return payChannelCode;
		}
		public void setPayChannelCode(String payChannelCode) {
			this.payChannelCode = payChannelCode;
		}
		public String getEcardSign() {
			return ecardSign;
		}
		public void setEcardSign(String ecardSign) {
			this.ecardSign = ecardSign;
		}
	}
	
}
