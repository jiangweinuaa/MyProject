package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.math.BigDecimal;
import java.util.List;

public class DCP_OrderQueryRes extends JsonRes
{
	private level1Elm datas;
	public level1Elm getDatas()
	{
		return datas;
	}
	public void setDatas(level1Elm datas)
	{
		this.datas = datas;
	}
	public class level1Elm
	{
		private List<orders> orderList;
		
		public List<orders> getOrderList()
		{
			return orderList;
		}
		
		public void setOrderList(List<orders> orderList)
		{
			this.orderList = orderList;
		}
	}
	
	public class orders
	{
		private String shopNo;
		private String shopName;
		private String orderNo;
		private String loadDocType;
		private String loadDocTypeName;
		private String channelId;
		private String channelIdName;
		private String shippingShopNo;
		private String shippingShopName;
		private String machShopNo;
		private String machShopName;
		private String contMan;
		private String contTel;
		private String address;//
		private String shipDate;//
		private String shipStartTime;
		private String shipEndTime;
		private String shipType;
		private String deliveryType;
		private String deliveryNo;
		private String deliveryStatus;
		private String subDeliveryCompanyNo;//物流公司编码
		private String subDeliveryCompanyName;//物流公司名称	
		private String deliveryHandinput;//是否手工录入的物流单号
		private String memo;
		private String delId;
		private String delName;
		private String delTelephone;
		private String createDatetime;
		private String sn;
		private String packageFee;
		private String shipFee;
		private String tot_oldAmt;
		private String tot_Amt;
		private String serviceCharge;
		private String rshipFee;
		private String tot_shipFee;
		private String incomeAmt;
		private String totDisc;
		private String sellerDisc;
		private String platformDisc;
		private String payAmt;
		private String totQty;
		private String isBook;
		private String status;
		private String payStatus;
		private String refundStatus;
		private String sTime;
		private String mealNumber;
		private String manualNo;
		private String getMan;
		private String getManTel;
		private String cardNo;
		private String memberId;
		private String memberName;
		private String partnerMember="";
		private String pointQty;
		private String sellNo;
		private String orderShop;
		private String orderShopName;
		private String outDocType;
		private String outDocTypeName;
		private String belfirm;
		private String longitude;
		private String latitude;
		private String province;
		private String city;
		private String county;
		private String street;
		private String proMemo;
		private String delMemo;
		private String refundAmt;
		private String writeOffAmt;
		private String detailType;
		private String headOrderNo;
		private String refundSourceBillNo;
		private String machineNo;
		private String eraseAmt;
		private String requestId;
		private String isOrgOrder;
		private String isShipCompany;
		private String isChargeOrder;
		private String printCount;
		private String bDate;
		private String sellCredit;
		private String loadDocBillType;
		private String loadDocOrderNo;
		private String refundReason;
		private String refundReasonNo;
		private String refundReasonName;
		private String exceptionStatus;
		private String exceptionMemo;
		private String yaoHuoNo;
		private String verNum;
		private String squadNo;
		private String workNo;
		private String opNo;
        private String opName;
		private String customer;
		private String customerName;
		private String tot_uAmt;
		private String memberPayNo; // 
		private String deliveryBusinessType;//配送业务类型（1随车 2代发）
		private String isUrgentOrder;//是否紧急订单
		private String isApportion;//订单创建时是否已处理套餐商品
		private String orderToSaleDateTime;//订转销时间
		private String tot_Amt_merReceive;//商家实收金额(含税)=TOT_OLDAMT-TOT_DISC_MERRECEIVE
		private String totDisc_merReceive;//商家支付折扣金额(含税)[单身所有商品的商家支付折扣合计]
		private String tot_Amt_custPayReal;//顾客实付金额[含税][TOT_OLDAMT-TOT_DISC_CUSTPAYREAL]
		private String totDisc_custPayReal;//顾客实付折扣金额[含税][单身所有商品的顾客实付折扣合计]
		private String isSystemInvoice;//是否中台开发票
		private String productStatus;//生产状态
		private String tablewareQty;//餐具数量
		private String deliveryTime;//送达时间yyyyMMddHHmmssSSS
		private String isDelete;//是否假删除Y/N
		private String isEvaluate;//是否评价Y/空
		private String preparationStatus;
		private String orderCodeView;//订单展示号一维码,美团外卖出餐码
		/**
		 * 美团饿了么渠道外卖订单商户实收类型 0：嘉华算法   1：店铺收入，其他渠道默认0
		 */
		private String waiMaiMerReceiveMode = "0";
		
		/**
		 * 是否降级订单 Y：是   N：否
		 */
		private String downgraded = "N";
		private String saleAmt;
		private String saleDisc;
		private String groupBuying;
		private String packerId;
		private String packerName;
		private String packerTelephone;
		private String canModify;
		private String operationType;
		private String mealStatus;
		/**
		 * 枚举值：1、接单并打印（不传默认为1） 2、不接单不打印 3、接单不打印
		 */
		private String printSet;
		private levelShipDetail shipDetail;
		private levelInvoice invoiceDetail;
		private String deliveryMoney;   //配送费
		private String superZoneMoney;  //超区费
		private String urgentMoney;     //加急费
        private String pickUpCode;      //取件码
        private String isHaveCard;      //是否含贺卡  Y/N
		private String isCardPrint;     //贺卡是否已打印  Y/N
		private String lineNo;          //路线编码
		private String lineName;        //路线名称
		private String lastRefundDate;        //最晚退单日期 add in 20231230 by liuning
		private String cancelRefundDate;//自动取消退货日期  add in 20231230 by liuning
		private String noRefundMemo;//拒绝退单说明  add in 20231230 by liuning
		private String refundNumber;//退单（申请）次数 add in 20231230 by liuning
		private String returnLogistics;//退货物流单号 add in 20231230 by liuning
        private String pOrderNo;//订转要 要货单号
        private String isIntention;//是否是意向单（Y/N）詹记需求
        /**
         * 追单订单 来源单号
         */
        private String addOrderOriginNo  = "";
        /**
         * 追加订单对应子单号
         */
        private String addOrderchildNo  = "";

