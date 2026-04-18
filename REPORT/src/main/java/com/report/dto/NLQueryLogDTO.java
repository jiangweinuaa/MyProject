package com.report.dto;

import lombok.Data;

/**
 * 智问日志 DTO
 */
@Data
public class NLQueryLogDTO {
    
    /**
     * 会话 ID
     */
    private String sessionId;
    
    /**
     * 用户问题
     */
    private String question;
    
    /**
     * 生成的 SQL
     */
    private String generatedSql;
    
    /**
     * 是否重试
     */
    private Boolean isRetry;
    
    /**
     * 原始 SQL（重试时有值）
     */
    private String originalSql;
    
    /**
     * 最终 SQL
     */
    private String finalSql;
    
    /**
     * 执行状态：SUCCESS/FAILED
     */
    private String status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * SQL 生成时间（毫秒）
     */
    private Long sqlGenTimeMs;
    
    /**
     * SQL 执行时间（毫秒）
     */
    private Long execTimeMs;
    
    /**
     * SQL 生成时间（毫秒）- 用于数据库字段 GENERATED_TIME_MS
     */
    private Long generatedTimeMs;
    
    /**
     * 总响应时间（毫秒）
     */
    private Long responseTimeMs;
    
    /**
     * 模型名称
     */
    private String modelName;
    
    /**
     * AI 版本：V1=大模型版，V2=智能体版
     */
    private String aiVersion;
    
    /**
     * Prompt Token 数（输入）
     */
    private Integer promptTokens;
    
    /**
     * Completion Token 数（输出）
     */
    private Integer completionTokens;
    
    /**
     * 总 Token 数
     */
    private Integer totalTokens;
    
    /**
     * 预估费用（元）
     */
    private Double estimatedCost;
    
    /**
     * 创建人（用户 ID）
     */
    private String createdBy;
}

    // 显式添加所有 getter/setter（避免 Lombok 问题）
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getGeneratedSql() { return generatedSql; }
    public void setGeneratedSql(String generatedSql) { this.generatedSql = generatedSql; }
    public Boolean getIsRetry() { return isRetry; }
    public void setIsRetry(Boolean isRetry) { this.isRetry = isRetry; }
    public String getOriginalSql() { return originalSql; }
    public void setOriginalSql(String originalSql) { this.originalSql = originalSql; }
    public String getFinalSql() { return finalSql; }
    public void setFinalSql(String finalSql) { this.finalSql = finalSql; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Long getSqlGenTimeMs() { return sqlGenTimeMs; }
    public void setSqlGenTimeMs(Long sqlGenTimeMs) { this.sqlGenTimeMs = sqlGenTimeMs; }
    public Long getExecTimeMs() { return execTimeMs; }
    public void setExecTimeMs(Long execTimeMs) { this.execTimeMs = execTimeMs; }
    public Long getGeneratedTimeMs() { return generatedTimeMs; }
    public void setGeneratedTimeMs(Long generatedTimeMs) { this.generatedTimeMs = generatedTimeMs; }
    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getAiVersion() { return aiVersion; }
    public void setAiVersion(String aiVersion) { this.aiVersion = aiVersion; }
    public Integer getPromptTokens() { return promptTokens; }
    public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }
    public Integer getCompletionTokens() { return completionTokens; }
    public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }
    public Integer getTotalTokens() { return totalTokens; }
}
