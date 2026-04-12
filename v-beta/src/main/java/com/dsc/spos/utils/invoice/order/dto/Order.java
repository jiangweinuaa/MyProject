package com.dsc.spos.utils.invoice.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单详细信息
 */
public class Order extends OrderBase {
    /**
     * 用户扫码key
     */
    private String scanCodeKey;

    /**
     * 店铺
     */
    private String shopName;

    /**
     * 店铺编号
     */
    private String shopCode;

    /**
     * 订单时间（yyyy-MM-dd HH:mm:ss）
     */
    private String orderTime;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String contactTel;

    /**
     * 邮箱
     */
    private String contactMail;

    /**
     * 配送地址
     */
    private String shippingAddress;

    /**
     * 销货方名称
     */
    private String taxpayerName;

    /**
     * 销货方地址
     */
    private String taxpayerAddress;

    /**
     * 销货方电话
     */
    private String taxpayerTel;

    /**
     * 销货方开户银行
     */
    private String taxpayerBankName;

    /**
     * 销货方银行账号
     */
    private String taxpayerBankAccount;

    /**
     * 购货方名称，即发票抬头
     */
    private String customerName;

    /**
     * 购货方纳税人识别号
     */
    private String customerCode;

    /**
     * 购货方地址
     */
    private String customerAddress;

    /**
     * 购货方电话
     */
    private String customerTel;

    /**
     * 购货方开户银行
     */
    private String customerBankName;

    /**
     * 购货方银行账号
     */
    private String customerBankAccount;

    /**
     * 是否自动开票
     */
    private boolean autoBilling;

    /**
     * 开票人
     */
    private String drawer;

    /**
     * 收款人
     */
    private String payee;

    /**
     * 复核人
     */
    private String reviewer;

    /**
     * 开票金额
     */
    private BigDecimal totalAmount;

    /**
     * 发票备注
     */
    private String remark;

    /**
     * 冲红原因
     */
    private String reason;

    /**
     * 订单明细
     */
    private List<OrderItem> orderItems;

    /**
     * 扩展参数
     */
    private Map<String, String> extendedParams;

    /**
     * 动态参数
     */
    private Map<String, String> dynamicParams;

    public String getScanCodeKey() {
        return scanCodeKey;
    }

    public void setScanCodeKey(String scanCodeKey) {
        this.scanCodeKey = scanCodeKey;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public String getContactMail() {
        return contactMail;
    }

    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getTaxpayerName() {
        return taxpayerName;
    }

    public void setTaxpayerName(String taxpayerName) {
        this.taxpayerName = taxpayerName;
    }

    public String getTaxpayerAddress() {
        return taxpayerAddress;
    }

    public void setTaxpayerAddress(String taxpayerAddress) {
        this.taxpayerAddress = taxpayerAddress;
    }

    public String getTaxpayerTel() {
        return taxpayerTel;
    }

    public void setTaxpayerTel(String taxpayerTel) {
        this.taxpayerTel = taxpayerTel;
    }

    public String getTaxpayerBankName() {
        return taxpayerBankName;
    }

    public void setTaxpayerBankName(String taxpayerBankName) {
        this.taxpayerBankName = taxpayerBankName;
    }

    public String getTaxpayerBankAccount() {
        return taxpayerBankAccount;
    }

    public void setTaxpayerBankAccount(String taxpayerBankAccount) {
        this.taxpayerBankAccount = taxpayerBankAccount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerTel() {
        return customerTel;
    }

    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }

    public String getCustomerBankName() {
        return customerBankName;
    }

    public void setCustomerBankName(String customerBankName) {
        this.customerBankName = customerBankName;
    }

    public String getCustomerBankAccount() {
        return customerBankAccount;
    }

    public void setCustomerBankAccount(String customerBankAccount) {
        this.customerBankAccount = customerBankAccount;
    }

    public boolean isAutoBilling() {
        return autoBilling;
    }

    public void setAutoBilling(boolean autoBilling) {
        this.autoBilling = autoBilling;
    }

    public String getDrawer() {
        return drawer;
    }

    public void setDrawer(String drawer) {
        this.drawer = drawer;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Map<String, String> getExtendedParams() {
        return extendedParams;
    }

    public void setExtendedParams(Map<String, String> extendedParams) {
        this.extendedParams = extendedParams;
    }

    public Map<String, String> getDynamicParams() {
        return dynamicParams;
    }

    public void setDynamicParams(Map<String, String> dynamicParams) {
        this.dynamicParams = dynamicParams;
    }
}
