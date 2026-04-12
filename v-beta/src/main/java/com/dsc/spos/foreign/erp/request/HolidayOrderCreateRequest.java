package com.dsc.spos.foreign.erp.request;

import com.dsc.spos.foreign.erp.Request;

import java.util.ArrayList;
import java.util.List;

public class HolidayOrderCreateRequest extends Request<List<HolidayOrderCreateRequest.RequestBean>> {
	//	public static class Std_dataBean
	//	{
	//		private RequestBean request = new RequestBean() ;
	//
	//		public RequestBean getRequest() {
	//			return request;
	//		}
	//
	//		public void setRequest(RequestBean request) {
	//			this.request = request;
	//		}
	//		
	//	}
	public static class RequestBean {
		private String version; // V30
		private String sellCredit;
		private String bdate;
		private String operation_type;
		private String memo;
		private String type;
		private String site_no;
		private String isholidayorder;
		private String approve_no;
		private String order_type;
		private String customer_no;
		private String ecCustomerNo; //电商客户编码
		private String modify_datetime;
		private String shippingshopname;
		private String loadDocType;
		private String totDisc;
		private String atmvirtualaccount;
		private String shiptime;
		private String phone;
		private String productionshop;
		private String shiptype;
		private String getman;
		private String sid;
		private String modify_no;
		private String subtype_name;
		private String erase_amt;
		private String create_datetime;
		private String creator;
		private String pickupway;
		private String address;
		private String invuser;
		private String agreee_date;
		private String shippingshop;
		private String getmantel;
		private String order_date;
		private String approve_datetime;
		private String front_no;
		private String buyerguino;
		private String shipStartTime; // 配送开始时间
		private String shipEndTime;  // 配送截止时间
		private String deliveryBusinessType; //配送业务类型（1随车 2代发）
		private String isUrgentOrder; //是否紧急订单
		private String channelId; //	String	Y	渠道编码
		private String machine; //	String		机台
		private String cardNo; //	String		会员卡号
		private String memberId; //	String		会员号
		private String point_qty; //	numeric		积分点数
		private String tot_qty; //	numeric		总数量
		private String tot_amt; //	numeric		订单总金额(含税)
		private String tot_oldAmt; //	numeric		原价总金额(含税)
		private String tot_disc; //	numeric		总折扣金额（含税）
		private String incomeAmt; //	numeric		商家实收金额
		private String rshipFee; //	numeric		配送费减免
		private String tot_shipFee; //	numeric		总配送费
		private String shopShareDeliveryFee; //	numeric		商家替顾客承担的配送费（饿了么做活动时商家可以选择参与替顾客承担部分配送费）
		private String sellerDisc; //	numeric		商户优惠总额
		private String platformDisc; //	numeric		"	平台优惠总额"
		private String payAmt; //	numeric		已付金额
		private String billType; //	String		单据类型	1：正向订单；-1：退单
		private String refundSourceBillNo; //	String		退单的来源单号
		private String loadDocBillType; //	String		渠道单据类型
		private String loadDocOrderNo; //	String		来源平台单号
		private String detailType; //	String		拆单类型	1无拆单 2 主单 3子单
		private String headOrderNo; //	String		来源主单号
		private String invoiceTitle; //	String		发票抬头
		private String peopleType; //	String		发票主体类型	主体类型：1公司，2个人
		private String invoiceType; //	String		发票类型	台湾发票类型：2二联-个人,3三联-公司     大陆：9普通发票,8增值税专用发票,
		private String taxRegNumber; // 	String		纳税人识别号
		private String mealNumber; //	numeric		用餐人数
		private String isBook; //	String		是否外卖预定单	Y是 N否
		private String order_Sn; // 	String		外卖订单流水号
		private String shipFee; //	numeric		外卖配送费
		private String packageFee; //	numeric		外卖餐盒费
		private String serviceCharge; //	numeric		服务费
		private String passPort; //	String		护照编号
		private String freeCode; //	String		免税证号
		private String verNuprivate; // String String		系统版本号
		private String squadNo; //	String		班别编号
		private String workNo; //	String		班号
		private String opNo; //	String		用户编号
		private String customer; //	String		赊销人（赊销客户账号）
		private String customerName; //	String		赊销人（赊销客户名称）
		private String carrierCode; //	String		载具类别编码
		private String carrierShowId; //	String		载具显码
		private String carrierHiddenId; //	String		载具隐码
		private String loveCode; //	String		爱心码
		private String invOperateType; //	String		
		private String invSplitType; //	String		发票开票拆分类型：1:不拆分、2:按商品拆分、3:按金額拆分
		private String memberPayNo; // 会员支付单号
		private String invNo;
		private String taxAbleUamt;
		private String zeroTaxAmt;
		private String freeTaxAmt;
		private String taxAbleAmt;
		private String taxAbleTax;
		private String untaxPayAmt;
		private String untaxPayTax;
		private String taxRate;
		private String invTotAmt;
		private String invAmt;
		private String invUamt;
		private String invTax;
		private String gftInvAmt;
		private String gftInvTax;
		private String accAmt;
		private String accTax;
		private String extraCpAmt;
		private String extraCpTax;
		private String province;
		private String city;
		private String county;
		private String street;
		private String status;
		private String payStatus;
		private String tot_amt_merReceive;
		private String tot_amt_custPayReal;
		private String tot_disc_merReceive;
		private String tot_disc_custPayReal;
		private String departNo;
		private String preparationStatus;
		
