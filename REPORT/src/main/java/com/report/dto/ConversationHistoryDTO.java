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
        private Date createdTime;
    }
}
