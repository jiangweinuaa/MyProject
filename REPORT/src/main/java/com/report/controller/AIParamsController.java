package com.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 版本切换控制器
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIParamsController {
    
    @Autowired
    private JdbcTemplate platformJdbcTemplate;
    
    /**
     * 获取当前 AI 版本
     */
    @GetMapping("/version")
    public Map<String, Object> getVersion() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = "SELECT ACCESSKEYID AS VERSION FROM PRODUCT_APPKEY WHERE PLATFORM = 'AI_VERSION'";
            String version = platformJdbcTemplate.queryForObject(sql, String.class);
            
            response.put("success", true);
            response.put("version", version != null ? version : "ALI_MODEL");
            response.put("message", "当前版本：" + (version != null ? version : "ALI_MODEL"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
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
            String sql = "UPDATE PRODUCT_APPKEY SET ACCESSKEYID = ?, UPDATED_TIME = SYSDATE WHERE PLATFORM = 'AI_VERSION'";
            platformJdbcTemplate.update(sql, version);
            
            response.put("success", true);
            response.put("message", "已切换到 " + version + " 版本");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 更新智能体配置
     */
    @PostMapping("/agent/config")
    public Map<String, Object> updateAgentConfig(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String appId = request.get("appId");
            String apiKey = request.get("apiKey");
            
            if (appId == null || appId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "智能体 APPID 不能为空");
                return response;
            }
            
            String sql = "UPDATE PRODUCT_APPKEY SET ACCESSKEYID = ?, ACCESSKEYSECRET = ?, UPDATED_TIME = SYSDATE WHERE PLATFORM = 'ALI_AGENT'";
            platformJdbcTemplate.update(sql, appId, apiKey);
            
            response.put("success", true);
            response.put("message", "智能体配置已更新");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
}
