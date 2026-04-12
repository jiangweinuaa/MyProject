package com.dsc.spos.waimai.entity;

public class orderPay
{
    private String item;

    private String payType="";
    
    private String payCode="";

    private String payCodeErp="";

    private String payName="";

    private String order_payCode="";

	private String mobile="";

    private String cardNo="";

	private String lpcardNo="";//禄品卡真实卡号


	private String ctType="";

    private String paySerNum="";

    private String serialNo="";

    private String refNo="";

    private String teriminalNo="";

    private String authCode;

    private String descore="0";

    private String pay;

    private String payDiscAmt;

    private String payAmt1;

    private String payAmt2;

    private String cardBeforeAmt;

    private String cardRemainAmt;

    private String extra="0";

    private String changed="0";

    private String isOrderPay;

    private String isOnlinePay;

    private double couponQty;

    private String isVerification;

    private String canInvoice;
    
    private String bDate="";
    
    private String funcNo="";
    
    private String paydoctype;//POS=4处理卡的扣款类型
    
    private String cardSendPay="0";//卡消费赠送金额
    
    private double merDiscount;//商户优惠金额，移动支付用，例如支付宝，微信等
    private double merReceive;//商家实收金额，移动支付用，例如支付宝，微信等
    private double thirdDiscount;//第三方优惠金额：移动支付用，例如支付宝，微信等
    private double custPayReal;//客户实付金额：移动支付用，例如支付宝，微信等
    private double couponMarketPrice;//券面值
    private double couponPrice;
	private String payChannelCode;//支付通道
	private double chargeAmount;//货郎农行，手续费
	private String password;// 企迈会员卡支付需要录入密码
	private String ecardSign;  // 企迈 ecardSign字段，0实体卡，1电子卡
	private String rnd1; //随机串1 -超港-乐享支付使用
	private String rnd2; //随机串2 -超港-乐享支付使用
    private String gainChannel;//企迈券所属渠道
    private String gainChannelName;//企迈券所属渠道
    
	
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

	public String getRnd1()
	{
		return rnd1;
	}

	public void setRnd1(String rnd1)
	{
		this.rnd1 = rnd1;
	}

	public String getRnd2()
	{
		return rnd2;
	}

	public void setRnd2(String rnd2)
	{
		this.rnd2 = rnd2;
	}

	public String getItem()
	{
		return item;
	}

	public void setItem(String item)
	{
		this.item = item;
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

	public String getLpcardNo()
	{
		return lpcardNo;
	}

	public void setLpcardNo(String lpcardNo)
	{
		this.lpcardNo = lpcardNo;
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

	public String getPayDiscAmt()
	{
		return payDiscAmt;
	}

	public void setPayDiscAmt(String payDiscAmt)
	{
		this.payDiscAmt = payDiscAmt;
	}

	public String getPayAmt1()
	{
		return payAmt1;
	}

	public void setPayAmt1(String payAmt1)
	{
		this.payAmt1 = payAmt1;
	}

	public String getPayAmt2()
	{
		return payAmt2;
	}

	public void setPayAmt2(String payAmt2)
	{
		this.payAmt2 = payAmt2;
	}

	public String getCardBeforeAmt()
	{
		return cardBeforeAmt;
	}

	public void setCardBeforeAmt(String cardBeforeAmt)
	{
		this.cardBeforeAmt = cardBeforeAmt;
	}

	public String getCardRemainAmt()
	{
		return cardRemainAmt;
	}

	public void setCardRemainAmt(String cardRemainAmt)
	{
		this.cardRemainAmt = cardRemainAmt;
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

	public double getCouponQty()
	{
		return couponQty;
	}

	public void setCouponQty(double couponQty)
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

	public String getCardSendPay()
	{
		return cardSendPay;
	}

	public void setCardSendPay(String cardSendPay)
	{
		this.cardSendPay = cardSendPay;
	}

	public double getMerDiscount()
	{
		return merDiscount;
	}

	public void setMerDiscount(double merDiscount)
	{
		this.merDiscount = merDiscount;
	}

	public double getMerReceive()
	{
		return merReceive;
	}

	public void setMerReceive(double merReceive)
	{
		this.merReceive = merReceive;
	}

	public double getThirdDiscount()
	{
		return thirdDiscount;
	}

	public void setThirdDiscount(double thirdDiscount)
	{
		this.thirdDiscount = thirdDiscount;
	}

	public double getCustPayReal()
	{
		return custPayReal;
	}

	public void setCustPayReal(double custPayReal)
	{
		this.custPayReal = custPayReal;
	}

	public double getCouponMarketPrice()
	{
		return couponMarketPrice;
	}

	public void setCouponMarketPrice(double couponMarketPrice)
	{
		this.couponMarketPrice = couponMarketPrice;
	}

	public double getCouponPrice()
	{
		return couponPrice;
	}

	public void setCouponPrice(double couponPrice)
	{
		this.couponPrice = couponPrice;
	}

	public String getPayChannelCode() {
		return payChannelCode;
	}

	public void setPayChannelCode(String payChannelCode) {
		this.payChannelCode = payChannelCode;
	}

	public double getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(double chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEcardSign() {
		return ecardSign;
	}
	
	public void setEcardSign(String ecardSign) {
		this.ecardSign = ecardSign;
	}
}
