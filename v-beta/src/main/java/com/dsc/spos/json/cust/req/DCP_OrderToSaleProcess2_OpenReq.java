package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.waimai.entity.order;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新方案，后续替换订转销服务
 */
public class DCP_OrderToSaleProcess2_OpenReq extends JsonBasicReq
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
        private String opMachineNo;//当前操作机台号
        private String opBDate;//当前操作营业日期
        private String opSquadNo;//当前操作班别
        private String opWorkNo;//当前操作班号
        private String opOpNo;//当前操作员编号
        private String opOpName;//当前操作员名称
        private String orderNo;
        private String eraseAmt;//抹零【订转销本次抹零】（尾款抹零要传）
        private String invMemo;//发票内容（尾款开发票要传）
        private String carrierCode;//载具类别编码（尾款开发票要传）
        private String loveCode;//爱心码（尾款开发票要传）
        private String buyerGuiNo;//买家统一编号（尾款开发票要传）
        private String isInvoice;//是否需开发票（尾款开发票要传）
        private String invoiceType;//发票类型（尾款开发票要传）
        private String carrierShowId;//载具显码（尾款开发票要传）
        private String carrierHiddenId;//载具隐码（尾款开发票要传）
        private String departNo;//部门编码
  


        private List<goods> goodsList;
        private List<Payment> pay;
        private List<order.CouponChange> couponChangeList;

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
        
        public String getEraseAmt()
        {
            return eraseAmt;
        }

        public void setEraseAmt(String eraseAmt)
        {
            this.eraseAmt = eraseAmt;
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

        public String getDepartNo()
		{
			return departNo;
		}

		public void setDepartNo(String departNo)
		{
			this.departNo = departNo;
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
    }

    public class goods
    {
        private String item;
        private BigDecimal qty;
        private String taxCode;
        private String taxType;
        private BigDecimal taxRate;
        private String inclTax;
        private String invSplitType;

        //这些字段都是服务自己处理，不需要前端传
        private String amt;
        private String virtual;
        private String disc_merReceive;//商家支付折扣金额
        private String disc_custPayReal;//顾客支付折扣金额
        private String amt_merReceive;//商家实收金额
        private String amt_custPayReal;//顾客实付金额
        private String packageType;
        private String packageFee;
        private String pluBarcode;
        private String pluNo;
        private String pluName;
        private String disc;
        private String boxNum;
        private String boxPrice;
        private String packageMitem;
        private String featureNo;
        private String featureName;
        private String warehouse;
        private String warehouseName;
        private String sUnit;
        private String sUnitName;
        private String specName;
        private String attrName;
        private String sellerNo;
        private String sellerName;
        private String giftReason;
        private String accNo;
        private String counterNo;
        private String couponType;
        private String couponCode;
        private String toppingType;
        private String toppingMitem;
        private String gift;
        private String oldPrice;
        private String oldAmt;
        private String price;
        private String invNo;
        private String invItem;
        private String pickQty;
        private String rQty;
        private String rcQty;
        private String unPickQty;
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

        public BigDecimal getQty()
        {
            return qty;
        }

        public void setQty(BigDecimal qty)
        {
            this.qty = qty;
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

        public BigDecimal getTaxRate()
        {
            return taxRate;
        }

        public void setTaxRate(BigDecimal taxRate)
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

        public String getAmt()
        {
            return amt;
        }

        public void setAmt(String amt)
        {
            this.amt = amt;
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

        public String getPackageType()
        {
            return packageType;
        }

        public void setPackageType(String packageType)
        {
            this.packageType = packageType;
        }

        public String getPackageFee()
        {
            return packageFee;
        }

        public void setPackageFee(String packageFee)
        {
            this.packageFee = packageFee;
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

        public String getPackageMitem()
        {
            return packageMitem;
        }

        public void setPackageMitem(String packageMitem)
        {
            this.packageMitem = packageMitem;
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

        public String getGiftReason()
        {
            return giftReason;
        }

        public void setGiftReason(String giftReason)
        {
            this.giftReason = giftReason;
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

        public String getGift()
        {
            return gift;
        }

        public void setGift(String gift)
        {
            this.gift = gift;
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

        public String getPrice()
        {
            return price;
        }

        public void setPrice(String price)
        {
            this.price = price;
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

        public String getRcQty()
        {
            return rcQty;
        }

        public void setRcQty(String rcQty)
        {
            this.rcQty = rcQty;
        }

        public String getUnPickQty()
        {
            return unPickQty;
        }

        public void setUnPickQty(String unPickQty)
        {
            this.unPickQty = unPickQty;
        }

        public String getFlavorStuffDetail() {
            return flavorStuffDetail;
        }

        public void setFlavorStuffDetail(String flavorStuffDetail) {
            this.flavorStuffDetail = flavorStuffDetail;
        }

        public List<Agio> getAgioInfo()
        {
            return agioInfo;
        }

        public void setAgioInfo(List<Agio> agioInfo)
        {
            this.agioInfo = agioInfo;
        }
    }

    public class Agio
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
        private String orderToSalePayAgioFlag;//订转销付款产生的折扣Y/N
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
        private String payType;//新零售支付编码
        private String payCode;//支付方式(小类)  ERP的小类
        private String payCodeErp;//支付类型(大类)  ERP的大类
        private String payName;//ERP付款名称
        private String loadDocType;//平台类型
        private String order_payCode;//平台支付方式
        private String mobile;//
        private String cardNo;//支付卡券号
        private String ctType;//卡券类型
        private String paySerNum;//第三方支付订单号
        private String serialNo;//银联卡流水号
        private String refNo;//银联卡交易参考号
        private String teriminalNo;//银联终端号
        private String authCode;//授权码
        private String descore;//积分抵现扣减
        private String pay;//金额   录入的金额
        private String extra;//溢收金额  主要针对券
        private String changed;//找零
        private String isOrderPay;//是否订金
        private String isOnlinePay;//是否平台支付
        private String couponQty;//券数量
        private String isVerification;//券是否核销:Y/N
        private String canInvoice;//0不开发票，1可开发票，2已开发票，3第三方开发票
        private String funcNo;//功能编码
        private String merDiscount;//商户优惠金额，移动支付用，例如支付宝，微信等
        private String merReceive;//商家实收金额，移动支付用，例如支付宝，微信等
        private String thirdDiscount;//第三方优惠金额：移动支付用，例如支付宝，微信等
        private String custPayReal;//客户实付金额：移动支付用，例如支付宝，微信等
        private String couponMarketPrice;//券面值
        private String couponPrice;//券售价
        private String gainChannel;//企迈券渠道
        private String gainChannelName;//企迈券渠道名称

        //不用传，服务处理
        private String paydoctype;//POS=4处理卡的扣款类型，
        private String isTurnover;
        private String cardBeforeAmt;
        private String cardRemainAmt;
        private String sendPay;
        private String payDiscAmt;
        private String payAmt1;
        private String payAmt2;

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

        public String getFuncNo()
        {
            return funcNo;
        }

        public void setFuncNo(String funcNo)
        {
            this.funcNo = funcNo;
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

        public String getPaydoctype()
        {
            return paydoctype;
        }

        public void setPaydoctype(String paydoctype)
        {
            this.paydoctype = paydoctype;
        }

        public String getIsTurnover()
        {
            return isTurnover;
        }

        public void setIsTurnover(String isTurnover)
        {
            this.isTurnover = isTurnover;
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
