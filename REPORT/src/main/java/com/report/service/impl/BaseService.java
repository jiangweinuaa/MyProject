package com.report.service.impl;

import com.report.config.MerchantDataSourceManager;
import com.report.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务基类 - 提供通用的数据源获取和 EID 解析等方法
 * 
 * 数据源使用规范：
 * - platformJdbcTemplate：AI 配置、Token 验证、商家配置读取
 * - resolveBusinessJdbcTemplate()：业务数据查询（优先商家库，降级平台库）
 */
public abstract class BaseService {

    /**
     * 平台 JdbcTemplate（注入默认数据源）
     * 用于：AI 配置、Token 验证、商家配置读取
     */
    @Autowired
    protected JdbcTemplate platformJdbcTemplate;

    /**
     * 商家数据源管理器
     */
    @Autowired
    protected MerchantDataSourceManager dataSourceManager;

    /**
     * 获取商家 JdbcTemplate
     * 
     * 规则：
     * 1. 如果 MERCHANT_DB_CONFIG 有启用配置（STATUS=100），返回商家库
     * 2. 否则返回 null（调用方需降级到平台库）
     * 
     * @return 商家 JdbcTemplate 或 null
     */
    protected JdbcTemplate getMerchantJdbcTemplate() {
        return dataSourceManager.getMerchantJdbcTemplate();
    }

    /**
     * 获取业务查询用的 JdbcTemplate
     * 
     * 规则：
     * 1. 优先使用商家库（如果有配置）
     * 2. 商家库不可用时，降级到平台库
     * 
     * @return 业务查询 JdbcTemplate
     */
    protected JdbcTemplate resolveBusinessJdbcTemplate() {
        JdbcTemplate merchantJdbc = getMerchantJdbcTemplate();
        if (merchantJdbc != null) {
            return merchantJdbc;
        }
        // 降级到平台库
        return platformJdbcTemplate;
    }

    /**
     * 从请求参数和 Token 中解析 EID（使用平台库）
     * 
     * @param params 请求参数
     * @return EID
     */
    protected String resolveEid(Map<String, Object> params) {
        String token = "";
        
        if (params != null && params.get("sign") instanceof Map) {
            Map<?, ?> signMap = (Map<?, ?>) params.get("sign");
            if (signMap.get("token") != null) {
                token = signMap.get("token").toString();
            }
        }
        
        return TokenUtil.getEidFromToken(platformJdbcTemplate, token);
    }
    
    /**
     * 从请求参数中获取字符串参数
     */
    protected String getStringParam(Map<String, Object> params, String key, String defaultValue) {
        if (params == null || !params.containsKey(key)) {
            return defaultValue;
        }
        Object value = params.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * 从请求参数中获取字符串参数（无默认值）
     */
    protected String getStringParam(Map<String, Object> params, String key) {
        if (params == null || !params.containsKey(key)) {
            return "";
        }
        Object value = params.get(key);
        return value != null ? value.toString().trim() : "";
    }
    
    /**
     * 分页查询辅助方法 - 统一的分页逻辑
     * 
     * @param jdbcTemplate 数据库连接
     * @param sql 主查询 SQL
     * @param countSql 计数 SQL
     * @param pageNumber 页码（从 1 开始）
     * @param pageSize 每页大小
     * @param sqlParams SQL 参数
     * @return 分页结果 {list, totalRecords, totalPages, pageNumber, pageSize}
     */
    protected Map<String, Object> buildPaginatedResult(
            JdbcTemplate jdbcTemplate,
            String sql, 
            String countSql,
            Integer pageNumber, 
            Integer pageSize,
            Object... sqlParams) {
        
        Map<String, Object> result = new HashMap<>();
        
        boolean needPagination = pageNumber != null && pageSize != null && pageNumber > 0 && pageSize > 0;
        
        if (needPagination) {
            int totalRecords = jdbcTemplate.queryForObject(countSql, Integer.class, sqlParams);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            if (pageNumber > totalPages) {
                pageNumber = totalPages > 0 ? totalPages : 1;
            }
            
            int startRow = (pageNumber - 1) * pageSize + 1;
            int endRow = pageNumber * pageSize;
            
            String paginatedSql = "select * from ( SELECT rownum as NUM, ALLTABLE.* FROM ( " + sql + " ) ALLTABLE ) where NUM >= " + startRow + " AND NUM <= " + endRow;
            
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(paginatedSql, sqlParams);
            
            result.put("list", resultList);
            result.put("totalRecords", totalRecords);
            result.put("totalPages", totalPages);
            result.put("pageNumber", pageNumber);
            result.put("pageSize", pageSize);
        } else {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, sqlParams);
            
            result.put("list", resultList);
            result.put("totalRecords", resultList.size());
            result.put("totalPages", resultList.size() > 0 ? 1 : 0);
            result.put("pageNumber", 1);
            result.put("pageSize", 0);
        }
        
        return result;
    }
}
