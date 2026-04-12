package com.dsc.spos.foreign.erp.request;

import com.dsc.spos.foreign.erp.Request;

import java.util.ArrayList;
import java.util.List;

public class OrderPayRefundCreateRequest extends Request<List<OrderPayRefundCreateRequest.RequestBean>> {

    public static class RequestBean
    {
        /**
         * version : 版本号
         * billNo : 收款单号
         * billDate : 单据日期
         * bDate : 营业日期
         * sourceBillType : 来源单据类型
         * sourceBillNo : 来源单据编号
         * companyId : 收款公司编码
         * shopId : 收款门店编码
         * loadDocType : 渠道编码
         * appType : 应用类型
         * machineId : 机台编号
         * squadNo : 班次流水ID
         * workNo : 班号
         * customerNo : 大客户编号
         * direction : 金额方向:1、-1
         * payAbleAmt : 付款金额
         * payDiscAmt : 优惠金额
         * payRealAmt : 实付金额
         * writeOffAmt : 冲销金额
         * lackAmt : 未冲销金额
         * useType : 款项用途：front-预付款 refund-退款 final-尾款【尾款存入零售单】
         * status : 收款状态
         * invAmt : 发票金额
         * invStatus : 开票状态：-1未开票 0已开票 1作废 2折让
         * sourceShopId; 订单的下单门店
         * orderPay_detail : [{"seq":"项次","paycode":"支付方式(小类)","paycodeErp":"支付方式(大类)","payName":"付款名称","loadDocType":"渠道编码","order_payCode":"平台支付方式编码","isOnlinePay":"是否平台支付","pay":"金额","payDiscAmt":"支付优惠金额","payAmt1":"卡支付本金金额","payAmt2":"卡支付赠送金额","descore":"积分抵现扣减","extra":"溢收金额","changed":"找零金额","ctType":"卡券类型","cardNo":"支付卡券号","cardBeforeAmt":"卡消费前金额","cardRemainAmt":"卡消费后金额","couponQty":"券数量","isVerification":"券是否核销:Y/N","writeOffAmt":"冲销金额","lackAmt":"未冲销金额","paySerNum":"第三方支付订单号","serialNo":"银联卡流水号","refNo":"第银联卡交易参考号","teriminalNo":"银联终端号","authCode":"授权码","canInvoice":"开票类型：0不开发票，1可开发票，2已开发票，3第三方开发票 "},{"seq":"项次","paycode":"支付方式(小类)","paycodeErp":"支付方式(大类)","payName":"付款名称","loadDocType":"渠道编码","order_payCode":"平台支付方式编码","isOnlinePay":"是否平台支付","pay":"金额","payDiscAmt":"支付优惠金额","payAmt1":"卡支付本金金额","payAmt2":"卡支付赠送金额","descore":"积分抵现扣减","extra":"溢收金额","changed":"找零金额","ctType":"卡券类型","cardNo":"支付卡券号","cardBeforeAmt":"卡消费前金额","cardRemainAmt":"卡消费后金额","couponQty":"券数量","isVerification":"券是否核销:Y/N","writeOffAmt":"冲销金额","lackAmt":"未冲销金额","paySerNum":"第三方支付订单号","serialNo":"银联卡流水号","refNo":"第银联卡交易参考号","teriminalNo":"银联终端号","authCode":"授权码","canInvoice":"开票类型：0不开发票，1可开发票，2已开发票，3第三方开发票 "}]
         */

        private String version;
        private String billNo;
        private String billDate;
        private String bDate;
        private String sourceBillType;
        private String sourceBillNo;
        private String sourceHeadBillNo;
        private String companyId;
        private String shopId;
        private String loadDocType;
        private String channelId;
        private String appType;
        private String machineId;
        private String squadNo;
        private String workNo;
        private String customerNo;
        private String direction;
        private String payAbleAmt;
        private String payDiscAmt;
        private String payRealAmt;
        private String writeOffAmt;
        private String lackAmt;
        private String useType;
        private String status;
        private String invAmt;
        private String invStatus;
        private String process_status;
        private String invoiceprocess_status;
        private String departNo;
        private String sourceShopId;

