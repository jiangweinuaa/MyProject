package com.report.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/embedding-test")
@CrossOrigin(origins = "*")
public class EmbeddingTestController {

    @Autowired
    private JdbcTemplate platformJdbcTemplate;

    @GetMapping("/api-key")
    public Map<String, Object> getApiKey() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> list = platformJdbcTemplate.queryForList(
                "SELECT PLATFORM, ACCESSKEYID, ACCESSKEYSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'");
            if (!list.isEmpty()) {
                Map<String, Object> row = list.get(0);
                String secret = (String) row.get("ACCESSKEYSECRET");
                result.put("keyLength", secret != null ? secret.length() : 0);
                result.put("keyPrefix", secret != null && secret.length() > 8 ? secret.substring(0, 8) : "");
                result.put("keyId", row.get("ACCESSKEYID"));
            }
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @GetMapping("/test")
    public Map<String, Object> test() throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();

        // Get API key
        String apiKeySql = "SELECT ACCESSKEYSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
        List<Map<String, Object>> list = platformJdbcTemplate.queryForList(apiKeySql);
        String apiKey = list.isEmpty() ? null : (String) list.get(0).get("ACCESSKEYSECRET");
        result.put("hasKey", apiKey != null && !apiKey.isEmpty());

        String testText = "数据库表名：CRM_CHANNEL\n字段列表：\n  ID (VARCHAR2)：主键\n  NAME (VARCHAR2)：名称";

        // Test 1: OpenAI compatible format
        JSONObject req1 = new JSONObject();
        req1.put("model", "text-embedding-v4");
        req1.put("input", new JSONArray().add(testText));
        req1.put("dimensions", 1024);
        String json1 = req1.toJSONString();
        result.put("format1", "openai-compatible");
        result.put("request1", json1);

        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS).build();

        try {
            okhttp3.RequestBody body = okhttp3.RequestBody.create(json1, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                .url("https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body).build();
            try (Response resp = client.newCall(request).execute()) {
                result.put("status1", resp.code());
                result.put("response1", resp.body() != null ? resp.body().string() : "");
            }
        } catch (Exception e) {
            result.put("error1", e.getMessage());
        }

        // Test 2: DashScope native format
        JSONObject req2 = new JSONObject();
        req2.put("model", "text-embedding-v4");
        JSONObject input = new JSONObject();
        input.put("texts", new JSONArray().add(testText));
        req2.put("input", input);
        String json2 = req2.toJSONString();
        result.put("format2", "dashscope-native");
        result.put("request2", json2);

        try {
            okhttp3.RequestBody body = okhttp3.RequestBody.create(json2, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                .url("https://dashscope.aliyuncs.com/api/v1/services/embeddings/text-embedding/text-embedding")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body).build();
            try (Response resp = client.newCall(request).execute()) {
                result.put("status2", resp.code());
                result.put("response2", resp.body() != null ? resp.body().string() : "");
            }
        } catch (Exception e) {
            result.put("error2", e.getMessage());
        }

        return result;
    }
}
