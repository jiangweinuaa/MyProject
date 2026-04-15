package com.report.service;

import com.alibaba.fastjson2.JSON;
import com.report.dto.ConversationContext;
import com.report.dto.NLQueryLogDTO;
import com.report.repository.ConversationRepository;
import com.report.service.impl.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 自然语言查询服务（支持上下文记忆）
 */
@Service
public class NLQueryService extends BaseService {
    
    @Autowired
    private AISQLService aiSQLService;
    
    @Autowired
    private NLQueryLogService nlQueryLogService;
    
    @Autowired
    private ConversationContextManager contextManager;
    
    @Autowired
    private QuestionEnhancer questionEnhancer;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    private static String sessionId = null;
    
    public Map<String, Object> query(String sessionId, String question) {
        return query(sessionId, question, null, false);
    }
    
    public Map<String, Object> query(String question) {
        if (sessionId == null) {
            sessionId = generateSessionId();
        }
        return query(sessionId, question, null, false);
    }
    
    public Map<String, Object> query(String sessionId, String question, String token) {
        return query(sessionId, question, token, false);
    }
    
    private Map<String, Object> query(String sessionId, String question, String token, boolean isRetry) {
        if (question == null || question.trim().isEmpty()) {
            return error("问题不能为空");
        }
        
        long startTime = System.currentTimeMillis();
        long sqlGenTime = 0;
        long sqlExecTime = 0;
        String enhancedQuestion = question;
        
        try {
            ConversationContext context = contextManager.getOrCreateSession(sessionId);
            enhancedQuestion = questionEnhancer.enhance(question, context.getVariables());
            
            long sqlStart = System.currentTimeMillis();
            // 从 token 解析 EID
            String eid = "99";
            if (token != null && !token.trim().isEmpty()) {
                eid = com.report.util.TokenUtil.getEidFromToken(platformJdbcTemplate, token);
            }
            String sql = aiSQLService.generateSQL(enhancedQuestion, context.getHistory(), eid);
            sqlGenTime = System.currentTimeMillis() - sqlStart;
            
            if (!aiSQLService.validateSQL(sql)) {
                logQuery(question, sql, isRetry, "FAILED", "生成的 SQL 不安全", sqlGenTime, 0L, startTime);
                return error("生成的 SQL 不安全，已拒绝执行：" + sql);
            }
            
            long sqlExecStart = System.currentTimeMillis();
            JdbcTemplate businessJdbc = aiSQLService.getBusinessJdbcTemplate();
            List<Map<String, Object>> result = businessJdbc.queryForList(sql);
            sqlExecTime = System.currentTimeMillis() - sqlExecStart;
            
            contextManager.addDialogue(sessionId, question, sql);
            extractAndUpdateVariables(sessionId, sql);
            logQuery(question, sql, isRetry, "SUCCESS", null, sqlGenTime, sqlExecTime, startTime);
            
            // 保存对话到数据库（包含完整结果集）
            try {
                // 从 token 解析 OPNO（用户 ID，如果没有 token，使用 default_user）
                String userId = "default_user";
                if (token != null && !token.trim().isEmpty()) {
                    userId = com.report.util.TokenUtil.getOpnoFromToken(platformJdbcTemplate, token);
                }
                
                // 先保存会话（MERGE 模式，自动判断插入或更新）
                String title = generateTitle(question);
                conversationRepository.saveConversation(sessionId, userId, title);
                
                // 再保存对话记录
                String resultData = JSON.toJSONString(result);
                conversationRepository.saveDialogue(sessionId, question, sql, resultData, result.size(), sqlExecTime);
                
            } catch (Exception e) {
                System.err.println("⚠️ 保存对话历史失败：" + e.getMessage());
                e.printStackTrace();
                // 不影响主流程
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sql", sql);
            response.put("data", result);
            response.put("question", question);
            response.put("enhancedQuestion", enhancedQuestion);
            response.put("rowCount", result.size());
            response.put("sessionId", sessionId);
            
            return response;
            
        } catch (DataAccessException e) {
            if (!isRetry) {
                String errorMsg = e.getMessage();
                if (errorMsg != null && (errorMsg.contains("ORA-009") || errorMsg.contains("SQLSyntaxErrorException"))) {
                    // 从 token 解析 EID
                    String eid = "99";
                    if (token != null && !token.trim().isEmpty()) {
                        eid = com.report.util.TokenUtil.getEidFromToken(platformJdbcTemplate, token);
                    }
                    String correctedSQL = aiSQLService.regenerateSQL(question, errorMsg, new java.util.ArrayList<>(), eid);
                    
                    if (!aiSQLService.validateSQL(correctedSQL)) {
                        return error("修正后的 SQL 不安全：" + correctedSQL);
                    }
                    
                    try {
                        JdbcTemplate businessJdbc = aiSQLService.getBusinessJdbcTemplate();
                        List<Map<String, Object>> result = businessJdbc.queryForList(correctedSQL);
                        
                        contextManager.addDialogue(sessionId, question, correctedSQL);
                        extractAndUpdateVariables(sessionId, correctedSQL);
                        
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("sql", correctedSQL);
                        response.put("data", result);
                        response.put("question", question);
                        response.put("enhancedQuestion", enhancedQuestion);
                        response.put("rowCount", result.size());
                        response.put("retry", true);
                        response.put("sessionId", sessionId);
                        
                        return response;
                    } catch (Exception retryEx) {
                        return error("修正后执行失败：" + retryEx.getMessage());
                    }
                }
            }
            return error("SQL 执行错误：" + e.getMessage());
            
        } catch (Exception e) {
            return error("查询失败：" + e.getMessage());
        }
    }
    
    private void extractAndUpdateVariables(String sessionId, String sql) {
        String shopId = extractValue(sql, "SHOPID");
        if (shopId != null) contextManager.updateVariable(sessionId, "shopId", shopId);
        
        String pluno = extractValue(sql, "PLUNO");
        if (pluno != null) contextManager.updateVariable(sessionId, "pluno", pluno);
    }
    
    private String extractValue(String sql, String column) {
        int idx = sql.toUpperCase().indexOf(column);
        if (idx == -1) return null;
        
        int eqIdx = sql.indexOf("=", idx);
        if (eqIdx == -1) return null;
        
        int startIdx = eqIdx + 1;
        while (startIdx < sql.length() && sql.charAt(startIdx) == ' ') startIdx++;
        
        int endIdx = startIdx;
        while (endIdx < sql.length() && sql.charAt(endIdx) != ' ' && sql.charAt(endIdx) != ')') endIdx++;
        
        String value = sql.substring(startIdx, endIdx).trim();
        if (value.startsWith("'") && value.endsWith("'")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }
    
    private String generateSessionId() {
        return "SESSION_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 9);
    }
    
    private String generateTitle(String question) {
        return question.length() > 30 ? question.substring(0, 30) + "..." : question;
    }
    
    private Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
    
    private void logQuery(String question, String sql, Boolean isRetry, String status, String errorMessage, 
                         Long sqlGenTime, Long sqlExecTime, Long startTime) {
        try {
            if (nlQueryLogService != null) {
                NLQueryLogDTO logDTO = new NLQueryLogDTO();
                logDTO.setSessionId(sessionId);
                logDTO.setQuestion(question);
                logDTO.setGeneratedSql(sql);
                logDTO.setFinalSql(sql);
                logDTO.setIsRetry(isRetry);
                logDTO.setStatus(status);
                logDTO.setErrorMessage(errorMessage);
                logDTO.setExecTimeMs(sqlExecTime);
                logDTO.setResponseTimeMs(System.currentTimeMillis() - startTime);
                logDTO.setModelName(getCurrentModel());
                logDTO.setSqlGenTimeMs(sqlGenTime);
                nlQueryLogService.logQuery(logDTO);
            }
        } catch (Exception e) {
            System.err.println("⚠️ 记录日志失败：" + e.getMessage());
        }
    }
    
    private String getCurrentModel() {
        String sql = "SELECT ACCESSKEYID FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
        List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
        if (list != null && !list.isEmpty()) {
            String model = (String) list.get(0).get("ACCESSKEYID");
            if (model != null && !model.trim().isEmpty()) return model;
        }
        throw new RuntimeException("PRODUCT_APPKEY 表中没有配置有效的模型");
    }
}
