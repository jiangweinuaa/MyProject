package com.dsc.spos.waimai.jddj;
import java.util.List;

public class OrderAdjustRespDTO {

   private long orderId;
   private int adjustCount;
   private String ProduceStationNo;
   private long orderTotalMoney;
   private long orderDiscountMoney;
   private long orderFreightMoney;
   private long orderBuyerPayableMoney;
   private List<OrderAdjustResEntity> oaList;
   public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    public long getOrderId() {
        return orderId;
    }

   public void setAdjustCount(int adjustCount) {
        this.adjustCount = adjustCount;
    }
    public int getAdjustCount() {
        return adjustCount;
    }

   public void setProduceStationNo(String ProduceStationNo) {
        this.ProduceStationNo = ProduceStationNo;
    }
    public String getProduceStationNo() {
        return ProduceStationNo;
    }

   public void setOrderTotalMoney(long orderTotalMoney) {
        this.orderTotalMoney = orderTotalMoney;
    }
    public long getOrderTotalMoney() {
        return orderTotalMoney;
    }

   public void setOrderDiscountMoney(long orderDiscountMoney) {
        this.orderDiscountMoney = orderDiscountMoney;
    }
    public long getOrderDiscountMoney() {
        return orderDiscountMoney;
    }

   public void setOrderFreightMoney(long orderFreightMoney) {
        this.orderFreightMoney = orderFreightMoney;
    }
    public long getOrderFreightMoney() {
        return orderFreightMoney;
    }

   public void setOrderBuyerPayableMoney(long orderBuyerPayableMoney) {
        this.orderBuyerPayableMoney = orderBuyerPayableMoney;
    }
    public long getOrderBuyerPayableMoney() {
        return orderBuyerPayableMoney;
    }

   public void setOaList(List<OrderAdjustResEntity> oaList) {
        this.oaList = oaList;
    }
    public List<OrderAdjustResEntity> getOaList() {
        return oaList;
    }

}
