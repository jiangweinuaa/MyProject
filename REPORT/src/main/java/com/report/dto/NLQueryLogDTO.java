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
