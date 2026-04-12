package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.waimai.entity.order;

import java.util.List;

public class DCP_OrderToSaleProcess_OpenReq extends JsonBasicReq
{
	
	private levelRequest request;
	public levelRequest getRequest()
	{
		return request;
	}
	public void setRequest(levelRequest request)
	{
		this.request = request;
	}
	
	public class levelRequest
	{
		private String eId;
		private String opShopId;//当前操作门店
		private String opMachineNo;
		private String opBDate;
		private String opSquadNo;
		private String opWorkNo;
		private String opOpNo;//带o的字段表示当前操作人，不带o的表示订单创建时的操作人		
		private String opOpName;
		private String orderNo;
		private String shopNo;//下订门店
		private String shopName;//下订门店名称
		private String belfirm;//所属公司
		private String loadDocType;
		private String channelId;
		private String shippingShopNo;//配送门店
		private String shippingShopName;//配送门店名称
		private String contMan;
		private String contTel;
		private String address;
		private String shipDate;
		private String shipStartTime;
		private String shipEndTime;
		private String shipType;
		private String memo;
		private String delName;
		private String delTelephone;
		private String deliveryType;//物流类型
		private String deliveryNo;//物流单号
		private String deliveryStatus;//物流状态
		private String subDeliveryCompanyNo;//物流公司编码
		private String subDeliveryCompanyName;//物流公司名称	
		private String deliveryHandinput;//是否手工录入的物流单号
		private String sn;
		private String packageFee;
		private String shipFee;//实际配送费
		private String tot_oldAmt;
		private String tot_Amt;
		private String serviceCharge;
		private String rshipFee;//配送费减免
		private String tot_shipFee;//总配送费
		private String deliveryFeeShop;//外卖：门店承担配送费
		private String incomeAmt;
		private String totDisc;
		private String saleDisc;
		private String sellerDisc;
		private String platformDisc;
		private String payStatus;//付款状态 1.未支付 2.部分支付 3.付清
		private String payAmt;//已付金额
		private String totQty;
		private String isBook;
		private String sTime;
		private String mealNumber;
		private String manualNo;
		private String getMan;
		private String getManTel;
		private String cardNo;
		private String memberId;
		private String memberName;
		private String pointQty;
		private String sellNo;
		private String delMemo;
		private String detailType;
		private String headOrderNo;
		private String machineNo;
		private String eraseAmt;
		private String bDate;
		private String sellCredit;
		private String refundReason;
		private String refundReasonNo;
		private String refundReasonName;
		private invoiceInfo invoiceDetail;
		private String verNum;
		private String squadNo;
		private String workNo;
		private String opNo;
		private String customer;
		private String customerName;
		private String memberPayNo;
		private String freeCode;//零税证号-只有外交官才有的凭证--零税的其中一种凭证
		private String passport;//护照编号-零税的其中一种凭证
		private String invMemo;//发票内容
		private String carrierCode;//载具类别编码
		private String loveCode;//爱心码
		private String buyerGuiNo;//买家统一编号
		private String isInvoice;//是否需开发票
		private String invoiceType;//发票类型
		private String carrierShowId;//载具显码
		private String carrierHiddenId;//载具隐码
		private String tot_Amt_merReceive;//商家实收金额(含税)=TOT_OLDAMT-TOT_DISC_MERRECEIVE
		private String totDisc_merReceive;//商家支付折扣金额(含税)[单身所有商品的商家支付折扣合计]
		private String tot_Amt_custPayReal;//顾客实付金额[含税][TOT_OLDAMT-TOT_DISC_CUSTPAYREAL]
		//	private String totDisc_custPayReal;//顾客实付折扣金额[含税][单身所有商品的顾客实付折扣合计]
		private String status;//订单状态
		private String refundStatus;//退单状态
		private String departNo;//部门编码
		private String waimaiMerreceiveMode;//外卖订单商户实收计算模式：0-商品金额+打包盒-商户优惠，1-外卖平台店铺收入
		private String isOrderToSaleAll;   //0或空:整单订转销  1:分批订转销
		private String islastOrderToSale;  //N/Y 最后一次订转销  用于支付分摊,内部会重新赋值
		private String totThirdDiscount;//第3方支付优惠折扣金额
		private String partnerMember;//企迈:qimai鼎捷:digiwin
		private List<goods> goodsList;
		private List<Payment> pay;
		private List<order.CouponChange> couponChangeList;
        private List<order.otherCoupnPay> otherCoupnPayList;


