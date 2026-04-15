package com.report.controller;

import com.report.config.MerchantDataSourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源状态检查接口
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class DataSourceStatusController {

    @Autowired
    private MerchantDataSourceManager dataSourceManager;

    /**
     * 获取数据源状态
     */
    @GetMapping("/datasource/status")
    public Map<String, Object> getDataSourceStatus() {
        Map<String, Object> result = new HashMap<>();
        
        // 商家数据源状态
        Map<String, Object> merchantStatus = dataSourceManager.getDataSourceStatus();
        result.put("merchant", merchantStatus);
        
        // 总体状态
        result.put("timestamp", System.currentTimeMillis());
        result.put("success", true);
        
        return result;
    }

    /**
     * 刷新商家数据源
     */
    @PostMapping("/datasource/refresh")
    public Map<String, Object> refreshDataSource() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            dataSourceManager.refreshMerchantDataSource();
            result.put("success", true);
            result.put("message", "商家数据源已刷新");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "刷新失败：" + e.getMessage());
        }
        
        return result;
    }
}
