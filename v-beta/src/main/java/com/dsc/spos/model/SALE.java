package com.dsc.spos.model;

import java.util.List;

/**
 * 公用类：SALE
 * 說明：销售单保存
 * @author jinzma 
 * @since  2019-04-18
 */

public class SALE 
{
	private String eId;
	private String shopId;
	private String verNum;	
	private String legalper;//法人
	private String machine;
	private String type;//单据类型:0-销售单1-凭单退货2-无单退货
	private String typeName;//单据类型名称
	private String bDate;//营业日期
	private String squadNO;//班次流水ID
	private String workNO;//班别编码
	private String opNO;//用户编码
	private String authorizerOpno;//授权人编码
	private String teriminalID;//终端ID	
	private String oShop;//原单门店
	private String oMachine;//原单机台号	
	private String oType;//原单类型
	private String ofNO;//原单号=主单号=order_id
	private String sourceSubOrderno;//原子单号
	private String otrno;//原单流水号
	private String oBdate;//原单营业日期
	private String approval;//钉钉审批id	
	private String cardNO;//会员卡号
	private String memberId;//会员编号
	private String memberName;//会员姓名
	private String cardTypeId;//卡类型编码
	private String cardAmount;//初始余额	
	private String pointQty;//本次积分
	private String remainPoint;//剩余积分	
	private String memberOrderno;//会员消费单号	
	private String orderShop;//下订门店
	private String contMan;//订货人
	private String contTel;//订货人电话
	private String getMode;//取货模式 宅配/超取(第三方门店)
	private String getShop;//取货门店(第三方门店)
	private String getMan;//收货人
	private String getManTel;//收货人联系方式
	private String shipAdd;//配送地址
	private String gDate;//配送日期
	private String gTime;//配送时间
	private String manualNO;//手工单号
	private String mealNumber;//用餐人数/大人数量
	private String childNumber;//儿童数量
	private String memo;//订单备注
	private String ecsFlg;//订单结案否Y/N
	private String ecsDate;//订单结案日期
	private String distribution;//配送人员	
	private String sendMemo;//配送备注
	private String tableNO;//桌台号	
	private String openerId;//开台人
	private String tableKind;//桌台类型
	private String guestNum;//桌台人数
	private String repastType;//就餐类型：0堂食1打包2外卖
	private String dinnerDate;//就餐日期
	private String dinnerTime;//就餐时间
	private String dinnerSign;//就餐牌号码
	private String dinnerType;//餐饮单据状态	
	private String tourCountryCode;//国家编码
	private String tourTravelNO;//旅行社编码
	private String tourGroupNO;//团号
	private String tourGuideNO;//导游编号
	private String tourPeopleNum;//进店人数
	private String totQty;//总数量
	private String totOldAmt;//原价金额
	private String totDisc;//折扣总金额saleDisc+payDisc
    private String saleAmt;//销售金额:非子商品的AMT累加
	private String saleDisc;//销售折扣金额:非子商品的DISC累加
	private String payDisc;//付款折扣金额	
	private String totAmt;//成交金额
	private String servCharge;//餐厅服务费
	private String orderAmount;//订金金额	
	private String freeCode;//零税证号-只有外交官才有的凭证--零税的其中一种凭证
	private String passport;//护照编号-零税的其中一种凭证
	private String isInvoice;//是否需要发票Y/N
	private String invoiceTitle;//发票抬头
	private String invoiceBank;//发票开户行
	private String invoiceAccount;//发票开户账号
	private String invoiceTel;//发票联系方式
	private String invoiceAddr;//发票公司地址
	private String taxRegNumber;//纳税人识别号
	private String sellCredit;//赊销标识
	private String customerNO;//大客户编号-赊销
	private String customerName;//大客户名称-赊销
	private String payAmt;//已收金额	
	private String eraseAmt;//整单抹零金额
	private String totChanged;//找零金额
	private String oInvstartno;//原发票号码
	private String isInvoiceMakeOut;//是否已开票
	private String invSplitType;//发票开票拆分类型：1不拆分 2按商品拆分 3按金额拆分
	private String invCount;//发票数量
	private String isTakeout;//是否外卖订单
	private String takeAway;//外卖平台
	private String orderID;//订单号=主单号
	private String orderSn;//外卖订单流水号
	private String platformDisc;//外卖：平台承担优惠金额
	private String sellerDisc;//外卖：商户承担优惠金额	
	private String packageFee;//外卖：打包费
	private String shippingFee;//外卖：配送费
	private String deliveryFeeShop;//外卖：门店承担配送费
	private String deliveryFeeUser;//外卖：用户承担配送费
	private String wmUserPaid;//外卖：用户实际支付
	private String platformFee;//外卖：服务费(平台向门店收取)
	private String wmExtraFee;//外卖：平台其他费用(每单捐赠1分钱支持环保之类的活动)
	private String shopIncome;//外卖：商家实际到账金额
	private String productionMode;//生产方式：0本店制作1异店制作2总部制作
	private String productionShop;//生产门店
	private String isBuff;//是否暂存
	private String isBuff_timeout;//暂存超时时间
	private String ecCustomerNO;//台湾电商订单归属客户代码
	private String status;//有效否:100-有效0-无效
	private String isReturn;//是否已退Y/N
	private String returnUserId;//退货收银员
	private String bsno;//退货理由码
	private String sDate;//系统日期
	private String sTime;//系统时间
	private String evaluate;//是否评价0-否1-是
	private String isUploaded;//是否已传第三方（例如商场）Y已上传，N未上传
	private String rsvId;//预订ID
	private String orderReturn;//订金退款类型：0:沒有訂金1:部份訂金2:全部訂金【CM专用】
	private String order_companyId;//下单公司编码
	private String order_channelId;//下单渠道编码
	private String order_appType;//下单应用类型
	private String oOrder_companyId;//来源公司编码
	private String oOrder_channelId;//来源渠道编码
	private String oOrder_appType;//来源应用类型
	private String wxOpenId;//第三方用户id
	private String tran_time;//传输时间
	private String tot_Amt_merReceive;//商家实收金额(含税)=TOT_OLDAMT-TOT_DISC_MERRECEIVE
	private String totDisc_merReceive;//商家支付折扣金额(含税)[单身所有商品的商家支付折扣合计]
	private String tot_Amt_custPayReal;//顾客实付金额[含税][TOT_OLDAMT-TOT_DISC_CUSTPAYREAL]
	private String totDisc_custPayReal;//顾客实付折扣金额[含税][单身所有商品的顾客实付折扣合计]
	private String isMerPay;//配送费是否商家结算Y/N
	private String departNo;//部门编码
    private String waimaiMerreceiveMode;//外卖订单商户实收计算模式：0-商品金额+打包盒-商户优惠，1-外卖平台店铺收入
	private String trno;//流水号
    private String groupBuying;
	private List<Detail> details;
	private List<Payment> pays;
	private String partnerMember;
	
	
	


	public String getPartnerMember() {
		return partnerMember;
	}

