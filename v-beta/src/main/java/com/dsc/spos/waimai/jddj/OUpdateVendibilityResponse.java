package com.dsc.spos.waimai.jddj;

public class OUpdateVendibilityResponse {

   private long skuId;
   private String stationNo;
   private String updatePin;
   private int vendibility;
   private int code;
   private String reason;
   public void setSkuId(long skuId) {
        this.skuId = skuId;
    }
    public long getSkuId() {
        return skuId;
    }

   public void setStationNo(String stationNo) {
        this.stationNo = stationNo;
    }
    public String getStationNo() {
        return stationNo;
    }

   public void setUpdatePin(String updatePin) {
        this.updatePin = updatePin;
    }
    public String getUpdatePin() {
        return updatePin;
    }

   public void setVendibility(int vendibility) {
        this.vendibility = vendibility;
    }
    public int getVendibility() {
        return vendibility;
    }

   public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

   public void setReason(String reason) {
        this.reason = reason;
    }
    public String getReason() {
        return reason;
    }

}
