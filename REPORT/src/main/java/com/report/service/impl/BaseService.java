package com.report.service.impl;

import com.report.util.TokenUtil;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务基类 - 提供通用的 EID 解析等方法
 */
public abstract class BaseService {

    /**
     * 从请求参数和 Token 中解析 EID
     * @param jdbcTemplate 数据库连接
     * @param params 请求参数
     * @return EID
     */
    protected String resolveEid(JdbcTemplate jdbcTemplate, Map<String, Object> params) {
        String token = "";
        
        // 从 sign 中获取 token
        if (params != null && params.get("sign") instanceof Map) {
            Map<?, ?> signMap = (Map<?, ?>) params.get("sign");
            if (signMap.get("token") != null) {
                token = signMap.get("token").toString();
            }
        }
        
        // 从 token 解析 EID
        return TokenUtil.getEidFromToken(jdbcTemplate, token);
    }
    
    /**
     * 从请求参数中获取字符串参数
     * @param params 请求参数
     * @param key 参数名
     * @param defaultValue 默认值
     * @return 参数值
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
     * @param params 请求参数
     * @param key 参数名
     * @return 参数值
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
            // 执行 COUNT 查询
            int totalRecords = jdbcTemplate.queryForObject(countSql, Integer.class, sqlParams);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            // 页码校正
            if (pageNumber > totalPages) {
                pageNumber = totalPages > 0 ? totalPages : 1;
            }
            
            // 计算分页范围
            int startRow = (pageNumber - 1) * pageSize + 1;
            int endRow = pageNumber * pageSize;
            
            // 构建分页 SQL（Oracle 语法）
            String paginatedSql = "select * from ( SELECT rownum as NUM, ALLTABLE.* FROM ( " + sql + " ) ALLTABLE ) where NUM >= " + startRow + " AND NUM <= " + endRow;
            
            // 执行分页查询
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(paginatedSql, sqlParams);
            
            result.put("list", resultList);
            result.put("totalRecords", totalRecords);
            result.put("totalPages", totalPages);
            result.put("pageNumber", pageNumber);
            result.put("pageSize", pageSize);
        } else {
            // 不分页，返回全部
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
