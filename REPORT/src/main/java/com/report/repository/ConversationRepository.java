package com.report.repository;

import com.report.dto.ConversationHistoryDTO;
import com.report.dto.ConversationHistoryDTO.DialogueDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会话历史 Repository
 */
@Repository
public class ConversationRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 保存会话
     */
    public void saveConversation(String sessionId, String userId, String title) {
        String sql = "MERGE INTO AI_NL_CONVERSATION c " +
                     "USING DUAL ON (c.SESSION_ID = ?) " +
                     "WHEN MATCHED THEN UPDATE SET UPDATED_TIME = SYSDATE " +
                     "WHEN NOT MATCHED THEN INSERT (SESSION_ID, USER_ID, TITLE, CREATED_TIME, UPDATED_TIME) " +
                     "VALUES (?, ?, ?, SYSDATE, SYSDATE)";
        jdbcTemplate.update(sql, sessionId, sessionId, userId, title);
    }
    
    /**
     * 保存对话记录（包含完整结果集）
     */
    public void saveDialogue(String sessionId, String question, String sql, String resultData, int rowCount, long executionTimeMs) {
        saveDialogue(sessionId, question, sql, resultData, rowCount, executionTimeMs, null, null);
    }
    
    /**
     * 保存对话记录（包含图表配置）
     * @param chartConfig 单个图表配置 JSON（兼容旧格式，可能包含 JS 函数）
     */
    public void saveDialogue(String sessionId, String question, String sql, String resultData, int rowCount, long executionTimeMs, String chartType, String chartConfig) {
        // 统一存储为 charts 数组格式：[{"type":"ai","config":"原始字符串"}]
        String chartsJson = null;
        if (chartConfig != null && !chartConfig.trim().isEmpty()) {
            com.alibaba.fastjson2.JSONArray chartsArray = new com.alibaba.fastjson2.JSONArray();
            com.alibaba.fastjson2.JSONObject chartObj = new com.alibaba.fastjson2.JSONObject();
            if (chartType != null) {
                chartObj.put("type", chartType);
            }
            // 原始 chartConfig 字符串作为 config 存储（可能包含 JS 函数，不解析）
            chartObj.put("config", chartConfig);
            chartsArray.add(chartObj);
            chartsJson = chartsArray.toJSONString();
        }
        String id = UUID.randomUUID().toString().replace("-", "");
        String insertSql = "INSERT INTO AI_NL_DIALOGUE (ID, SESSION_ID, QUESTION, SQL_GENERATED, RESULT_DATA, ROW_COUNT, EXECUTION_TIME_MS, CHART_TYPE, CHART_CONFIG, CREATED_TIME) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
        jdbcTemplate.update(insertSql, id, sessionId, question, sql, resultData, rowCount, executionTimeMs, chartType, chartsJson);
    }
    
    /**
     * 获取会话历史（分页）
     */
    public List<DialogueDTO> getDialogueHistory(String sessionId, int page, int size) {
        int offset = page * size;
        int limit = offset + size;
        
        String sql = "SELECT * FROM (" +
                     "  SELECT d.*, ROWNUM rn FROM (" +
                     "    SELECT ID, SESSION_ID, QUESTION, SQL_GENERATED, RESULT_DATA, ROW_COUNT, EXECUTION_TIME_MS, CHART_TYPE, CHART_CONFIG, CREATED_TIME " +
                     "    FROM AI_NL_DIALOGUE " +
                     "    WHERE SESSION_ID = ? " +
                     "    ORDER BY CREATED_TIME DESC" +
                     "  ) d WHERE ROWNUM <= ?" +
                     ") WHERE rn > ?";
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, sessionId, limit, offset);
        
        return list.stream().map(this::mapToDialogueDTO).collect(Collectors.toList());
    }
    
    /**
     * 获取对话总数
     */
    public int getDialogueCount(String sessionId) {
        String sql = "SELECT COUNT(*) FROM AI_NL_DIALOGUE WHERE SESSION_ID = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, sessionId);
    }
    
    /**
     * 获取用户的会话列表（分页）
     */
    public List<Map<String, Object>> getUserConversations(String userId, int page, int size) {
        int offset = page * size;
        int limit = offset + size;
        
        String sql = "SELECT * FROM (" +
                     "  SELECT c.*, ROWNUM rn FROM (" +
                     "    SELECT SESSION_ID, USER_ID, TITLE, CREATED_TIME, UPDATED_TIME, STATUS," +
                     "           (SELECT COUNT(*) FROM AI_NL_DIALOGUE d WHERE d.SESSION_ID = c.SESSION_ID) as DIALOGUE_COUNT " +
                     "    FROM AI_NL_CONVERSATION c " +
                     "    WHERE USER_ID = ? AND STATUS = 100 " +
                     "    ORDER BY UPDATED_TIME DESC" +
                     "  ) c WHERE ROWNUM <= ?" +
                     ") WHERE rn > ?";
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId, limit, offset);
        
        return list.stream().map(row -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("sessionId", row.get("SESSION_ID"));
            dto.put("userId", row.get("USER_ID"));
            dto.put("title", row.get("TITLE"));
            dto.put("createdTime", row.get("CREATED_TIME"));
            dto.put("updatedTime", row.get("UPDATED_TIME"));
            dto.put("dialogueCount", row.get("DIALOGUE_COUNT"));
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取会话总数
     */
    public int getConversationCount(String userId) {
        String sql = "SELECT COUNT(*) FROM AI_NL_CONVERSATION WHERE USER_ID = ? AND STATUS = 100";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }
    
    /**
     * 删除会话（级联删除对话记录）
     */
    public void deleteConversation(String sessionId) {
        // 外键会级联删除对话记录
        String sql = "DELETE FROM AI_NL_CONVERSATION WHERE SESSION_ID = ?";
        jdbcTemplate.update(sql, sessionId);
    }
    
    /**
     * 更新会话标题
     */
    public void updateConversationTitle(String sessionId, String title) {
        String sql = "UPDATE AI_NL_CONVERSATION SET TITLE = ?, UPDATED_TIME = SYSDATE WHERE SESSION_ID = ?";
        jdbcTemplate.update(sql, title, sessionId);
    }
    
    /**
     * 映射到 DTO
     */
    private DialogueDTO mapToDialogueDTO(Map<String, Object> row) {
        DialogueDTO dto = new DialogueDTO();
        dto.setId((String) row.get("ID"));
        dto.setSessionId((String) row.get("SESSION_ID"));
        dto.setQuestion(clobToString(row.get("QUESTION")));
        dto.setSqlGenerated(clobToString(row.get("SQL_GENERATED")));
        dto.setResultData(clobToString(row.get("RESULT_DATA")));
        dto.setRowCount(((Number) row.get("ROW_COUNT")).intValue());
        dto.setExecutionTimeMs(row.get("EXECUTION_TIME_MS") != null ? ((Number) row.get("EXECUTION_TIME_MS")).longValue() : null);
        dto.setChartType((String) row.get("CHART_TYPE"));
        dto.setChartConfig(clobToString(row.get("CHART_CONFIG")));
        dto.setCreatedTime((Date) row.get("CREATED_TIME"));
        
        // 解析 charts 数组（新格式），向前端返回真正的数组结构
        String chartConfigStr = dto.getChartConfig();
        if (chartConfigStr != null && !chartConfigStr.trim().isEmpty()) {
            try {
                Object parsed = com.alibaba.fastjson2.JSON.parse(chartConfigStr);
                if (parsed instanceof com.alibaba.fastjson2.JSONArray) {
                    // 新格式：已经是 charts 数组
                    dto.setCharts(parsed);
                } else {
                    // 旧数据：单个图表对象，包装成数组
                    com.alibaba.fastjson2.JSONArray chartsArray = new com.alibaba.fastjson2.JSONArray();
                    com.alibaba.fastjson2.JSONObject chartObj = new com.alibaba.fastjson2.JSONObject();
                    String type = dto.getChartType() != null ? dto.getChartType() : "ai";
                    chartObj.put("type", type);
                    // 保留原始 chartConfig 字符串作为 config（可能包含 JS 函数）
                    chartObj.put("config", chartConfigStr);
                    chartsArray.add(chartObj);
                    dto.setCharts(chartsArray);
                }
            } catch (Exception e) {
                // JSON 解析失败（可能包含 JS 函数），直接包装原始字符串
                com.alibaba.fastjson2.JSONArray chartsArray = new com.alibaba.fastjson2.JSONArray();
                com.alibaba.fastjson2.JSONObject chartObj = new com.alibaba.fastjson2.JSONObject();
                String type = dto.getChartType() != null ? dto.getChartType() : "ai";
                chartObj.put("type", type);
                chartObj.put("config", chartConfigStr);
                chartsArray.add(chartObj);
                dto.setCharts(chartsArray);
            }
        }
        
        return dto;
    }
    
    /**
     * CLOB 转 String
     */
    private String clobToString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Clob) {
            try {
                Clob clob = (Clob) obj;
                return clob.getSubString(1, (int) clob.length());
            } catch (SQLException e) {
                throw new RuntimeException("CLOB 转换失败", e);
            }
        }
        return obj.toString();
    }
}
