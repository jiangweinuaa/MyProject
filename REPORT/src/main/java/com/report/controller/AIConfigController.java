package com.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 配置控制器
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIConfigController {
    
    @Autowired
    private JdbcTemplate platformJdbcTemplate;
    
    /**
     * 获取当前 AI 版本
     */
    @GetMapping("/version")
    public Map<String, Object> getVersion() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = "SELECT ACCESSKEYID FROM PRODUCT_APPKEY WHERE PLATFORM = 'AI_VERSION'";
            String version = platformJdbcTemplate.queryForObject(sql, String.class);
            
            response.put("success", true);
            response.put("version", version != null ? version : "ALI_MODEL");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("version", "ALI_MODEL");
        }
        
        return response;
    }
    
    /**
     * 切换 AI 版本
     */
    @PostMapping("/switch-version")
    public Map<String, Object> switchVersion(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String version = request.get("version");
            
            if (!"ALI_MODEL".equals(version) && !"ALI_AGENT".equals(version)) {
                response.put("success", false);
                response.put("message", "版本必须是 ALI_MODEL 或 ALI_AGENT");
                return response;
            }
            
            // 更新版本配置
            String sql = "UPDATE PRODUCT_APPKEY SET ACCESSKEYID = ? WHERE PLATFORM = 'AI_VERSION'";
            platformJdbcTemplate.update(sql, version);
            
            response.put("success", true);
            response.put("message", "已切换到 " + (version.equals("ALI_MODEL") ? "大模型模式" : "智能体模式"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 获取当前模型
     */
    @GetMapping("/current-model")
    public Map<String, Object> getCurrentModel() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从 ALI_QWEN 读取模型 ID（无论 AI 版本是什么，都显示实际配置的模型）
            String sql = "SELECT ACCESSKEYID FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
            String model = platformJdbcTemplate.queryForObject(sql, String.class);
            
            response.put("success", true);
            response.put("model", model != null ? model : "qwen-plus");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("model", "qwen-plus");
        }
        
        return response;
    }
    
    /**
     * 获取可用模型列表
     */
    @GetMapping("/models")
    public Map<String, Object> getModels() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = "SELECT MODEL_ID, MODEL_NAME FROM AI_MODEL_LIST WHERE STATUS IN ('100', 100) ORDER BY SORT_ORDER";
            List<Map<String, Object>> models = platformJdbcTemplate.queryForList(sql);
            
            response.put("success", true);
            response.put("data", models);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", new java.util.ArrayList<>());
        }
        
        return response;
    }
    
    /**
     * 切换模型
     */
    @PostMapping("/switch-model")
    public Map<String, Object> switchModel(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String modelId = request.get("modelId");
            
            if (modelId == null || modelId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "模型 ID 不能为空");
                return response;
            }
            
            // 更新 PRODUCT_APPKEY 表
            String sql = "UPDATE PRODUCT_APPKEY SET ACCESSKEYID = ? WHERE PLATFORM = 'ALI_QWEN'";
            platformJdbcTemplate.update(sql, modelId);
            
            response.put("success", true);
            response.put("message", "模型切换成功");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
}