		private String longitude;
		private String latitude;
		private String proMemo;
		private String delMemo;
		private String deliveryType;
		private String deliveryNo;
		private String isIntention;
		



		private List<RequestDetailBean> request_detail = new ArrayList<>();

		public String getSellCredit() {
			return sellCredit;
		}

		public void setSellCredit(String sellCredit) {
			this.sellCredit = sellCredit;
		}

		public String getBdate() {
			return bdate;
		}

		public void setBdate(String bdate) {
			this.bdate = bdate;
		}

		public String getOperation_type() {
			return operation_type;
		}

		public void setOperation_type(String operation_type) {
			this.operation_type = operation_type;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getSite_no() {
			return site_no;
		}

		public void setSite_no(String site_no) {
			this.site_no = site_no;
		}

		public String getServiceCharge() {
			return serviceCharge;
		}

		public void setServiceCharge(String serviceCharge) {
			this.serviceCharge = serviceCharge;
		}

		public String getIsholidayorder() {
			return isholidayorder;
		}

		public void setIsholidayorder(String isholidayorder) {
			this.isholidayorder = isholidayorder;
		}

		public String getApprove_no() {
			return approve_no;
		}

		public void setApprove_no(String approve_no) {
			this.approve_no = approve_no;
		}

		public String getPlatformDisc() {
			return platformDisc;
		}

		public void setPlatformDisc(String platformDisc) {
			this.platformDisc = platformDisc;
		}

		public String getOrder_type() {
			return order_type;
		}

		public void setOrder_type(String order_type) {
			this.order_type = order_type;
		}

		public String getCustomer_no() {
			return customer_no;
		}

		public void setCustomer_no(String customer_no) {
			this.customer_no = customer_no;
		}

		public String getModify_datetime() {
			return modify_datetime;
		}

		public void setModify_datetime(String modify_datetime) {
			this.modify_datetime = modify_datetime;
		}

		public String getShippingshopname() {
			return shippingshopname;
		}

		public void setShippingshopname(String shippingshopname) {
			this.shippingshopname = shippingshopname;
		}

		public String getIncomeAmt() {
			return incomeAmt;
		}

		public void setIncomeAmt(String incomeAmt) {
			this.incomeAmt = incomeAmt;
		}

		public String getPayAmt() {
			return payAmt;
		}

		public void setPayAmt(String payAmt) {
			this.payAmt = payAmt;
		}

		public String getTot_amt() {
			return tot_amt;
		}

		public void setTot_amt(String tot_amt) {
			this.tot_amt = tot_amt;
		}

		public String getLoadDocType() {
			return loadDocType;
		}

		public void setLoadDocType(String loadDocType) {
			this.loadDocType = loadDocType;
		}

		public String getTot_shipFee() {
			return tot_shipFee;
		}

		public void setTot_shipFee(String tot_shipFee) {
			this.tot_shipFee = tot_shipFee;
		}

		public String getTot_oldAmt() {
			return tot_oldAmt;
		}

		public void setTot_oldAmt(String tot_oldAmt) {
			this.tot_oldAmt = tot_oldAmt;
		}

		public String getTotDisc() {
			return totDisc;
		}

		public void setTotDisc(String totDisc) {
			this.totDisc = totDisc;
		}

		public String getAtmvirtualaccount() {
			return atmvirtualaccount;
		}

		public void setAtmvirtualaccount(String atmvirtualaccount) {
			this.atmvirtualaccount = atmvirtualaccount;
		}

		public String getShiptime() {
			return shiptime;
		}

		public void setShiptime(String shiptime) {
			this.shiptime = shiptime;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}


		public String getProductionshop() {
			return productionshop;
		}

		public void setProductionshop(String productionshop) {
			this.productionshop = productionshop;
		}

		public String getShiptype() {
			return shiptype;
		}

		public void setShiptype(String shiptype) {
			this.shiptype = shiptype;
		}

		public String getShipFee() {
			return shipFee;
		}

		public void setShipFee(String shipFee) {
			this.shipFee = shipFee;
		}

		public String getGetman() {
			return getman;
		}

		public void setGetman(String getman) {
			this.getman = getman;
		}

		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
		}

