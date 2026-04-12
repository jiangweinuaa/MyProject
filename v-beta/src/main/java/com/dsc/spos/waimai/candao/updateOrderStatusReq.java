package com.dsc.spos.waimai.candao;

public class updateOrderStatusReq {

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
     * 订单状态，1：待支付；7：商家待接单；10：商家已接单；12：备餐中；14：配送中；16：就餐中；18：待取餐；20：取餐超时；100：订单完成；-1：订单取消；21：备餐完成
     */
    private int status;
    /**
     * 取消订单原因分类 参见枚举类cancelReason，取消必传
     * 101：超时未接单；102地址无法配送；103：餐品售罄；104：重复订单；105：联系不上用户；106：餐厅繁忙；107：门店错送漏送；108：餐厅休息中； 109：餐品变质；201：用户取消；202：用户测试；203：支付超时；204：用户下错单；301：配送延迟；302：配送异常；401：系统异常；402：调试或测试单；403：平台取消；501：其它原因；（选其他时需要传描述）
     */
    private int cancelReason = 201;
    /**
     * 取消订单原因
     */
    private String cancelReasonName;
    /**
     * 取消原因备注，原因分类为其他时必传
     */
    private String cancelNote;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(int cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelReasonName() {
        return cancelReasonName;
    }

    public void setCancelReasonName(String cancelReasonName) {
        this.cancelReasonName = cancelReasonName;
    }

    public String getCancelNote() {
        return cancelNote;
    }

    public void setCancelNote(String cancelNote) {
        this.cancelNote = cancelNote;
    }
}
