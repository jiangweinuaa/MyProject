package com.dsc.spos.waimai.candao;

public class getRiderPositionReq {
    private String orderId;
    private String extOrderId;
    private String extOrderNo;
    /**
     * 订单来源，参见fromType枚举，建议传此字段便于区分订单渠道
     */
    private String fromType = "dgw" ;
    /**
     * 供应商门店id
     */
    private String subStoreId;

    /**
     * 配送系统编码
     */
    private String deliverySysType;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getExtOrderId() {
        return extOrderId;
    }

    public void setExtOrderId(String extOrderId) {
        this.extOrderId = extOrderId;
    }

    public String getExtOrderNo() {
        return extOrderNo;
    }

    public void setExtOrderNo(String extOrderNo) {
        this.extOrderNo = extOrderNo;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getSubStoreId() {
        return subStoreId;
    }

    public void setSubStoreId(String subStoreId) {
        this.subStoreId = subStoreId;
    }

    public String getDeliverySysType() {
        return deliverySysType;
    }

    public void setDeliverySysType(String deliverySysType) {
        this.deliverySysType = deliverySysType;
    }
}
