package com.report.dto;

/**
 * 门店销售预估查询请求
 */
public class ShopSaleForecastQueryRequest {
    
    private String eid;              // 企业 ID
    private String shopId;           // 门店 ID
    private String startDate;        // 开始日期
    private String endDate;          // 结束日期
    
    public String getEid() {
        return eid;
    }
    
    public void setEid(String eid) {
        this.eid = eid;
    }
    
    public String getShopId() {
        return shopId;
    }
    
    public void setShopId(String shopId) {
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
}
