package com.report.controller;

import com.report.dto.ServiceRequest;
import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统一服务入口 Controller
 * 提供 RESTful API 接口
 */
@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private ReportService reportService;

    /**
     * 统一服务入口
     * POST /api/service
     * 
     * @param request 服务请求 {"serviceId":"xxx","request":{},"sign":{}}
     * @return 服务响应
     */
    @PostMapping("/service")
    public ServiceResponse<?> service(@RequestBody ServiceRequest request) {
        if (request.getServiceId() == null || request.getServiceId().isEmpty()) {
            return ServiceResponse.error("400", "serviceId 不能为空");
        }
        return reportService.execute(request);
    }

    /**
     * 直接调用指定服务
     * POST /api/service/{serviceId}
     * 
     * @param serviceId 服务 ID
     * @param params 请求参数
     * @return 服务响应
     */
    @PostMapping("/service/{serviceId}")
    public ServiceResponse<?> serviceById(
            @PathVariable String serviceId,
            @RequestBody Object params) {
        return reportService.executeByServiceId(serviceId, params, null);
    }

    /**
     * GET 方式调用服务 (简单查询)
     * GET /api/service/{serviceId}?param1=value1&param2=value2
     */
    @GetMapping("/service/{serviceId}")
    public ServiceResponse<?> serviceByIdGet(
            @PathVariable String serviceId,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String withAccount) {
        
        ServiceRequest request = new ServiceRequest();
        request.setServiceId(serviceId);
        
        if (mobile != null || withAccount != null) {
            request.setRequest(new QueryMemberRequest(mobile, withAccount));
        }
        
        return reportService.execute(request);
    }

    /**
     * 健康检查
     * GET /api/health
     */
    @GetMapping("/health")
    public ServiceResponse<?> health() {
        return ServiceResponse.success("OK", "服务运行正常");
    }

    /**
     * 内部请求参数类 (用于 GET 请求)
     */
    public static class QueryMemberRequest {
        private String mobile;
        private String withAccount;

        public QueryMemberRequest() {}

        public QueryMemberRequest(String mobile, String withAccount) {
            this.mobile = mobile;
            this.withAccount = withAccount;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getWithAccount() {
            return withAccount;
        }

        public void setWithAccount(String withAccount) {
            this.withAccount = withAccount;
        }
    }
}
