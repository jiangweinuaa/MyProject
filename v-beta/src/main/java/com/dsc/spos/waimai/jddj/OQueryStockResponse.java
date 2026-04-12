package com.dsc.spos.waimai.jddj;

public class OQueryStockResponse {

   private String skuId;
   private String stationNo;
   private int usableQty;
   private int lockQty;
   private int orderQty;
   private int vendibility;
   public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
    public String getSkuId() {
        return skuId;
    }

   public void setStationNo(String stationNo) {
        this.stationNo = stationNo;
    }
    public String getStationNo() {
        return stationNo;
    }

   public void setUsableQty(int usableQty) {
        this.usableQty = usableQty;
    }
    public int getUsableQty() {
        return usableQty;
    }

   public void setLockQty(int lockQty) {
        this.lockQty = lockQty;
    }
    public int getLockQty() {
        return lockQty;
    }

   public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }
    public int getOrderQty() {
        return orderQty;
    }

   public void setVendibility(int vendibility) {
        this.vendibility = vendibility;
    }
    public int getVendibility() {
        return vendibility;
    }

}