package com.report.service;

import org.springframework.beans.factory.annotation.Autowired;
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