        /**
         *赊销单是否已核销
         */
        private String isCreditSell;


        public String getIsCreditSell()
        {
            return isCreditSell;
        }

        public void setIsCreditSell(String isCreditSell)
        {
            this.isCreditSell = isCreditSell;
        }

        public String getReturnLogistics() {
			return returnLogistics;
		}
		public void setReturnLogistics(String returnLogistics) {
			this.returnLogistics = returnLogistics;
		}
		public String getLastRefundDate() {
			return lastRefundDate;
		}
		public void setLastRefundDate(String lastRefundDate) {
			this.lastRefundDate = lastRefundDate;
		}
		public String getCancelRefundDate() {
			return cancelRefundDate;
		}
		public void setCancelRefundDate(String cancelRefundDate) {
			this.cancelRefundDate = cancelRefundDate;
		}
		public String getNoRefundMemo() {
			return noRefundMemo;
		}
		public void setNoRefundMemo(String noRefundMemo) {
			this.noRefundMemo = noRefundMemo;
		}
		public String getRefundNumber() {
			return refundNumber;
		}
		public void setRefundNumber(String refundNumber) {
			this.refundNumber = refundNumber;
		}
		private List<levelGoods> goodsList;
		private List<levelPay> pay;
		private List<orders> childOrderList;
		private List<orders> refundOrderList;


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

