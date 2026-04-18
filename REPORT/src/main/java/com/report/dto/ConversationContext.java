package com.report.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 会话上下文
 */
@Data
@NoArgsConstructor
public class ConversationContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 会话 ID
     */
    private String sessionId;
    
    // 显式添加 getter/setter
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    /**
     * 用户 ID（可选）
     */
    private String userId;
    
    /**
     * 对话历史
     */
    private List<Dialogue> history = new LinkedList<>();
    
    /**
     * 上下文变量
     */
    private ContextVariables variables = new ContextVariables();
    
    // 显式添加 getter/setter
    public ContextVariables getVariables() { return variables; }
    public void setVariables(ContextVariables variables) { this.variables = variables; }
    
    /**
     * 最后活跃时间
     */
    private long lastActiveTime = System.currentTimeMillis();
    
    // 显式添加所有 getter/setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public java.util.List<Dialogue> getHistory() { return history; }
    public void setHistory(java.util.List<Dialogue> history) { this.history = history; }
    public long getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(long lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    
    /**
     * 添加对话记录
     * @param question 问题
     * @param sql SQL
     * @param maxHistory 最大保留轮数
     */
    public void addDialogue(String question, String sql, int maxHistory) {
        Dialogue dialogue = new Dialogue(question, sql, System.currentTimeMillis());
        history.add(dialogue);
        
        // 保留最近 N 轮（可配置）
        while (history.size() > maxHistory) {
            history.remove(0);
        }
        
        lastActiveTime = System.currentTimeMillis();
    }
    
    /**
     * 添加对话记录（默认 5 轮）
     */
    public void addDialogue(String question, String sql) {
        addDialogue(question, sql, 5);
    }
    
    /**
     * 获取最近的 SQL
     */
    public String getLastSql() {
        if (history.isEmpty()) {
            return null;
        }
        return history.get(history.size() - 1).getSql();
    }
    
    /**
     * 获取对话历史文本（用于 Prompt）
     */
    public String getHistoryText() {
        if (history.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 对话历史\n\n");
        for (int i = 0; i < history.size(); i++) {
            Dialogue d = history.get(i);
            sb.append(i + 1).append(". 用户：").append(d.question).append("\n");
            sb.append("   AI：").append(d.sql).append("\n\n");
        }
        return sb.toString();
    }
    
    @Data
    @NoArgsConstructor
    public static class Dialogue implements Serializable {
        public String question;
        public String sql;
        public long timestamp;
        
        public Dialogue(String question, String sql, long timestamp) {
            this.question = question;
            this.sql = sql;
            this.timestamp = timestamp;
        }
        
        // 显式添加 getter
        public String getQuestion() { return question; }
        public String getSql() { return sql; }
        public long getTimestamp() { return timestamp; }
    }
}
