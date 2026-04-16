package com.report.controller;

import com.report.repository.ConversationRepository;
import com.report.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 从 token 解析用户 ID（OPNO）
     * @throws RuntimeException 如果 token 无效
     */
    private String getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("token 不能为空");
        }
        String opno = TokenUtil.getOpnoFromToken(jdbcTemplate, token);
        // TokenUtil 在解析失败时返回 "default_user"，需要检查
        if (opno == null || opno.trim().isEmpty() || "default_user".equals(opno)) {
            throw new RuntimeException("token 无效或已过期");
        }
        return opno;
    }
    
    /**
     * 获取会话列表（分页）
     */
    @GetMapping("/list")
    public Map<String, Object> getConversationList(
            @RequestParam(required = false) String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            // 从 token 解析用户 ID
            String userId = getUserIdFromToken(token);
            System.out.println("🔍 [ConversationController] token=" + (token != null ? "有" : "无") + 
                ", userId=" + userId);
            
            List list = conversationRepository.getUserConversations(userId, page, size);
            int total = conversationRepository.getConversationCount(userId);
            
            System.out.println("🔍 [ConversationController] 查询结果：total=" + total + 
                ", 返回记录数=" + list.size());
            
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
            
            // token 相关错误返回 401
            if (e.getMessage() != null && e.getMessage().contains("token")) {
                response.put("serviceStatus", "401");
                response.put("message", "登录已过期，请重新登录");
            } else {
                response.put("message", "查询失败：" + e.getMessage());
            }
            
            return response;
        }
    }
    
    /**
     * 获取会话详情（完整历史，分页）
     */
    @GetMapping("/history")
    public Map<String, Object> getHistory(
            @RequestParam String sessionId,
            @RequestParam(required = false) String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            // 从 token 解析 EID 作为 userId（用于权限验证）
            String userId = getUserIdFromToken(token);
            
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
            
            // token 相关错误返回 401
            if (e.getMessage() != null && e.getMessage().contains("token")) {
                response.put("serviceStatus", "401");
                response.put("message", "登录已过期，请重新登录");
            } else {
                response.put("message", "查询失败：" + e.getMessage());
            }
            
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
    
    /**
     * 批量更新 userId（临时接口）
     */
    @GetMapping("/update-userid")
    public Map<String, Object> updateUserId(
            @RequestParam(defaultValue = "default_user") String from,
            @RequestParam(defaultValue = "admin") String to) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = "UPDATE AI_NL_CONVERSATION SET USER_ID = ? WHERE USER_ID = ?";
            int updated = jdbcTemplate.update(sql, to, from);
            
            response.put("success", true);
            response.put("message", "更新成功");
            response.put("updated", updated);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
        }
        
        return response;
    }
}
