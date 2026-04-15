package com.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员接口
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 更新会话的 userId
     */
    @PostMapping("/update-userid")
    public Map<String, Object> updateUserId(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String from = request.get("from");
            String to = request.get("to");
            
            if (from == null || to == null) {
                response.put("success", false);
                response.put("message", "参数错误");
                return response;
            }
            
            // 更新会话表
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
