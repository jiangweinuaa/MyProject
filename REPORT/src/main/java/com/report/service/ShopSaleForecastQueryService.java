package com.report.service;

import com.report.dto.ShopSaleForecastQueryRequest;
import com.report.dto.ServiceResponse;

/**
 * 门店销售预估查询服务接口
 */
public interface ShopSaleForecastQueryService {
    
    /**
     * 查询门店销售预估数据（原始数据）
     * @param request 查询请求
     * @return 服务响应
     */
    ServiceResponse queryShopSaleForecast(ShopSaleForecastQueryRequest request);
    
    /**
     * 查询销售预估准确性分析完整数据（包含所有前端需要的数据）
     * @param request 查询请求
     * @return 服务响应
     */
    ServiceResponse queryAccuracyAnalysisFull(ShopSaleForecastQueryRequest request);
}
