package com.report.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.report.service.ModelService;
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
    
    @Autowired
    private ModelService modelService;
    
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
     * 获取模型列表（直接从阿里云 API 获取实时数据）
     */
    @GetMapping("/models")
    public Map<String, Object> getModels() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 直接从阿里云获取最新数据
            List<Map<String, Object>> models = modelService.getModelsFromAliyun();
            response.put("success", true);
            response.put("data", models);
            response.put("count", models.size());
            response.put("message", "获取成功，共 " + models.size() + " 个模型");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取失败：" + e.getMessage());
            response.put("data", new java.util.ArrayList<>());
        }
        
        return response;
    }
    
    /**
     * 从阿里云同步模型列表（只插入不存在的，不更新已存在的）
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
            
            // 调用阿里云 DashScope API（分页获取所有模型）
            String apiUrl = "https://dashscope.aliyuncs.com/api/v1/models";
            int pageNo = 1;
            int pageSize = 100;
            int totalModels = 0;
            
            java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder().build();
            
            int insertCount = 0;
            int skipCount = 0;
            
            // 只插入不存在的模型，已存在的不做任何更新
            String checkSql = "SELECT COUNT(*) FROM AI_MODEL_LIST WHERE MODEL_ID = ?";
            String insertSql = "INSERT INTO AI_MODEL_LIST (MODEL_ID, MODEL_NAME, STATUS, SORT_ORDER, CREATED_TIME) VALUES (?, ?, '100', ?, SYSDATE)";
            
            while (true) {
                String paginatedUrl = apiUrl + "?page_no=" + pageNo + "&page_size=" + pageSize;
                System.out.println("🔍 请求第 " + pageNo + " 页：" + paginatedUrl);
                
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(paginatedUrl))
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
                
                String responseBody = httpResponse.body();
                JSONObject result = JSON.parseObject(responseBody);
                
                // 阿里云新版 API 格式：output.models
                JSONArray modelsArray = null;
                if (result.containsKey("output")) {
                    JSONObject output = result.getJSONObject("output");
                    if (output != null && output.containsKey("models")) {
                        modelsArray = output.getJSONArray("models");
                    }
                }
                
                // 兼容旧版 API 格式：data
                if (modelsArray == null) {
                    modelsArray = result.getJSONArray("data");
                }
                
                if (modelsArray == null || modelsArray.size() == 0) {
                    System.out.println("✅ 没有更多模型了");
                    break;
                }
                
                System.out.println("✅ 第 " + pageNo + " 页解析到 " + modelsArray.size() + " 个模型");
                
                // 只插入不存在的模型，已存在的不做任何更新
                for (int i = 0; i < modelsArray.size(); i++) {
                    JSONObject model = modelsArray.getJSONObject(i);
                    // 阿里云 API 字段名：model, name
                    String modelId = model.getString("model");
                    String modelName = model.getString("name");
                    
                    if (modelId != null && !modelId.trim().isEmpty()) {
                        // 检查模型是否已存在
                        Integer count = platformJdbcTemplate.queryForObject(checkSql, Integer.class, modelId);
                        
                        if (count != null && count > 0) {
                            // 已存在，跳过不更新
                            System.out.println("⏭️ 跳过已存在的模型：" + modelId);
                            skipCount++;
                        } else {
                            // 不存在，插入新模型
                            platformJdbcTemplate.update(insertSql, modelId, modelName != null ? modelName : modelId, totalModels);
                            insertCount++;
                            System.out.println("✅ 插入新模型：" + modelId);
                            totalModels++;
                        }
                    }
                }
                
                // 检查是否还有下一页
                if (result.containsKey("output")) {
                    JSONObject output = result.getJSONObject("output");
                    if (output != null) {
                        Integer total = output.getInteger("total");
                        if (total != null && totalModels >= total) {
                            System.out.println("✅ 已同步所有 " + total + " 个模型");
                            break;
                        }
                    }
                }
                
                pageNo++;
            }
            
            
            response.put("success", true);
            response.put("message", "同步成功，新增 " + insertCount + " 个模型，跳过 " + skipCount + " 个已存在模型");
            response.put("totalModels", totalModels);
            response.put("insertCount", insertCount);
            response.put("skipCount", skipCount);
            
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
