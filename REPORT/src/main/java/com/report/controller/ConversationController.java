package com.report.controller;

import com.report.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话历史管理 Controller
 */
@RestController
@RequestMapping("/api/conversation")
@CrossOrigin(origins = "*")
public class ConversationController {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    /**
     * 获取会话列表（分页）
     */
    @GetMapping("/list")
    public Map<String, Object> getConversationList(
            @RequestParam(defaultValue = "default_user") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            List list = conversationRepository.getUserConversations(userId, page, size);
            int total = conversationRepository.getConversationCount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", list);
            response.put("pagination", Map.of(
                "page", page,
                "size", size,
                "total", total,
                "hasMore", (page + 1) * size < total
            ));
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "查询失败：" + e.getMessage());
            return response;
        }
    }
    
    /**
     * 获取会话详情（完整历史，分页）
     */
    @GetMapping("/history")
    public Map<String, Object> getHistory(
            @RequestParam String sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            List history = conversationRepository.getDialogueHistory(sessionId, page, size);
            int total = conversationRepository.getDialogueCount(sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", history);
            response.put("pagination", Map.of(
                "page", page,
                "size", size,
                "total", total,
                "hasMore", (page + 1) * size < total
            ));
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "查询失败：" + e.getMessage());
            return response;
        }
    }
    
    /**
     * 删除会话
     */
    @DeleteMapping("/{sessionId}")
    public Map<String, Object> deleteConversation(@PathVariable String sessionId) {
        try {
            conversationRepository.deleteConversation(sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "会话已删除");
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
            return response;
        }
    }
    
    /**
     * 更新会话标题
     */
    @PutMapping("/{sessionId}/title")
    public Map<String, Object> updateTitle(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {
        
        try {
            String title = request.get("title");
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("标题不能为空");
            }
            
            conversationRepository.updateConversationTitle(sessionId, title);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "标题已更新");
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
            return response;
        }
    }
}