        private List<RequestBeanInvoice> invoiceInfo=new ArrayList<>();

        private List<OrderPayDetailBean> orderPay_detail = new ArrayList<>();

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getBillNo() {
            return billNo;
        }

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

        public String getBillDate() {
            return billDate;
        }

        public void setBillDate(String billDate) {
            this.billDate = billDate;
        }

        public String getBDate() {
            return bDate;
        }

        public void setBDate(String bDate) {
            this.bDate = bDate;
        }

        public String getSourceBillType() {
            return sourceBillType;
        }

        public void setSourceBillType(String sourceBillType) {
            this.sourceBillType = sourceBillType;
        }

        public String getSourceBillNo() {
            return sourceBillNo;
        }

        public void setSourceBillNo(String sourceBillNo) {
            this.sourceBillNo = sourceBillNo;
        }

        public String getSourceHeadBillNo()
        {
            return sourceHeadBillNo;
        }

        public void setSourceHeadBillNo(String sourceHeadBillNo)
        {
            this.sourceHeadBillNo = sourceHeadBillNo;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getLoadDocType() {
            return loadDocType;
        }

        public void setLoadDocType(String loadDocType) {
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

		public String getAppType() {
            return appType;
        }

        public void setAppType(String appType) {
            this.appType = appType;
        }

        public String getMachineId() {
            return machineId;
        }

        public void setMachineId(String machineId) {
            this.machineId = machineId;
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

        public String getCustomerNo() {
            return customerNo;
        }

        public void setCustomerNo(String customerNo) {
            this.customerNo = customerNo;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getPayAbleAmt() {
            return payAbleAmt;
        }

        public void setPayAbleAmt(String payAbleAmt) {
            this.payAbleAmt = payAbleAmt;
        }

        public String getPayDiscAmt() {
            return payDiscAmt;
        }

        public void setPayDiscAmt(String payDiscAmt) {
            this.payDiscAmt = payDiscAmt;
        }

        public String getPayRealAmt() {
            return payRealAmt;
        }

        public void setPayRealAmt(String payRealAmt) {
            this.payRealAmt = payRealAmt;
        }

        public String getWriteOffAmt() {
            return writeOffAmt;
        }

        public void setWriteOffAmt(String writeOffAmt) {
            this.writeOffAmt = writeOffAmt;
        }

        public String getLackAmt() {
            return lackAmt;
        }

        public void setLackAmt(String lackAmt) {
            this.lackAmt = lackAmt;
        }

        public String getUseType() {
            return useType;
        }

        public void setUseType(String useType) {
            this.useType = useType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInvAmt() {
            return invAmt;
        }

        public void setInvAmt(String invAmt) {
            this.invAmt = invAmt;
        }

        public String getInvStatus() {
            return invStatus;
        }

        public void setInvStatus(String invStatus) {
            this.invStatus = invStatus;
        }


        public String getProcess_status()
        {
            return process_status;
        }

        public void setProcess_status(String process_status)
        {
            this.process_status = process_status;
        }

        public String getInvoiceprocess_status()
        {
            return invoiceprocess_status;
        }

        public void setInvoiceprocess_status(String invoiceprocess_status)
        {
            this.invoiceprocess_status = invoiceprocess_status;
        }

        public String getbDate()
        {
            return bDate;
        }

        public void setbDate(String bDate)
        {
            this.bDate = bDate;
        }

        public String getDepartNo()
		{
			return departNo;
		}

		public void setDepartNo(String departNo)
		{
			this.departNo = departNo;
		}

        public String getSourceShopId() {
            return sourceShopId;
        }

        public void setSourceShopId(String sourceShopId) {
            this.sourceShopId = sourceShopId;
        }

        public List<RequestBeanInvoice> getInvoiceInfo()
        {
            return invoiceInfo;
        }

        public void setInvoiceInfo(List<RequestBeanInvoice> invoiceInfo)
        {
            this.invoiceInfo = invoiceInfo;
        }

        public List<OrderPayDetailBean> getOrderPay_detail() {
            return orderPay_detail;
        }

        public void setOrderPay_detail(List<OrderPayDetailBean> orderPay_detail) {
            this.orderPay_detail = orderPay_detail;
        }
    }

    public static class OrderPayDetailBean {
        /**
         * seq : 项次
         * paycode : 支付方式(小类)
         * paycodeErp : 支付方式(大类)
         * payName : 付款名称
         * loadDocType : 渠道编码
         * order_payCode : 平台支付方式编码
         * isOnlinePay : 是否平台支付
         * pay : 金额
         * payDiscAmt : 支付优惠金额
         * payAmt1 : 卡支付本金金额
         * payAmt2 : 卡支付赠送金额
         * descore : 积分抵现扣减
         * extra : 溢收金额
         * changed : 找零金额
         * ctType : 卡券类型
         * cardNo : 支付卡券号
         * cardBeforeAmt : 卡消费前金额
         * cardRemainAmt : 卡消费后金额
         * couponQty : 券数量
         * isVerification : 券是否核销:Y/N
         * writeOffAmt : 冲销金额
         * lackAmt : 未冲销金额
         * paySerNum : 第三方支付订单号
         * serialNo : 银联卡流水号
         * refNo : 第银联卡交易参考号
         * teriminalNo : 银联终端号
         * authCode : 授权码
         * canInvoice : 开票类型：0不开发票，1可开发票，2已开发票，3第三方开发票
         */

        private String seq;
        private String paycode;
        private String paycodeErp;
        private String payName;
        private String loadDocType;
        private String order_payCode;
        private String isOnlinePay;
        private String pay;
        private String payDiscAmt;
        private String payAmt1;
        private String payAmt2;
        private String descore;
        private String extra;
        private String changed;
        private String ctType;
        private String cardNo;
        private String cardBeforeAmt;
        private String cardRemainAmt;
        private String couponQty;
        private String isVerification;
        private String writeOffAmt;
        private String lackAmt;
        private String paySerNum;
        private String serialNo;
        private String refNo;
        private String teriminalNo;
        private String authCode;
        private String canInvoice;
        private String  merDiscount;
        private String merReceive;
        private String thirdDiscount;
        private String custPayReal;
        private String sendPay;
        private String couponPrice;
        private String couponMarketPrice;

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public String getPaycode() {
            return paycode;
        }

        public void setPaycode(String paycode) {
            this.paycode = paycode;
        }

        public String getPaycodeErp() {
            return paycodeErp;
        }

        public void setPaycodeErp(String paycodeErp) {
            this.paycodeErp = paycodeErp;
        }

        public String getPayName() {
            return payName;
        }

        public void setPayName(String payName) {
            this.payName = payName;
        }

        public String getLoadDocType() {
            return loadDocType;
        }

        public void setLoadDocType(String loadDocType) {
            this.loadDocType = loadDocType;
        }

        public String getOrder_payCode() {
            return order_payCode;
        }

        public void setOrder_payCode(String order_payCode) {
            this.order_payCode = order_payCode;
        }

        public String getIsOnlinePay() {
            return isOnlinePay;
        }

        public void setIsOnlinePay(String isOnlinePay) {
            this.isOnlinePay = isOnlinePay;
        }

        public String getPay() {
            return pay;
        }

        public void setPay(String pay) {
            this.pay = pay;
        }

        public String getPayDiscAmt() {
            return payDiscAmt;
        }

        public void setPayDiscAmt(String payDiscAmt) {
            this.payDiscAmt = payDiscAmt;
        }

        public String getPayAmt1() {
            return payAmt1;
        }

        public void setPayAmt1(String payAmt1) {
            this.payAmt1 = payAmt1;
        }

        public String getPayAmt2() {
            return payAmt2;
        }

        public void setPayAmt2(String payAmt2) {
            this.payAmt2 = payAmt2;
        }

        public String getDescore() {
            return descore;
        }

        public void setDescore(String descore) {
            this.descore = descore;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String getChanged() {
            return changed;
        }

        public void setChanged(String changed) {
            this.changed = changed;
        }

        public String getCtType() {
            return ctType;
        }

        public void setCtType(String ctType) {
            this.ctType = ctType;
        }

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public String getCardBeforeAmt() {
            return cardBeforeAmt;
        }

        public void setCardBeforeAmt(String cardBeforeAmt) {
            this.cardBeforeAmt = cardBeforeAmt;
        }

        public String getCardRemainAmt() {
            return cardRemainAmt;
        }

        public void setCardRemainAmt(String cardRemainAmt) {
            this.cardRemainAmt = cardRemainAmt;
        }

        public String getCouponQty() {
            return couponQty;
        }

        public void setCouponQty(String couponQty) {
            this.couponQty = couponQty;
        }

        public String getIsVerification() {
            return isVerification;
        }

        public void setIsVerification(String isVerification) {
            this.isVerification = isVerification;
        }

        public String getWriteOffAmt() {
            return writeOffAmt;
        }

        public void setWriteOffAmt(String writeOffAmt) {
            this.writeOffAmt = writeOffAmt;
        }

        public String getLackAmt() {
            return lackAmt;
        }

        public void setLackAmt(String lackAmt) {
            this.lackAmt = lackAmt;
        }

        public String getPaySerNum() {
            return paySerNum;
        }

        public void setPaySerNum(String paySerNum) {
            this.paySerNum = paySerNum;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getRefNo() {
            return refNo;
        }

        public void setRefNo(String refNo) {
            this.refNo = refNo;
        }

        public String getTeriminalNo() {
            return teriminalNo;
        }

        public void setTeriminalNo(String teriminalNo) {
            this.teriminalNo = teriminalNo;
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }

        public String getCanInvoice() {
            return canInvoice;
        }

        public void setCanInvoice(String canInvoice) {
            this.canInvoice = canInvoice;
        }

        public String getMerDiscount() {
            return merDiscount;
        }

        public void setMerDiscount(String merDiscount) {
            this.merDiscount = merDiscount;
        }

        public String getMerReceive() {
            return merReceive;
        }

        public void setMerReceive(String merReceive) {
            this.merReceive = merReceive;
        }

        public String getThirdDiscount() {
            return thirdDiscount;
        }

        public void setThirdDiscount(String thirdDiscount) {
            this.thirdDiscount = thirdDiscount;
        }

        public String getCustPayReal() {
            return custPayReal;
        }

        public void setCustPayReal(String custPayReal) {
            this.custPayReal = custPayReal;
        }

        public String getSendPay() {
            return sendPay;
        }

        public void setSendPay(String sendPay) {
            this.sendPay = sendPay;
        }

        public String getCouponPrice() {
            return couponPrice;
        }

        public void setCouponPrice(String couponPrice) {
            this.couponPrice = couponPrice;
        }

        public String getCouponMarketPrice() {
            return couponMarketPrice;
        }

        public void setCouponMarketPrice(String couponMarketPrice) {
            this.couponMarketPrice = couponMarketPrice;
        }
    }

    //发票
    public static class RequestBeanInvoice
    {
        private String billNo;//开票单号
        private String companyId;//开票所属公司
        private String shopId;//开票机构
        private String saleType;//来源单据类型Order-订单
        private String saleNo;//来源订单号
        private String invSplitType;//发票开票拆分类型：1:不拆分、2:按商品拆分、3:按金額拆分
        private String bDate;//营业日期
        private String machineId;//机台编号
        private String sDate;//系统日期
        private String sTime;//系统时间
        private String recordType;//记录类型：0-开立 1-作废 2-折让
        private String invNo;//发票号码
        private String invFNo;//发票流水序列号
        private String invMemo;//发票内容
        private String sellerGuiNo;//卖家统一编号
        private String invYear;//发票年份
        private String invStartMon;//发票起始月
        private String invEndMon;//发票截止月
        private String taxAtionType;//课税别
        private String invType;//发票类型：01手开二联，02手开三联，03收银机二联(目前不支持)，04收银机三联(目前不支持)，07电子发票
        private String invFormat;//媒体申报格式
        private String buyerGuiNo;//买家统一编号
        private String totAmt;//商品成交金额(含税)
        private String freeTaxAmt;//销售免税金额
        private String zeroTaxAmt;//销售零税金额
        private String taxAbleUamt;//销售未税金额
        private String taxRate;//税率
        private String taxAbleAmt;//销售应税金额
        private String taxAbleTax;//销售税额
        private String payAmt;//付款金额
        private String untaxPayAmt;//不可开票付款金额(含税)
        private String untaxPayTax;//不可开票付款税额
        private String gftInvAmt;//付款已开票金额
        private String gftInvTax;//付款已开票税额
        private String extraCpAmt;//已开发票礼券溢收金额
        private String extraCpTax;//已开发票礼券溢收税额
        private String invTotAmt;//发票金额
        private String invFreeAmt;//发票免税金额
        private String invZeroAmt;//发票零税金额
        private String invAmt;//发票应税金额
        private String invUamt;//发票未税金额
        private String invTax;//发票税额
        private String accAmt;//留抵金额
        private String accTax;//留抵税额
        private String isEInvoice;//电子发票否Y/N，手开发票时为N
        private String carrierCode;//载具类别编码
        private String carrierShowId;//载具显码
        private String carrierHiddenId;//载具隐码
        private String loveCode;//爱心码
        private String randomCode;//发票防伪随机码
        private String printcount;//发票防伪随机码
        private String osDate;//原发票日期：作废或折让时使用
        private String rebateNo;//折让单单号：折让时使用
        private String rebateAmt;//发票折让金额/开立折让单的金额
        private String invAlidOp;//作废人员：作废时使用
        private String invAlidCode;//作废理由码：作废时使用
        private String invAlidName;//作废理由描述：作废时使用
        private String isNullified;//发票是否已作废Y/N：发票作废后更新原单记录的状态为Y
        private String invCheckStatus;//FANYU发票检核状态：N-待检核E-检核不通过 Y-检核通过
        private String invCheckMemo;//FANYU发票检核结果描述
        private List<RequestBeanInvoice_Detail> invoiceInfo_detail=new ArrayList<>();//
        public String getBillNo()
        {
            return billNo;
        }
        public void setBillNo(String billNo)
        {
            this.billNo = billNo;
        }
        public String getCompanyId()
        {
            return companyId;
        }
        public void setCompanyId(String companyId)
        {
            this.companyId = companyId;
        }
        public String getShopId()
        {
            return shopId;
        }
        public void setShopId(String shopId)
        {
            this.shopId = shopId;
        }
        public String getSaleType()
        {
            return saleType;
        }
        public void setSaleType(String saleType)
        {
            this.saleType = saleType;
        }
        public String getSaleNo()
        {
            return saleNo;
        }
        public void setSaleNo(String saleNo)
        {
            this.saleNo = saleNo;
        }
        public String getInvSplitType()
        {
            return invSplitType;
        }
        public void setInvSplitType(String invSplitType)
        {
            this.invSplitType = invSplitType;
        }
        public String getbDate()
        {
            return bDate;
        }
        public void setbDate(String bDate)
        {
            this.bDate = bDate;
        }
        public String getMachineId()
        {
            return machineId;
        }
        public void setMachineId(String machineId)
        {
            this.machineId = machineId;
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
        public String getRecordType()
        {
            return recordType;
        }
        public void setRecordType(String recordType)
        {
            this.recordType = recordType;
        }
        public String getInvNo()
        {
            return invNo;
        }
        public void setInvNo(String invNo)
        {
            this.invNo = invNo;
        }
        public String getInvFNo()
        {
            return invFNo;
        }
        public void setInvFNo(String invFNo)
        {
            this.invFNo = invFNo;
        }
        public String getInvMemo()
        {
            return invMemo;
        }
        public void setInvMemo(String invMemo)
        {
            this.invMemo = invMemo;
        }
        public String getSellerGuiNo()
        {
            return sellerGuiNo;
        }
        public void setSellerGuiNo(String sellerGuiNo)
        {
            this.sellerGuiNo = sellerGuiNo;
        }
        public String getInvYear()
        {
            return invYear;
        }
        public void setInvYear(String invYear)
        {
            this.invYear = invYear;
        }
        public String getInvStartMon()
        {
            return invStartMon;
        }
        public void setInvStartMon(String invStartMon)
        {
            this.invStartMon = invStartMon;
        }
        public String getInvEndMon()
        {
            return invEndMon;
        }
        public void setInvEndMon(String invEndMon)
        {
            this.invEndMon = invEndMon;
        }
        public String getTaxAtionType()
        {
            return taxAtionType;
        }
        public void setTaxAtionType(String taxAtionType)
        {
            this.taxAtionType = taxAtionType;
        }
        public String getInvType()
        {
            return invType;
        }
        public void setInvType(String invType)
        {
            this.invType = invType;
        }
        public String getInvFormat()
        {
            return invFormat;
        }
        public void setInvFormat(String invFormat)
        {
            this.invFormat = invFormat;
        }
        public String getBuyerGuiNo()
        {
            return buyerGuiNo;
        }
        public void setBuyerGuiNo(String buyerGuiNo)
        {
            this.buyerGuiNo = buyerGuiNo;
        }
        public String getTotAmt()
        {
            return totAmt;
        }
        public void setTotAmt(String totAmt)
        {
            this.totAmt = totAmt;
        }
        public String getFreeTaxAmt()
        {
            return freeTaxAmt;
        }
        public void setFreeTaxAmt(String freeTaxAmt)
        {
            this.freeTaxAmt = freeTaxAmt;
        }
        public String getZeroTaxAmt()
        {
            return zeroTaxAmt;
        }
        public void setZeroTaxAmt(String zeroTaxAmt)
        {
            this.zeroTaxAmt = zeroTaxAmt;
        }
        public String getTaxAbleUamt()
        {
            return taxAbleUamt;
        }
        public void setTaxAbleUamt(String taxAbleUamt)
        {
            this.taxAbleUamt = taxAbleUamt;
        }
        public String getTaxRate()
        {
            return taxRate;
        }
        public void setTaxRate(String taxRate)
        {
            this.taxRate = taxRate;
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
        public String getPayAmt()
        {
            return payAmt;
        }
        public void setPayAmt(String payAmt)
        {
            this.payAmt = payAmt;
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
        public String getInvTotAmt()
        {
            return invTotAmt;
        }
        public void setInvTotAmt(String invTotAmt)
        {
            this.invTotAmt = invTotAmt;
        }
        public String getInvFreeAmt()
        {
            return invFreeAmt;
        }
        public void setInvFreeAmt(String invFreeAmt)
        {
            this.invFreeAmt = invFreeAmt;
        }
        public String getInvZeroAmt()
        {
            return invZeroAmt;
        }
        public void setInvZeroAmt(String invZeroAmt)
        {
            this.invZeroAmt = invZeroAmt;
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
        public String getIsEInvoice()
        {
            return isEInvoice;
        }
        public void setIsEInvoice(String isEInvoice)
        {
            this.isEInvoice = isEInvoice;
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
        public String getRandomCode()
        {
            return randomCode;
        }
        public void setRandomCode(String randomCode)
        {
            this.randomCode = randomCode;
        }
        public String getOsDate()
        {
            return osDate;
        }
        public void setOsDate(String osDate)
        {
            this.osDate = osDate;
        }
        public String getRebateNo()
        {
            return rebateNo;
        }
        public void setRebateNo(String rebateNo)
        {
            this.rebateNo = rebateNo;
        }
        public String getRebateAmt()
        {
            return rebateAmt;
        }
        public void setRebateAmt(String rebateAmt)
        {
            this.rebateAmt = rebateAmt;
        }
        public String getInvAlidOp()
        {
            return invAlidOp;
        }
        public void setInvAlidOp(String invAlidOp)
        {
            this.invAlidOp = invAlidOp;
        }
        public String getInvAlidCode()
        {
            return invAlidCode;
        }
        public void setInvAlidCode(String invAlidCode)
        {
            this.invAlidCode = invAlidCode;
        }
        public String getInvAlidName()
        {
            return invAlidName;
        }
        public void setInvAlidName(String invAlidName)
        {
            this.invAlidName = invAlidName;
        }
        public String getIsNullified()
        {
            return isNullified;
        }
        public void setIsNullified(String isNullified)
        {
            this.isNullified = isNullified;
        }
        public String getInvCheckStatus()
        {
            return invCheckStatus;
        }
        public void setInvCheckStatus(String invCheckStatus)
        {
            this.invCheckStatus = invCheckStatus;
        }
        public String getInvCheckMemo()
        {
            return invCheckMemo;
        }
        public void setInvCheckMemo(String invCheckMemo)
        {
            this.invCheckMemo = invCheckMemo;
        }

        public String getPrintcount()
        {
            return printcount;
        }
        public void setPrintcount(String printcount)
        {
            this.printcount = printcount;
        }
        public List<RequestBeanInvoice_Detail> getInvoiceInfo_detail()
        {
            return invoiceInfo_detail;
        }
        public void setInvoiceInfo_detail(List<RequestBeanInvoice_Detail> invoiceInfo_detail)
        {
            this.invoiceInfo_detail = invoiceInfo_detail;
        }

    }

    //发票明细
    public static class RequestBeanInvoice_Detail
    {
        private String item;//项次
        private String invNo;//发票号码
        private String bDate;//营业日期
        private String sDate;//系统日期
        private String sTime;//系统时间
        private String oItem;//原商品项次
        private String content;//发票商品名称
        private String qty;//发票商品数量
        private String amt;//销售小计金额
        private String freeTaxAmt;//销售免税金额
        private String zeroTaxAmt;//销售零税金额
        private String taxAbleUamt;//销售未税金额
        private String taxRate;//税率
        private String taxAbleAmt;//销售应税金额
        private String taxAbleTax;//销售税额
        private String payAmt;//付款金额
        private String untaxPayAmt;//不可开票付款金额(含税)
        private String untaxPayTax;//不可开票付款税额
        private String gftInvAmt;//付款已开票金额
        private String gftInvTax;//付款已开票税额
        private String extraCpAmt;//已开发票礼券溢收金额
        private String extraCpTax;//已开发票礼券溢收税额
        private String invTotAmt;//发票金额
        private String invFreeAmt;//发票免税金额
        private String invZeroAmt;//发票零税金额
        private String invAmt;//发票应税金额
        private String invUamt;//发票未税金额
        private String invTax;//发票税额
        private String accAmt;//留抵金额
        private String accTax;//留抵税额

        public String getItem()
        {
            return item;
        }
        public void setItem(String item)
        {
            this.item = item;
        }
        public String getInvNo()
        {
            return invNo;
        }
        public void setInvNo(String invNo)
        {
            this.invNo = invNo;
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
        public String getoItem()
        {
            return oItem;
        }
        public void setoItem(String oItem)
        {
            this.oItem = oItem;
        }
        public String getContent()
        {
            return content;
        }
        public void setContent(String content)
        {
            this.content = content;
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
        public String getFreeTaxAmt()
        {
            return freeTaxAmt;
        }
        public void setFreeTaxAmt(String freeTaxAmt)
        {
            this.freeTaxAmt = freeTaxAmt;
        }
        public String getZeroTaxAmt()
        {
            return zeroTaxAmt;
        }
        public void setZeroTaxAmt(String zeroTaxAmt)
        {
            this.zeroTaxAmt = zeroTaxAmt;
        }
        public String getTaxAbleUamt()
        {
            return taxAbleUamt;
        }
        public void setTaxAbleUamt(String taxAbleUamt)
        {
            this.taxAbleUamt = taxAbleUamt;
        }
        public String getTaxRate()
        {
            return taxRate;
        }
        public void setTaxRate(String taxRate)
        {
            this.taxRate = taxRate;
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
        public String getPayAmt()
        {
            return payAmt;
        }
        public void setPayAmt(String payAmt)
        {
            this.payAmt = payAmt;
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
        public String getInvTotAmt()
        {
            return invTotAmt;
        }
        public void setInvTotAmt(String invTotAmt)
        {
            this.invTotAmt = invTotAmt;
        }
        public String getInvFreeAmt()
        {
            return invFreeAmt;
        }
        public void setInvFreeAmt(String invFreeAmt)
        {
            this.invFreeAmt = invFreeAmt;
        }
        public String getInvZeroAmt()
        {
            return invZeroAmt;
        }
        public void setInvZeroAmt(String invZeroAmt)
        {
            this.invZeroAmt = invZeroAmt;
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

    }


}

/**
 {
 "version": "版本号",
 "billNo": "收款单号",
 "billDate": "单据日期",
 "bDate": "营业日期",
 "sourceBillType": "来源单据类型",
 "sourceBillNo": "来源单据编号",
 "companyId": "收款公司编码",
 "shopId": "收款门店编码",
 "loadDocType": "渠道编码",
 "appType": "应用类型",
 "machineId": "机台编号",
 "squadNo": "班次流水ID",
 "workNo": "班号",
 "customerNo": "大客户编号",
 "direction": "金额方向:1、-1",
 "payAbleAmt": "付款金额",
 "payDiscAmt": "优惠金额",
 "payRealAmt": "实付金额",
 "writeOffAmt": "冲销金额",
 "lackAmt": "未冲销金额",
 "useType": "款项用途：front-预付款 refund-退款 final-尾款【尾款存入零售单】",
 "status": "收款状态",
 "invAmt": "发票金额",
 "invStatus": "开票状态：-1未开票 0已开票 1作废 2折让",
 "orderPay_detail": [{
 "seq": "项次",
 "paycode": "支付方式(小类)",
 "paycodeErp": "支付方式(大类)",
 "payName": "付款名称",
 "loadDocType": "渠道编码",
 "order_payCode": "平台支付方式编码",
 "isOnlinePay": "是否平台支付",
 "pay": "金额",
 "payDiscAmt": "支付优惠金额",
 "payAmt1": "卡支付本金金额",
 "payAmt2": "卡支付赠送金额",
 "descore": "积分抵现扣减",
 "extra": "溢收金额",
 "changed": "找零金额",
 "ctType": "卡券类型",
 "cardNo": "支付卡券号",
 "cardBeforeAmt": "卡消费前金额",
 "cardRemainAmt": "卡消费后金额",
 "couponQty": "券数量",
 "isVerification": "券是否核销:Y/N",
 "writeOffAmt": "冲销金额",
 "lackAmt": "未冲销金额",
 "paySerNum": "第三方支付订单号",
 "serialNo": "银联卡流水号",
 "refNo": "第银联卡交易参考号",
 "teriminalNo": "银联终端号",
 "authCode": "授权码",
 "canInvoice": "开票类型：0不开发票，1可开发票，2已开发票，3第三方开发票 "
 }, {
 "seq": "项次",
 "paycode": "支付方式(小类)",
 "paycodeErp": "支付方式(大类)",
 "payName": "付款名称",
 "loadDocType": "渠道编码",
 "order_payCode": "平台支付方式编码",
 "isOnlinePay": "是否平台支付",
 "pay": "金额",
 "payDiscAmt": "支付优惠金额",
 "payAmt1": "卡支付本金金额",
 "payAmt2": "卡支付赠送金额",
 "descore": "积分抵现扣减",
 "extra": "溢收金额",
 "changed": "找零金额",
 "ctType": "卡券类型",
 "cardNo": "支付卡券号",
 "cardBeforeAmt": "卡消费前金额",
 "cardRemainAmt": "卡消费后金额",
 "couponQty": "券数量",
 "isVerification": "券是否核销:Y/N",
 "writeOffAmt": "冲销金额",
 "lackAmt": "未冲销金额",
 "paySerNum": "第三方支付订单号",
 "serialNo": "银联卡流水号",
 "refNo": "第银联卡交易参考号",
 "teriminalNo": "银联终端号",
 "authCode": "授权码",
 "canInvoice": "开票类型：0不开发票，1可开发票，2已开发票，3第三方开发票 "

 }]
 }
 */
