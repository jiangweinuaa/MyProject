package com.report.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 会话历史 DTO
 */
@Data
public class ConversationHistoryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sessionId;
    private String userId;
    private String title;
    private Date createdTime;
    private Date updatedTime;
    private int dialogueCount;
    private List<DialogueDTO> dialogues;
    
    /**
     * 对话记录 DTO
     */
    @Data
    public static class DialogueDTO implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private String id;
        private String sessionId;
        private String question;
        private String sqlGenerated;
        private String resultData;      // JSON 格式的完整结果集
        private int rowCount;
        private Long executionTimeMs;
        private String chartType;       // 'ai' 或 'auto'（保留兼容）
        private String chartConfig;     // AI 生成的图表配置（JSON 字符串，保留兼容）
        private String charts;          // 图表配置数组（JSON 字符串，新格式）
        private Date createdTime;
        
        // 显式添加 getter/setter 方法（避免 Lombok 问题）
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getSqlGenerated() { return sqlGenerated; }
        public void setSqlGenerated(String sqlGenerated) { this.sqlGenerated = sqlGenerated; }
        public String getResultData() { return resultData; }
        public void setResultData(String resultData) { this.resultData = resultData; }
        public int getRowCount() { return rowCount; }
        public void setRowCount(int rowCount) { this.rowCount = rowCount; }
        public Long getExecutionTimeMs() { return executionTimeMs; }
        public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
        public String getChartType() { return chartType; }
        public void setChartType(String chartType) { this.chartType = chartType; }
        public String getChartConfig() { return chartConfig; }
        public void setChartConfig(String chartConfig) { this.chartConfig = chartConfig; }
        public String getCharts() { return charts; }
        public void setCharts(String charts) { this.charts = charts; }
        public Date getCreatedTime() { return createdTime; }
        public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    }
}
