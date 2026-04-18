package com.report.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
     * 从阿里云同步模型列表
     */
    @PostMapping("/sync-models")
    public Map<String, Object> syncModels() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取 API Key
            String apiKeySql = "SELECT ACCESSKEYSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
            List<Map<String, Object>> apiKeyList = platformJdbcTemplate.queryForList(apiKeySql);
            String apiKey = "";
            if (apiKeyList != null && !apiKeyList.isEmpty()) {
                apiKey = (String) apiKeyList.get(0).get("ACCESSKEYSECRET");
            }
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "未配置 API Key");
                return response;
            }
            
            // 调用阿里云 DashScope API
            String apiUrl = "https://dashscope.aliyuncs.com/api/v1/models";
            
            java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder().build();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .GET()
                .build();
            
            java.net.http.HttpResponse<String> httpResponse = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            
            if (httpResponse.statusCode() != 200) {
                response.put("success", false);
                response.put("message", "阿里云 API 调用失败：" + httpResponse.statusCode());
                return response;
            }
            
            JSONObject result = JSON.parseObject(httpResponse.body());
            JSONArray modelsArray = result.getJSONArray("data");
            
            if (modelsArray == null) {
                response.put("success", false);
                response.put("message", "阿里云 API 返回数据格式异常");
                return response;
            }
            
            // 清空现有模型列表
            platformJdbcTemplate.update("DELETE FROM AI_MODEL_LIST");
            
            // 插入新模型
            String insertSql = "INSERT INTO AI_MODEL_LIST (MODEL_ID, MODEL_NAME, STATUS, SORT_ORDER, CREATED_TIME) VALUES (?, ?, '100', ?, SYSDATE)";
            int sort = 0;
            
            for (int i = 0; i < modelsArray.size(); i++) {
                JSONObject model = modelsArray.getJSONObject(i);
                String modelId = model.getString("model_id");
                String modelName = model.getString("model_name");
                
                if (modelId != null && !modelId.trim().isEmpty()) {
                    platformJdbcTemplate.update(insertSql, modelId, modelName != null ? modelName : modelId, sort++);
                }
            }
            
            response.put("success", true);
            response.put("message", "同步成功，共同步 " + sort + " 个模型");
            response.put("count", sort);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "同步失败：" + e.getMessage());
            e.printStackTrace();
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