        public String getPartnerMember() {
			return partnerMember;
		}
		public void setPartnerMember(String partnerMember) {
			this.partnerMember = partnerMember;
		}
		public String getTotThirdDiscount() {
			return totThirdDiscount;
		}
		public void setTotThirdDiscount(String totThirdDiscount) {
			this.totThirdDiscount = totThirdDiscount;
		}
		public String getIslastOrderToSale() {
			return islastOrderToSale;
		}
		public void setIslastOrderToSale(String islastOrderToSale) {
			this.islastOrderToSale = islastOrderToSale;
		}
		public String getIsOrderToSaleAll() {
			return isOrderToSaleAll;
		}
		public void setIsOrderToSaleAll(String isOrderToSaleAll) {
			this.isOrderToSaleAll = isOrderToSaleAll;
		}
		public String geteId()
		{
			return eId;
		}
		public void seteId(String eId)
		{
			this.eId = eId;
		}
		public String getOpShopId()
		{
			return opShopId;
		}
		public void setOpShopId(String opShopId)
		{
			this.opShopId = opShopId;
		}
		public String getOpMachineNo()
		{
			return opMachineNo;
		}
		public void setOpMachineNo(String opMachineNo)
		{
			this.opMachineNo = opMachineNo;
		}
		public String getOpBDate()
		{
			return opBDate;
		}
		public void setOpBDate(String opBDate)
		{
			this.opBDate = opBDate;
		}
		public String getOpSquadNo()
		{
			return opSquadNo;
		}
		public void setOpSquadNo(String opSquadNo)
		{
			this.opSquadNo = opSquadNo;
		}
		public String getOpWorkNo()
		{
			return opWorkNo;
		}
		public void setOpWorkNo(String opWorkNo)
		{
			this.opWorkNo = opWorkNo;
		}
		public String getOpOpNo()
		{
			return opOpNo;
		}
		public void setOpOpNo(String opOpNo)
		{
			this.opOpNo = opOpNo;
		}
		public String getOpOpName()
		{
			return opOpName;
		}
		public void setOpOpName(String opOpName)
		{
			this.opOpName = opOpName;
		}
		public String getOrderNo()
		{
			return orderNo;
		}
		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}
		public String getShopNo() {
			return shopNo;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public void setShopNo(String shopNo)
		{
			this.shopNo = shopNo;
		}
		public String getBelfirm()
		{
			return belfirm;
		}
		public void setBelfirm(String belfirm)
		{
			this.belfirm = belfirm;
		}
		public String getLoadDocType()
		{
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType)
		{
			this.loadDocType = loadDocType;
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
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
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
		public String getSn()
		{
			return sn;
		}
		public void setSn(String sn)
		{
			this.sn = sn;
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
		public String getDeliveryFeeShop()
		{
			return deliveryFeeShop;
		}
		public void setDeliveryFeeShop(String deliveryFeeShop)
		{
			this.deliveryFeeShop = deliveryFeeShop;
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
		public String getSaleDisc()
		{
			return saleDisc;
		}
		public void setSaleDisc(String saleDisc)
		{
			this.saleDisc = saleDisc;
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
		public String getPayStatus()
		{
			return payStatus;
		}
		public void setPayStatus(String payStatus)
		{
			this.payStatus = payStatus;
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
		public String getDelMemo()
		{
			return delMemo;
		}
		public void setDelMemo(String delMemo)
		{
			this.delMemo = delMemo;
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
		public invoiceInfo getInvoiceDetail()
		{
			return invoiceDetail;
		}
		public void setInvoiceDetail(invoiceInfo invoiceDetail)
		{
			this.invoiceDetail = invoiceDetail;
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
		public String getMemberPayNo()
		{
			return memberPayNo;
		}
		public void setMemberPayNo(String memberPayNo)
		{
			this.memberPayNo = memberPayNo;
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
		public String getInvMemo()
		{
			return invMemo;
		}
		public void setInvMemo(String invMemo)
		{
			this.invMemo = invMemo;
		}
		public String getCarrierCode()
		{
			return carrierCode;
		}
		public void setCarrierCode(String carrierCode)
		{
			this.carrierCode = carrierCode;
		}
		public String getLoveCode()
		{
			return loveCode;
		}
		public void setLoveCode(String loveCode)
		{
			this.loveCode = loveCode;
		}
		public String getBuyerGuiNo()
		{
			return buyerGuiNo;
		}
		public void setBuyerGuiNo(String buyerGuiNo)
		{
			this.buyerGuiNo = buyerGuiNo;
		}
		public String getIsInvoice()
		{
			return isInvoice;
		}
		public void setIsInvoice(String isInvoice)
		{
			this.isInvoice = isInvoice;
		}
		public String getInvoiceType()
		{
			return invoiceType;
		}
		public void setInvoiceType(String invoiceType)
		{
			this.invoiceType = invoiceType;
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
//		public String getTotDisc_custPayReal()
//		{
//			return totDisc_custPayReal;
//		}
//		public void setTotDisc_custPayReal(String totDisc_custPayReal)
//		{
//			this.totDisc_custPayReal = totDisc_custPayReal;
//		}
		
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
		public void setWaimaiMerreceiveMode(String waimaiMerreceiveMode) {
			this.waimaiMerreceiveMode = waimaiMerreceiveMode;
		}
		public List<goods> getGoodsList()
		{
			return goodsList;
		}
		public void setGoodsList(List<goods> goodsList)
		{
			this.goodsList = goodsList;
		}
		public List<Payment> getPay()
		{
			return pay;
		}
		public void setPay(List<Payment> pay)
		{
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
	
	public class invoiceInfo
	{
		private String invNo;
		
		public String getInvNo()
		{
			return invNo;
		}
		public void setInvNo(String invNo)
		{
			this.invNo = invNo;
		}
	}
	
	public class goods
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
		private String price;
		private String qty;
		private String goodsGroup;
		private String disc;
		private String boxNum;
		private String boxPrice;
		private String amt;
		private String packageType;
		private String packageMitem;
		private String toppingType;
		private String toppingMitem;
		private String couponType;
		private String couponCode;
		private String gift;
		private String giftSourceSerialNo;
		private String goodsUrl;
		private String sellerNo;
		private String sellerName;
		private String accNo;
		private String counterNo;
		private String oldPrice;
		private String oldAmt;
		private String giftReason;
		private String sTime;
		private String oItem;
		private String oReItem;
		private String taxCode;
		private String taxType;
		private String taxRate;
		private String inclTax;
		private String invSplitType;
		private String invNo;
		private String invItem;
		private String virtual;
		private String disc_merReceive;//商家支付折扣金额			
		private String disc_custPayReal;//顾客支付折扣金额			
		private String amt_merReceive;//商家实收金额			
		private String amt_custPayReal;//顾客实付金额
//		private String pickQty;//已提货数量
//		private String writeOffAmt; //成交总额已冲销金额
//		private String discSale;//折扣已冲销金额
//		private String discMerReceiveSale;//商家支付折扣已冲销金额
//		private String discCustPayRealsale;//顾客支付折扣已冲销金额
//		private String amtMerReceiveSale; //商家实收已冲销金额  
//		private String amtCustPayRealSale;//顾客实付已冲销金额
//		private String oldAmtSale;//原单总金额（含税）已冲销金额
        private String flavorStuffDetail;//口味加料细节
		
		private List<Agio> agioInfo;
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
		public String getoReItem()
		{
			return oReItem;
		}
		public void setoReItem(String oReItem)
		{
			this.oReItem = oReItem;
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
		public List<Agio> getAgioInfo()
		{
			return agioInfo;
		}
		public void setAgioInfo(List<Agio> agioInfo)
		{
			this.agioInfo = agioInfo;
		}

        public String getFlavorStuffDetail() {
            return flavorStuffDetail;
        }

        public void setFlavorStuffDetail(String flavorStuffDetail) {
            this.flavorStuffDetail = flavorStuffDetail;
        }
    }
	
	public class Agio
	{
		private String item;
		private String mitem;
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
		private String orderToSalePayAgioFlag;//订转销付款产生的折扣Y/N
		private String disc_merReceive;//折扣金额[商户实收]
		private String disc_custPayReal;//折扣金额[顾客实付]
		
		public String getMitem() {
			return mitem;
		}
		public void setMitem(String mitem) {
			this.mitem = mitem;
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
		private String isOrderPay;
		private String isOnlinePay;
		private String couponQty;
		private String isVerification;
		private String canInvoice;//0不开发票，1可开发票，2已开发票，3第三方开发票
		private String funcNo;//功能编码		
		private String paydoctype;//POS=4处理卡的扣款类型
		private String payType;//新零售支付编码
		private String merDiscount;//商户优惠金额，移动支付用，例如支付宝，微信等
		private String merReceive;//商家实收金额，移动支付用，例如支付宝，微信等
		private String thirdDiscount;//第三方优惠金额：移动支付用，例如支付宝，微信等
		private String custPayReal;//客户实付金额：移动支付用，例如支付宝，微信等
		private String couponMarketPrice;//券面值
		private String couponPrice;//券售价
        private String gainChannel;//企迈券渠道
        private String gainChannelName;//企迈券渠道名称
		
		/**
		 * 货郎农行手续费
		 */
		private double chargeAmount;
		
		/**
		 * 支付通道编码
		 */
		private String payChannelCode;

        private String password;//禄品卡密码
		
		private String ecardSign; //企迈 ecardSign字段，0实体卡，1电子卡


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

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }
		
		public String getEcardSign() {
			return ecardSign;
		}
		
		public void setEcardSign(String ecardSign) {
			this.ecardSign = ecardSign;
		}
	}
	
}
