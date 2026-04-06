package com.report.dto;

import java.io.Serializable;

/**
 * 每日门店商品查询请求参数
 */
public class DayShopGoodsQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开始日期 (格式：YYYYMMDD)
     */
    private String startDate;

    /**
     * 截止日期 (格式：YYYYMMDD)
     */
    private String endDate;

    /**
     * 门店 ID
     */
    private String shopId;

    public DayShopGoodsQueryRequest() {
    }

    public DayShopGoodsQueryRequest(String startDate, String endDate, String shopId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.shopId = shopId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "DayShopGoodsQueryRequest{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", shopId='" + shopId + '\'' +
                '}';
    }
}
