package com.dsc.spos.waimai.entity;

import lombok.Data;

import java.util.List;

public class order {
	
	private String eId="";
	private String billType;
	private String orderNo="";
	private String shopNo="";
	private String shopName="";
	private String machShopNo="";
	private String machShopName="";
	private String shippingShopNo="";
	private String shippingShopName="";
	private String loadDocType="";
	private String loadDocTypeName="";
	private String channelId="";
	private String channelIdName="";
	private String contMan="";
	private String contTel="";
	private String address="";
	private String shipDate="";
	private String shipStartTime="";
	private String shipEndTime="";
	private String shipType="";
	private String deliveryType="";
	private String deliveryNo="";
	private String deliveryStatus="";
	private String productStatus ="";//生产状态
	private String memo="";
	private String createDatetime="";
	private String sn;
	private double packageFee;
	private String tot_shipFee;
	private String rshipFee;
	private double shipFee;
	private double shopShareShipfee;
	private double tot_oldAmt;
	private double tot_Amt;
	private double tot_qty;
	private double totQty;
	private double serviceCharge;
	private double incomeAmt;
	private double totDisc;
	private double sellerDisc;
	private double platformDisc;
	private double payAmt;
	private double saleAmt;
	private double saleDisc;
	private String isBook="";
	private String status="";
	private String refundStatus="";
	private String payStatus="";
	private String sTime="";
	private double mealNumber;
	private String manualNo="";
	private String getMan="";
	private String getManTel="";
	private String cardNo;
	/**
	 *  digiwin  鼎捷   qimai企迈   空为鼎捷
	 */
	private String partnerMember;
	private String memberId;
	private String memberName;
	private double pointQty;
	private String sellNo;
	private String outDocType;
	private String outDocTypeName;
	private String orderShop;
	private String orderShopName;
	private String belfirm="";
	private String longitude;
	private String latitude;
	private String province;
	private String city;
	private String county;
	private String street;
	private String proMemo;
	private String delMemo;
	private String machineNo;
	private double eraseAmt;
	private String requestId;
	private String isChargeOrder;
	private String chargeOrderGetAmt;
	private String bDate="";
	private String sellCredit;
	private String refundBdate;
	private String refundDatetime;
	/**
	 * 退货的金额
	 */
	private double refundAmt;
	/**
	 * 冲销金额
	 */
	private double writeOffAmt;
	/**
	 * 拆单类型 : 1无拆单 2主单 3子单
	 */
	private String detailType;
	/**
	 * 主单号
	 */
	private String headOrderNo;
	/**
	 * 退单的来源单号
	 */
	private String refundSourceBillNo;
	private String loadDocBillType;
	private String loadDocOrderNo;
	private String verNum = "";
	private String squadNo;
	private String workNo;
	private String opNo;
    private String opName;
	private String customer;
	private String customerName;
	private double tot_uAmt;
	//private double zeroTaxAmt;
	//private double freeTaxAmt;
	//private double taxAbleAmt;
	//private double tax;
	private orderInvoice invoiceDetail;
	//private double taxRate;
	private String memberPayNo;
	/**
	 * 配送业务类型（1随车 2代发）
	 */
	private String deliveryBusinessType;
	/**
	 * 是否紧急订单（Y，N）
	 */
	private String isUrgentOrder;
	/**
	 * 退货原因描述
	 */
	private String refundReason;
	/**
	 * 退货原因代号
	 */
	private String refundReasonNo;
	/**
	 * 退货原因代号名称
	 */
	private String refundReasonName;
	/**
	 * 是否自动发快递
	 */
	private String autoDelivery;
	/**
	 * 是否已分摊过套餐
	 */
	private String isApportion = "";
	/**
	 * 是否已分摊过套餐异常标识（Y存在异常,N不存在异常）
	 */
	private String exceptionStatus = "N";
	/**
	 * 商家和外卖平台对账用到商家实收（嘉华目前对账逻辑）
	 */
	private double tot_Amt_merReceive;//商家实收    自配送=（商品总价+餐盒费）-商家承担优惠+（配送费）;平台配送=（商品总价+餐盒费）-商家承担优惠
	/**
	 * 商家实收对应得折扣金额（嘉华目前对账逻辑）同商家承担优惠金额=sellerDisc
	 */
	private double totDisc_merReceive;
	/**
	 * 商家和外卖平台对账用到顾客实付 （商品总价+餐盒费）+（配送费）-所有优惠
	 */
	private double tot_Amt_custPayReal;//顾客实付 （商品总价+餐盒费）+（配送费）-所有优惠
	/**
	 * 顾客实付对应得折扣金额同优惠总金额=totDisc=商家承担+平台承担
	 */
	private double totDisc_custPayReal;
	/**
	 * 配送费是否商家结算
	 */
	private String isMerPay  = "N";
	/**
	 * 是否总部生产
	 */
	private String isShipCompany  = "N";
	/**
	 * 配送员ID
	 */
	private String delId;
	private String delName;
	private String delTelephone;
	private String packerId;
	private String packerName;
	private String packerTelephone;
	/**
	 * 餐具数量
	 */
	private int tablewareQty;
	/**
	 * 是否已删除
	 */
	private String isDelete;
	/**
	 * 配送完成时间
	 */
	private String deliveryTime;
	/**
	 * 是否已评价
	 */
	private String isEvaluate;
	/**
	 * 预配状态 0：待预配   1：预配完成
	 */
	private String preparationStatus;
	/**
	 * 是否节日订单 (Y/N)  POS渠道专用
	 */
	private String isOrgOrder="N";
	/**
	 * 订单展示号一维码（美团外卖出餐码）
	 */
	private String orderCodeView="";
	/**
	 * 美团饿了么渠道外卖订单商户实收类型 0：嘉华算法   1：店铺收入，其他渠道默认0
	 */
	private String waiMaiMerReceiveMode = "0";
	/**
	 * 是否降级订单 Y：是   N：否
	 */
	private String downgraded = "N";
	/**
	 * 是否已传(N未上传  E无需上传  Y已上传)
	 */
	private String process_status  = "N";
	/**
	 * Y表示需要补录，N或者空表示不需要补录
	 */
	private String canModify="N";
	//是否团购
	private String groupBuying;
	/**
	 * 0或者空 表示不可修改订单不可退订 1表示可修改订单，2表示可退单
	 */
	private String operationType="";
	private String mealStatus;
	/**
	 * 枚举值：1、接单并打印（不传默认为1） 2、不接单不打印 3、接单不打印
	 */
	private String printSet = "1";
	/**
	 * 收款门店(货郎商城需求)
	 */
	private String payShopId;
	private String deliveryMoney;   //配送费
	private String superZoneMoney;  //超区费
	private String urgentMoney;     //加急费

