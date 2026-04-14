package com.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 自然语言查询服务（AI 版）
 */
@Service
public class NLQueryService {
    
    @Autowired
    private AISQLService aiSQLService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 自然语言查询
     * @param question 用户问题
     * @return 查询结果
     */
    public Map<String, Object> query(String question) {
        return query(question, false);
    }
    
    /**
     * 自然语言查询（内部方法，支持重试）
     * @param question 用户问题
     * @param isRetry 是否重试
     * @return 查询结果
     */
    private Map<String, Object> query(String question, boolean isRetry) {
        if (question == null || question.trim().isEmpty()) {
            return error("问题不能为空");
        }
        
        try {
            // 1. 生成 SQL
            String sql = aiSQLService.generateSQL(question);
            
            // 2. 验证 SQL
            if (!aiSQLService.validateSQL(sql)) {
                return error("生成的 SQL 不安全，已拒绝执行：" + sql);
            }
            
            // 3. 执行 SQL
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            
            // 4. 返回结果
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sql", sql);
            response.put("data", result);
            response.put("question", question);
            response.put("rowCount", result.size());
            
            return response;
            
        } catch (DataAccessException e) {
            // SQL 语法错误，检查是否需要重试
            if (!isRetry) {
                String errorMsg = e.getMessage();
                // 检查是否是 Oracle 语法错误
                if (errorMsg != null && (errorMsg.contains("ORA-009") || errorMsg.contains("SQLSyntaxErrorException"))) {
                    System.err.println("⚠️ SQL 语法错误，尝试重新生成 Oracle 11g 兼容的 SQL");
                    System.err.println("错误信息：" + errorMsg);
                    
                    // 重新生成 SQL（加上 Oracle 11g 要求）
                    String correctedSQL = aiSQLService.regenerateSQL(question, errorMsg);
                    
                    System.out.println("✅ 修正后的 SQL: " + correctedSQL);
                    
                    // 验证修正后的 SQL
                    if (!aiSQLService.validateSQL(correctedSQL)) {
                        return error("修正后的 SQL 不安全，已拒绝执行：" + correctedSQL);
                    }
                    
                    // 重试执行
                    try {
                        List<Map<String, Object>> result = jdbcTemplate.queryForList(correctedSQL);
                        
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("sql", correctedSQL);
                        response.put("data", result);
                        response.put("question", question);
                        response.put("rowCount", result.size());
                        response.put("retry", true);
                        response.put("originalError", errorMsg);
                        
                        return response;
                    } catch (Exception retryEx) {
                        return error("SQL 语法修正后执行仍失败：" + retryEx.getMessage());
                    }
                }
            }
            
            return error("SQL 执行错误：" + e.getMessage());
            
        } catch (Exception e) {
            return error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 错误响应
     */
    private Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
