package com.report.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 阿里云百炼模型服务
 */
@Service
public class ModelService {
    
    @Autowired
    private JdbcTemplate platformJdbcTemplate;
    
    /**
     * 从阿里云获取模型列表
     * @return 模型列表
     */
    public List<Map<String, Object>> getModelsFromAliyun() {
        List<Map<String, Object>> models = new ArrayList<>();
        
        try {
            // 获取 API Key
            String apiKeySql = "SELECT ACCESSKEYSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
            List<Map<String, Object>> apiKeyList = platformJdbcTemplate.queryForList(apiKeySql);
            String apiKey = "";
            if (apiKeyList != null && !apiKeyList.isEmpty()) {
                apiKey = (String) apiKeyList.get(0).get("ACCESSKEYSECRET");
            }
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.err.println("⚠️ 未配置 API Key");
                return models;
            }
            
            // 调用阿里云 DashScope API（分页获取所有模型）
            String apiUrl = "https://dashscope.aliyuncs.com/api/v1/models";
            int pageNo = 1;
            int pageSize = 100;
            int totalModels = 0;
            
            HttpClient client = HttpClient.newBuilder().build();
            
            while (true) {
                String paginatedUrl = apiUrl + "?page_no=" + pageNo + "&page_size=" + pageSize;
                System.out.println("🔍 请求阿里云 API 第 " + pageNo + " 页：" + paginatedUrl);
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(paginatedUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
                
                HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (httpResponse.statusCode() != 200) {
                    System.err.println("❌ 阿里云 API 调用失败：" + httpResponse.statusCode());
                    break;
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
                
                // 解析模型数据
                for (int i = 0; i < modelsArray.size(); i++) {
                    JSONObject model = modelsArray.getJSONObject(i);
                    Map<String, Object> modelMap = new java.util.HashMap<>();
                    modelMap.put("model_id", model.getString("model"));
                    modelMap.put("model_name", model.getString("name"));
                    modelMap.put("description", model.getString("description"));
                    modelMap.put("provider", model.getString("provider"));
                    modelMap.put("capabilities", model.getJSONArray("capabilities"));
                    modelMap.put("published_time", model.getString("published_time"));
                    
                    // 解析价格信息
                    if (model.containsKey("prices")) {
                        JSONArray prices = model.getJSONArray("prices");
                        if (prices != null && prices.size() > 0) {
                            JSONObject firstPrice = prices.getJSONObject(0);
                            JSONArray priceDetails = firstPrice.getJSONArray("prices");
                            if (priceDetails != null && priceDetails.size() > 0) {
                                JSONObject inputPrice = priceDetails.getJSONObject(0);
                                modelMap.put("price", inputPrice.getString("price"));
                                modelMap.put("price_unit", inputPrice.getString("price_unit"));
                            }
                        }
                    }
                    
                    models.add(modelMap);
                    totalModels++;
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
            
        } catch (Exception e) {
            System.err.println("❌ 获取模型列表失败：" + e.getMessage());
            e.printStackTrace();
        }
        
        return models;
    }
    
    /**
     * 从数据库获取已同步的模型列表
     * @return 模型列表
     */
    public List<Map<String, Object>> getModelsFromDatabase() {
        try {
            String sql = "SELECT MODEL_ID, MODEL_NAME, STATUS, SORT_ORDER, CREATED_TIME FROM AI_MODEL_LIST ORDER BY SORT_ORDER, MODEL_ID";
            return platformJdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            System.err.println("❌ 从数据库获取模型列表失败：" + e.getMessage());
            return new ArrayList<>();
        }
    }
}