    private String isHaveCard;    //是否含贺卡：Y/N
	private String isCardPrint;   //贺卡是否已打印 Y/N
	private String lineNo;        //路线信息
	private String lineName;      //路线名称
	/**
	 * 订转要 要货单号
	 */
	private String pOrderNo  = "";
    /**
     * 是否是意向单（Y/N）詹记需求
     */
    private String isIntention  = "N";
    /**
     * 追单订单 来源单号
     */
    private String addOrderOriginNo  = "";
    /**
     * 追加订单对应子单号
     */
    private String addOrderchildNo  = "";

	private List<orderGoodsItem> goodsList;
	private List<orderPay> pay;
	private List<orderAbnormal> abnormalList;
	private List<CouponChange> couponChangeList;
	private List<otherCoupnPay> otherCoupnPayList;


    public String getAddOrderOriginNo() {
        return addOrderOriginNo;
    }

    public void setAddOrderOriginNo(String addOrderOriginNo) {
        this.addOrderOriginNo = addOrderOriginNo;
    }

    public String getAddOrderchildNo() {
        return addOrderchildNo;
    }

    public void setAddOrderchildNo(String addOrderchildNo) {
        this.addOrderchildNo = addOrderchildNo;
    }

    public String geteId()
	{
		return eId;
	}
	
	public void seteId(String eId)
	{
		this.eId = eId;
	}
	
	public String getBillType()
	{
		return billType;
	}
	
	public void setBillType(String billType)
	{
		this.billType = billType;
	}
	
