package com.report.service;

import com.report.dto.NLQueryLogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 智问日志服务
 */
@Service
public class NLQueryLogService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 记录对话日志
     */
    public Map<String, Object> logQuery(NLQueryLogDTO logDTO) {
        try {
            String logId = UUID.randomUUID().toString().replace("-", "");
            String sessionId = logDTO.getSessionId() != null ? logDTO.getSessionId() : UUID.randomUUID().toString().replace("-", "");
            
            String sql = "INSERT INTO AI_NL_QUERY_LOG (" +
                "LOG_ID, SESSION_ID, USER_QUESTION, GENERATED_SQL, IS_RETRY, " +
                "ORIGINAL_SQL, FINAL_SQL, EXEC_STATUS, ERROR_MESSAGE, " +
                "EXEC_TIME_MS, RESPONSE_TIME_MS, GENERATED_TIME_MS, MODEL_NAME, AI_VERSION, " +
                "PROMPT_TOKENS, COMPLETION_TOKENS, TOTAL_TOKENS, ESTIMATED_COST, " +
                "CREATED_BY, CREATED_TIME " +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
            
            jdbcTemplate.update(sql,
                logId,
                sessionId,
                logDTO.getQuestion(),
                logDTO.getGeneratedSql(),
                logDTO.getIsRetry() != null && logDTO.getIsRetry() ? "Y" : "N",
                logDTO.getOriginalSql(),
                logDTO.getFinalSql(),
                logDTO.getStatus(),
                logDTO.getErrorMessage(),
                logDTO.getExecTimeMs(),
                logDTO.getResponseTimeMs(),
                logDTO.getGeneratedTimeMs() != null ? logDTO.getGeneratedTimeMs() : logDTO.getSqlGenTimeMs(),
                logDTO.getModelName(),
                logDTO.getAiVersion() != null ? logDTO.getAiVersion() : "V1",
                logDTO.getPromptTokens(),
                logDTO.getCompletionTokens(),
                logDTO.getTotalTokens(),
                logDTO.getEstimatedCost(),
                logDTO.getCreatedBy()
            );
            
            // 更新或创建会话统计
            updateOrCreateSession(sessionId, logDTO.getStatus(), logDTO.getQuestion());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("logId", logId);
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
    
    /**
     * 更新或创建会话统计
     */
    private void updateOrCreateSession(String sessionId, String status, String lastQuestion) {
        // 先检查会话是否存在
        String checkSql = "SELECT COUNT(*) FROM AI_NL_QUERY_SESSION WHERE SESSION_ID = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, sessionId);
        
        if (count > 0) {
            // 更新会话
            String successCol = status.equals("SUCCESS") ? "SUCCESS_COUNT" : "FAILED_COUNT";
            String updateSql = "UPDATE AI_NL_QUERY_SESSION " +
                "SET QUESTION_COUNT = QUESTION_COUNT + 1, " +
                successCol + " = " + successCol + " + 1, " +
                "LAST_QUESTION = ?, " +
                "END_TIME = SYSDATE " +
                "WHERE SESSION_ID = ?";
            
            jdbcTemplate.update(updateSql, lastQuestion, sessionId);
        } else {
            // 创建新会话
            String successCol = status.equals("SUCCESS") ? "SUCCESS_COUNT" : "FAILED_COUNT";
            String insertSql = "INSERT INTO AI_NL_QUERY_SESSION (" +
                "SESSION_ID, QUESTION_COUNT, " + successCol + ", FAILED_COUNT, " +
                "LAST_QUESTION, START_TIME, CREATED_TIME " +
                ") VALUES (?, 1, ?, ?, ?, SYSDATE, SYSDATE)";
            
            jdbcTemplate.update(insertSql,
                sessionId,
                status.equals("SUCCESS") ? 1 : 0,
                status.equals("SUCCESS") ? 0 : 1,
                lastQuestion
            );
        }
    }
    
    /**
     * 查询日志列表
     */
    public Map<String, Object> getLogs(int page, int size) {
        int offset = (page - 1) * size;
        
        String sql = "SELECT * FROM ( " +
            "SELECT t.*, ROWNUM rn FROM ( " +
            "SELECT * FROM AI_NL_QUERY_LOG ORDER BY CREATED_TIME DESC " +
            ") t WHERE ROWNUM <= ? " +
            ") WHERE rn > ?";
        
        List<Map<String, Object>> logs = jdbcTemplate.queryForList(sql, offset + size, offset);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", logs);
        response.put("total", getTotalCount());
        response.put("page", page);
        response.put("size", size);
        
        return response;
    }
    
    /**
     * 查询总数
     */
    private int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM AI_NL_QUERY_LOG";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
    
    /**
     * 查询统计数据（按天）
     */
    public Map<String, Object> getDailyStats(String startDate, String endDate) {
        String sql = "SELECT * FROM V_AI_NL_QUERY_DAILY_STATS " +
            "WHERE STAT_DATE BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') " +
            "ORDER BY STAT_DATE DESC";
        
        List<Map<String, Object>> stats = jdbcTemplate.queryForList(sql, startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return response;
    }
    
    /**
     * 查询统计数据（按问题类型）
     */
    public Map<String, Object> getQuestionStats() {
        String sql = "SELECT * FROM V_AI_NL_QUERY_QUESTION_STATS ORDER BY TOTAL_QUERIES DESC";
        
        List<Map<String, Object>> stats = jdbcTemplate.queryForList(sql);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return response;
    }
    
    /**
     * 查询会话详情
     */
    public Map<String, Object> getSessionDetail(String sessionId) {
        String sql = "SELECT * FROM AI_NL_QUERY_SESSION WHERE SESSION_ID = ?";
        
        List<Map<String, Object>> sessions = jdbcTemplate.queryForList(sql, sessionId);
        
        Map<String, Object> response = new HashMap<>();
        if (sessions.isEmpty()) {
            response.put("success", false);
            response.put("message", "会话不存在");
        } else {
            response.put("success", true);
            response.put("data", sessions.get(0));
        }
        
        return response;
    }
}
