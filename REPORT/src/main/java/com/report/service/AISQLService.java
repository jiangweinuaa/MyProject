package com.report.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * AI SQL 生成服务
 * 调用通义千问 API 生成 SQL
 */
@Service
public class AISQLService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private SchemaService schemaService;
    
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build();
    
    /**
     * 从数据库获取 API Key
     * @return API Key
     */
    private String getApiKey() {
        try {
            String sql = "SELECT ACCESSKEYSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            
            if (list != null && !list.isEmpty()) {
                return (String) list.get(0).get("ACCESSKEYSECRET");
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("❌ 读取 API Key 失败：" + e.getMessage());
            return null;
        }
    }
    
    /**
     * 根据自然语言生成 SQL
     * @param question 用户问题
     * @return SQL 语句
     */
    public String generateSQL(String question) {
        // 1. 读取表结构
        String schema = schemaService.getAllTables();
        
        // 2. 构建 Prompt
        String prompt = buildPrompt(schema, question);
        
        // 记录完整的 Prompt 到日志
        System.out.println("🤖 传递给大模型的完整 Prompt:");
        System.out.println("========================================");
        System.out.println(prompt);
        System.out.println("========================================");
        
        // 3. 调用 AI API
        String sql = callAI(prompt);
        
        // 4. 清理 SQL（移除 markdown 标记）
        sql = cleanSQL(sql);
        
        return sql;
    }
    
    /**
     * 构建 Prompt
     */
    private String buildPrompt(String schema, String question) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的 Oracle SQL 专家，请根据以下数据库表结构，将用户的自然语言问题转换为 Oracle SQL 查询。\n\n");
        prompt.append("## 数据库表结构\n\n");
        prompt.append(schema);
        prompt.append("\n\n## 用户问题\n\n");
        prompt.append(question);
        prompt.append("\n\n## 要求\n\n");
        prompt.append("1. 只返回 SQL 语句，不要任何解释\n");
        prompt.append("2. 只使用 SELECT 语句，禁止使用 INSERT、UPDATE、DELETE、DROP 等\n");
        prompt.append("3. 使用 Oracle 语法（如 NVL、TRUNC、TO_CHAR、TO_DATE 等）\n");
        prompt.append("4. 如果问题不明确，返回最简单的查询\n");
        prompt.append("5. 确保 SQL 可以执行\n");
        prompt.append("6. 只查询必要的字段，不要 SELECT *\n");
        prompt.append("7. 日期字段如果是字符串类型（如 BDATE），使用 TO_DATE(BDATE, 'YYYY-MM-DD HH24:MI:SS') 转换\n");
        prompt.append("8. 使用 TRUNC(SYSDATE) 表示今天\n");
        prompt.append("9. 聚合查询（SUM、COUNT 等）使用 NVL 处理 NULL 值，如 NVL(SUM(字段), 0)\n\n");
        prompt.append("## SQL\n");
        
        return String.format(prompt.toString(), schema, question);
    }
    
    /**
     * 调用 AI API
     */
    private String callAI(String prompt) {
        try {
            // 从数据库获取 API Key
            String apiKey = getApiKey();
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new RuntimeException("API Key 未配置，请在 PRODUCT_APPKEY 表中添加 PLATFORM='ALI_QWEN'的记录");
            }
            
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "qwen-plus");
            
            JSONObject input = new JSONObject();
            List<Map<String, String>> messages = new ArrayList<>();
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            input.put("messages", messages);
            requestBody.put("input", input);
            
            // 构建 HTTP 请求
            RequestBody body = RequestBody.create(
                requestBody.toJSONString(),
                MediaType.parse("application/json")
            );
            
            Request request = new Request.Builder()
                .url("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
            
            // 发送请求
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("API 调用失败：" + response.code());
                }
                
                String responseBody = response.body().string();
                JSONObject result = JSON.parseObject(responseBody);
                
                // 解析响应
                JSONObject output = result.getJSONObject("output");
                String text = output.getString("text");
                
                return text;
            }
            
        } catch (Exception e) {
            throw new RuntimeException("AI 调用失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 清理 SQL（移除 markdown 标记）
     */
    private String cleanSQL(String sql) {
        if (sql == null) {
            return null;
        }
        
        // 移除 ```sql ... ``` 标记
        sql = sql.replaceAll("```sql\\s*", "");
        sql = sql.replaceAll("```\\s*", "");
        sql = sql.trim();
        
        // 移除开头的 "SQL：" 等文字
        sql = sql.replaceAll("^[Ss][Qq][Ll]：?\\s*", "");
        
        return sql;
    }
    
    /**
     * 验证 SQL（安全检查）
     */
    public boolean validateSQL(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }
        
        String upperSQL = sql.trim().toUpperCase();
        
        // 1. 只允许 SELECT
        if (!upperSQL.startsWith("SELECT")) {
            return false;
        }
        
        // 2. 禁止危险操作
        String[] dangerous = {
            "DROP ", "DELETE ", "UPDATE ", "INSERT ", 
            "TRUNCATE ", "ALTER ", "CREATE ", "GRANT ", "REVOKE ",
            "EXEC ", "EXECUTE "
        };
        
        for (String keyword : dangerous) {
            if (upperSQL.contains(keyword)) {
                return false;
            }
        }
        
        // 3. 从 AI_TABLE_FILTER 表读取允许的表
        String[] allowedTables = getAllowedTables();
        
        boolean hasAllowedTable = false;
        for (String table : allowedTables) {
            if (upperSQL.contains(table)) {
                hasAllowedTable = true;
                break;
            }
        }
        
        if (!hasAllowedTable) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 从 AI_TABLE_FILTER 表读取允许的表
     * @return 允许的表名数组
     */
    private String[] getAllowedTables() {
        try {
            String sql = "SELECT TABLE_NAME FROM AI_TABLE_FILTER WHERE ENABLED = 'Y' ORDER BY SORT_ORDER";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            
            List<String> allowedTablesList = new ArrayList<>();
            for (Map<String, Object> row : list) {
                String tableName = (String) row.get("TABLE_NAME");
                if (tableName != null && !tableName.trim().isEmpty()) {
                    allowedTablesList.add(tableName);
                }
            }
            
            // 如果数据库中没有配置，使用默认列表
            if (allowedTablesList.isEmpty()) {
                allowedTablesList = Arrays.asList(
                    "SALES", "DCP_SALE", "PRODUCT", "STOCK", "SHOP", "CATEGORY",
                    "PRODUCT_FEATURES", "PRODUCT_TRAINING_SAMPLES",
                    "PRODUCT_RECOGNITION_LOGS", "NL_QUERY_RULES"
                );
            }
            
            return allowedTablesList.toArray(new String[0]);
            
        } catch (Exception e) {
            System.err.println("⚠️ 读取允许的表失败：" + e.getMessage());
            // 返回默认列表
            return new String[]{
                "SALES", "DCP_SALE", "PRODUCT", "STOCK", "SHOP", "CATEGORY",
                "PRODUCT_FEATURES", "PRODUCT_TRAINING_SAMPLES",
                "PRODUCT_RECOGNITION_LOGS", "NL_QUERY_RULES"
            };
        }
    }
}
