package com.report.dto;

import java.io.Serializable;

/**
 * 每日销售查询请求参数
 */
public class DaySaleQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开始日期 (格式：YYYYMMDD)
     */
    private String startDate;

    /**
     * 截止日期 (格式：YYYYMMDD)
     */
    private String endDate;

    public DaySaleQueryRequest() {
    }

    public DaySaleQueryRequest(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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

    @Override
    public String toString() {
        return "DaySaleQueryRequest{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
