package com.report.dto;

import java.io.Serializable;

/**
 * 每日销售数据响应
 */
public class DaySaleData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 销售日期
     */
    private String saleDate;

    /**
     * 销售金额
     */
    private Double amount;

    public DaySaleData() {
    }

    public DaySaleData(String saleDate, Double amount) {
        this.saleDate = saleDate;
        this.amount = amount;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "DaySaleData{" +
                "saleDate='" + saleDate + '\'' +
                ", amount=" + amount +
                '}';
    }
}
