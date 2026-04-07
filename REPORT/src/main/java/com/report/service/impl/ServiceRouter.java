package com.report.service.impl;

import com.report.dto.ServiceRequest;
import com.report.dto.ServiceResponse;
import com.report.service.impl.sales.*;
import com.report.service.impl.stock.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 服务路由类 - 根据 serviceId 路由到对应的服务实现
 */
@Service("serviceRouter")
public class ServiceRouter {

    @Autowired(required = false)
    private DaySaleQueryServiceImpl daySaleQueryService;

    @Autowired(required = false)
    private DcpSaleQtyServiceImpl dcpSaleQtyService;

    @Autowired(required = false)
    private DayShopGoodsQueryServiceImpl dayShopGoodsQueryService;

    @Autowired(required = false)
    private DayChannelQueryServiceImpl dayChannelQueryService;

    @Autowired(required = false)
    private DayShopChannelQueryServiceImpl dayShopChannelQueryService;

    @Autowired(required = false)
    private StockSumQueryServiceImpl stockSumQueryService;

    @Autowired(required = false)
    private StockQueryServiceImpl stockQueryService;

    /**
     * 根据 serviceId 路由到对应的服务
     */
    public ServiceResponse<?> route(ServiceRequest request) {
        String serviceId = request.getServiceId();
        Object params = request.getRequest();
        Integer pageNumber = request.getPageNumber();
        Integer pageSize = request.getPageSize();
        
        Map<String, Object> paramsMap = null;
        if (params instanceof Map) {
            paramsMap = (Map<String, Object>) params;
        }

        switch (serviceId) {
            case "DaySaleQuery":
                return daySaleQueryService != null ? daySaleQueryService.execute(paramsMap, pageNumber, pageSize) : ServiceResponse.error("500", "服务未初始化");
            case "DcpSaleQty":
                return dcpSaleQtyService != null ? dcpSaleQtyService.execute(paramsMap, pageNumber, pageSize) : ServiceResponse.error("500", "服务未初始化");
            case "DayShopGoodsQuery":
                return dayShopGoodsQueryService != null ? dayShopGoodsQueryService.execute(paramsMap, pageNumber, pageSize) : ServiceResponse.error("500", "服务未初始化");
            case "DayChannelQuery":
                return dayChannelQueryService != null ? dayChannelQueryService.execute(paramsMap, pageNumber, pageSize) : ServiceResponse.error("500", "服务未初始化");
            case "DayShopChannelQuery":
                return dayShopChannelQueryService != null ? dayShopChannelQueryService.execute(paramsMap, pageNumber, pageSize) : ServiceResponse.error("500", "服务未初始化");
            case "StockSumQuery":
                return stockSumQueryService != null ? stockSumQueryService.execute(paramsMap, pageNumber, pageSize) : ServiceResponse.error("500", "服务未初始化");
            case "StockQuery":
                return stockQueryService != null ? stockQueryService.execute(paramsMap, pageNumber, pageSize) : ServiceResponse.error("500", "服务未初始化");
            default:
                return ServiceResponse.error("999", "未知服务 ID: " + serviceId);
        }
    }
}
