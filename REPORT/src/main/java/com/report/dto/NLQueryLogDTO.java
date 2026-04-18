package com.report.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 智问日志 DTO
 */
public class NLQueryLogDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sessionId;
    private String question;
    private String generatedSql;
    private Boolean isRetry;
    private String originalSql;
    private String finalSql;
    private String status;
    private String errorMessage;
    private Long sqlGenTimeMs;
    private Long execTimeMs;
    private Long generatedTimeMs;
    private Long responseTimeMs;
    private String modelName;
    private String aiVersion;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private Double estimatedCost;
    private String createdBy;
    private Date createdTime;
    
    // Getter 和 Setter 方法
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
    public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
}
