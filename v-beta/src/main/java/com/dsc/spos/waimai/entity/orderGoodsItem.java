package com.dsc.spos.waimai.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class orderGoodsItem implements Serializable
{

    private String item;

    private String pluBarcode="";

    private String pluNo="";

    private String pluName="";

    private String featureNo;

    private String featureName;

    private String sUnit="";

    private String sUnitName="";

    private String specName="";

    private String attrName="";

	private String specName_origin="";

	private String attrName_origin="";
    
    private String skuId;

    private double price;

    private double qty;

    private String goodsGroup;

    private double disc;
    
    /**
     * 商家实收分摊的商品折扣合计（嘉华对账逻辑）
     */
    private double disc_merReceive;
    
    /**
     * 顾客实付分摊的商品折扣合计
     */
    private double disc_custPayReal;

    private double boxNum;

    private double boxPrice;

    private double amt;
    
    /**
     * 商家实收对应商品金额
     */
    private double amt_merReceive;
    
    /**
     *  顾客实付对应商品金额
     */
    private double amt_custPayReal;

    private String isMemo="";

    private double shopQty;

    private double pickQty;
    
    private double rQty;

    ////1、正常商品 2、套餐主商品 3、套餐子商品
    private String packageType="1";

    private String packageMitem;

    private String toppingType;

    private String toppingMitem;

    private String couponType;

    private String couponCode;

    private String gift = "0";

    private String giftSourceSerialNo;

    private String goodsUrl;

    private String sellerNo;

    private String sellerName;

    private String accNo;

    private String counterNo;

    private double oldPrice;

    private double oldAmt;

    private String giftReason;

    private String sTime;

    private String oItem;

    //private double uAmt;

    private String taxCode;

    private String taxType;

    //private double zeroTaxAmt;

    //private double freeTaxAmt;

    //private double taxAbleAmt;

    //private double tax;

    //private double untaxPayAmt;

    //private double untaxPayTax;

    //private double taxRate;

    private String inclTax;

    private String invSplitType;

    private String invStartNo;

    private String invEndNo;

    //private double invTotAmt;

    //private double invAmt;

    //private double invUamt;

    //private double invTax;

    //private double couponInvAmt;

    //private double couponInvTax;

    //private double shareAmt;

    //private double accAmt;

    //private double accTax;

    //private double extraCpAmt;

    //private double extraCpTax;
    
    private String warehouse;

    private String warehouseName;
    
    private String virtual="N";

	/**
	 * 预配状态 0：待预配   1：预配完成
	 */
	private String preparationStatus;


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

	/**
	 * 生产门店需要生产数量
	 */
	private double machShopQty;

    /**
     * 口味描述
     */
    private String flavorStuffDetail;
    /**
     * 套餐子商品是否参与折扣分摊(0-不参与，1-参与)
     */
    private String split;

	private List<orderGoodsItemMessage> messages;

    private List<orderGoodsItemAgio> agioInfo;

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

	public String getSkuId()
	{
		return skuId;
	}

	public void setSkuId(String skuId)
	{
		this.skuId = skuId;
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

	public String getGoodsGroup()
	{
		return goodsGroup;
	}

	public void setGoodsGroup(String goodsGroup)
	{
		this.goodsGroup = goodsGroup;
	}

	public double getDisc()
	{
		return disc;
	}

	public void setDisc(double disc)
	{
		this.disc = disc;
	}

	public double getBoxNum()
	{
		return boxNum;
	}

	public void setBoxNum(double boxNum)
	{
		this.boxNum = boxNum;
	}

	public double getBoxPrice()
	{
		return boxPrice;
	}

	public void setBoxPrice(double boxPrice)
	{
		this.boxPrice = boxPrice;
	}

	public double getAmt()
	{
		return amt;
	}

	public void setAmt(double amt)
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

	public double getShopQty()
	{
		return shopQty;
	}

	public void setShopQty(double shopQty)
	{
		this.shopQty = shopQty;
	}

	public double getPickQty()
	{
		return pickQty;
	}

	public void setPickQty(double pickQty)
	{
		this.pickQty = pickQty;
	}

	public double getrQty()
	{
		return rQty;
	}

	public void setrQty(double rQty)
	{
		this.rQty = rQty;
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

	public double getOldPrice()
	{
		return oldPrice;
	}

	public void setOldPrice(double oldPrice)
	{
		this.oldPrice = oldPrice;
	}

	public double getOldAmt()
	{
		return oldAmt;
	}

	public void setOldAmt(double oldAmt)
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

	public String getVirtual()
	{
		return virtual;
	}

	public void setVirtual(String virtual)
	{
		this.virtual = virtual;
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

	public double getDisc_merReceive()
	{
		return disc_merReceive;
	}

	public void setDisc_merReceive(double disc_merReceive)
	{
		this.disc_merReceive = disc_merReceive;
	}

	public double getDisc_custPayReal()
	{
		return disc_custPayReal;
	}

	public void setDisc_custPayReal(double disc_custPayReal)
	{
		this.disc_custPayReal = disc_custPayReal;
	}

	public double getAmt_merReceive()
	{
		return amt_merReceive;
	}

	public void setAmt_merReceive(double amt_merReceive)
	{
		this.amt_merReceive = amt_merReceive;
	}

	public double getAmt_custPayReal()
	{
		return amt_custPayReal;
	}

	public void setAmt_custPayReal(double amt_custPayReal)
	{
		this.amt_custPayReal = amt_custPayReal;
	}

	public double getMachShopQty() {
		return machShopQty;
	}

	public void setMachShopQty(double machShopQty) {
		this.machShopQty = machShopQty;
	}

    public String getFlavorStuffDetail() {
        return flavorStuffDetail;
    }

    public void setFlavorStuffDetail(String flavorStuffDetail) {
        this.flavorStuffDetail = flavorStuffDetail;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public List<orderGoodsItemMessage> getMessages()
	{
		return messages;
	}

	public void setMessages(List<orderGoodsItemMessage> messages)
	{
		this.messages = messages;
	}

	public List<orderGoodsItemAgio> getAgioInfo()
	{
		return agioInfo;
	}

	public void setAgioInfo(List<orderGoodsItemAgio> agioInfo)
	{
		this.agioInfo = agioInfo;
	}

    

}
