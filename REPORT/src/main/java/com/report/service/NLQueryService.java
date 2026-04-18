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
        long sqlExecStart = 0;
        String enhancedQuestion = question;
        String sql = null;
        
        try {
            ConversationContext context = contextManager.getOrCreateSession(sessionId);
            
            long sqlStart = System.currentTimeMillis();
            
            // 检测是否直接执行 SQL（以 SELECT 开头，不区分大小写）
            if (question.trim().toUpperCase().startsWith("SELECT")) {
                sql = question.trim();
                sqlGenTime = 0;
                enhancedQuestion = question;  // 直接执行的 SQL 不需要增强
                System.out.println("⚡ 直接执行 SQL：" + sql.substring(0, Math.min(100, sql.length())) + "...");
            } else {
                // 自然语言查询，需要增强并调用大模型
                enhancedQuestion = questionEnhancer.enhance(question, context.getVariables());
                
                // 从 token 解析 EID
                String eid = "99";
                if (token != null && !token.trim().isEmpty()) {
                    eid = com.report.util.TokenUtil.getEidFromToken(platformJdbcTemplate, token);
                }
                sql = aiSQLService.generateSQL(enhancedQuestion, context.getHistory(), eid);
                sqlGenTime = System.currentTimeMillis() - sqlStart;
            }
            
            // 验证 SQL 安全性（直接执行的 SQL 也需要验证）
            if (!aiSQLService.validateSQL(sql)) {
                logQuery(question, sql, isRetry, "FAILED", "SQL 不安全", sqlGenTime, 0L, startTime, token);
                return error("SQL 不安全，已拒绝执行：" + sql);
            }
            
            sqlExecStart = System.currentTimeMillis();
            JdbcTemplate businessJdbc = aiSQLService.getBusinessJdbcTemplate();
            List<Map<String, Object>> result = businessJdbc.queryForList(sql);
            sqlExecTime = System.currentTimeMillis() - sqlExecStart;
            
            contextManager.addDialogue(sessionId, question, sql);
            extractAndUpdateVariables(sessionId, sql);
            logQuery(question, sql, isRetry, "SUCCESS", null, sqlGenTime, sqlExecTime, startTime, token);
            
            // 保存对话到数据库（包含完整结果集）
            try {
                // 从 token 解析 OPNO（用户 ID）
                String userId = getUserIdFromToken(token);
                
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
            
            // 生成图表配置
            String chartType = "auto";
            String chartConfig = null;
            try {
                String chartModel = getChartModel();
                if (chartModel != null && !chartModel.isEmpty() && !"NONE".equals(chartModel)) {
                    // 调用大模型生成图表配置
                    String chartConfigJson = aiSQLService.callChartModel(chartModel, result, question);
                    if (chartConfigJson != null && !chartConfigJson.trim().isEmpty()) {
                        try {
                            chartConfig = chartConfigJson;
                            chartType = "ai";
                            System.out.println("✅ AI 图表配置生成成功");
                        } catch (Exception e) {
                            System.err.println("⚠️ 解析 AI 图表配置失败：" + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ 生成图表配置失败：" + e.getMessage());
            }
            
            response.put("chartType", chartType);
            if ("ai".equals(chartType) && chartConfig != null) {
                response.put("chartConfig", JSON.parse(chartConfig));
            }
            
            // 保存对话到数据库（包含完整结果集和图表配置）
            try {
                // 从 token 解析 OPNO（用户 ID）
                String userId = getUserIdFromToken(token);
                
                // 先保存会话（MERGE 模式，自动判断插入或更新）
                String title = generateTitle(question);
                conversationRepository.saveConversation(sessionId, userId, title);
                
                // 再保存对话记录（包含图表配置）
                String resultData = JSON.toJSONString(result);
                conversationRepository.saveDialogue(sessionId, question, sql, resultData, result.size(), sqlExecTime, chartType, chartConfig);
                
            } catch (Exception e) {
                System.err.println("⚠️ 保存对话历史失败：" + e.getMessage());
                e.printStackTrace();
                // 不影响主流程
            }
            
            // 添加 token 消耗信息
            AISQLService.TokenContext.TokenInfo tokenInfo = AISQLService.TokenContext.getTokenInfo();
            if (tokenInfo != null) {
                Map<String, Object> tokenUsage = new HashMap<>();
                tokenUsage.put("prompt", tokenInfo.promptTokens);
                tokenUsage.put("completion", tokenInfo.completionTokens);
                tokenUsage.put("total", tokenInfo.totalTokens);
                tokenUsage.put("estimatedCost", tokenInfo.estimatedCost);
                response.put("tokenUsage", tokenUsage);
            }
            
            return response;
            
        } catch (DataAccessException e) {
            // SQL 执行失败，记录日志
            long sqlExecTimeFailed = System.currentTimeMillis() - sqlExecStart;
            logQuery(question, sql, Boolean.valueOf(isRetry), "FAILED", "SQL 执行失败：" + e.getMessage(), 
                    Long.valueOf(sqlGenTime), Long.valueOf(sqlExecTimeFailed), Long.valueOf(System.currentTimeMillis() - startTime), token);
            System.err.println("❌ SQL 执行失败：" + e.getMessage());
            
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
                        long retryExecTime = System.currentTimeMillis() - sqlExecStart;
                        
                        contextManager.addDialogue(sessionId, question, correctedSQL);
                        extractAndUpdateVariables(sessionId, correctedSQL);
                        
                        // 保存重试成功的对话记录
                        try {
                            String userId = getUserIdFromToken(token);
                            String title = generateTitle(question);
                            conversationRepository.saveConversation(sessionId, userId, title);
                            
                            String resultData = JSON.toJSONString(result);
                            conversationRepository.saveDialogue(sessionId, question, correctedSQL, resultData, result.size(), retryExecTime);
                        } catch (Exception saveEx) {
                            System.err.println("⚠️ 保存重试对话失败：" + saveEx.getMessage());
                        }
                        
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("sql", correctedSQL);
                        response.put("data", result);
                        response.put("question", question);
                        response.put("enhancedQuestion", enhancedQuestion);
                        response.put("rowCount", Integer.valueOf(result.size()));
                        response.put("retry", Boolean.valueOf(true));
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
                         Long sqlGenTime, Long sqlExecTime, Long startTime, String token) {
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
                
                // 设置 AI 版本和模型名称
                String currentModel = getCurrentModel();
                if ("ALI_AGENT".equals(currentModel)) {
                    logDTO.setAiVersion("V2");
                    logDTO.setModelName("ALI_AGENT");
                } else {
                    logDTO.setAiVersion("V1");
                    logDTO.setModelName(currentModel);
                }
                
                logDTO.setSqlGenTimeMs(sqlGenTime);
                
                // 从 token 解析用户 ID（createdBy）
                String userId = getUserIdFromToken(token);
                logDTO.setCreatedBy(userId);
                
                // 从 TokenContext 获取 token 信息
                AISQLService.TokenContext.TokenInfo tokenInfo = AISQLService.TokenContext.getTokenInfo();
                if (tokenInfo != null) {
                    logDTO.setPromptTokens(tokenInfo.promptTokens);
                    logDTO.setCompletionTokens(tokenInfo.completionTokens);
                    logDTO.setTotalTokens(tokenInfo.totalTokens);
                    logDTO.setEstimatedCost(tokenInfo.estimatedCost);
                    System.out.println("✅ 记录 token 消耗：prompt=" + tokenInfo.promptTokens + 
                        ", completion=" + tokenInfo.completionTokens + 
                        ", total=" + tokenInfo.totalTokens);
                } else {
                    System.err.println("⚠️ TokenContext 为空，未记录 token 消耗");
                }
                
                nlQueryLogService.logQuery(logDTO);
                
                // 清理 ThreadLocal
                AISQLService.TokenContext.clear();
            }
        } catch (Exception e) {
            System.err.println("⚠️ 记录日志失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getCurrentModel() {
        // 先获取 AI 版本配置
        String versionSql = "SELECT ACCESSKEYID FROM PRODUCT_APPKEY WHERE PLATFORM = 'AI_VERSION'";
        String aiVersion = platformJdbcTemplate.queryForObject(versionSql, String.class);
        
        if ("ALI_AGENT".equals(aiVersion)) {
            return "ALI_AGENT";
        }
        
        // 默认从 ALI_QWEN 读取模型
        String sql = "SELECT ACCESSKEYID FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
        List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
        if (list != null && !list.isEmpty()) {
            String model = (String) list.get(0).get("ACCESSKEYID");
            if (model != null && !model.trim().isEmpty()) return model;
        }
        throw new RuntimeException("PRODUCT_APPKEY 表中没有配置有效的模型");
    }
    
    /**
     * 获取图表模型配置
     * @return 模型 ID，如 "qwen-plus"，"NONE" 或 null
     */
    private String getChartModel() {
        try {
            String sql = "SELECT ACCESSKEYID FROM PRODUCT_APPKEY WHERE PLATFORM = ?";
            List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql, "AI_ECHARTS_MODEL");
            if (list != null && !list.isEmpty()) {
                return (String) list.get(0).get("ACCESSKEYID");
            }
        } catch (Exception e) {
            System.err.println("⚠️ 读取图表模型配置失败：" + e.getMessage());
        }
        return null;
    }
    
    /**
     * 自动生成图表配置（硬编码逻辑）
     * @param data 查询结果数据
     * @return ECharts 配置对象
     */
    private Map<String, Object> generateAutoChart(List<Map<String, Object>> data) {
        Map<String, Object> config = new HashMap<>();
        
        if (data == null || data.isEmpty()) {
            return config;
        }
        
        // 获取列名
        Map<String, Object> firstRow = data.get(0);
        List<String> columns = new ArrayList<>(firstRow.keySet());
        
        // 简单判断：如果有 2 列，假设第 1 列是类别，第 2 列是数值
        if (columns.size() >= 2) {
            String categoryColumn = columns.get(0);
            String valueColumn = columns.get(1);
            
            List<String> categories = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> row : data) {
                categories.add(String.valueOf(row.get(categoryColumn)));
                values.add(row.get(valueColumn));
            }
            
            // 构建 ECharts 配置
            Map<String, Object> title = new HashMap<>();
            title.put("text", "查询结果");
            config.put("title", title);
            
            Map<String, Object> tooltip = new HashMap<>();
            config.put("tooltip", tooltip);
            
            Map<String, Object> xAxis = new HashMap<>();
            xAxis.put("type", "category");
            xAxis.put("data", categories);
            config.put("xAxis", xAxis);
            
            Map<String, Object> yAxis = new HashMap<>();
            yAxis.put("type", "value");
            config.put("yAxis", yAxis);
            
            Map<String, Object> series = new HashMap<>();
            series.put("type", "bar");
            series.put("data", values);
            series.put("itemStyle", Map.of("color", "#5470c6"));
            
            List<Map<String, Object>> seriesList = new ArrayList<>();
            seriesList.add(series);
            config.put("series", seriesList);
        }
        
        return config;
    }
    
    /**
     * 从 token 解析用户 ID（OPNO）
     * @param token 用户 token
     * @return 用户 ID（OPNO），如果 token 无效则返回 "anonymous"
     */
    private String getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return "anonymous";
        }
        try {
            String opno = com.report.util.TokenUtil.getOpnoFromToken(platformJdbcTemplate, token);
            if (opno != null && !opno.trim().isEmpty()) {
                return opno;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 解析 token 失败：" + e.getMessage());
        }
        return "anonymous";
    }
}
