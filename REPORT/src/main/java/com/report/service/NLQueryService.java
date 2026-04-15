package com.report.service;

import com.report.dto.NLQueryLogDTO;
import com.report.service.impl.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 自然语言查询服务（AI 版）
 * 
 * 数据源使用规范：
 * - 平台库：AI 配置（PRODUCT_APPKEY）、日志记录（NL_QUERY_LOG）
 * - 商家库：SQL 执行、表结构读取
 */
@Service
public class NLQueryService extends BaseService {
    
    @Autowired
    private AISQLService aiSQLService;
    
    @Autowired
    private NLQueryLogService nlQueryLogService;
    
    // 会话 ID（用于多轮对话追踪）
    private static String sessionId = null;
    
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
        
        long startTime = System.currentTimeMillis();
        long sqlGenTime = 0;
        long sqlExecTime = 0;
        
        try {
            // 1. 生成 SQL（使用平台库配置 + 商家库表结构）
            long sqlStart = System.currentTimeMillis();
            String sql = aiSQLService.generateSQL(question);
            sqlGenTime = System.currentTimeMillis() - sqlStart;
            
            // 2. 验证 SQL
            if (!aiSQLService.validateSQL(sql)) {
                logQuery(question, sql, sql, isRetry, "FAILED", "生成的 SQL 不安全", sqlGenTime, 0L, System.currentTimeMillis() - startTime);
                return error("生成的 SQL 不安全，已拒绝执行：" + sql);
            }
            
            // 3. 执行 SQL（使用商家库）
            long sqlExecStart = System.currentTimeMillis();
            JdbcTemplate businessJdbc = aiSQLService.getBusinessJdbcTemplate();
            List<Map<String, Object>> result = businessJdbc.queryForList(sql);
            sqlExecTime = System.currentTimeMillis() - sqlExecStart;
            
            // 4. 记录日志（平台库）
            logQuery(question, sql, sql, isRetry, "SUCCESS", null, sqlGenTime, sqlExecTime, System.currentTimeMillis() - startTime);
            
            // 5. 返回结果
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
                        // 记录日志（失败）
                        logQuery(question, correctedSQL, correctedSQL, true, "FAILED", "修正后的 SQL 不安全", sqlGenTime, 0L, System.currentTimeMillis() - startTime);
                        return error("修正后的 SQL 不安全，已拒绝执行：" + correctedSQL);
                    }
                    
                    // 重试执行（使用商家库）
                    try {
                        long sqlExecStart = System.currentTimeMillis();
                        JdbcTemplate businessJdbc = aiSQLService.getBusinessJdbcTemplate();
                        List<Map<String, Object>> result = businessJdbc.queryForList(correctedSQL);
                        sqlExecTime = System.currentTimeMillis() - sqlExecStart;
                        
                        // 记录日志（成功，重试）
                        logQuery(question, correctedSQL, correctedSQL, true, "SUCCESS", null, sqlGenTime, sqlExecTime, System.currentTimeMillis() - startTime);
                        
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
    
    /**
     * 记录查询日志（使用平台库）
     */
    private void logQuery(String question, String generatedSql, String finalSql, 
                         Boolean isRetry, String status, String errorMessage, 
                         Long sqlGenTime, Long sqlExecTime, Long responseTimeMs) {
        try {
            if (nlQueryLogService != null) {
                NLQueryLogDTO logDTO = new NLQueryLogDTO();
                logDTO.setSessionId(getSessionId());
                logDTO.setQuestion(question);
                logDTO.setGeneratedSql(generatedSql);
                logDTO.setFinalSql(finalSql);
                logDTO.setIsRetry(isRetry);
                logDTO.setStatus(status);
                logDTO.setErrorMessage(errorMessage);
                logDTO.setExecTimeMs(sqlExecTime);
                logDTO.setResponseTimeMs(responseTimeMs);
                logDTO.setModelName(getCurrentModel());
                
                // 保存 SQL 生成时间
                logDTO.setSqlGenTimeMs(sqlGenTime);
                logDTO.setGeneratedTimeMs(sqlGenTime);
                
                nlQueryLogService.logQuery(logDTO);
            }
        } catch (Exception e) {
            System.err.println("⚠️ 记录日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前使用的模型名称（使用平台库）
     */
    private String getCurrentModel() {
        String sql = "SELECT ACCESSKEYID FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
        List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
        if (list != null && !list.isEmpty()) {
            String model = (String) list.get(0).get("ACCESSKEYID");
            if (model != null && !model.trim().isEmpty()) {
                return model;
            }
        }
        throw new RuntimeException("PRODUCT_APPKEY 表中没有配置有效的模型");
    }
    
    /**
     * 获取或创建会话 ID
     */
    private String getSessionId() {
        if (sessionId == null) {
            sessionId = "SESSION_" + System.currentTimeMillis() + "_" + 
                       UUID.randomUUID().toString().replace("-", "").substring(0, 9);
        }
        return sessionId;
    }
}
