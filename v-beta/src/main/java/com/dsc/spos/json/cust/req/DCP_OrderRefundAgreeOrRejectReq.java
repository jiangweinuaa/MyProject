package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;


public class DCP_OrderRefundAgreeOrRejectReq extends JsonBasicReq 
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
		 * 操作类型 1:同意 2：拒绝	
		 */		
		private String opType;
		
		/**
		 * 退单类型 0：全退（全部商品（未出货和已出货都退）),1：仅退未出货商品（POS操作退订）,2：仅退已出货商品（POS操作退销售单）
		 */
		private String refundType;
		private String orderNo;
		private String loadDocType;
		private String channelId;
		private String pickGoodsRefundType;
		private String refundReasonNo;
		private String refundReasonName;
		private String refundReason;
		private String invOperateType;
		private String refundBdate;
		private String refundDatetime;
        private String isRefundDeliverAmt;//是否退运费 0否1是
		private String tot_amt;
		
		private List<levelGoods> goods;
		
		private List<levelPay> pay;
		public String getTot_amt() {
			return tot_amt;
		}
		public void setTot_amt(String tot_amt) {
			this.tot_amt = tot_amt;
		}
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
		public String getOpType()
		{
			return opType;
		}
		public void setOpType(String opType)
		{
			this.opType = opType;
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
		public List<levelGoods> getGoods()
		{
			return goods;
		}
		public void setGoods(List<levelGoods> goods)
		{
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
				
	}
	
	public class levelGoods
	{
		private String item;
		private String pluNo;
		private String pluName;
		private String sUnit;
		private String pluBarcode;
		private String featureNo;
		private String specName;
		private String attrName;
		private double oldPrice;
		private double price;
		private double qty;
		private double amt;
		
		public String getPluName() {
			return pluName;
		}

		public void setPluName(String pluName) {
			this.pluName = pluName;
		}

		public String getPluBarcode() {
			return pluBarcode;
		}

		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}

		/**
		 * 是否赠品（Y/N）
		 */
		private String gift;
		
		/**
		 * 套餐类型1、正常商品 2、套餐主商品  3、套餐子商品
		 */
		private String packageType;
		
		/**
		 * 套餐主商品项次	
		 */
		private String packageMitem;

		public String getItem()
		{
			return item;
		}

		public void setItem(String item)
		{
			this.item = item;
		}

		public String getPluNo()
		{
			return pluNo;
		}

		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}

		public String getsUnit()
		{
			return sUnit;
		}

		public void setsUnit(String sUnit)
		{
			this.sUnit = sUnit;
		}

		public String getFeatureNo()
		{
			return featureNo;
		}

		public void setFeatureNo(String featureNo)
		{
			this.featureNo = featureNo;
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

		public double getOldPrice()
		{
			return oldPrice;
		}

		public void setOldPrice(double oldPrice)
		{
			this.oldPrice = oldPrice;
		}

		public double getPrice()
		{
			return price;
		}

		public void setPrice(double price)
		{
			this.price = price;
		}

		public double getQty()
		{
			return qty;
		}

		public void setQty(double qty)
		{
			this.qty = qty;
		}

		public double getAmt()
		{
			return amt;
		}

		public void setAmt(double amt)
		{
			this.amt = amt;
		}

		public String getGift()
		{
			return gift;
		}

		public void setGift(String gift)
		{
			this.gift = gift;
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
		
		
		
		
	}
	
	public class levelPay
	{
		private String item;
		private String payCode;
		private String payCodeerp;		
		private String payName;
		private String cardNo;
		private String ctType;
		
		/**
		 * 支付订单号
		 */
		private String paySernum;
		private String serialNo;
		private String refNo;
		private String teriminalNo;
		private double descore;
		private double pay;
		private double extra;
		private double changed;
		
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

		public String getPaySernum()
		{
			return paySernum;
		}

		public void setPaySernum(String paySernum)
		{
			this.paySernum = paySernum;
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
		
		
	}
}