        public String getPartnerMember() {
			return partnerMember;
		}
		public void setPartnerMember(String partnerMember) {
			this.partnerMember = partnerMember;
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
		public String getSubDeliveryCompanyNo()
		{
			return subDeliveryCompanyNo;
		}
		public void setSubDeliveryCompanyNo(String subDeliveryCompanyNo)
		{
			this.subDeliveryCompanyNo = subDeliveryCompanyNo;
		}
		public String getSubDeliveryCompanyName()
		{
			return subDeliveryCompanyName;
		}
		public void setSubDeliveryCompanyName(String subDeliveryCompanyName) {
			this.subDeliveryCompanyName = subDeliveryCompanyName;
		}
		public String getDeliveryHandinput()
		{
			return deliveryHandinput;
		}
		public void setDeliveryHandinput(String deliveryHandinput)
		{
			this.deliveryHandinput = deliveryHandinput;
		}
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
		}
		public String getDelId()
		{
			return delId;
		}
		public void setDelId(String delId)
		{
			this.delId = delId;
		}
		public String getDelName()
		{
			return delName;
		}
		public void setDelName(String delName)
		{
			this.delName = delName;
		}
		public String getDelTelephone()
		{
			return delTelephone;
		}
		public void setDelTelephone(String delTelephone)
		{
			this.delTelephone = delTelephone;
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
		public String getPackageFee()
		{
			return packageFee;
		}
		public void setPackageFee(String packageFee)
		{
			this.packageFee = packageFee;
		}
		public String getShipFee()
		{
			return shipFee;
		}
		public void setShipFee(String shipFee)
		{
			this.shipFee = shipFee;
		}
		public String getTot_oldAmt()
		{
			return tot_oldAmt;
		}
		public void setTot_oldAmt(String tot_oldAmt)
		{
			this.tot_oldAmt = tot_oldAmt;
		}
		public String getTot_Amt()
		{
			return tot_Amt;
		}
		public void setTot_Amt(String tot_Amt)
		{
			this.tot_Amt = tot_Amt;
		}
		public String getServiceCharge()
		{
			return serviceCharge;
		}
		public void setServiceCharge(String serviceCharge)
		{
			this.serviceCharge = serviceCharge;
		}
		public String getRshipFee()
		{
			return rshipFee;
		}
		public void setRshipFee(String rshipFee)
		{
			this.rshipFee = rshipFee;
		}
		public String getTot_shipFee()
		{
			return tot_shipFee;
		}
		public void setTot_shipFee(String tot_shipFee)
		{
			this.tot_shipFee = tot_shipFee;
		}
		public String getIncomeAmt()
		{
			return incomeAmt;
		}
		public void setIncomeAmt(String incomeAmt)
		{
			this.incomeAmt = incomeAmt;
		}
		public String getTotDisc()
		{
			return totDisc;
		}
		public void setTotDisc(String totDisc)
		{
			this.totDisc = totDisc;
		}
		public String getSellerDisc()
		{
			return sellerDisc;
		}
		public void setSellerDisc(String sellerDisc)
		{
			this.sellerDisc = sellerDisc;
		}
		public String getPlatformDisc()
		{
			return platformDisc;
		}
		public void setPlatformDisc(String platformDisc)
		{
			this.platformDisc = platformDisc;
		}
		public String getPayAmt()
		{
			return payAmt;
		}
		public void setPayAmt(String payAmt)
		{
			this.payAmt = payAmt;
		}
		public String getTotQty()
		{
			return totQty;
		}
		public void setTotQty(String totQty)
		{
			this.totQty = totQty;
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
		public String getPayStatus()
		{
			return payStatus;
		}
		public void setPayStatus(String payStatus)
		{
			this.payStatus = payStatus;
		}
		public String getRefundStatus()
		{
			return refundStatus;
		}
		public void setRefundStatus(String refundStatus)
		{
			this.refundStatus = refundStatus;
		}
		public String getsTime()
		{
			return sTime;
		}
		public void setsTime(String sTime)
		{
			this.sTime = sTime;
		}
		public String getMealNumber()
		{
			return mealNumber;
		}
		public void setMealNumber(String mealNumber)
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
		public String getPointQty()
		{
			return pointQty;
		}
		public void setPointQty(String pointQty)
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
		public String getRefundAmt()
		{
			return refundAmt;
		}
		public void setRefundAmt(String refundAmt)
		{
			this.refundAmt = refundAmt;
		}
		public String getWriteOffAmt()
		{
			return writeOffAmt;
		}
		public void setWriteOffAmt(String writeOffAmt)
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
		public String getMachineNo()
		{
			return machineNo;
		}
		public void setMachineNo(String machineNo)
		{
			this.machineNo = machineNo;
		}
		public String getEraseAmt()
		{
			return eraseAmt;
		}
		public void setEraseAmt(String eraseAmt)
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
		public String getIsOrgOrder()
		{
			return isOrgOrder;
		}
		public void setIsOrgOrder(String isOrgOrder)
		{
			this.isOrgOrder = isOrgOrder;
		}
		public String getIsShipCompany()
		{
			return isShipCompany;
		}
		public void setIsShipCompany(String isShipCompany)
		{
			this.isShipCompany = isShipCompany;
		}
		public String getIsChargeOrder()
		{
			return isChargeOrder;
		}
		public void setIsChargeOrder(String isChargeOrder)
		{
			this.isChargeOrder = isChargeOrder;
		}
		public String getPrintCount()
		{
			return printCount;
		}
		public void setPrintCount(String printCount)
		{
			this.printCount = printCount;
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
		public String getExceptionStatus()
		{
			return exceptionStatus;
		}
		public void setExceptionStatus(String exceptionStatus)
		{
			this.exceptionStatus = exceptionStatus;
		}
		public String getExceptionMemo()
		{
			return exceptionMemo;
		}
		public void setExceptionMemo(String exceptionMemo)
		{
			this.exceptionMemo = exceptionMemo;
		}
		public String getYaoHuoNo()
		{
			return yaoHuoNo;
		}
		public void setYaoHuoNo(String yaoHuoNo)
		{
			this.yaoHuoNo = yaoHuoNo;
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
		public String getTot_uAmt()
		{
			return tot_uAmt;
		}
		public void setTot_uAmt(String tot_uAmt)
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
		public String getIsApportion()
		{
			return isApportion;
		}
		public void setIsApportion(String isApportion)
		{
			this.isApportion = isApportion;
		}
		public String getOrderToSaleDateTime()
		{
			return orderToSaleDateTime;
		}
		public void setOrderToSaleDateTime(String orderToSaleDateTime)
		{
			this.orderToSaleDateTime = orderToSaleDateTime;
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
		public String getIsSystemInvoice()
		{
			return isSystemInvoice;
		}
		public void setIsSystemInvoice(String isSystemInvoice)
		{
			this.isSystemInvoice = isSystemInvoice;
		}
		public String getOrderCodeView()
		{
			return orderCodeView;
		}
		public void setOrderCodeView(String orderCodeView)
		{
			this.orderCodeView = orderCodeView;
		}
		public List<levelGoods> getGoodsList()
		{
			return goodsList;
		}
		public void setGoodsList(List<levelGoods> goodsList)
		{
			this.goodsList = goodsList;
		}
		public List<levelPay> getPay()
		{
			return pay;
		}
		public void setPay(List<levelPay> pay)
		{
			this.pay = pay;
		}
		public List<orders> getChildOrderList()
		{
			return childOrderList;
		}
		public void setChildOrderList(List<orders> childOrderList)
		{
			this.childOrderList = childOrderList;
		}
		public List<orders> getRefundOrderList()
		{
			return refundOrderList;
		}
		public void setRefundOrderList(List<orders> refundOrderList)
		{
			this.refundOrderList = refundOrderList;
		}
		public levelShipDetail getShipDetail()
		{
			return shipDetail;
		}
		public void setShipDetail(levelShipDetail shipDetail)
		{
			this.shipDetail = shipDetail;
		}
		public levelInvoice getInvoiceDetail()
		{
			return invoiceDetail;
		}
		public void setInvoiceDetail(levelInvoice invoiceDetail)
		{
			this.invoiceDetail = invoiceDetail;
		}
		public String getProductStatus()
		{
			return productStatus;
		}
		public void setProductStatus(String productStatus)
		{
			this.productStatus = productStatus;
		}
		public String getTablewareQty()
		{
			return tablewareQty;
		}
		public void setTablewareQty(String tablewareQty)
		{
			this.tablewareQty = tablewareQty;
		}
		public String getDeliveryTime()
		{
			return deliveryTime;
		}
		public void setDeliveryTime(String deliveryTime)
		{
			this.deliveryTime = deliveryTime;
		}
		public String getIsDelete()
		{
			return isDelete;
		}
		public void setIsDelete(String isDelete)
		{
			this.isDelete = isDelete;
		}
		public String getIsEvaluate()
		{
			return isEvaluate;
		}
		public void setIsEvaluate(String isEvaluate)
		{
			this.isEvaluate = isEvaluate;
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
		public String getSaleAmt() {
			return saleAmt;
		}
		public void setSaleAmt(String saleAmt) {
			this.saleAmt = saleAmt;
		}
		public String getSaleDisc() {
			return saleDisc;
		}
		public void setSaleDisc(String saleDisc) {
			this.saleDisc = saleDisc;
		}
		public String getPreparationStatus() {
			return preparationStatus;
		}
		public void setPreparationStatus(String preparationStatus) {
			this.preparationStatus = preparationStatus;
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
        public String getPickUpCode() {
            return pickUpCode;
        }
        public void setPickUpCode(String pickUpCode) {
            this.pickUpCode = pickUpCode;
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
    }
	
	
	public class levelGoods
	{
		private String item;
		private String pluBarcode;
		private String pluNo;
		private String pluName;
		private String featureNo;
		private String featureName;
		private String warehouse;
		private String warehouseName;
		private String sUnit;
		private String sUnitName;
		private String specName;
		private String attrName;
		private String specName_origin;
		private String attrName_origin;
		private String price;
		private String qty;
		private String goodsGroup;
		private String disc;//
		private String boxNum;//
		private String boxPrice;//
		private String amt;//
		private String isMemo;
		private String pickQty;
		private String rQty;//已退总数
		private String rUnpickQty;//未提退货数
		private String shopQty;//
		private String packageType;//
		private String packageMitem;//
		private String toppingType;//
		private String toppingMitem;//
		private String couponType;//
		private String couponCode;//
		private String gift;//	
		private String giftSourceSerialNo;//		
		private String goodsUrl;//		
		private String sellerNo;//		
		private String sellerName;//		
		private String accNo;//		
		private String counterNo;//		
		private String oldPrice;//		
		private String oldAmt;//		
		private String giftReason;//		
		private String sTime;//		
		private String oItem;//				
		private String taxCode;//		
		private String taxType;//		
		private String inclTax;//		
		private String invSplitType;//		
		private String invNo;//			
		private String oReItem;//	
		private String invItem;
		private String virtual;//			
		private String disc_merReceive;//商家支付折扣金额			
		private String disc_custPayReal;//顾客支付折扣金额			
		private String amt_merReceive;//商家实收金额			
		private String amt_custPayReal;//顾客实付金额			
		private String preparationStatus;
		/**
		 * 生产门店需要生产数量
		 */
		private String machShopQty;
		/**
		 * 是否酒水Y/N
		 */
		private String isLiquor;
		/**
		 * ksdMaxMakeQty
		 */
		private BigDecimal kdsMaxMakeQty;
		/**
		 *isQtyPrint：Y/N
		 */
		private String isQtyPrint;
		/**
		 * 是否打印退菜单Y/N
		 */
		private String isPrintReturn;
		/**
		 * 是否打印划菜单Y/N
		 */
		private String isPrintCrossMenu;
		/**
		 * 划菜单打印机
		 */
		private String crossPrinter;
		/**
		 * 后厨打印机
		 */
		private String kitchenPrinter;
		private String goodsType;    //是否需要生产  1不需要 2需要
		private String shelfLife;    //保质期
		private String netContent;   //净含量
		private String amountRefund;   //已退金额
        /**
         * 口味描述
         */
        private String flavorStuffDetail;
        private String goodsMemo;
		private String shelfLifen; //保质期 (小时)
        /**
         *产地
         */
        private String producer;//产地
        private String storagecon;//存储条件
        private String manuFacturer;//生产商
        private String hotline;//热线
        private String ingretable;//配料表
        private String foodProlicnum;//食品生产许可证编号
        private String eatingMethod;//食用方式
        private String exstandard;//执行标准
        private String propAddress;//产址

        private List<levelNutrition> nutritions;
        private List<levelMemo> messages;
		private List<levelAgio> agioInfo;

        public String getProducer() {
            return producer;
        }

        public void setProducer(String producer) {
            this.producer = producer;
        }

        public String getStoragecon() {
            return storagecon;
        }

        public void setStoragecon(String storagecon) {
            this.storagecon = storagecon;
        }

        public String getManuFacturer() {
            return manuFacturer;
        }

        public void setManuFacturer(String manuFacturer) {
            this.manuFacturer = manuFacturer;
        }

        public String getHotline() {
            return hotline;
        }

        public void setHotline(String hotline) {
            this.hotline = hotline;
        }

        public String getIngretable() {
            return ingretable;
        }

        public void setIngretable(String ingretable) {
            this.ingretable = ingretable;
        }

        public String getFoodProlicnum() {
            return foodProlicnum;
        }

        public void setFoodProlicnum(String foodProlicnum) {
            this.foodProlicnum = foodProlicnum;
        }

        public String getEatingMethod() {
            return eatingMethod;
        }

        public void setEatingMethod(String eatingMethod) {
            this.eatingMethod = eatingMethod;
        }

        public String getExstandard() {
            return exstandard;
        }

        public void setExstandard(String exstandard) {
            this.exstandard = exstandard;
        }

        public String getPropAddress() {
            return propAddress;
        }

        public void setPropAddress(String propAddress) {
            this.propAddress = propAddress;
        }

        public List<levelNutrition> getNutritions() {
            return nutritions;
        }

        public void setNutritions(List<levelNutrition> nutritions) {
            this.nutritions = nutritions;
        }

        public String getAmountRefund() {
			return amountRefund;
		}
		public void setAmountRefund(String amountRefund) {
			this.amountRefund = amountRefund;
		}
		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}
		public String getPluBarcode()
		{
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode)
		{
			this.pluBarcode = pluBarcode;
		}
		public String getPluNo()
		{
			return pluNo;
		}
		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}
		public String getPluName()
		{
			return pluName;
		}
		public void setPluName(String pluName)
		{
			this.pluName = pluName;
		}
		public String getFeatureNo()
		{
			return featureNo;
		}
		public void setFeatureNo(String featureNo)
		{
			this.featureNo = featureNo;
		}
		public String getFeatureName()
		{
			return featureName;
		}
		public void setFeatureName(String featureName)
		{
			this.featureName = featureName;
		}
		public String getWarehouse()
		{
			return warehouse;
		}
		public void setWarehouse(String warehouse)
		{
			this.warehouse = warehouse;
		}
		public String getWarehouseName()
		{
			return warehouseName;
		}
		public void setWarehouseName(String warehouseName)
		{
			this.warehouseName = warehouseName;
		}
		public String getsUnit()
		{
			return sUnit;
		}
		public void setsUnit(String sUnit)
		{
			this.sUnit = sUnit;
		}
		public String getsUnitName()
		{
			return sUnitName;
		}
		public void setsUnitName(String sUnitName)
		{
			this.sUnitName = sUnitName;
		}
		public String getSpecName()
		{
			return specName;
		}
		public void setSpecName(String specName)
		{
			this.specName = specName;
		}
		public String getAttrName()
		{
			return attrName;
		}
		public void setAttrName(String attrName)
		{
			this.attrName = attrName;
		}
		public String getPrice()
		{
			return price;
		}
		public void setPrice(String price)
		{
			this.price = price;
		}
		public String getQty()
		{
			return qty;
		}
		public void setQty(String qty)
		{
			this.qty = qty;
		}
		public String getGoodsGroup()
		{
			return goodsGroup;
		}
		public void setGoodsGroup(String goodsGroup)
		{
			this.goodsGroup = goodsGroup;
		}
		public String getDisc()
		{
			return disc;
		}
		public void setDisc(String disc)
		{
			this.disc = disc;
		}
		public String getBoxNum()
		{
			return boxNum;
		}
		public void setBoxNum(String boxNum)
		{
			this.boxNum = boxNum;
		}
		public String getBoxPrice()
		{
			return boxPrice;
		}
		public void setBoxPrice(String boxPrice)
		{
			this.boxPrice = boxPrice;
		}
		public String getAmt()
		{
			return amt;
		}
		public void setAmt(String amt)
		{
			this.amt = amt;
		}
		public String getIsMemo()
		{
			return isMemo;
		}
		public void setIsMemo(String isMemo)
		{
			this.isMemo = isMemo;
		}
		public String getPickQty()
		{
			return pickQty;
		}
		public void setPickQty(String pickQty)
		{
			this.pickQty = pickQty;
		}
		public String getrQty()
		{
			return rQty;
		}
		public void setrQty(String rQty)
		{
			this.rQty = rQty;
		}
		public String getrUnpickQty()
		{
			return rUnpickQty;
		}
		public void setrUnpickQty(String rUnpickQty)
		{
			this.rUnpickQty = rUnpickQty;
		}
		public String getShopQty()
		{
			return shopQty;
		}
		public void setShopQty(String shopQty)
		{
			this.shopQty = shopQty;
		}
		public String getPackageType()
		{
			return packageType;
		}
		public void setPackageType(String packageType)
		{
			this.packageType = packageType;
		}
		public String getPackageMitem()
		{
			return packageMitem;
		}
		public void setPackageMitem(String packageMitem)
		{
			this.packageMitem = packageMitem;
		}
		public String getToppingType()
		{
			return toppingType;
		}
		public void setToppingType(String toppingType)
		{
			this.toppingType = toppingType;
		}
		public String getToppingMitem()
		{
			return toppingMitem;
		}
		public void setToppingMitem(String toppingMitem)
		{
			this.toppingMitem = toppingMitem;
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
		public String getGift()
		{
			return gift;
		}
		public void setGift(String gift)
		{
			this.gift = gift;
		}
		public String getGiftSourceSerialNo()
		{
			return giftSourceSerialNo;
		}
		public void setGiftSourceSerialNo(String giftSourceSerialNo)
		{
			this.giftSourceSerialNo = giftSourceSerialNo;
		}
		public String getGoodsUrl()
		{
			return goodsUrl;
		}
		public void setGoodsUrl(String goodsUrl)
		{
			this.goodsUrl = goodsUrl;
		}
		public String getSellerNo()
		{
			return sellerNo;
		}
		public void setSellerNo(String sellerNo)
		{
			this.sellerNo = sellerNo;
		}
		public String getSellerName()
		{
			return sellerName;
		}
		public void setSellerName(String sellerName)
		{
			this.sellerName = sellerName;
		}
		public String getAccNo()
		{
			return accNo;
		}
		public void setAccNo(String accNo)
		{
			this.accNo = accNo;
		}
		public String getCounterNo()
		{
			return counterNo;
		}
		public void setCounterNo(String counterNo)
		{
			this.counterNo = counterNo;
		}
		public String getOldPrice()
		{
			return oldPrice;
		}
		public void setOldPrice(String oldPrice)
		{
			this.oldPrice = oldPrice;
		}
		public String getOldAmt()
		{
			return oldAmt;
		}
		public void setOldAmt(String oldAmt)
		{
			this.oldAmt = oldAmt;
		}
		public String getGiftReason()
		{
			return giftReason;
		}
		public void setGiftReason(String giftReason)
		{
			this.giftReason = giftReason;
		}
		public String getsTime()
		{
			return sTime;
		}
		public void setsTime(String sTime)
		{
			this.sTime = sTime;
		}
		public String getoItem()
		{
			return oItem;
		}
		public void setoItem(String oItem)
		{
			this.oItem = oItem;
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
		public String getInclTax()
		{
			return inclTax;
		}
		public void setInclTax(String inclTax)
		{
			this.inclTax = inclTax;
		}
		public String getInvSplitType()
		{
			return invSplitType;
		}
		public void setInvSplitType(String invSplitType)
		{
			this.invSplitType = invSplitType;
		}
		public String getInvNo()
		{
			return invNo;
		}
		public void setInvNo(String invNo)
		{
			this.invNo = invNo;
		}
		public String getoReItem()
		{
			return oReItem;
		}
		public void setoReItem(String oReItem)
		{
			this.oReItem = oReItem;
		}
		public String getInvItem()
		{
			return invItem;
		}
		public void setInvItem(String invItem)
		{
			this.invItem = invItem;
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
		public String getPreparationStatus() {
			return preparationStatus;
		}
		public void setPreparationStatus(String preparationStatus) {
			this.preparationStatus = preparationStatus;
		}
		public String getIsLiquor()
		{
			return isLiquor;
		}
		public void setIsLiquor(String isLiquor)
		{
			this.isLiquor = isLiquor;
		}
		public BigDecimal getKdsMaxMakeQty()
		{
			return kdsMaxMakeQty;
		}
		public void setKdsMaxMakeQty(BigDecimal kdsMaxMakeQty)
		{
			this.kdsMaxMakeQty = kdsMaxMakeQty;
		}
		public String getIsQtyPrint()
		{
			return isQtyPrint;
		}
		public void setIsQtyPrint(String isQtyPrint)
		{
			this.isQtyPrint = isQtyPrint;
		}
		public String getIsPrintReturn()
		{
			return isPrintReturn;
		}
		public void setIsPrintReturn(String isPrintReturn)
		{
			this.isPrintReturn = isPrintReturn;
		}
		public String getIsPrintCrossMenu()
		{
			return isPrintCrossMenu;
		}
		public void setIsPrintCrossMenu(String isPrintCrossMenu)
		{
			this.isPrintCrossMenu = isPrintCrossMenu;
		}
		public String getCrossPrinter()
		{
			return crossPrinter;
		}
		public void setCrossPrinter(String crossPrinter)
		{
			this.crossPrinter = crossPrinter;
		}
		public String getKitchenPrinter()
		{
			return kitchenPrinter;
		}
		public void setKitchenPrinter(String kitchenPrinter)
		{
			this.kitchenPrinter = kitchenPrinter;
		}
		public String getSpecName_origin() {
			return specName_origin;
		}
		public void setSpecName_origin(String specName_origin) {
			this.specName_origin = specName_origin;
		}
		public String getAttrName_origin() {
			return attrName_origin;
		}
		public void setAttrName_origin(String attrName_origin) {
			this.attrName_origin = attrName_origin;
		}
		public String getMachShopQty() {
			return machShopQty;
		}
		public void setMachShopQty(String machShopQty) {
			this.machShopQty = machShopQty;
		}
		public List<levelMemo> getMessages()
		{
			return messages;
		}
		public void setMessages(List<levelMemo> messages)
		{
			this.messages = messages;
		}
		public List<levelAgio> getAgioInfo()
		{
			return agioInfo;
		}
		public void setAgioInfo(List<levelAgio> agioInfo)
		{
			this.agioInfo = agioInfo;
		}
		public String getGoodsType() {
			return goodsType;
		}
		public void setGoodsType(String goodsType) {
			this.goodsType = goodsType;
		}
		public String getShelfLife() {
			return shelfLife;
		}
		public void setShelfLife(String shelfLife) {
			this.shelfLife = shelfLife;
		}
		public String getNetContent() {
			return netContent;
		}
		public void setNetContent(String netContent) {
			this.netContent = netContent;
		}
        public String getFlavorStuffDetail() {
            return flavorStuffDetail;
        }
        public void setFlavorStuffDetail(String flavorStuffDetail) {
            this.flavorStuffDetail = flavorStuffDetail;
        }
		public String getGoodsMemo() {
			return goodsMemo;
		}
		public void setGoodsMemo(String goodsMemo) {
			this.goodsMemo = goodsMemo;
		}
		public String getShelfLifen() {
			return shelfLifen;
		}
		public void setShelfLifen(String shelfLifen) {
			this.shelfLifen = shelfLifen;
		}
	}
	
	public class levelPay
	{
		private String item;
		private String payCode;
		private String payCodeErp;
		private String payName;
		private String loadDocType;
		private String order_payCode;
		private String mobile;
		private String cardNo;
		private String ctType;
		private String paySerNum;
		private String serialNo;
		private String refNo;
		private String teriminalNo;
		private String authCode;
		private String isTurnover;
		private String descore;
		private String pay;
		private String payDiscAmt;
		private String payAmt1;
		private String payAmt2;
		private String cardBeforeAmt;
		private String cardRemainAmt;
		private String sendPay;
		private String extra;
		private String changed;
		private String isOrderpay;
		private String isOnlinePay;
		private String couponQty;
		private String isVerification;
		private String canInvoice;//
		private String bDate;//
		private String funcNo;//功能编码		
		private String paydoctype;//POS=4处理卡的扣款类型
		private String payType;//新零售支付编码
		private String merDiscount;//商户优惠金额，移动支付用，例如支付宝，微信等
		private String merReceive;//商家实收金额，移动支付用，例如支付宝，微信等
		private String thirdDiscount;//第三方优惠金额：移动支付用，例如支付宝，微信等
		private String custPayReal;//客户实付金额：移动支付用，例如支付宝，微信等
		private String couponMarketPrice;//券面值
		private String couponPrice;//券售价
		private String payChannelCode;
        private String couponTypeName;   //券类型名称
        private String gainChannel;//企迈券渠道
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
		public String getLoadDocType()
		{
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType)
		{
			this.loadDocType = loadDocType;
		}
		public String getOrder_payCode()
		{
			return order_payCode;
		}
		public void setOrder_payCode(String order_payCode)
		{
			this.order_payCode = order_payCode;
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
		public String getAuthCode()
		{
			return authCode;
		}
		public void setAuthCode(String authCode)
		{
			this.authCode = authCode;
		}
		public String getIsTurnover()
		{
			return isTurnover;
		}
		public void setIsTurnover(String isTurnover)
		{
			this.isTurnover = isTurnover;
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
		public String getSendPay()
		{
			return sendPay;
		}
		public void setSendPay(String sendPay)
		{
			this.sendPay = sendPay;
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
		public String getCardNo()
		{
			return cardNo;
		}
		public void setCardNo(String cardNo)
		{
			this.cardNo = cardNo;
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
		public String getPayType()
		{
			return payType;
		}
		public void setPayType(String payType)
		{
			this.payType = payType;
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
		public String getMobile()
		{
			return mobile;
		}
		public void setMobile(String mobile)
		{
			this.mobile = mobile;
		}
		public String getCouponTypeName() {
			return couponTypeName;
		}
		public void setCouponTypeName(String couponTypeName) {
			this.couponTypeName = couponTypeName;
		}
	}
	
	public class levelAgio
	{
		private String item;
		private String qty;
		private String amt;
		private String inputDisc;
		private String realDisc;
		private String disc;
		private String dcType;
		private String dcTypeName;
		private String pmtNo;
		private String giftCtf;
		private String giftCtfNo;
		private String bsNo;
		private String disc_merReceive;//折扣金额[商户实收]
		private String disc_custPayReal;//折扣金额[顾客实付]
		
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
		public String getInputDisc()
		{
			return inputDisc;
		}
		public void setInputDisc(String inputDisc)
		{
			this.inputDisc = inputDisc;
		}
		public String getRealDisc()
		{
			return realDisc;
		}
		public void setRealDisc(String realDisc)
		{
			this.realDisc = realDisc;
		}
		public String getDisc()
		{
			return disc;
		}
		public void setDisc(String disc)
		{
			this.disc = disc;
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
		public String getGiftCtf()
		{
			return giftCtf;
		}
		public void setGiftCtf(String giftCtf)
		{
			this.giftCtf = giftCtf;
		}
		public String getGiftCtfNo()
		{
			return giftCtfNo;
		}
		public void setGiftCtfNo(String giftCtfNo)
		{
			this.giftCtfNo = giftCtfNo;
		}
		public String getBsNo()
		{
			return bsNo;
		}
		public void setBsNo(String bsNo)
		{
			this.bsNo = bsNo;
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
	
	public class levelMemo
	{
		private String msgType;
		private String msgName;
		private String message;
		public String getMsgType() {
			return msgType;
		}
		public void setMsgType(String msgType) {
			this.msgType = msgType;
		}
		public String getMsgName() {
			return msgName;
		}
		public void setMsgName(String msgName) {
			this.msgName = msgName;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
	}
	
	public class levelInvoice
	{
		private String isInvoice;
		private String invOperateType;//	
		private String peopleType;//主体类型：1公司，2个人
		private String invoiceType;//
		private String invoiceDate;//	
		private String invoiceTitle;
		private String taxRegNumber;//		
		private String passPort;//
		private String freeCode;//
		private String buyerGuiNo;//
		private String carrierCode;//
		private String carrierShowId;//
		private String carrierHiddenId;//
		private String loveCode;//
		private String invMemo;//
		private String invSplitType;//
		private String invNo;//
		
		
		public String getIsInvoice()
		{
			return isInvoice;
		}
		public void setIsInvoice(String isInvoice)
		{
			this.isInvoice = isInvoice;
		}
		public String getInvOperateType()
		{
			return invOperateType;
		}
		public void setInvOperateType(String invOperateType)
		{
			this.invOperateType = invOperateType;
		}
		
		public String getPeopleType()
		{
			return peopleType;
		}
		public void setPeopleType(String peopleType)
		{
			this.peopleType = peopleType;
		}
		public String getInvoiceType()
		{
			return invoiceType;
		}
		public void setInvoiceType(String invoiceType)
		{
			this.invoiceType = invoiceType;
		}
		public String getInvoiceDate()
		{
			return invoiceDate;
		}
		public void setInvoiceDate(String invoiceDate)
		{
			this.invoiceDate = invoiceDate;
		}
		public String getInvoiceTitle()
		{
			return invoiceTitle;
		}
		public void setInvoiceTitle(String invoiceTitle)
		{
			this.invoiceTitle = invoiceTitle;
		}
		public String getTaxRegNumber()
		{
			return taxRegNumber;
		}
		public void setTaxRegNumber(String taxRegNumber)
		{
			this.taxRegNumber = taxRegNumber;
		}
		public String getPassPort()
		{
			return passPort;
		}
		public void setPassPort(String passPort)
		{
			this.passPort = passPort;
		}
		public String getFreeCode()
		{
			return freeCode;
		}
		public void setFreeCode(String freeCode)
		{
			this.freeCode = freeCode;
		}
		public String getBuyerGuiNo()
		{
			return buyerGuiNo;
		}
		public void setBuyerGuiNo(String buyerGuiNo)
		{
			this.buyerGuiNo = buyerGuiNo;
		}
		public String getCarrierCode()
		{
			return carrierCode;
		}
		public void setCarrierCode(String carrierCode)
		{
			this.carrierCode = carrierCode;
		}
		public String getCarrierShowId()
		{
			return carrierShowId;
		}
		public void setCarrierShowId(String carrierShowId)
		{
			this.carrierShowId = carrierShowId;
		}
		public String getCarrierHiddenId()
		{
			return carrierHiddenId;
		}
		public void setCarrierHiddenId(String carrierHiddenId)
		{
			this.carrierHiddenId = carrierHiddenId;
		}
		public String getLoveCode()
		{
			return loveCode;
		}
		public void setLoveCode(String loveCode)
		{
			this.loveCode = loveCode;
		}
		public String getInvMemo()
		{
			return invMemo;
		}
		public void setInvMemo(String invMemo)
		{
			this.invMemo = invMemo;
		}
		public String getInvSplitType()
		{
			return invSplitType;
		}
		public void setInvSplitType(String invSplitType)
		{
			this.invSplitType = invSplitType;
		}
		public String getInvNo()
		{
			return invNo;
		}
		public void setInvNo(String invNo)
		{
			this.invNo = invNo;
		}
		
	}
	
	public class levelShipDetail
	{
		private String packageNo;
		private String packageName;
		private String measureNo;
		private String measureName;
		private String temperateNo;
		private String temperateName;
		private String weight;
		private String collectAmt;
		private String pickUpDocPrint;
		private String distanceNo;
		private String distanceName;
		private String receiverFivecode;
		private String receiverSevencode;
		private String shopeeMode;
		private String shopeeAddressId;
		private String shopeePickuptimeId;
		private String shopeeBranchId;
		private String shopeeSenderRealName;
		private String greenworld_logisticsId;
		private String greenworld_merchantTradeNo;
		private String greenworld_validNo;
		private String greenworld_rtnLogisticsId;
		private String greenworld_rtnMerchantTradeNo;
		private String greenworld_rtnValidNo;
		private String greenworld_rtnOrderNo;
		public String getPackageNo()
		{
			return packageNo;
		}
		public void setPackageNo(String packageNo)
		{
			this.packageNo = packageNo;
		}
		public String getPackageName()
		{
			return packageName;
		}
		public void setPackageName(String packageName)
		{
			this.packageName = packageName;
		}
		public String getMeasureNo()
		{
			return measureNo;
		}
		public void setMeasureNo(String measureNo)
		{
			this.measureNo = measureNo;
		}
		public String getMeasureName()
		{
			return measureName;
		}
		public void setMeasureName(String measureName)
		{
			this.measureName = measureName;
		}
		public String getTemperateNo()
		{
			return temperateNo;
		}
		public void setTemperateNo(String temperateNo)
		{
			this.temperateNo = temperateNo;
		}
		public String getTemperateName()
		{
			return temperateName;
		}
		public void setTemperateName(String temperateName)
		{
			this.temperateName = temperateName;
		}
		
		public String getWeight()
		{
			return weight;
		}
		public void setWeight(String weight)
		{
			this.weight = weight;
		}
		public String getCollectAmt()
		{
			return collectAmt;
		}
		public void setCollectAmt(String collectAmt)
		{
			this.collectAmt = collectAmt;
		}
		public String getPickUpDocPrint()
		{
			return pickUpDocPrint;
		}
		public void setPickUpDocPrint(String pickUpDocPrint)
		{
			this.pickUpDocPrint = pickUpDocPrint;
		}
		public String getDistanceNo()
		{
			return distanceNo;
		}
		public void setDistanceNo(String distanceNo)
		{
			this.distanceNo = distanceNo;
		}
		public String getDistanceName()
		{
			return distanceName;
		}
		public void setDistanceName(String distanceName)
		{
			this.distanceName = distanceName;
		}
		public String getReceiverFivecode()
		{
			return receiverFivecode;
		}
		public void setReceiverFivecode(String receiverFivecode)
		{
			this.receiverFivecode = receiverFivecode;
		}
		public String getReceiverSevencode()
		{
			return receiverSevencode;
		}
		public void setReceiverSevencode(String receiverSevencode)
		{
			this.receiverSevencode = receiverSevencode;
		}
		public String getShopeeMode()
		{
			return shopeeMode;
		}
		public void setShopeeMode(String shopeeMode)
		{
			this.shopeeMode = shopeeMode;
		}
		public String getShopeeAddressId()
		{
			return shopeeAddressId;
		}
		public void setShopeeAddressId(String shopeeAddressId)
		{
			this.shopeeAddressId = shopeeAddressId;
		}
		public String getShopeePickuptimeId()
		{
			return shopeePickuptimeId;
		}
		public void setShopeePickuptimeId(String shopeePickuptimeId)
		{
			this.shopeePickuptimeId = shopeePickuptimeId;
		}
		public String getShopeeBranchId()
		{
			return shopeeBranchId;
		}
		public void setShopeeBranchId(String shopeeBranchId)
		{
			this.shopeeBranchId = shopeeBranchId;
		}
		public String getShopeeSenderRealName()
		{
			return shopeeSenderRealName;
		}
		public void setShopeeSenderRealName(String shopeeSenderRealName)
		{
			this.shopeeSenderRealName = shopeeSenderRealName;
		}
		public String getGreenworld_logisticsId()
		{
			return greenworld_logisticsId;
		}
		public void setGreenworld_logisticsId(String greenworld_logisticsId)
		{
			this.greenworld_logisticsId = greenworld_logisticsId;
		}
		public String getGreenworld_merchantTradeNo()
		{
			return greenworld_merchantTradeNo;
		}
		public void setGreenworld_merchantTradeNo(String greenworld_merchantTradeNo)
		{
			this.greenworld_merchantTradeNo = greenworld_merchantTradeNo;
		}
		public String getGreenworld_validNo()
		{
			return greenworld_validNo;
		}
		public void setGreenworld_validNo(String greenworld_validNo)
		{
			this.greenworld_validNo = greenworld_validNo;
		}
		public String getGreenworld_rtnLogisticsId()
		{
			return greenworld_rtnLogisticsId;
		}
		public void setGreenworld_rtnLogisticsId(String greenworld_rtnLogisticsId)
		{
			this.greenworld_rtnLogisticsId = greenworld_rtnLogisticsId;
		}
		public String getGreenworld_rtnMerchantTradeNo()
		{
			return greenworld_rtnMerchantTradeNo;
		}
		public void setGreenworld_rtnMerchantTradeNo(String greenworld_rtnMerchantTradeNo)
		{
			this.greenworld_rtnMerchantTradeNo = greenworld_rtnMerchantTradeNo;
		}
		public String getGreenworld_rtnValidNo()
		{
			return greenworld_rtnValidNo;
		}
		public void setGreenworld_rtnValidNo(String greenworld_rtnValidNo)
		{
			this.greenworld_rtnValidNo = greenworld_rtnValidNo;
		}
		public String getGreenworld_rtnOrderNo()
		{
			return greenworld_rtnOrderNo;
		}
		public void setGreenworld_rtnOrderNo(String greenworld_rtnOrderNo)
		{
			this.greenworld_rtnOrderNo = greenworld_rtnOrderNo;
		}
	}

    public class levelNutrition
    {
        private String nutritionName;
        private String nutritionContent;
        private String referenceValue;

        public String getNutritionName() {
            return nutritionName;
        }

        public void setNutritionName(String nutritionName) {
            this.nutritionName = nutritionName;
        }

        public String getNutritionContent() {
            return nutritionContent;
        }

        public void setNutritionContent(String nutritionContent) {
            this.nutritionContent = nutritionContent;
        }

        public String getReferenceValue() {
            return referenceValue;
        }

        public void setReferenceValue(String referenceValue) {
            this.referenceValue = referenceValue;
        }
    }
}
