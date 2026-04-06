package com.report.service;

import com.report.dto.ServiceRequest;
import com.report.dto.ServiceResponse;

/**
 * 报表服务接口
 */
public interface ReportService {

    /**
     * 执行服务
     * @param request 服务请求
     * @return 服务响应
     */
    ServiceResponse<?> execute(ServiceRequest request);

    /**
     * 根据服务 ID 执行对应服务
     * @param serviceId 服务 ID
     * @param params 请求参数
     * @param key 密钥
     * @return 服务响应
     */
    ServiceResponse<?> executeByServiceId(String serviceId, Object params, String key);
}
