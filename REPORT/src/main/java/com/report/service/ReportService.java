package com.report.service;

import com.report.dto.ServiceResponse;
import java.util.Map;

/**
 * 报表服务接口
 */
public interface ReportService {

    /**
     * 执行服务
     * @param params 请求参数
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @return 服务响应
     */
    ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize);
}
