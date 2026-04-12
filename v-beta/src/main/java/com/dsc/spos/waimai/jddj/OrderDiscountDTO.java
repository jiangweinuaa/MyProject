package com.dsc.spos.waimai.jddj;

public class OrderDiscountDTO {

   private long orderId;
   private long adjustId;
   private String skuIds;
   private int discountType;
   private int discountDetailType;
   private String discountCode;
   private int discountPrice;
   private int venderPayMoney;
   private int platPayMoney;
   public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    public long getOrderId() {
        return orderId;
    }

   public void setAdjustId(long adjustId) {
        this.adjustId = adjustId;
    }
    public long getAdjustId() {
        return adjustId;
    }

   public void setSkuIds(String skuIds) {
        this.skuIds = skuIds;
    }
    public String getSkuIds() {
        return skuIds;
    }

   public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }
    public int getDiscountType() {
        return discountType;
    }

   public void setDiscountDetailType(int discountDetailType) {
        this.discountDetailType = discountDetailType;
    }
    public int getDiscountDetailType() {
        return discountDetailType;
    }

   public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
    public String getDiscountCode() {
        return discountCode;
    }

   public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }
    public int getDiscountPrice() {
        return discountPrice;
    }

   public void setVenderPayMoney(int venderPayMoney) {
        this.venderPayMoney = venderPayMoney;
    }
    public int getVenderPayMoney() {
        return venderPayMoney;
    }

   public void setPlatPayMoney(int platPayMoney) {
        this.platPayMoney = platPayMoney;
    }
    public int getPlatPayMoney() {
        return platPayMoney;
    }

}