	public String getOrderNo()
	{
		return orderNo;
	}
	
	public void setOrderNo(String orderNo)
	{
		this.orderNo = orderNo;
	}
	
	public String getShopNo()
	{
		return shopNo;
	}
	
	public void setShopNo(String shopNo)
	{
		this.shopNo = shopNo;
	}
	
	public String getShopName()
	{
		return shopName;
	}
	
	public void setShopName(String shopName)
	{
		this.shopName = shopName;
	}
	
	public String getMachShopNo()
	{
		return machShopNo;
	}
	
	public void setMachShopNo(String machShopNo)
	{
		this.machShopNo = machShopNo;
	}
	
	public String getMachShopName()
	{
		return machShopName;
	}
	
	public void setMachShopName(String machShopName)
	{
		this.machShopName = machShopName;
	}
	
	public String getShippingShopNo()
	{
		return shippingShopNo;
	}
	
	public void setShippingShopNo(String shippingShopNo)
	{
		this.shippingShopNo = shippingShopNo;
	}
	
	public String getShippingShopName()
	{
		return shippingShopName;
	}
	
	public void setShippingShopName(String shippingShopName)
	{
		this.shippingShopName = shippingShopName;
	}
	
	public String getLoadDocType()
	{
		return loadDocType;
	}
	
	public void setLoadDocType(String loadDocType)
	{
		this.loadDocType = loadDocType;
		if (loadDocType!=null)
		{
			switch (loadDocType)
			{
				case "POS":loadDocTypeName="WIN版POS";
					break;
				case "POSANDROID":loadDocTypeName="安卓版POS";
					break;
				case "WECHAT":loadDocTypeName="微信手机商城";
					break;
				case "MINI":loadDocTypeName="小程序商城";
					break;
				case "LINE":loadDocTypeName="LINE手机商城";
					break;
				case "POSSELF":loadDocTypeName="自助收银";
					break;
				case "SCAN":loadDocTypeName="小程序点单";
					break;
				case "PADGUIDE":loadDocTypeName="PAD导购";
					break;
				case "WAIMAI":loadDocTypeName="鼎捷外卖点单";
					break;
				case "APPDISH":loadDocTypeName="掌上云POS";
					break;
				case "PADDISH":loadDocTypeName="PAD点餐";
					break;
				case "MEITUAN":loadDocTypeName="美团";
					break;
				case "ELEME":loadDocTypeName="饿了么";
					break;
				case "JDDJ":loadDocTypeName="京东到家";
					break;
				case "YOUZAN":loadDocTypeName="有赞";
					break;
				case "GUANYIYUN":loadDocTypeName="管易云";
					break;
				case "WUXIANG":loadDocTypeName="舞象云";
					break;
				case "OFFICIAL":loadDocTypeName="味多美官网";
					break;
				case "JDMALL":loadDocTypeName="京东商城";
					break;
				case "SELFDEFINE":loadDocTypeName="自定义渠道";
					break;
				case "XIAOYOU":loadDocTypeName="晓柚";
					break;
				case "QIMAI":loadDocTypeName="企迈";
					break;
                case "OWNCHANNEL":loadDocTypeName="鼎捷云中台";
                    break;
				default:loadDocTypeName=loadDocType;
					break;
			}
		}
		
	}
	
	public String getLoadDocTypeName()
	{
		return loadDocTypeName;
	}
	
	public void setLoadDocTypeName(String loadDocTypeName)
	{
		this.loadDocTypeName = loadDocTypeName;
	}
	
	public String getChannelIdName()
	{
		return channelIdName;
	}
	
	public void setChannelIdName(String channelIdName)
	{
		this.channelIdName = channelIdName;
	}
	
	public String getChannelId()
	{
		return channelId;
	}
	
