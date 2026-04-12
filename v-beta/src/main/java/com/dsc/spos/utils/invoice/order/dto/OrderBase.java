package com.dsc.spos.utils.invoice.order.dto;

/**
 * 订单基本信息
 */
public class OrderBase {
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 子订单编号
     */
    private String subOrderNo;

    /**
     * 销货方纳税人识别号
     */
    private String taxpayerCode;

    /**
     * 发票类型
     */
    private String invoiceType;

    public OrderBase() {
    }

    public OrderBase(String orderNo, String taxpayerCode) {
        this.orderNo = orderNo;
        this.taxpayerCode = taxpayerCode;
    }

    public OrderBase(String orderNo, String subOrderNo, String taxpayerCode) {
        this.orderNo = orderNo;
        this.subOrderNo = subOrderNo;
        this.taxpayerCode = taxpayerCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSubOrderNo() {
        return subOrderNo;
    }

    public void setSubOrderNo(String subOrderNo) {
        this.subOrderNo = subOrderNo;
    }

    public String getTaxpayerCode() {
        return taxpayerCode;
    }

    public void setTaxpayerCode(String taxpayerCode) {
        this.taxpayerCode = taxpayerCode;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }
}
