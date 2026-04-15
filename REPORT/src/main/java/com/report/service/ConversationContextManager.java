package com.report.service;

import com.report.dto.ConversationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话上下文管理器（内存版）
 * 
 * 使用 ConcurrentHashMap 存储会话上下文
 * 定时清理过期会话（30 分钟无活动）
 */
@Service
public class ConversationContextManager {
    
    /**
     * 会话缓存
     * Key: sessionId
     * Value: ConversationContext
     */
    private final Map<String, ConversationContext> sessionCache = new ConcurrentHashMap<>();
    
    /**
     * 会话过期时间（30 分钟）
     */
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000;
    
    /**
     * 对话历史最大轮数（可配置，默认 5 轮）
     * 
     * 配置方式：
     * 1. application.yml: conversation.max-history=5
     * 2. 或直接修改默认值
     * 
     * 建议值：
     * - 0: 不使用对话历史（每个问题独立）
     * - 3: 保留最近 3 轮（轻量级）
     * - 5: 保留最近 5 轮（推荐）
     * - 10: 保留最近 10 轮（完整历史）
     */
    @Value("${conversation.max-history:5}")
    private int maxHistory = 5;
    
    /**
     * 获取或创建会话
     */
    public ConversationContext getOrCreateSession(String sessionId) {
        return sessionCache.computeIfAbsent(sessionId, id -> {
            ConversationContext context = new ConversationContext();
            context.setSessionId(id);
            return context;
        });
    }
    
    /**
     * 获取会话（不存在返回 null）
     */
    public ConversationContext getSession(String sessionId) {
        return sessionCache.get(sessionId);
    }
    
    /**
     * 添加对话记录（使用配置的 maxHistory）
     */
    public void addDialogue(String sessionId, String question, String sql) {
        ConversationContext context = getOrCreateSession(sessionId);
        context.addDialogue(question, sql, maxHistory);
    }
    
    /**
     * 更新上下文变量
     */
    public void updateVariable(String sessionId, String key, String value) {
        ConversationContext context = getOrCreateSession(sessionId);
        context.getVariables().update(key, value);
    }
    
    /**
     * 删除会话
     */
    public void removeSession(String sessionId) {
        sessionCache.remove(sessionId);
    }
    
    /**
     * 获取活跃会话数
     */
    public int getActiveSessionCount() {
        return sessionCache.size();
    }
    
    /**
     * 获取当前配置的 maxHistory
     */
    public int getMaxHistory() {
        return maxHistory;
    }
    
    /**
     * 定时清理过期会话
     * 每 5 分钟执行一次
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        sessionCache.entrySet().removeIf(entry -> {
            long inactiveTime = now - entry.getValue().getLastActiveTime();
            return inactiveTime > SESSION_TIMEOUT_MS;
        });
    }
}