	public void setChannelId(String channelId)
	{
		this.channelId = channelId;
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
	
	public String getAddress()
	{
		return address;
	}
	
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	public String getShipDate()
	{
		return shipDate;
	}
	
	public void setShipDate(String shipDate)
	{
		this.shipDate = shipDate;
	}
	
	public String getShipStartTime()
	{
		return shipStartTime;
	}
	
	public void setShipStartTime(String shipStartTime)
	{
		this.shipStartTime = shipStartTime;
	}
	
	public String getShipEndTime()
	{
		return shipEndTime;
	}
	
	public void setShipEndTime(String shipEndTime)
	{
		this.shipEndTime = shipEndTime;
	}
	
	public String getShipType()
	{
		return shipType;
	}
	
	public void setShipType(String shipType)
	{
		this.shipType = shipType;
	}
	
	public String getDeliveryType()
	{
		return deliveryType;
	}
	
	public void setDeliveryType(String deliveryType)
	{
		this.deliveryType = deliveryType;
	}
	
	public String getDeliveryNo()
	{
		return deliveryNo;
	}
	
	public void setDeliveryNo(String deliveryNo)
	{
		this.deliveryNo = deliveryNo;
	}
	
	public String getDeliveryStatus()
	{
		return deliveryStatus;
	}
	
	public void setDeliveryStatus(String deliveryStatus)
	{
		this.deliveryStatus = deliveryStatus;
	}
	
	public String getAutoDelivery()
	{
		return autoDelivery;
	}
	
	public void setAutoDelivery(String autoDelivery)
	{
		this.autoDelivery = autoDelivery;
	}
	
	public String getDeliveryBusinessType()
	{
		return deliveryBusinessType;
	}
	
	public void setDeliveryBusinessType(String deliveryBusinessType)
	{
		this.deliveryBusinessType = deliveryBusinessType;
	}
	
	public String getIsUrgentOrder()
	{
		return isUrgentOrder;
	}
	
	public void setIsUrgentOrder(String isUrgentOrder)
	{
		this.isUrgentOrder = isUrgentOrder;
	}
	
	public String getMemo()
	{
		return memo;
	}
	
	public void setMemo(String memo)
	{
		this.memo = memo;
	}
	
	public String getCreateDatetime()
	{
		return createDatetime;
	}
	
	public void setCreateDatetime(String createDatetime)
	{
		this.createDatetime = createDatetime;
	}
	
	public String getSn()
	{
		return sn;
	}
	
	public void setSn(String sn)
	{
		this.sn = sn;
	}
	
	public double getPackageFee()
	{
		return packageFee;
	}
	
	public void setPackageFee(double packageFee)
	{
		this.packageFee = packageFee;
	}
	
	public String getTot_shipFee()
	{
		return tot_shipFee;
	}
	
	public void setTot_shipFee(String tot_shipFee)
	{
		this.tot_shipFee = tot_shipFee;
	}
	
	public String getRshipFee()
	{
		return rshipFee;
	}
	
	public void setRshipFee(String rshipFee)
	{
		this.rshipFee = rshipFee;
	}
	
	public double getShipFee()
	{
		return shipFee;
	}
	
	public void setShipFee(double shipFee)
	{
		this.shipFee = shipFee;
	}
	
	public double getShopShareShipfee()
	{
		return shopShareShipfee;
	}
	
	public void setShopShareShipfee(double shopShareShipfee)
	{
		this.shopShareShipfee = shopShareShipfee;
	}
	
	public double getTot_oldAmt()
	{
		return tot_oldAmt;
	}
	
	public void setTot_oldAmt(double tot_oldAmt)
	{
		this.tot_oldAmt = tot_oldAmt;
	}
	
	public double getTot_Amt()
	{
		return tot_Amt;
	}
	
	public double getTot_qty()
	{
		return tot_qty;
	}
	
	public void setTot_qty(double tot_qty)
	{
		this.tot_qty = tot_qty;
	}
	
	public double getTotQty()
	{
		return totQty;
	}
	
	public void setTotQty(double totQty)
	{
		this.totQty = totQty;
	}
	
	public void setTot_Amt(double tot_Amt)
	{
		this.tot_Amt = tot_Amt;
	}
	
	public double getServiceCharge()
	{
		return serviceCharge;
	}
	
	public void setServiceCharge(double serviceCharge)
	{
		this.serviceCharge = serviceCharge;
	}
	
	public double getIncomeAmt()
	{
		return incomeAmt;
	}
	
	public void setIncomeAmt(double incomeAmt)
	{
		this.incomeAmt = incomeAmt;
	}
	
	public double getTotDisc()
	{
		return totDisc;
	}
	
	public void setTotDisc(double totDisc)
	{
		this.totDisc = totDisc;
	}
	
	public double getSellerDisc()
	{
		return sellerDisc;
	}
	
	public void setSellerDisc(double sellerDisc)
	{
		this.sellerDisc = sellerDisc;
	}
	
	public double getPlatformDisc()
	{
		return platformDisc;
	}
	
	public void setPlatformDisc(double platformDisc)
	{
		this.platformDisc = platformDisc;
	}
	
	public double getPayAmt()
	{
		return payAmt;
	}
	
	public void setPayAmt(double payAmt)
	{
		this.payAmt = payAmt;
	}
	
	public double getSaleAmt() {
		return saleAmt;
	}
	
	public void setSaleAmt(double saleAmt) {
		this.saleAmt = saleAmt;
	}
	
	public double getSaleDisc() {
		return saleDisc;
	}
	
	public void setSaleDisc(double saleDisc) {
		this.saleDisc = saleDisc;
	}
	
	public String getIsBook()
	{
		return isBook;
	}
	
	public void setIsBook(String isBook)
	{
		this.isBook = isBook;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public String getRefundStatus()
	{
		return refundStatus;
	}
	
	public void setRefundStatus(String refundStatus)
	{
		this.refundStatus = refundStatus;
	}
	
	public String getPayStatus()
	{
		return payStatus;
	}
	
	public void setPayStatus(String payStatus)
	{
		this.payStatus = payStatus;
	}
	
	public String getsTime()
	{
		return sTime;
	}
	
	public void setsTime(String sTime)
	{
		this.sTime = sTime;
	}
	
	public double getMealNumber()
	{
		return mealNumber;
	}
	
	public void setMealNumber(double mealNumber)
	{
		this.mealNumber = mealNumber;
	}
	
	public String getManualNo()
	{
		return manualNo;
	}
	
	public void setManualNo(String manualNo)
	{
		this.manualNo = manualNo;
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
	
	public String getCardNo()
	{
		return cardNo;
	}
	
	public void setCardNo(String cardNo)
	{
		this.cardNo = cardNo;
	}
	
	public String getPartnerMember() {
		return partnerMember;
	}
	
	public void setPartnerMember(String partnerMember) {
		this.partnerMember = partnerMember;
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
	
	public double getPointQty()
	{
		return pointQty;
	}
	
	public void setPointQty(double pointQty)
	{
		this.pointQty = pointQty;
	}
	
	public String getSellNo()
	{
		return sellNo;
	}
	
	public void setSellNo(String sellNo)
	{
		this.sellNo = sellNo;
	}
	
	public String getOutDocType()
	{
		return outDocType;
	}
	
	public void setOutDocType(String outDocType)
	{
		this.outDocType = outDocType;
	}
	
	public String getOutDocTypeName()
	{
		return outDocTypeName;
	}
	
	public void setOutDocTypeName(String outDocTypeName)
	{
		this.outDocTypeName = outDocTypeName;
	}
	
	public String getOrderShop()
	{
		return orderShop;
	}
	
	public void setOrderShop(String orderShop)
	{
		this.orderShop = orderShop;
	}
	
	public String getOrderShopName()
	{
		return orderShopName;
	}
	
	public void setOrderShopName(String orderShopName)
	{
		this.orderShopName = orderShopName;
	}
	
	public String getBelfirm()
	{
		return belfirm;
	}
	
	public void setBelfirm(String belfirm)
	{
		this.belfirm = belfirm;
	}
	
	public String getLongitude()
	{
		return longitude;
	}
	
	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}
	
	public String getLatitude()
	{
		return latitude;
	}
	
	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}
	
	public String getProvince()
	{
		return province;
	}
	
	public void setProvince(String province)
	{
		this.province = province;
	}
	
	public String getCity()
	{
		return city;
	}
	
	public void setCity(String city)
	{
		this.city = city;
	}
	
	public String getCounty()
	{
		return county;
	}
	
	public void setCounty(String county)
	{
		this.county = county;
	}
	
	public String getStreet()
	{
		return street;
	}
	
	public void setStreet(String street)
	{
		this.street = street;
	}
	
	public String getProMemo()
	{
		return proMemo;
	}
	
	public void setProMemo(String proMemo)
	{
		this.proMemo = proMemo;
	}
	
	public String getDelMemo()
	{
		return delMemo;
	}
	
	public void setDelMemo(String delMemo)
	{
		this.delMemo = delMemo;
	}
	
	public String getMachineNo()
	{
		return machineNo;
	}
	
	public void setMachineNo(String machineNo)
	{
		this.machineNo = machineNo;
	}
	
	public double getEraseAmt()
	{
		return eraseAmt;
	}
	
	public void setEraseAmt(double eraseAmt)
	{
		this.eraseAmt = eraseAmt;
	}
	
	public String getRequestId()
	{
		return requestId;
	}
	
	public void setRequestId(String requestId)
	{
		this.requestId = requestId;
	}
	
	public String getIsChargeOrder()
	{
		return isChargeOrder;
	}
	
	public void setIsChargeOrder(String isChargeOrder)
	{
		this.isChargeOrder = isChargeOrder;
	}
	
	public String getChargeOrderGetAmt()
	{
		return chargeOrderGetAmt;
	}
	
	public void setChargeOrderGetAmt(String chargeOrderGetAmt)
	{
		this.chargeOrderGetAmt = chargeOrderGetAmt;
	}
	
	public String getbDate()
	{
		return bDate;
	}
	
	public void setbDate(String bDate)
	{
		this.bDate = bDate;
	}
	
	public String getSellCredit()
	{
		return sellCredit;
	}
	
	public void setSellCredit(String sellCredit)
	{
		this.sellCredit = sellCredit;
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
	
	public String getLoadDocBillType()
	{
		return loadDocBillType;
	}
	
	public void setLoadDocBillType(String loadDocBillType)
	{
		this.loadDocBillType = loadDocBillType;
	}
	
	public String getLoadDocOrderNo()
	{
		return loadDocOrderNo;
	}
	
	public void setLoadDocOrderNo(String loadDocOrderNo)
	{
		this.loadDocOrderNo = loadDocOrderNo;
	}
	
	public double getRefundAmt()
	{
		return refundAmt;
	}
	
	public void setRefundAmt(double refundAmt)
	{
		this.refundAmt = refundAmt;
	}
	
	public double getWriteOffAmt()
	{
		return writeOffAmt;
	}
	
	public void setWriteOffAmt(double writeOffAmt)
	{
		this.writeOffAmt = writeOffAmt;
	}
	
	public String getDetailType()
	{
		return detailType;
	}
	
	public void setDetailType(String detailType)
	{
		this.detailType = detailType;
	}
	
	public String getHeadOrderNo()
	{
		return headOrderNo;
	}
	
	public void setHeadOrderNo(String headOrderNo)
	{
		this.headOrderNo = headOrderNo;
	}
	
	public String getRefundSourceBillNo()
	{
		return refundSourceBillNo;
	}
	
	public void setRefundSourceBillNo(String refundSourceBillNo)
	{
		this.refundSourceBillNo = refundSourceBillNo;
	}
	
	public String getRefundReason()
	{
		return refundReason;
	}
	
	public void setRefundReason(String refundReason)
	{
		this.refundReason = refundReason;
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
	
	public String getVerNum()
	{
		return verNum;
	}
	
	public void setVerNum(String verNum)
	{
		this.verNum = verNum;
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
	
	public String getOpNo()
	{
		return opNo;
	}
	
	public void setOpNo(String opNo)
	{
		this.opNo = opNo;
	}

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getCustomer()
	{
		return customer;
	}
	
	public void setCustomer(String customer)
	{
		this.customer = customer;
	}
	
	public String getCustomerName()
	{
		return customerName;
	}
	
	public void setCustomerName(String customerName)
	{
		this.customerName = customerName;
	}
	
	public double getTot_uAmt()
	{
		return tot_uAmt;
	}
	
	public void setTot_uAmt(double tot_uAmt)
	{
		this.tot_uAmt = tot_uAmt;
	}
	
	public String getMemberPayNo()
	{
		return memberPayNo;
	}
	
	public void setMemberPayNo(String memberPayNo)
	{
		this.memberPayNo = memberPayNo;
	}
	
	public orderInvoice getInvoiceDetail()
	{
		return invoiceDetail;
	}
	
	public void setInvoiceDetail(orderInvoice invoiceDetail)
	{
		this.invoiceDetail = invoiceDetail;
	}
	
	public String getIsApportion()
	{
		return isApportion;
	}
	
	public void setIsApportion(String isApportion)
	{
		this.isApportion = isApportion;
	}
	
	public String getExceptionStatus()
	{
		return exceptionStatus;
	}
	
	public void setExceptionStatus(String exceptionStatus)
	{
		this.exceptionStatus = exceptionStatus;
	}
	
	public double getTot_Amt_merReceive()
	{
		return tot_Amt_merReceive;
	}
	
	public void setTot_Amt_merReceive(double tot_Amt_merReceive)
	{
		this.tot_Amt_merReceive = tot_Amt_merReceive;
	}
	
	public double getTot_Amt_custPayReal()
	{
		return tot_Amt_custPayReal;
	}
	
	public void setTot_Amt_custPayReal(double tot_Amt_custPayReal)
	{
		this.tot_Amt_custPayReal = tot_Amt_custPayReal;
	}
	
	public double getTotDisc_merReceive()
	{
		return totDisc_merReceive;
	}
	
	public void setTotDisc_merReceive(double totDisc_merReceive)
	{
		this.totDisc_merReceive = totDisc_merReceive;
	}
	
	public double getTotDisc_custPayReal()
	{
		return totDisc_custPayReal;
	}
	
	public void setTotDisc_custPayReal(double totDisc_custPayReal)
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
	
	public String getIsShipCompany()
	{
		return isShipCompany;
	}
	
	public String getDelId()
	{
		return delId;
	}
	
	public void setDelId(String delId)
	{
		this.delId = delId;
	}
	
	public void setIsShipCompany(String isShipCompany)
	{
		this.isShipCompany = isShipCompany;
	}
	
	public int getTablewareQty()
	{
		return tablewareQty;
	}
	
	public void setTablewareQty(int tablewareQty)
	{
		this.tablewareQty = tablewareQty;
	}
	
	public String getIsDelete()
	{
		return isDelete;
	}
	
	public void setIsDelete(String isDelete)
	{
		this.isDelete = isDelete;
	}
	
	public String getDeliveryTime()
	{
		return deliveryTime;
	}
	
	public void setDeliveryTime(String deliveryTime)
	{
		this.deliveryTime = deliveryTime;
	}
	
	public String getIsEvaluate()
	{
		return isEvaluate;
	}
	
	public void setIsEvaluate(String isEvaluate)
	{
		this.isEvaluate = isEvaluate;
	}
	
	public String getPreparationStatus() {
		return preparationStatus;
	}
	
	public void setPreparationStatus(String preparationStatus) {
		this.preparationStatus = preparationStatus;
	}
	
	public String getIsOrgOrder()
	{
		return isOrgOrder;
	}
	
	public void setIsOrgOrder(String isOrgOrder)
	{
		this.isOrgOrder = isOrgOrder;
	}
	
	public String getOrderCodeView()
	{
		return orderCodeView;
	}
	
	public void setOrderCodeView(String orderCodeView)
	{
		this.orderCodeView = orderCodeView;
	}
	
	public String getWaiMaiMerReceiveMode() {
		return waiMaiMerReceiveMode;
	}
	
	public void setWaiMaiMerReceiveMode(String waiMaiMerReceiveMode) {
		this.waiMaiMerReceiveMode = waiMaiMerReceiveMode;
	}
	
	public String getDowngraded() {
		return downgraded;
	}
	
	public void setDowngraded(String downgraded) {
		this.downgraded = downgraded;
	}
	
	public String getProductStatus() {
		return productStatus;
	}
	
	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}
	
	public String getCanModify() {
		return canModify;
	}
	
	public void setCanModify(String canModify) {
		this.canModify = canModify;
	}
	
	public String getOperationType() {
		return operationType;
	}
	
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	
	public String getMealStatus() {
		return mealStatus;
	}
	
	public void setMealStatus(String mealStatus) {
		this.mealStatus = mealStatus;
	}
	
	public List<orderGoodsItem> getGoodsList()
	{
		return goodsList;
	}
	
	public void setGoodsList(List<orderGoodsItem> goodsList)
	{
		this.goodsList = goodsList;
	}
	
	public List<orderPay> getPay()
	{
		return pay;
	}
	
	public void setPay(List<orderPay> pay)
	{
		this.pay = pay;
	}
	
	public List<orderAbnormal> getAbnormalList()
	{
		return abnormalList;
	}
	
	public void setAbnormalList(List<orderAbnormal> abnormalList)
	{
		this.abnormalList = abnormalList;
	}
	
	public String getDelName() {
		return delName;
	}
	
	public void setDelName(String delName) {
		this.delName = delName;
	}
	
	public String getDelTelephone() {
		return delTelephone;
	}
	
	public void setDelTelephone(String delTelephone) {
		this.delTelephone = delTelephone;
	}
	
	public String getPackerId() {
		return packerId;
	}
	
	public void setPackerId(String packerId) {
		this.packerId = packerId;
	}
	
	public String getPackerName() {
		return packerName;
	}
	
	public void setPackerName(String packerName) {
		this.packerName = packerName;
	}
	
	public String getPackerTelephone() {
		return packerTelephone;
	}
	
	public void setPackerTelephone(String packerTelephone) {
		this.packerTelephone = packerTelephone;
	}
	
	public String getProcess_status() {
		return process_status;
	}
	
	public void setProcess_status(String process_status) {
		this.process_status = process_status;
	}
	
	public String getGroupBuying() {
		return groupBuying;
	}
	
	public void setGroupBuying(String groupBuying) {
		this.groupBuying = groupBuying;
	}
	
	public String getPrintSet() {
		return printSet;
	}
	
	public void setPrintSet(String printSet) {
		this.printSet = printSet;
	}
	
	public String getPayShopId() {
		return payShopId;
	}
	
	public void setPayShopId(String payShopId) {
		this.payShopId = payShopId;
	}
	
	public List<CouponChange> getCouponChangeList() {
		return couponChangeList;
	}
	
	public void setCouponChangeList(List<CouponChange> couponChangeList) {
		this.couponChangeList = couponChangeList;
	}
	
	public String getDeliveryMoney() {
		return deliveryMoney;
	}
	
	public void setDeliveryMoney(String deliveryMoney) {
		this.deliveryMoney = deliveryMoney;
	}
	
	public String getSuperZoneMoney() {
		return superZoneMoney;
	}
	
	public void setSuperZoneMoney(String superZoneMoney) {
		this.superZoneMoney = superZoneMoney;
	}
	
	public String getUrgentMoney() {
		return urgentMoney;
	}
	
	public void setUrgentMoney(String urgentMoney) {
		this.urgentMoney = urgentMoney;
	}

	public String getIsHaveCard() {
		return isHaveCard;
	}

	public void setIsHaveCard(String isHaveCard) {
		this.isHaveCard = isHaveCard;
	}

	public String getIsCardPrint() {
		return isCardPrint;
	}

	public void setIsCardPrint(String isCardPrint) {
		this.isCardPrint = isCardPrint;
	}

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getpOrderNo() {
		return pOrderNo;
	}

	public void setpOrderNo(String pOrderNo) {
		this.pOrderNo = pOrderNo;
	}

	public String getIsIntention() {
        return isIntention;
    }

    public void setIsIntention(String isIntention) {
        this.isIntention = isIntention;
    }

    public List<otherCoupnPay> getOtherCoupnPayList()
    {
        return otherCoupnPayList;
    }

    public void setOtherCoupnPayList(List<otherCoupnPay> otherCoupnPayList)
    {
        this.otherCoupnPayList = otherCoupnPayList;
    }

    @Data
	public class CouponChange{
		private String couponCode;
		private String couponNo;
		private String couponType;
		private String faceAmount;
		private String quantity;
	}

    @Data
    public class otherCoupnPay
    {
        private String item;
        private String couponCode;
    }



}