		public String getModify_no() {
			return modify_no;
		}

		public void setModify_no(String modify_no) {
			this.modify_no = modify_no;
		}

		public String getSubtype_name() {
			return subtype_name;
		}

		public void setSubtype_name(String subtype_name) {
			this.subtype_name = subtype_name;
		}

		public String getErase_amt() {
			return erase_amt;
		}

		public void setErase_amt(String erase_amt) {
			this.erase_amt = erase_amt;
		}

		public String getCreate_datetime() {
			return create_datetime;
		}

		public void setCreate_datetime(String create_datetime) {
			this.create_datetime = create_datetime;
		}

		public String getRshipFee() {
			return rshipFee;
		}

		public void setRshipFee(String rshipFee) {
			this.rshipFee = rshipFee;
		}

		public String getPackageFee() {
			return packageFee;
		}

		public void setPackageFee(String packageFee) {
			this.packageFee = packageFee;
		}

		public String getCreator() {
			return creator;
		}

		public void setCreator(String creator) {
			this.creator = creator;
		}

		public String getPickupway() {
			return pickupway;
		}

		public void setPickupway(String pickupway) {
			this.pickupway = pickupway;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getInvuser() {
			return invuser;
		}

		public void setInvuser(String invuser) {
			this.invuser = invuser;
		}

		public String getEcCustomerNo() {
			return ecCustomerNo;
		}

		public void setEcCustomerNo(String ecCustomerNo) {
			this.ecCustomerNo = ecCustomerNo;
		}

		public String getShopShareDeliveryFee() {
			return shopShareDeliveryFee;
		}

		public void setShopShareDeliveryFee(String shopShareDeliveryFee) {
			this.shopShareDeliveryFee = shopShareDeliveryFee;
		}

		public String getAgreee_date() {
			return agreee_date;
		}

		public void setAgreee_date(String agreee_date) {
			this.agreee_date = agreee_date;
		}

		public String getSellerDisc() {
			return sellerDisc;
		}

		public void setSellerDisc(String sellerDisc) {
			this.sellerDisc = sellerDisc;
		}

		public String getShippingshop() {
			return shippingshop;
		}

		public void setShippingshop(String shippingshop) {
			this.shippingshop = shippingshop;
		}

		public String getGetmantel() {
			return getmantel;
		}

		public void setGetmantel(String getmantel) {
			this.getmantel = getmantel;
		}

		public String getOrder_date() {
			return order_date;
		}

		public void setOrder_date(String order_date) {
			this.order_date = order_date;
		}

		public String getApprove_datetime() {
			return approve_datetime;
		}

		public void setApprove_datetime(String approve_datetime) {
			this.approve_datetime = approve_datetime;
		}

		public String getFront_no() {
			return front_no;
		}

		public void setFront_no(String front_no) {
			this.front_no = front_no;
		}

		public String getBuyerguino() {
			return buyerguino;
		}

		public void setBuyerguino(String buyerguino) {
			this.buyerguino = buyerguino;
		}

		public List<RequestDetailBean> getRequest_detail() {
			return request_detail;
		}

		public void setRequest_detail(List<RequestDetailBean> request_detail) {
			this.request_detail = request_detail;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getShipStartTime() {
			return shipStartTime;
		}

		public void setShipStartTime(String shipStartTime) {
			this.shipStartTime = shipStartTime;
		}

		public String getShipEndTime() {
			return shipEndTime;
		}

		public void setShipEndTime(String shipEndTime) {
			this.shipEndTime = shipEndTime;
		}

		public String getDeliveryBusinessType() {
			return deliveryBusinessType;
		}

		public void setDeliveryBusinessType(String deliveryBusinessType) {
			this.deliveryBusinessType = deliveryBusinessType;
		}

		public String getIsUrgentOrder() {
			return isUrgentOrder;
		}

		public void setIsUrgentOrder(String isUrgentOrder) {
			this.isUrgentOrder = isUrgentOrder;
		}

		public String getChannelId() {
			return channelId;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public String getMachine() {
			return machine;
		}

		public void setMachine(String machine) {
			this.machine = machine;
		}

		public String getCardNo() {
			return cardNo;
		}

		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}

		public String getMemberId() {
			return memberId;
		}

		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}

		public String getPoint_qty() {
			return point_qty;
		}

		public void setPoint_qty(String point_qty) {
			this.point_qty = point_qty;
		}

		public String getTot_qty() {
			return tot_qty;
		}

		public void setTot_qty(String tot_qty) {
			this.tot_qty = tot_qty;
		}

		public String getTot_disc() {
			return tot_disc;
		}

		public void setTot_disc(String tot_disc) {
			this.tot_disc = tot_disc;
		}

		public String getBillType() {
			return billType;
		}

		public void setBillType(String billType) {
			this.billType = billType;
		}

		public String getRefundSourceBillNo() {
			return refundSourceBillNo;
		}

		public void setRefundSourceBillNo(String refundSourceBillNo) {
			this.refundSourceBillNo = refundSourceBillNo;
		}

		public String getLoadDocBillType() {
			return loadDocBillType;
		}

		public void setLoadDocBillType(String loadDocBillType) {
			this.loadDocBillType = loadDocBillType;
		}

		public String getLoadDocOrderNo() {
			return loadDocOrderNo;
		}

		public void setLoadDocOrderNo(String loadDocOrderNo) {
			this.loadDocOrderNo = loadDocOrderNo;
		}

		public String getDetailType() {
			return detailType;
		}

		public void setDetailType(String detailType) {
			this.detailType = detailType;
		}

		public String getHeadOrderNo() {
			return headOrderNo;
		}

		public void setHeadOrderNo(String headOrderNo) {
			this.headOrderNo = headOrderNo;
		}

		public String getInvoiceTitle() {
			return invoiceTitle;
		}

		public void setInvoiceTitle(String invoiceTitle) {
			this.invoiceTitle = invoiceTitle;
		}

		public String getPeopleType() {
			return peopleType;
		}

		public void setPeopleType(String peopleType) {
			this.peopleType = peopleType;
		}

		public String getInvoiceType() {
			return invoiceType;
		}

		public void setInvoiceType(String invoiceType) {
			this.invoiceType = invoiceType;
		}

		public String getTaxRegNumber() {
			return taxRegNumber;
		}

		public void setTaxRegNumber(String taxRegNumber) {
			this.taxRegNumber = taxRegNumber;
		}

		public String getMealNumber() {
			return mealNumber;
		}

		public void setMealNumber(String mealNumber) {
			this.mealNumber = mealNumber;
		}

		public String getIsBook() {
			return isBook;
		}

		public void setIsBook(String isBook) {
			this.isBook = isBook;
		}

		public String getOrder_Sn() {
			return order_Sn;
		}

		public void setOrder_Sn(String order_Sn) {
			this.order_Sn = order_Sn;
		}

		public String getPassPort() {
			return passPort;
		}

		public void setPassPort(String passPort) {
			this.passPort = passPort;
		}

		public String getFreeCode() {
			return freeCode;
		}

		public void setFreeCode(String freeCode) {
			this.freeCode = freeCode;
		}

		public String getVerNuprivate() {
			return verNuprivate;
		}

		public void setVerNuprivate(String verNuprivate) {
			this.verNuprivate = verNuprivate;
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

		public String getOpNo() {
			return opNo;
		}

		public void setOpNo(String opNo) {
			this.opNo = opNo;
		}

		public String getCustomer() {
			return customer;
		}

		public void setCustomer(String customer) {
			this.customer = customer;
		}

		public String getCustomerName() {
			return customerName;
		}

		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}

		public String getCarrierCode() {
			return carrierCode;
		}

		public void setCarrierCode(String carrierCode) {
			this.carrierCode = carrierCode;
		}

		public String getCarrierShowId() {
			return carrierShowId;
		}

		public void setCarrierShowId(String carrierShowId) {
			this.carrierShowId = carrierShowId;
		}

		public String getCarrierHiddenId() {
			return carrierHiddenId;
		}

		public void setCarrierHiddenId(String carrierHiddenId) {
			this.carrierHiddenId = carrierHiddenId;
		}

		public String getLoveCode() {
			return loveCode;
		}

		public void setLoveCode(String loveCode) {
			this.loveCode = loveCode;
		}

		public String getInvOperateType()
		{
			return invOperateType;
		}

		public void setInvOperateType(String invOperateType)
		{
			this.invOperateType = invOperateType;
		}

		public String getInvNo()
		{
			return invNo;
		}

		public void setInvNo(String invNo)
		{
			this.invNo = invNo;
		}

		public String getInvSplitType() {
			return invSplitType;
		}

		public void setInvSplitType(String invSplitType) {
			this.invSplitType = invSplitType;
		}

		public String getMemberPayNo() {
			return memberPayNo;
		}

		public void setMemberPayNo(String memberPayNo) {
			this.memberPayNo = memberPayNo;
		}

		public String getTaxAbleUamt()
		{
			return taxAbleUamt;
		}

		public void setTaxAbleUamt(String taxAbleUamt)
		{
			this.taxAbleUamt = taxAbleUamt;
		}

		public String getZeroTaxAmt()
		{
			return zeroTaxAmt;
		}

		public void setZeroTaxAmt(String zeroTaxAmt)
		{
			this.zeroTaxAmt = zeroTaxAmt;
		}

		public String getFreeTaxAmt()
		{
			return freeTaxAmt;
		}

		public void setFreeTaxAmt(String freeTaxAmt)
		{
			this.freeTaxAmt = freeTaxAmt;
		}

		public String getTaxAbleAmt()
		{
			return taxAbleAmt;
		}

		public void setTaxAbleAmt(String taxAbleAmt)
		{
			this.taxAbleAmt = taxAbleAmt;
		}

		public String getTaxAbleTax()
		{
			return taxAbleTax;
		}

		public void setTaxAbleTax(String taxAbleTax)
		{
			this.taxAbleTax = taxAbleTax;
		}

		public String getUntaxPayAmt()
		{
			return untaxPayAmt;
		}

		public void setUntaxPayAmt(String untaxPayAmt)
		{
			this.untaxPayAmt = untaxPayAmt;
		}

		public String getUntaxPayTax()
		{
			return untaxPayTax;
		}

		public void setUntaxPayTax(String untaxPayTax)
		{
			this.untaxPayTax = untaxPayTax;
		}

		public String getTaxRate()
		{
			return taxRate;
		}

		public void setTaxRate(String taxRate)
		{
			this.taxRate = taxRate;
		}

		public String getInvTotAmt()
		{
			return invTotAmt;
		}

		public void setInvTotAmt(String invTotAmt)
		{
			this.invTotAmt = invTotAmt;
		}

		public String getInvAmt()
		{
			return invAmt;
		}

		public void setInvAmt(String invAmt)
		{
			this.invAmt = invAmt;
		}

		public String getInvUamt()
		{
			return invUamt;
		}

		public void setInvUamt(String invUamt)
		{
			this.invUamt = invUamt;
		}

		public String getInvTax()
		{
			return invTax;
		}

		public void setInvTax(String invTax)
		{
			this.invTax = invTax;
		}

		public String getGftInvAmt()
		{
			return gftInvAmt;
		}

		public void setGftInvAmt(String gftInvAmt)
		{
			this.gftInvAmt = gftInvAmt;
		}

		public String getGftInvTax()
		{
			return gftInvTax;
		}

		public void setGftInvTax(String gftInvTax)
		{
			this.gftInvTax = gftInvTax;
		}

		public String getAccAmt()
		{
			return accAmt;
		}

		public void setAccAmt(String accAmt)
		{
			this.accAmt = accAmt;
		}

		public String getAccTax()
		{
			return accTax;
		}

		public void setAccTax(String accTax)
		{
			this.accTax = accTax;
		}

		public String getExtraCpAmt()
		{
			return extraCpAmt;
		}

		public void setExtraCpAmt(String extraCpAmt)
		{
			this.extraCpAmt = extraCpAmt;
		}

		public String getExtraCpTax()
		{
			return extraCpTax;
		}

		public void setExtraCpTax(String extraCpTax)
		{
			this.extraCpTax = extraCpTax;
		}

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getCounty() {
			return county;
		}

		public void setCounty(String county) {
			this.county = county;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getPayStatus() {
			return payStatus;
		}

		public void setPayStatus(String payStatus) {
			this.payStatus = payStatus;
		}

		public String getTot_amt_merReceive() {
			return tot_amt_merReceive;
		}

		public void setTot_amt_merReceive(String tot_amt_merReceive) {
			this.tot_amt_merReceive = tot_amt_merReceive;
		}

		public String getTot_amt_custPayReal() {
			return tot_amt_custPayReal;
		}

		public void setTot_amt_custPayReal(String tot_amt_custPayReal) {
			this.tot_amt_custPayReal = tot_amt_custPayReal;
		}

		public String getTot_disc_merReceive() {
			return tot_disc_merReceive;
		}

		public void setTot_disc_merReceive(String tot_disc_merReceive) {
			this.tot_disc_merReceive = tot_disc_merReceive;
		}

		public String getTot_disc_custPayReal() {
			return tot_disc_custPayReal;
		}

		public void setTot_disc_custPayReal(String tot_disc_custPayReal) {
			this.tot_disc_custPayReal = tot_disc_custPayReal;
		}

		public String getDepartNo()
		{
			return departNo;
		}

		public void setDepartNo(String departNo)
		{
			this.departNo = departNo;
		}

		public String getPreparationStatus()
		{
			return preparationStatus;
		}

		public void setPreparationStatus(String preparationStatus)
		{
			this.preparationStatus = preparationStatus;
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

		public String getIsIntention() {
			return isIntention;
		}

		public void setIsIntention(String isIntention) {
			this.isIntention = isIntention;
		}
	}

	public static class RequestDetailBean {
		private String gift;
		private String amount;
		private String qrcode_key;
		private String item_no;
		private String sUnit;
		private String shopqty;
		private String oriprice;
		private String price;
		private String qty;
		private String packagemitem;
		private String packagetype;
		private String seq;
		private String feature_no;
		private String warehouse_no;
		private String disc;
		private String toppingType; // 加料类型
		private String toppingMitem;// 加料主商品项次
		private String couponType;
		private String couponCode;
		private String boxNum;
		private String boxPrice;
		private String pickQty;
		private String rQty;
		private String sourceCode;
		private String sellerNo;
		private String sellerName;
		private String accNo;
		private String counterNo;
		private String oItem;
		private String oReItem;
		private String sTime;
		private String taxCode;
		private String taxType;
		private String taxRate;
		private String inclTax;
		private String giftReason;

		private String gftInvAmt;
		private String gftInvTax;
		private String taxAbleUamt;
		private String zeroTaxAmt;
		private String freeTaxAmt;
		private String taxAbleAmt;
		private String oldAmt;
		private String invItem;
		private String untaxPayAmt;
		private String untaxPayTax;
		private String invSplitType;
		private String invTotAmt;
		private String invAmt;
		private String invUamt;
		private String invTax;
		private String accAmt;
		private String accTax;
		private String extraCpAmt;
		private String extraCpTax;
		private String disc_merReceive;
		private String amt_merReceive;
		private String disc_custPayReal;
		private String amt_custPayReal;
		private String preparationStatus;

		private List<MessagesBean> messages = new ArrayList<>();
		private List<AgioInfo> agio_info = new ArrayList<>();

		public String getGift() {
			return gift;
		}

		public void setGift(String gift) {
			this.gift = gift;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

		public String getQrcode_key() {
			return qrcode_key;
		}

		public void setQrcode_key(String qrcode_key) {
			this.qrcode_key = qrcode_key;
		}

		public String getItem_no() {
			return item_no;
		}

		public void setItem_no(String item_no) {
			this.item_no = item_no;
		}
		public String getShopqty() {
			return shopqty;
		}

		public void setShopqty(String shopqty) {
			this.shopqty = shopqty;
		}

		public String getOriprice() {
			return oriprice;
		}

		public void setOriprice(String oriprice) {
			this.oriprice = oriprice;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public String getQty() {
			return qty;
		}

		public void setQty(String qty) {
			this.qty = qty;
		}

		public String getPackagemitem() {
			return packagemitem;
		}

		public void setPackagemitem(String packagemitem) {
			this.packagemitem = packagemitem;
		}

		public String getPackagetype() {
			return packagetype;
		}

		public void setPackagetype(String packagetype) {
			this.packagetype = packagetype;
		}

		public String getSeq() {
			return seq;
		}

		public void setSeq(String seq) {
			this.seq = seq;
		}

		public List<MessagesBean> getMessages() {
			return messages;
		}

		public void setMessages(List<MessagesBean> messages) {
			this.messages = messages;
		}

		public String getsUnit() {
			return sUnit;
		}

		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}

		public String getFeature_no() {
			return feature_no;
		}

		public void setFeature_no(String feature_no) {
			this.feature_no = feature_no;
		}

		public String getWarehouse_no() {
			return warehouse_no;
		}

		public void setWarehouse_no(String warehouse_no) {
			this.warehouse_no = warehouse_no;
		}

		public String getDisc() {
			return disc;
		}

		public void setDisc(String disc) {
			this.disc = disc;
		}

		public String getToppingType() {
			return toppingType;
		}

		public void setToppingType(String toppingType) {
			this.toppingType = toppingType;
		}

		public String getToppingMitem() {
			return toppingMitem;
		}

		public void setToppingMitem(String toppingMitem) {
			this.toppingMitem = toppingMitem;
		}

		public String getCouponType() {
			return couponType;
		}

		public void setCouponType(String couponType) {
			this.couponType = couponType;
		}

		public String getCouponCode() {
			return couponCode;
		}

		public void setCouponCode(String couponCode) {
			this.couponCode = couponCode;
		}

		public String getBoxNum() {
			return boxNum;
		}

		public void setBoxNum(String boxNum) {
			this.boxNum = boxNum;
		}

		public String getBoxPrice() {
			return boxPrice;
		}

		public void setBoxPrice(String boxPrice) {
			this.boxPrice = boxPrice;
		}

		public String getPickQty() {
			return pickQty;
		}

		public void setPickQty(String pickQty) {
			this.pickQty = pickQty;
		}

		public String getrQty() {
			return rQty;
		}

		public void setrQty(String rQty) {
			this.rQty = rQty;
		}

		public String getSourceCode() {
			return sourceCode;
		}

		public void setSourceCode(String sourceCode) {
			this.sourceCode = sourceCode;
		}

		public String getSellerNo() {
			return sellerNo;
		}

		public void setSellerNo(String sellerNo) {
			this.sellerNo = sellerNo;
		}

		public String getSellerName() {
			return sellerName;
		}

		public void setSellerName(String sellerName) {
			this.sellerName = sellerName;
		}

		public String getAccNo() {
			return accNo;
		}

		public void setAccNo(String accNo) {
			this.accNo = accNo;
		}

		public String getCounterNo() {
			return counterNo;
		}

		public void setCounterNo(String counterNo) {
			this.counterNo = counterNo;
		}

		public String getoItem() {
			return oItem;
		}

		public void setoItem(String oItem) {
			this.oItem = oItem;
		}

		public String getoReItem() {
			return oReItem;
		}

		public void setoReItem(String oReItem) {
			this.oReItem = oReItem;
		}

		public String getsTime() {
			return sTime;
		}

		public void setsTime(String sTime) {
			this.sTime = sTime;
		}

		public String getTaxCode() {
			return taxCode;
		}

		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}

		public String getTaxType() {
			return taxType;
		}

		public void setTaxType(String taxType) {
			this.taxType = taxType;
		}

		public String getTaxRate() {
			return taxRate;
		}

		public void setTaxRate(String taxRate) {
			this.taxRate = taxRate;
		}

		public String getInclTax() {
			return inclTax;
		}

		public void setInclTax(String inclTax) {
			this.inclTax = inclTax;
		}

		public String getGiftReason() {
			return giftReason;
		}

		public void setGiftReason(String giftReason) {
			this.giftReason = giftReason;
		}


		public String getGftInvAmt()
		{
			return gftInvAmt;
		}

		public void setGftInvAmt(String gftInvAmt)
		{
			this.gftInvAmt = gftInvAmt;
		}

		public String getGftInvTax()
		{
			return gftInvTax;
		}

		public void setGftInvTax(String gftInvTax)
		{
			this.gftInvTax = gftInvTax;
		}

		public String getTaxAbleUamt()
		{
			return taxAbleUamt;
		}

		public void setTaxAbleUamt(String taxAbleUamt)
		{
			this.taxAbleUamt = taxAbleUamt;
		}

		public String getZeroTaxAmt()
		{
			return zeroTaxAmt;
		}

		public void setZeroTaxAmt(String zeroTaxAmt)
		{
			this.zeroTaxAmt = zeroTaxAmt;
		}

		public String getFreeTaxAmt()
		{
			return freeTaxAmt;
		}

		public void setFreeTaxAmt(String freeTaxAmt)
		{
			this.freeTaxAmt = freeTaxAmt;
		}

		public String getTaxAbleAmt()
		{
			return taxAbleAmt;
		}

		public void setTaxAbleAmt(String taxAbleAmt)
		{
			this.taxAbleAmt = taxAbleAmt;
		}

		public String getOldAmt()
		{
			return oldAmt;
		}

		public void setOldAmt(String oldAmt)
		{
			this.oldAmt = oldAmt;
		}

		public String getInvItem()
		{
			return invItem;
		}

		public void setInvItem(String invItem)
		{
			this.invItem = invItem;
		}

		public String getUntaxPayAmt()
		{
			return untaxPayAmt;
		}

		public void setUntaxPayAmt(String untaxPayAmt)
		{
			this.untaxPayAmt = untaxPayAmt;
		}

		public String getUntaxPayTax()
		{
			return untaxPayTax;
		}

		public void setUntaxPayTax(String untaxPayTax)
		{
			this.untaxPayTax = untaxPayTax;
		}

		public String getInvSplitType()
		{
			return invSplitType;
		}

		public void setInvSplitType(String invSplitType)
		{
			this.invSplitType = invSplitType;
		}

		public String getInvTotAmt()
		{
			return invTotAmt;
		}

		public void setInvTotAmt(String invTotAmt)
		{
			this.invTotAmt = invTotAmt;
		}

		public String getInvAmt()
		{
			return invAmt;
		}

		public void setInvAmt(String invAmt)
		{
			this.invAmt = invAmt;
		}

		public String getInvUamt()
		{
			return invUamt;
		}

		public void setInvUamt(String invUamt)
		{
			this.invUamt = invUamt;
		}

		public String getInvTax()
		{
			return invTax;
		}

		public void setInvTax(String invTax)
		{
			this.invTax = invTax;
		}

		public String getAccAmt()
		{
			return accAmt;
		}

		public void setAccAmt(String accAmt)
		{
			this.accAmt = accAmt;
		}

		public String getAccTax()
		{
			return accTax;
		}

		public void setAccTax(String accTax)
		{
			this.accTax = accTax;
		}

		public String getExtraCpAmt()
		{
			return extraCpAmt;
		}

		public void setExtraCpAmt(String extraCpAmt)
		{
			this.extraCpAmt = extraCpAmt;
		}

		public String getExtraCpTax()
		{
			return extraCpTax;
		}

		public void setExtraCpTax(String extraCpTax)
		{
			this.extraCpTax = extraCpTax;
		}

		public List<AgioInfo> getAgio_info() {
			return agio_info;
		}

		public void setAgio_info(List<AgioInfo> agio_info) {
			this.agio_info = agio_info;
		}

		public String getDisc_merReceive() {
			return disc_merReceive;
		}

		public void setDisc_merReceive(String disc_merReceive) {
			this.disc_merReceive = disc_merReceive;
		}

		public String getAmt_merReceive() {
			return amt_merReceive;
		}

		public void setAmt_merReceive(String amt_merReceive) {
			this.amt_merReceive = amt_merReceive;
		}

		public String getDisc_custPayReal() {
			return disc_custPayReal;
		}

		public void setDisc_custPayReal(String disc_custPayReal) {
			this.disc_custPayReal = disc_custPayReal;
		}

		public String getAmt_custPayReal() {
			return amt_custPayReal;
		}

		public void setAmt_custPayReal(String amt_custPayReal) {
			this.amt_custPayReal = amt_custPayReal;
		}

		public String getPreparationStatus()
		{
			return preparationStatus;
		}

		public void setPreparationStatus(String preparationStatus)
		{
			this.preparationStatus = preparationStatus;
		}
		
	}

	public static class MessagesBean {
		private String message;
		private String msgtype;
		private String msgname;
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getMsgtype() {
			return msgtype;
		}

		public void setMsgtype(String msgtype) {
			this.msgtype = msgtype;
		}

		public String getMsgname() {
			return msgname;
		}

		public void setMsgname(String msgname) {
			this.msgname = msgname;
		}
	}

	public static class AgioInfo {
		private String item;//	INT		项次
		private String qty;//	numeric		参与数量
		private String amt;//	numeric		参与金额
		private String inputDisc;//	numeric		折扣录入
		private String realDisc;//	numeric		实际折扣
		private String disc;//	numeric		折扣金额
		private String dcType;//	String		折扣方式
		private String dcTypeName;//	String		折扣方式名称
		private String pmtNo;//	String		促销单号
		private String giftCtf;//	String		折扣券券种
		private String giftCtfNo;//	String		折扣券券号
		private String bsNo;//	String		折扣理由码
		private String disc_merReceive;
		private String disc_custPayReal;

		public String getItem() {
			return item;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getQty() {
			return qty;
		}

		public void setQty(String qty) {
			this.qty = qty;
		}

		public String getAmt() {
			return amt;
		}

		public void setAmt(String amt) {
			this.amt = amt;
		}

		public String getInputDisc() {
			return inputDisc;
		}

		public void setInputDisc(String inputDisc) {
			this.inputDisc = inputDisc;
		}

		public String getRealDisc() {
			return realDisc;
		}

		public void setRealDisc(String realDisc) {
			this.realDisc = realDisc;
		}

		public String getDisc() {
			return disc;
		}

		public void setDisc(String disc) {
			this.disc = disc;
		}

		public String getDcType() {
			return dcType;
		}

		public void setDcType(String dcType) {
			this.dcType = dcType;
		}

		public String getDcTypeName() {
			return dcTypeName;
		}

		public void setDcTypeName(String dcTypeName) {
			this.dcTypeName = dcTypeName;
		}

		public String getPmtNo() {
			return pmtNo;
		}

		public void setPmtNo(String pmtNo) {
			this.pmtNo = pmtNo;
		}

		public String getGiftCtf() {
			return giftCtf;
		}

		public void setGiftCtf(String giftCtf) {
			this.giftCtf = giftCtf;
		}

		public String getGiftCtfNo() {
			return giftCtfNo;
		}

		public void setGiftCtfNo(String giftCtfNo) {
			this.giftCtfNo = giftCtfNo;
		}

		public String getBsNo() {
			return bsNo;
		}

		public void setBsNo(String bsNo) {
			this.bsNo = bsNo;
		}

		public String getDisc_merReceive() {
			return disc_merReceive;
		}

		public void setDisc_merReceive(String disc_merReceive) {
			this.disc_merReceive = disc_merReceive;
		}

		public String getDisc_custPayReal() {
			return disc_custPayReal;
		}

		public void setDisc_custPayReal(String disc_custPayReal) {
			this.disc_custPayReal = disc_custPayReal;
		}
	}
}