	public void setPartnerMember(String partnerMember) {
		this.partnerMember = partnerMember;
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

	public String getVerNum()
	{
		return verNum;
	}

	public void setVerNum(String verNum)
	{
		this.verNum = verNum;
	}

	public String getLegalper()
	{
		return legalper;
	}

	public void setLegalper(String legalper)
	{
		this.legalper = legalper;
	}

	public String getMachine()
	{
		return machine;
	}

	public void setMachine(String machine)
	{
		this.machine = machine;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public String getbDate()
	{
		return bDate;
	}

	public void setbDate(String bDate)
	{
		this.bDate = bDate;
	}

	public String getSquadNO()
	{
		return squadNO;
	}

	public void setSquadNO(String squadNO)
	{
		this.squadNO = squadNO;
	}

	public String getWorkNO()
	{
		return workNO;
	}

	public void setWorkNO(String workNO)
	{
		this.workNO = workNO;
	}

	public String getOpNO()
	{
		return opNO;
	}

	public void setOpNO(String opNO)
	{
		this.opNO = opNO;
	}

	public String getAuthorizerOpno()
	{
		return authorizerOpno;
	}

	public void setAuthorizerOpno(String authorizerOpno)
	{
		this.authorizerOpno = authorizerOpno;
	}

	public String getTeriminalID()
	{
		return teriminalID;
	}

	public void setTeriminalID(String teriminalID)
	{
		this.teriminalID = teriminalID;
	}

	public String getoShop()
	{
		return oShop;
	}

	public void setoShop(String oShop)
	{
		this.oShop = oShop;
	}

	public String getoMachine()
	{
		return oMachine;
	}

	public void setoMachine(String oMachine)
	{
		this.oMachine = oMachine;
	}

	public String getoType()
	{
		return oType;
	}

	public void setoType(String oType)
	{
		this.oType = oType;
	}

	public String getOfNO()
	{
		return ofNO;
	}

	public void setOfNO(String ofNO)
	{
		this.ofNO = ofNO;
	}

	public String getSourceSubOrderno()
	{
		return sourceSubOrderno;
	}

	public void setSourceSubOrderno(String sourceSubOrderno)
	{
		this.sourceSubOrderno = sourceSubOrderno;
	}

	public String getOtrno()
	{
		return otrno;
	}

	public void setOtrno(String otrno)
	{
		this.otrno = otrno;
	}

	public String getoBdate()
	{
		return oBdate;
	}

	public void setoBdate(String oBdate)
	{
		this.oBdate = oBdate;
	}

	public String getApproval()
	{
		return approval;
	}

	public void setApproval(String approval)
	{
		this.approval = approval;
	}

	public String getCardNO()
	{
		return cardNO;
	}

	public void setCardNO(String cardNO)
	{
		this.cardNO = cardNO;
	}

	public String getMemberId()
	{
		return memberId;
	}

	public void setMemberId(String memberId)
	{
		this.memberId = memberId;
	}

	public String getMemberName()
	{
		return memberName;
	}

	public void setMemberName(String memberName)
	{
		this.memberName = memberName;
	}

	public String getCardTypeId()
	{
		return cardTypeId;
	}

	public void setCardTypeId(String cardTypeId)
	{
		this.cardTypeId = cardTypeId;
	}

	public String getCardAmount()
	{
		return cardAmount;
	}

	public void setCardAmount(String cardAmount)
	{
		this.cardAmount = cardAmount;
	}

	public String getPointQty()
	{
		return pointQty;
	}

	public void setPointQty(String pointQty)
	{
		this.pointQty = pointQty;
	}

	public String getRemainPoint()
	{
		return remainPoint;
	}

	public void setRemainPoint(String remainPoint)
	{
		this.remainPoint = remainPoint;
	}

	public String getMemberOrderno()
	{
		return memberOrderno;
	}

	public void setMemberOrderno(String memberOrderno)
	{
		this.memberOrderno = memberOrderno;
	}

	public String getOrderShop()
	{
		return orderShop;
	}

	public void setOrderShop(String orderShop)
	{
		this.orderShop = orderShop;
	}

	public String getContMan()
	{
		return contMan;
	}

	public void setContMan(String contMan)
	{
		this.contMan = contMan;
	}

	public String getContTel()
	{
		return contTel;
	}

	public void setContTel(String contTel)
	{
		this.contTel = contTel;
	}

	public String getGetMode()
	{
		return getMode;
	}

	public void setGetMode(String getMode)
	{
		this.getMode = getMode;
	}

	public String getGetShop()
	{
		return getShop;
	}

	public void setGetShop(String getShop)
	{
		this.getShop = getShop;
	}

	public String getGetMan()
	{
		return getMan;
	}

	public void setGetMan(String getMan)
	{
		this.getMan = getMan;
	}

	public String getGetManTel()
	{
		return getManTel;
	}

	public void setGetManTel(String getManTel)
	{
		this.getManTel = getManTel;
	}

	public String getShipAdd()
	{
		return shipAdd;
	}

	public void setShipAdd(String shipAdd)
	{
		this.shipAdd = shipAdd;
	}

	public String getgDate()
	{
		return gDate;
	}

	public void setgDate(String gDate)
	{
		this.gDate = gDate;
	}

	public String getgTime()
	{
		return gTime;
	}

	public void setgTime(String gTime)
	{
		this.gTime = gTime;
	}

	public String getManualNO()
	{
		return manualNO;
	}

	public void setManualNO(String manualNO)
	{
		this.manualNO = manualNO;
	}

	public String getMealNumber()
	{
		return mealNumber;
	}

	public void setMealNumber(String mealNumber)
	{
		this.mealNumber = mealNumber;
	}

	public String getChildNumber()
	{
		return childNumber;
	}

	public void setChildNumber(String childNumber)
	{
		this.childNumber = childNumber;
	}

	public String getMemo()
	{
		return memo;
	}

	public void setMemo(String memo)
	{
		this.memo = memo;
	}

	public String getEcsFlg()
	{
		return ecsFlg;
	}

	public void setEcsFlg(String ecsFlg)
	{
		this.ecsFlg = ecsFlg;
	}

	public String getEcsDate()
	{
		return ecsDate;
	}

	public void setEcsDate(String ecsDate)
	{
		this.ecsDate = ecsDate;
	}

	public String getDistribution()
	{
		return distribution;
	}

	public void setDistribution(String distribution)
	{
		this.distribution = distribution;
	}

	public String getSendMemo()
	{
		return sendMemo;
	}

	public void setSendMemo(String sendMemo)
	{
		this.sendMemo = sendMemo;
	}

	public String getTableNO()
	{
		return tableNO;
	}

	public void setTableNO(String tableNO)
	{
		this.tableNO = tableNO;
	}

	public String getOpenerId()
	{
		return openerId;
	}

	public void setOpenerId(String openerId)
	{
		this.openerId = openerId;
	}

	public String getTableKind()
	{
		return tableKind;
	}

	public void setTableKind(String tableKind)
	{
		this.tableKind = tableKind;
	}

	public String getGuestNum()
	{
		return guestNum;
	}

	public void setGuestNum(String guestNum)
	{
		this.guestNum = guestNum;
	}

	public String getRepastType()
	{
		return repastType;
	}

	public void setRepastType(String repastType)
	{
		this.repastType = repastType;
	}

	public String getDinnerDate()
	{
		return dinnerDate;
	}

	public void setDinnerDate(String dinnerDate)
	{
		this.dinnerDate = dinnerDate;
	}

	public String getDinnerTime()
	{
		return dinnerTime;
	}

	public void setDinnerTime(String dinnerTime)
	{
		this.dinnerTime = dinnerTime;
	}

	public String getDinnerSign()
	{
		return dinnerSign;
	}

	public void setDinnerSign(String dinnerSign)
	{
		this.dinnerSign = dinnerSign;
	}

	public String getDinnerType()
	{
		return dinnerType;
	}

	public void setDinnerType(String dinnerType)
	{
		this.dinnerType = dinnerType;
	}

	public String getTourCountryCode()
	{
		return tourCountryCode;
	}

	public void setTourCountryCode(String tourCountryCode)
	{
		this.tourCountryCode = tourCountryCode;
	}

	public String getTourTravelNO()
	{
		return tourTravelNO;
	}

	public void setTourTravelNO(String tourTravelNO)
	{
		this.tourTravelNO = tourTravelNO;
	}

	public String getTourGroupNO()
	{
		return tourGroupNO;
	}

	public void setTourGroupNO(String tourGroupNO)
	{
		this.tourGroupNO = tourGroupNO;
	}

	public String getTourGuideNO()
	{
		return tourGuideNO;
	}

	public void setTourGuideNO(String tourGuideNO)
	{
		this.tourGuideNO = tourGuideNO;
	}

	public String getTourPeopleNum()
	{
		return tourPeopleNum;
	}

	public void setTourPeopleNum(String tourPeopleNum)
	{
		this.tourPeopleNum = tourPeopleNum;
	}

	public String getTotQty()
	{
		return totQty;
	}

	public void setTotQty(String totQty)
	{
		this.totQty = totQty;
	}

	public String getTotOldAmt()
	{
		return totOldAmt;
	}

	public void setTotOldAmt(String totOldAmt)
	{
		this.totOldAmt = totOldAmt;
	}

	public String getTotDisc()
	{
		return totDisc;
	}

	public void setTotDisc(String totDisc)
	{
		this.totDisc = totDisc;
	}

    public String getSaleAmt()
    {
        return saleAmt;
    }

    public void setSaleAmt(String saleAmt)
    {
        this.saleAmt = saleAmt;
    }

    public String getSaleDisc()
	{
		return saleDisc;
	}

	public void setSaleDisc(String saleDisc)
	{
		this.saleDisc = saleDisc;
	}

	public String getPayDisc()
	{
		return payDisc;
	}

	public void setPayDisc(String payDisc)
	{
		this.payDisc = payDisc;
	}

	public String getTotAmt()
	{
		return totAmt;
	}

	public void setTotAmt(String totAmt)
	{
		this.totAmt = totAmt;
	}

	public String getServCharge()
	{
		return servCharge;
	}

	public void setServCharge(String servCharge)
	{
		this.servCharge = servCharge;
	}

	public String getOrderAmount()
	{
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount)
	{
		this.orderAmount = orderAmount;
	}

	public String getFreeCode()
	{
		return freeCode;
	}

	public void setFreeCode(String freeCode)
	{
		this.freeCode = freeCode;
	}

	public String getPassport()
	{
		return passport;
	}

	public void setPassport(String passport)
	{
		this.passport = passport;
	}

	public String getIsInvoice()
	{
		return isInvoice;
	}

	public void setIsInvoice(String isInvoice)
	{
		this.isInvoice = isInvoice;
	}

	public String getInvoiceTitle()
	{
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle)
	{
		this.invoiceTitle = invoiceTitle;
	}

	public String getInvoiceBank()
	{
		return invoiceBank;
	}

	public void setInvoiceBank(String invoiceBank)
	{
		this.invoiceBank = invoiceBank;
	}

	public String getInvoiceAccount()
	{
		return invoiceAccount;
	}

	public void setInvoiceAccount(String invoiceAccount)
	{
		this.invoiceAccount = invoiceAccount;
	}

	public String getInvoiceTel()
	{
		return invoiceTel;
	}

	public void setInvoiceTel(String invoiceTel)
	{
		this.invoiceTel = invoiceTel;
	}

	public String getInvoiceAddr()
	{
		return invoiceAddr;
	}

	public void setInvoiceAddr(String invoiceAddr)
	{
		this.invoiceAddr = invoiceAddr;
	}

	public String getTaxRegNumber()
	{
		return taxRegNumber;
	}

	public void setTaxRegNumber(String taxRegNumber)
	{
		this.taxRegNumber = taxRegNumber;
	}

	public String getSellCredit()
	{
		return sellCredit;
	}

	public void setSellCredit(String sellCredit)
	{
		this.sellCredit = sellCredit;
	}

	public String getCustomerNO()
	{
		return customerNO;
	}

	public void setCustomerNO(String customerNO)
	{
		this.customerNO = customerNO;
	}

	public String getCustomerName()
	{
		return customerName;
	}

	public void setCustomerName(String customerName)
	{
		this.customerName = customerName;
	}

	public String getPayAmt()
	{
		return payAmt;
	}

	public void setPayAmt(String payAmt)
	{
		this.payAmt = payAmt;
	}
	
	public String getGroupBuying() {
		return groupBuying;
	}
	
	public void setGroupBuying(String groupBuying) {
		this.groupBuying = groupBuying;
	}
	
	public String getEraseAmt()
	{
		return eraseAmt;
	}

	public void setEraseAmt(String eraseAmt)
	{
		this.eraseAmt = eraseAmt;
	}

	public String getTotChanged()
	{
		return totChanged;
	}

	public void setTotChanged(String totChanged)
	{
		this.totChanged = totChanged;
	}

	public String getoInvstartno()
	{
		return oInvstartno;
	}

	public void setoInvstartno(String oInvstartno)
	{
		this.oInvstartno = oInvstartno;
	}

	public String getIsInvoiceMakeOut()
	{
		return isInvoiceMakeOut;
	}

	public void setIsInvoiceMakeOut(String isInvoiceMakeOut)
	{
		this.isInvoiceMakeOut = isInvoiceMakeOut;
	}

	public String getInvSplitType()
	{
		return invSplitType;
	}

	public void setInvSplitType(String invSplitType)
	{
		this.invSplitType = invSplitType;
	}

	public String getInvCount()
	{
		return invCount;
	}

	public void setInvCount(String invCount)
	{
		this.invCount = invCount;
	}

	public String getIsTakeout()
	{
		return isTakeout;
	}

	public void setIsTakeout(String isTakeout)
	{
		this.isTakeout = isTakeout;
	}

	public String getTakeAway()
	{
		return takeAway;
	}

	public void setTakeAway(String takeAway)
	{
		this.takeAway = takeAway;
	}

	public String getOrderID()
	{
		return orderID;
	}

	public void setOrderID(String orderID)
	{
		this.orderID = orderID;
	}

	public String getOrderSn()
	{
		return orderSn;
	}

	public void setOrderSn(String orderSn)
	{
		this.orderSn = orderSn;
	}

	public String getPlatformDisc()
	{
		return platformDisc;
	}

	public void setPlatformDisc(String platformDisc)
	{
		this.platformDisc = platformDisc;
	}

	public String getSellerDisc()
	{
		return sellerDisc;
	}

	public void setSellerDisc(String sellerDisc)
	{
		this.sellerDisc = sellerDisc;
	}

	public String getPackageFee()
	{
		return packageFee;
	}

	public void setPackageFee(String packageFee)
	{
		this.packageFee = packageFee;
	}

	public String getShippingFee()
	{
		return shippingFee;
	}

	public void setShippingFee(String shippingFee)
	{
		this.shippingFee = shippingFee;
	}

	public String getDeliveryFeeShop()
	{
		return deliveryFeeShop;
	}

	public void setDeliveryFeeShop(String deliveryFeeShop)
	{
		this.deliveryFeeShop = deliveryFeeShop;
	}

	public String getDeliveryFeeUser()
	{
		return deliveryFeeUser;
	}

	public void setDeliveryFeeUser(String deliveryFeeUser)
	{
		this.deliveryFeeUser = deliveryFeeUser;
	}

	public String getWmUserPaid()
	{
		return wmUserPaid;
	}

	public void setWmUserPaid(String wmUserPaid)
	{
		this.wmUserPaid = wmUserPaid;
	}

	public String getPlatformFee()
	{
		return platformFee;
	}

	public void setPlatformFee(String platformFee)
	{
		this.platformFee = platformFee;
	}

	public String getWmExtraFee()
	{
		return wmExtraFee;
	}

	public void setWmExtraFee(String wmExtraFee)
	{
		this.wmExtraFee = wmExtraFee;
	}

	public String getShopIncome()
	{
		return shopIncome;
	}

	public void setShopIncome(String shopIncome)
	{
		this.shopIncome = shopIncome;
	}

	public String getProductionMode()
	{
		return productionMode;
	}

	public void setProductionMode(String productionMode)
	{
		this.productionMode = productionMode;
	}

	public String getProductionShop()
	{
		return productionShop;
	}

	public void setProductionShop(String productionShop)
	{
		this.productionShop = productionShop;
	}

	public String getIsBuff()
	{
		return isBuff;
	}

	public void setIsBuff(String isBuff)
	{
		this.isBuff = isBuff;
	}

	public String getIsBuff_timeout()
	{
		return isBuff_timeout;
	}

	public void setIsBuff_timeout(String isBuff_timeout)
	{
		this.isBuff_timeout = isBuff_timeout;
	}

	public String getEcCustomerNO()
	{
		return ecCustomerNO;
	}

	public void setEcCustomerNO(String ecCustomerNO)
	{
		this.ecCustomerNO = ecCustomerNO;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getIsReturn()
	{
		return isReturn;
	}

	public void setIsReturn(String isReturn)
	{
		this.isReturn = isReturn;
	}

	public String getReturnUserId()
	{
		return returnUserId;
	}

	public void setReturnUserId(String returnUserId)
	{
		this.returnUserId = returnUserId;
	}

	public String getBsno()
	{
		return bsno;
	}

	public void setBsno(String bsno)
	{
		this.bsno = bsno;
	}

	public String getsDate()
	{
		return sDate;
	}

	public void setsDate(String sDate)
	{
		this.sDate = sDate;
	}

	public String getsTime()
	{
		return sTime;
	}

	public void setsTime(String sTime)
	{
		this.sTime = sTime;
	}

	public String getEvaluate()
	{
		return evaluate;
	}

	public void setEvaluate(String evaluate)
	{
		this.evaluate = evaluate;
	}

	public String getIsUploaded()
	{
		return isUploaded;
	}

	public void setIsUploaded(String isUploaded)
	{
		this.isUploaded = isUploaded;
	}

	public String getRsvId()
	{
		return rsvId;
	}

	public void setRsvId(String rsvId)
	{
		this.rsvId = rsvId;
	}

	public String getOrderReturn()
	{
		return orderReturn;
	}

	public void setOrderReturn(String orderReturn)
	{
		this.orderReturn = orderReturn;
	}

	public String getOrder_companyId()
	{
		return order_companyId;
	}

	public void setOrder_companyId(String order_companyId)
	{
		this.order_companyId = order_companyId;
	}

	public String getOrder_channelId()
	{
		return order_channelId;
	}

	public void setOrder_channelId(String order_channelId)
	{
		this.order_channelId = order_channelId;
	}

	public String getOrder_appType()
	{
		return order_appType;
	}

	public void setOrder_appType(String order_appType)
	{
		this.order_appType = order_appType;
	}

	public String getoOrder_companyId()
	{
		return oOrder_companyId;
	}

	public void setoOrder_companyId(String oOrder_companyId)
	{
		this.oOrder_companyId = oOrder_companyId;
	}

	public String getoOrder_channelId()
	{
		return oOrder_channelId;
	}

	public void setoOrder_channelId(String oOrder_channelId)
	{
		this.oOrder_channelId = oOrder_channelId;
	}

	public String getoOrder_appType()
	{
		return oOrder_appType;
	}

	public void setoOrder_appType(String oOrder_appType)
	{
		this.oOrder_appType = oOrder_appType;
	}

	public String getWxOpenId()
	{
		return wxOpenId;
	}

	public void setWxOpenId(String wxOpenId)
	{
		this.wxOpenId = wxOpenId;
	}

	public String getTran_time()
	{
		return tran_time;
	}

	public void setTran_time(String tran_time)
	{
		this.tran_time = tran_time;
	}

	public String getTot_Amt_merReceive()
	{
		return tot_Amt_merReceive;
	}

	public void setTot_Amt_merReceive(String tot_Amt_merReceive)
	{
		this.tot_Amt_merReceive = tot_Amt_merReceive;
	}

	public String getTotDisc_merReceive()
	{
		return totDisc_merReceive;
	}

	public void setTotDisc_merReceive(String totDisc_merReceive)
	{
		this.totDisc_merReceive = totDisc_merReceive;
	}

	public String getTot_Amt_custPayReal()
	{
		return tot_Amt_custPayReal;
	}

	public void setTot_Amt_custPayReal(String tot_Amt_custPayReal)
	{
		this.tot_Amt_custPayReal = tot_Amt_custPayReal;
	}

	public String getTotDisc_custPayReal()
	{
		return totDisc_custPayReal;
	}

	public void setTotDisc_custPayReal(String totDisc_custPayReal)
	{
		this.totDisc_custPayReal = totDisc_custPayReal;
	}

	public String getIsMerPay()
	{
		return isMerPay;
	}

	public void setIsMerPay(String isMerPay)
	{
		this.isMerPay = isMerPay;
	}

	public String getDepartNo()
	{
		return departNo;
	}

	public void setDepartNo(String departNo)
	{
		this.departNo = departNo;
	}

    public String getWaimaiMerreceiveMode()
    {
        return waimaiMerreceiveMode;
    }

    public void setWaimaiMerreceiveMode(String waimaiMerreceiveMode)
    {
        this.waimaiMerreceiveMode = waimaiMerreceiveMode;
    }

	public String getTrno()
	{
		return trno;
	}

	public void setTrno(String trno)
	{
		this.trno = trno;
	}

	public List<Detail> getDetails()
	{
		return details;
	}

	public void setDetails(List<Detail> details)
	{
		this.details = details;
	}

	public List<Payment> getPays()
	{
		return pays;
	}

	public void setPays(List<Payment> pays)
	{
		this.pays = pays;
	}

	public class Detail 
	{
		private String eId;
		private String shopId;
		private String warehouse;//出货仓库
		private String item;//项次		
		private String oItem;//来源项次			
		private String clerkNo;//营业员编号		
		private String clerkName;//营业员名称		
		private String accno;//授权人员		
		private String tableNo;//桌台号	
		private String dealType;//商品交易类型：1销售2退货3赠送11免单
		private String couponType;//提货券券类型
		private String couponCode;//提货券券号
		private String pluNo;//商品编码
		private String pName;//商品名称
		private String isGift;//是否赠品Y/N
		private String giftReason;//赠品原因
		private String giftOpno;//赠送人编码
		private String giftTime;//赠送时间
		private String isExchange;//是否换购品Y/N
		private String isPickGoods;//是否现提商品Y/N
		private String pluBarcode;//条形码		
		private String scanNo;//扫描码		
		private String mno;//商场码
		private String counterNo;//租柜编号
		private String srackNo;//货架编号		
		private String batchNo;//批次号
		private String tgCategoryNo;//团务分类编号
		private String featureNo;//特征码
		private String attr01;//商品特性1		
		private String attr02;//商品特性2	
		private String unit;//销售单位
		private String baseUnit;//基准单位
		private String qty;//数量
		private String oldPrice;//原价	
		private String price2;//底价	
		private String price3;//最高退价
		private String canBack;//是否可退Y/N
		private String bsno;//退货理由码
		private String rQty;//退货数量
		private String returnUserId;//退货人id
		private String returnTableNo;//退货桌号
		private String refundOpno;//退菜人编号
		private String refundTime;//退菜时间
		private String oldAMT;//原价总金额
		private String disc;//总折扣金额
		private String saleDisc;//销售折扣金额
		private String payDisc;//付款折扣
		private String price;//售价
		private String additionalPrice;//加价
		private String amt;//成交金额	
		private String pointQty;//积分	
		private String counterAmt;//专柜成交金额
		private String servCharge;//餐厅服务费
		private String packageMaster;//是否套餐主商品Y/N		
		private String isPackage;//是否套餐商品Y/N		
		private String packageAmt;//套餐商品金额		
		private String packageQty;//套餐商品数量	
		private String upItem;//套餐主商品项次
		private String shareAmt;//套餐分摊金额
		private String isStuff;//是否加料商品Y/N
		private String detailItem;//加料主商品项次	
		private String flavorStuffDetail;//口味加料细节
		private String cakeBlessing;//蛋糕祝福
		private String materials;//蛋糕坯子
		private String dishesStatus;//菜品状态
		private String socalled;//等叫类型
		private String repastType;//就餐类型：0堂食1打包2外卖
		private String packageAmount;//餐盒数量		
		private String packagePrice;//餐盒单价		
		private String packageFee;//餐盒总价			
		private String inclTax;//售价含税否		
		private String taxCode;//税别编号		
		private String taxType;//税别类型		
		private String taxRate;//税率		
		private String orderRateAmount;//订金分摊金额
		private String memo;//备注	
		private String confirmOpno;//确认人
		private String confirmTime;//确认时间
		private String orderTime;//预定时间
		private String status;//有效否100-有效0-无效
		private String bDate;//营业日期
		private String sDate;//系统日期
		private String sTime;//系统时间
		private String promCouponNo;//促销赠券券号
		private String virtual;//是否虚拟商品：N否Y-是
		private String disc_merReceive;//商家支付折扣金额			
		private String disc_custPayReal;//顾客支付折扣金额			
		private String amt_merReceive;//商家实收金额			
		private String amt_custPayReal;//顾客实付金额
        private double unitRatio=1;
        private double baseQty=1;


		private List<DetailAgio> detailAgios ;

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

		public String getWarehouse()
		{
			return warehouse;
		}

		public void setWarehouse(String warehouse)
		{
			this.warehouse = warehouse;
		}

		public String getItem()
		{
			return item;
		}

		public void setItem(String item)
		{
			this.item = item;
		}

		public String getoItem()
		{
			return oItem;
		}

		public void setoItem(String oItem)
		{
			this.oItem = oItem;
		}

		public String getClerkNo()
		{
			return clerkNo;
		}

		public void setClerkNo(String clerkNo)
		{
			this.clerkNo = clerkNo;
		}

		public String getClerkName()
		{
			return clerkName;
		}

		public void setClerkName(String clerkName)
		{
			this.clerkName = clerkName;
		}

		public String getAccno()
		{
			return accno;
		}

		public void setAccno(String accno)
		{
			this.accno = accno;
		}

		public String getTableNo()
		{
			return tableNo;
		}

		public void setTableNo(String tableNo)
		{
			this.tableNo = tableNo;
		}

		public String getDealType()
		{
			return dealType;
		}

		public void setDealType(String dealType)
		{
			this.dealType = dealType;
		}

		public String getCouponType()
		{
			return couponType;
		}

		public void setCouponType(String couponType)
		{
			this.couponType = couponType;
		}

		public String getCouponCode()
		{
			return couponCode;
		}

		public void setCouponCode(String couponCode)
		{
			this.couponCode = couponCode;
		}

		public String getPluNo()
		{
			return pluNo;
		}

		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}

		public String getpName()
		{
			return pName;
		}

		public void setpName(String pName)
		{
			this.pName = pName;
		}

		public String getIsGift()
		{
			return isGift;
		}

		public void setIsGift(String isGift)
		{
			this.isGift = isGift;
		}

		public String getGiftReason()
		{
			return giftReason;
		}

		public void setGiftReason(String giftReason)
		{
			this.giftReason = giftReason;
		}

		public String getGiftOpno()
		{
			return giftOpno;
		}

		public void setGiftOpno(String giftOpno)
		{
			this.giftOpno = giftOpno;
		}

		public String getGiftTime()
		{
			return giftTime;
		}

		public void setGiftTime(String giftTime)
		{
			this.giftTime = giftTime;
		}

		public String getIsExchange()
		{
			return isExchange;
		}

		public void setIsExchange(String isExchange)
		{
			this.isExchange = isExchange;
		}

		public String getIsPickGoods()
		{
			return isPickGoods;
		}

		public void setIsPickGoods(String isPickGoods)
		{
			this.isPickGoods = isPickGoods;
		}

		public String getPluBarcode()
		{
			return pluBarcode;
		}

		public void setPluBarcode(String pluBarcode)
		{
			this.pluBarcode = pluBarcode;
		}

		public String getScanNo()
		{
			return scanNo;
		}

		public void setScanNo(String scanNo)
		{
			this.scanNo = scanNo;
		}

		public String getMno()
		{
			return mno;
		}

		public void setMno(String mno)
		{
			this.mno = mno;
		}

		public String getCounterNo()
		{
			return counterNo;
		}

		public void setCounterNo(String counterNo)
		{
			this.counterNo = counterNo;
		}

		public String getSrackNo()
		{
			return srackNo;
		}

		public void setSrackNo(String srackNo)
		{
			this.srackNo = srackNo;
		}

		public String getBatchNo()
		{
			return batchNo;
		}

		public void setBatchNo(String batchNo)
		{
			this.batchNo = batchNo;
		}

		public String getTgCategoryNo()
		{
			return tgCategoryNo;
		}

		public void setTgCategoryNo(String tgCategoryNo)
		{
			this.tgCategoryNo = tgCategoryNo;
		}

		public String getFeatureNo()
		{
			return featureNo;
		}

		public void setFeatureNo(String featureNo)
		{
			this.featureNo = featureNo;
		}

		public String getAttr01()
		{
			return attr01;
		}

		public void setAttr01(String attr01)
		{
			this.attr01 = attr01;
		}

		public String getAttr02()
		{
			return attr02;
		}

		public void setAttr02(String attr02)
		{
			this.attr02 = attr02;
		}

		public String getUnit()
		{
			return unit;
		}

		public void setUnit(String unit)
		{
			this.unit = unit;
		}

		public String getBaseUnit()
		{
			return baseUnit;
		}

		public void setBaseUnit(String baseUnit)
		{
			this.baseUnit = baseUnit;
		}

		public String getQty()
		{
			return qty;
		}

		public void setQty(String qty)
		{
			this.qty = qty;
		}

		public String getOldPrice()
		{
			return oldPrice;
		}

		public void setOldPrice(String oldPrice)
		{
			this.oldPrice = oldPrice;
		}

		public String getPrice2()
		{
			return price2;
		}

		public void setPrice2(String price2)
		{
			this.price2 = price2;
		}

		public String getPrice3()
		{
			return price3;
		}

		public void setPrice3(String price3)
		{
			this.price3 = price3;
		}

		public String getCanBack()
		{
			return canBack;
		}

		public void setCanBack(String canBack)
		{
			this.canBack = canBack;
		}

		public String getBsno()
		{
			return bsno;
		}

		public void setBsno(String bsno)
		{
			this.bsno = bsno;
		}

		public String getrQty()
		{
			return rQty;
		}

		public void setrQty(String rQty)
		{
			this.rQty = rQty;
		}

		public String getReturnUserId()
		{
			return returnUserId;
		}

		public void setReturnUserId(String returnUserId)
		{
			this.returnUserId = returnUserId;
		}

		public String getReturnTableNo()
		{
			return returnTableNo;
		}

		public void setReturnTableNo(String returnTableNo)
		{
			this.returnTableNo = returnTableNo;
		}	

		public String getRefundOpno()
		{
			return refundOpno;
		}

		public void setRefundOpno(String refundOpno)
		{
			this.refundOpno = refundOpno;
		}

		public String getRefundTime()
		{
			return refundTime;
		}

		public void setRefundTime(String refundTime)
		{
			this.refundTime = refundTime;
		}

		public String getOldAMT()
		{
			return oldAMT;
		}

		public void setOldAMT(String oldAMT)
		{
			this.oldAMT = oldAMT;
		}

		public String getDisc()
		{
			return disc;
		}

		public void setDisc(String disc)
		{
			this.disc = disc;
		}

		public String getSaleDisc()
		{
			return saleDisc;
		}

		public void setSaleDisc(String saleDisc)
		{
			this.saleDisc = saleDisc;
		}

		public String getPayDisc()
		{
			return payDisc;
		}

		public void setPayDisc(String payDisc)
		{
			this.payDisc = payDisc;
		}

		public String getPrice()
		{
			return price;
		}

		public void setPrice(String price)
		{
			this.price = price;
		}

		public String getAdditionalPrice()
		{
			return additionalPrice;
		}

		public void setAdditionalPrice(String additionalPrice)
		{
			this.additionalPrice = additionalPrice;
		}

		public String getAmt()
		{
			return amt;
		}

		public void setAmt(String amt)
		{
			this.amt = amt;
		}

		public String getPointQty()
		{
			return pointQty;
		}

		public void setPointQty(String pointQty)
		{
			this.pointQty = pointQty;
		}

		public String getCounterAmt()
		{
			return counterAmt;
		}

		public void setCounterAmt(String counterAmt)
		{
			this.counterAmt = counterAmt;
		}

		public String getServCharge()
		{
			return servCharge;
		}

		public void setServCharge(String servCharge)
		{
			this.servCharge = servCharge;
		}

		public String getPackageMaster()
		{
			return packageMaster;
		}

		public void setPackageMaster(String packageMaster)
		{
			this.packageMaster = packageMaster;
		}

		public String getIsPackage()
		{
			return isPackage;
		}

		public void setIsPackage(String isPackage)
		{
			this.isPackage = isPackage;
		}

		public String getPackageAmt()
		{
			return packageAmt;
		}

		public void setPackageAmt(String packageAmt)
		{
			this.packageAmt = packageAmt;
		}

		public String getPackageQty()
		{
			return packageQty;
		}

		public void setPackageQty(String packageQty)
		{
			this.packageQty = packageQty;
		}

		public String getUpItem()
		{
			return upItem;
		}

		public void setUpItem(String upItem)
		{
			this.upItem = upItem;
		}

		public String getShareAmt()
		{
			return shareAmt;
		}

		public void setShareAmt(String shareAmt)
		{
			this.shareAmt = shareAmt;
		}

		public String getIsStuff()
		{
			return isStuff;
		}

		public void setIsStuff(String isStuff)
		{
			this.isStuff = isStuff;
		}

		public String getDetailItem()
		{
			return detailItem;
		}

		public void setDetailItem(String detailItem)
		{
			this.detailItem = detailItem;
		}

		public String getFlavorStuffDetail()
		{
			return flavorStuffDetail;
		}

		public void setFlavorStuffDetail(String flavorStuffDetail)
		{
			this.flavorStuffDetail = flavorStuffDetail;
		}

		public String getCakeBlessing()
		{
			return cakeBlessing;
		}

		public void setCakeBlessing(String cakeBlessing)
		{
			this.cakeBlessing = cakeBlessing;
		}

		public String getMaterials()
		{
			return materials;
		}

		public void setMaterials(String materials)
		{
			this.materials = materials;
		}

		public String getDishesStatus()
		{
			return dishesStatus;
		}

		public void setDishesStatus(String dishesStatus)
		{
			this.dishesStatus = dishesStatus;
		}

		public String getSocalled()
		{
			return socalled;
		}

		public void setSocalled(String socalled)
		{
			this.socalled = socalled;
		}

		public String getRepastType()
		{
			return repastType;
		}

		public void setRepastType(String repastType)
		{
			this.repastType = repastType;
		}

		public String getPackageAmount()
		{
			return packageAmount;
		}

		public void setPackageAmount(String packageAmount)
		{
			this.packageAmount = packageAmount;
		}

		public String getPackagePrice()
		{
			return packagePrice;
		}

		public void setPackagePrice(String packagePrice)
		{
			this.packagePrice = packagePrice;
		}

		public String getPackageFee()
		{
			return packageFee;
		}

		public void setPackageFee(String packageFee)
		{
			this.packageFee = packageFee;
		}

		public String getInclTax()
		{
			return inclTax;
		}

		public void setInclTax(String inclTax)
		{
			this.inclTax = inclTax;
		}

		public String getTaxCode()
		{
			return taxCode;
		}

		public void setTaxCode(String taxCode)
		{
			this.taxCode = taxCode;
		}

		public String getTaxType()
		{
			return taxType;
		}

		public void setTaxType(String taxType)
		{
			this.taxType = taxType;
		}

		public String getTaxRate()
		{
			return taxRate;
		}

		public void setTaxRate(String taxRate)
		{
			this.taxRate = taxRate;
		}

		public String getOrderRateAmount()
		{
			return orderRateAmount;
		}

		public void setOrderRateAmount(String orderRateAmount)
		{
			this.orderRateAmount = orderRateAmount;
		}

		public String getMemo()
		{
			return memo;
		}

		public void setMemo(String memo)
		{
			this.memo = memo;
		}

		public String getConfirmOpno()
		{
			return confirmOpno;
		}

		public void setConfirmOpno(String confirmOpno)
		{
			this.confirmOpno = confirmOpno;
		}

		public String getConfirmTime()
		{
			return confirmTime;
		}

		public void setConfirmTime(String confirmTime)
		{
			this.confirmTime = confirmTime;
		}

		public String getOrderTime()
		{
			return orderTime;
		}

		public void setOrderTime(String orderTime)
		{
			this.orderTime = orderTime;
		}

		public String getStatus()
		{
			return status;
		}

		public void setStatus(String status)
		{
			this.status = status;
		}

		public String getbDate()
		{
			return bDate;
		}

		public void setbDate(String bDate)
		{
			this.bDate = bDate;
		}

		public String getsDate()
		{
			return sDate;
		}

		public void setsDate(String sDate)
		{
			this.sDate = sDate;
		}

		public String getsTime()
		{
			return sTime;
		}

		public void setsTime(String sTime)
		{
			this.sTime = sTime;
		}

		public String getPromCouponNo()
		{
			return promCouponNo;
		}

		public void setPromCouponNo(String promCouponNo)
		{
			this.promCouponNo = promCouponNo;
		}	

		public String getVirtual()
		{
			return virtual;
		}

		public void setVirtual(String virtual)
		{
			this.virtual = virtual;
		}	

		public String getDisc_merReceive()
		{
			return disc_merReceive;
		}

		public void setDisc_merReceive(String disc_merReceive)
		{
			this.disc_merReceive = disc_merReceive;
		}

		public String getDisc_custPayReal()
		{
			return disc_custPayReal;
		}

		public void setDisc_custPayReal(String disc_custPayReal)
		{
			this.disc_custPayReal = disc_custPayReal;
		}

		public String getAmt_merReceive()
		{
			return amt_merReceive;
		}

		public void setAmt_merReceive(String amt_merReceive)
		{
			this.amt_merReceive = amt_merReceive;
		}

		public String getAmt_custPayReal()
		{
			return amt_custPayReal;
		}

		public void setAmt_custPayReal(String amt_custPayReal)
		{
			this.amt_custPayReal = amt_custPayReal;
		}

		public List<DetailAgio> getDetailAgios()
		{
			return detailAgios;
		}

		public void setDetailAgios(List<DetailAgio> detailAgios)
		{
			this.detailAgios = detailAgios;
		}

        public double getUnitRatio()
        {
            return unitRatio;
        }

        public void setUnitRatio(double unitRatio)
        {
            this.unitRatio = unitRatio;
        }

        public double getBaseQty()
        {
            return baseQty;
        }

        public void setBaseQty(double baseQty)
        {
            this.baseQty = baseQty;
        }
    }

	public class DetailAgio
	{	
		private String eId;
		private String shopId;
		private String mItem;//主项次		
		private String item;//项次		
		private String qty;//参与数量		
		private String amt;//参与金额		
		private String disc;//折扣金额	
		private String realDisc;//实际折扣金额	
		private String dcType;//折扣方式	
		private String dcTypeName;//折扣方式名称
		private String pmtNo;//促销单号
		private String giftctf;//折扣券券种		
		private String giftctfNo;//折扣券券号	
		private String bsno;//理由码
		private String dcOpno;//打折人/授权人
		private String dcTime;//打折时间
		private String memo;//折扣备注		
		private String status;//有效否100-有效0-无效		
		private String orderToSalePayAgioFlag;//订转销付款产生的折扣Y/N
		private String disc_merReceive;//折扣金额[商户实收]
		private String disc_custPayReal;//折扣金额[顾客实付]
		
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
		public String getmItem()
		{
			return mItem;
		}
		public void setmItem(String mItem)
		{
			this.mItem = mItem;
		}
		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}
		public String getQty()
		{
			return qty;
		}
		public void setQty(String qty)
		{
			this.qty = qty;
		}
		public String getAmt()
		{
			return amt;
		}
		public void setAmt(String amt)
		{
			this.amt = amt;
		}
		public String getDisc()
		{
			return disc;
		}
		public void setDisc(String disc)
		{
			this.disc = disc;
		}
		public String getRealDisc()
		{
			return realDisc;
		}
		public void setRealDisc(String realDisc)
		{
			this.realDisc = realDisc;
		}
		public String getDcType()
		{
			return dcType;
		}
		public void setDcType(String dcType)
		{
			this.dcType = dcType;
		}
		public String getDcTypeName()
		{
			return dcTypeName;
		}
		public void setDcTypeName(String dcTypeName)
		{
			this.dcTypeName = dcTypeName;
		}
		public String getPmtNo()
		{
			return pmtNo;
		}
		public void setPmtNo(String pmtNo)
		{
			this.pmtNo = pmtNo;
		}
		public String getGiftctf()
		{
			return giftctf;
		}
		public void setGiftctf(String giftctf)
		{
			this.giftctf = giftctf;
		}
		public String getGiftctfNo()
		{
			return giftctfNo;
		}
		public void setGiftctfNo(String giftctfNo)
		{
			this.giftctfNo = giftctfNo;
		}
		public String getBsno()
		{
			return bsno;
		}
		public void setBsno(String bsno)
		{
			this.bsno = bsno;
		}
		public String getDcOpno()
		{
			return dcOpno;
		}
		public void setDcOpno(String dcOpno)
		{
			this.dcOpno = dcOpno;
		}
		public String getDcTime()
		{
			return dcTime;
		}
		public void setDcTime(String dcTime)
		{
			this.dcTime = dcTime;
		}
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		
		public String getOrderToSalePayAgioFlag() {
			return orderToSalePayAgioFlag;
		}
		public void setOrderToSalePayAgioFlag(String orderToSalePayAgioFlag) {
			this.orderToSalePayAgioFlag = orderToSalePayAgioFlag;
		}
		public String getDisc_merReceive()
		{
			return disc_merReceive;
		}
		public void setDisc_merReceive(String disc_merReceive)
		{
			this.disc_merReceive = disc_merReceive;
		}
		public String getDisc_custPayReal()
		{
			return disc_custPayReal;
		}
		public void setDisc_custPayReal(String disc_custPayReal)
		{
			this.disc_custPayReal = disc_custPayReal;
		}
		
	}

	public class Payment
	{
		private String eId;
		private String shopId;
		private String item;//项次	
		private String payDocType;//支付平台类型：POS-4	
		private String paycode;//ERP支付方式(小类)	
		private String paycodeErp;//ERP支付类型(大类)	
		private String payName;//支付方式名称	
		private String pay;//金额	
		private String posPay;//POS支付金额	
		private String changed;//找零
		private String extra;//溢收金额	
		private String returnRate;//退货吸收比率	
		private String payserNum;//第三方支付订单号	
		private String serialNo;//银联卡流水号	
		private String refNo;//银联卡交易参考号	
		private String teriminalNo;//银联终端号	
		private String ctType;//卡券类型 1-卡2-券	
		private String cardNo;//支付卡/券号码
        private String lpcardNo;//禄品返回的真实卡号
        private String cardAmtBefore;//卡消费前金额
		private String cardRemainAmt;//卡剩余金额
		private String cardSendPay;//卡消费赠送金额	
		private String isVerifycation;//券是否核销Y/N		
		private String couponQty;//券数量	
		private String descore;//积分抵现扣减	
		private String isOrderPay;//是否订金冲销Y/N	
		private String prePayBillNo;//订金收款单号	
		private String authCode;//授权码	
		private String isTurnOver;//是否纳入营业额Y/N	
		private String status;//状态	
		private String bDate;//营业日期	
		private String sDate;//系统日期	
		private String sTime;//系统时间	
		private String payType;//支付方式	
		private String payShop;//支付门店编号
		private String isDeposit;//是否订金Y/N【CM专用】	
		private String funcNo;//功能编码
        /**
         * 货郎农行手续费
         */
        private double chargeAmount;

        /**
         * 支付通道编码
         */
        private String payChannelCode;


		private String merDiscount;//商户优惠
		private String merReceive;//商户实收
		private String thirdDiscout;//平台优惠
		private String custPayReal;//顾客实付
		private String password;// 禄品卡录入密码

        private String rnd1=""; //随机串1 -超港-乐享支付使用
        private String rnd2=""; //随机串2 -超港-乐享支付使用

        private String ecardSign; //企迈 ecardSign字段，0实体卡，1电子卡




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
		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}
		public String getPayDocType()
		{
			return payDocType;
		}
		public void setPayDocType(String payDocType)
		{
			this.payDocType = payDocType;
		}
		public String getPaycode()
		{
			return paycode;
		}
		public void setPaycode(String paycode)
		{
			this.paycode = paycode;
		}
		public String getPaycodeErp()
		{
			return paycodeErp;
		}
		public void setPaycodeErp(String paycodeErp)
		{
			this.paycodeErp = paycodeErp;
		}
		public String getPayName()
		{
			return payName;
		}
		public void setPayName(String payName)
		{
			this.payName = payName;
		}
		public String getPay()
		{
			return pay;
		}
		public void setPay(String pay)
		{
			this.pay = pay;
		}
		public String getPosPay()
		{
			return posPay;
		}
		public void setPosPay(String posPay)
		{
			this.posPay = posPay;
		}
		public String getChanged()
		{
			return changed;
		}
		public void setChanged(String changed)
		{
			this.changed = changed;
		}
		public String getExtra()
		{
			return extra;
		}
		public void setExtra(String extra)
		{
			this.extra = extra;
		}
		public String getReturnRate()
		{
			return returnRate;
		}
		public void setReturnRate(String returnRate)
		{
			this.returnRate = returnRate;
		}
		public String getPayserNum()
		{
			return payserNum;
		}
		public void setPayserNum(String payserNum)
		{
			this.payserNum = payserNum;
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
		public String getCtType()
		{
			return ctType;
		}
		public void setCtType(String ctType)
		{
			this.ctType = ctType;
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

        public String getCardAmtBefore()
		{
			return cardAmtBefore;
		}
		public void setCardAmtBefore(String cardAmtBefore)
		{
			this.cardAmtBefore = cardAmtBefore;
		}
		public String getCardRemainAmt()
		{
			return cardRemainAmt;
		}
		public void setCardRemainAmt(String cardRemainAmt)
		{
			this.cardRemainAmt = cardRemainAmt;
		}
		public String getCardSendPay()
		{
			return cardSendPay;
		}
		public void setCardSendPay(String cardSendPay)
		{
			this.cardSendPay = cardSendPay;
		}
		public String getIsVerifycation()
		{
			return isVerifycation;
		}
		public void setIsVerifycation(String isVerifycation)
		{
			this.isVerifycation = isVerifycation;
		}
		public String getCouponQty()
		{
			return couponQty;
		}
		public void setCouponQty(String couponQty)
		{
			this.couponQty = couponQty;
		}
		public String getDescore()
		{
			return descore;
		}
		public void setDescore(String descore)
		{
			this.descore = descore;
		}
		public String getIsOrderPay()
		{
			return isOrderPay;
		}
		public void setIsOrderPay(String isOrderPay)
		{
			this.isOrderPay = isOrderPay;
		}
		public String getPrePayBillNo()
		{
			return prePayBillNo;
		}
		public void setPrePayBillNo(String prePayBillNo)
		{
			this.prePayBillNo = prePayBillNo;
		}
		public String getAuthCode()
		{
			return authCode;
		}
		public void setAuthCode(String authCode)
		{
			this.authCode = authCode;
		}
		public String getIsTurnOver()
		{
			return isTurnOver;
		}
		public void setIsTurnOver(String isTurnOver)
		{
			this.isTurnOver = isTurnOver;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		public String getbDate()
		{
			return bDate;
		}
		public void setbDate(String bDate)
		{
			this.bDate = bDate;
		}
		public String getsDate()
		{
			return sDate;
		}
		public void setsDate(String sDate)
		{
			this.sDate = sDate;
		}
		public String getsTime()
		{
			return sTime;
		}
		public void setsTime(String sTime)
		{
			this.sTime = sTime;
		}
		public String getPayType()
		{
			return payType;
		}
		public void setPayType(String payType)
		{
			this.payType = payType;
		}
		public String getPayShop()
		{
			return payShop;
		}
		public void setPayShop(String payShop)
		{
			this.payShop = payShop;
		}
		public String getIsDeposit()
		{
			return isDeposit;
		}
		public void setIsDeposit(String isDeposit)
		{
			this.isDeposit = isDeposit;
		}
		public String getFuncNo()
		{
			return funcNo;
		}
		public void setFuncNo(String funcNo)
		{
			this.funcNo = funcNo;
		}

        public double getChargeAmount()
        {
            return chargeAmount;
        }

        public void setChargeAmount(double chargeAmount)
        {
            this.chargeAmount = chargeAmount;
        }

        public String getPayChannelCode()
        {
            return payChannelCode;
        }

        public void setPayChannelCode(String payChannelCode)
        {
            this.payChannelCode = payChannelCode;
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

		public String getThirdDiscout()
		{
			return thirdDiscout;
		}

		public void setThirdDiscout(String thirdDiscout)
		{
			this.thirdDiscout = thirdDiscout;
		}

		public String getCustPayReal()
		{
			return custPayReal;
		}

		public void setCustPayReal(String custPayReal)
		{
			this.custPayReal = custPayReal;
		}

		public String getPassword()
		{
			return password;
		}

		public void setPassword(String password)
		{
			this.password = password;
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

        public String getEcardSign() {
			return ecardSign;
		}
		
		public void setEcardSign(String ecardSign) {
			this.ecardSign = ecardSign;
		}
	}


}

