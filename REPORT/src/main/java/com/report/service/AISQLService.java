package com.report.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.report.config.MerchantDataSourceManager;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * AI SQL 生成服务
 * 
 * 数据源使用规范：
 * - 平台库：AI 配置（PRODUCT_APPKEY）、Prompt 配置、表结构过滤（AI_TABLE_FILTER）
 * - 商家库：表结构读取（USER_TAB_COLUMNS）、SQL 执行
 */
@Service
public class AISQLService {
    
    /**
     * 平台 JdbcTemplate
     * 用于：AI 配置、Prompt 配置、表结构过滤
     */
    @Autowired
    private JdbcTemplate platformJdbcTemplate;
    
    @Autowired
    private AIModelService aiModelService;
    
    @Autowired
    private MerchantDataSourceManager dataSourceManager;
    
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build();
    
    // Prompt 配置缓存
    private String roleDefinitionCache = null;
    private List<String> requirementsCache = null;
    private long cacheTime = 0;
    private static final long CACHE_TTL_MS = 60000; // 缓存 1 分钟
    
    /**
     * 获取商家 JdbcTemplate（用于表结构读取和 SQL 执行）
     * @return 商家 JdbcTemplate
     */
    public JdbcTemplate getBusinessJdbcTemplate() {
        JdbcTemplate merchantJdbc = dataSourceManager.getMerchantJdbcTemplate();
        return merchantJdbc != null ? merchantJdbc : platformJdbcTemplate;
    }
    
    /**
     * 获取所有表结构（用于 AI 生成 SQL）
     * 直接实现，避免循环依赖
     */
    private String getAllTablesSchema() {
        StringBuilder schema = new StringBuilder();
        
        // 1. 查询过滤表中配置的表（使用平台库）
        String tablesSql = "SELECT f.TABLE_NAME, f.TABLE_COMMENT FROM AI_TABLE_FILTER f WHERE f.ENABLED = 'Y' ORDER BY f.SORT_ORDER";
        List<Map<String, Object>> tables = platformJdbcTemplate.queryForList(tablesSql);
        
        // 2. 获取商家 JdbcTemplate
        JdbcTemplate businessJdbc = getBusinessJdbcTemplate();
        
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("TABLE_NAME");
            String tableComment = (String) table.get("TABLE_COMMENT");
            
            schema.append("### 表：").append(tableName);
            if (tableComment != null && !tableComment.isEmpty()) {
                schema.append("（").append(tableComment).append("）");
            }
            schema.append("\n\n");
            
            // 3. 查询表的列（使用商家库）
            String columnsSql = "SELECT c.COLUMN_NAME, c.DATA_TYPE, c.DATA_LENGTH, cm.COMMENTS, c.NULLABLE FROM USER_TAB_COLUMNS c LEFT JOIN USER_COL_COMMENTS cm ON c.TABLE_NAME = cm.TABLE_NAME AND c.COLUMN_NAME = cm.COLUMN_NAME WHERE c.TABLE_NAME = ? ORDER BY c.COLUMN_ID";
            List<Map<String, Object>> columns = businessJdbc.queryForList(columnsSql, tableName);
            
            schema.append("| 字段名 | 类型 | 长度 | 注释 | 可空 |\n");
            schema.append("|--------|------|------|------|------|\n");
            
            for (Map<String, Object> col : columns) {
                String colName = (String) col.get("COLUMN_NAME");
                String dataType = (String) col.get("DATA_TYPE");
                Number dataLength = (Number) col.get("DATA_LENGTH");
                String comment = (String) col.get("COMMENTS");
                String nullable = (String) col.get("NULLABLE");
                
                schema.append("| ").append(colName);
                schema.append(" | ").append(dataType);
                schema.append(" | ").append(dataLength != null ? dataLength.intValue() : "");
                schema.append(" | ").append(comment != null ? comment : "");
                schema.append(" | ").append(nullable);
                schema.append(" |\n");
            }
            
            schema.append("\n");
        }
        
        return schema.toString();
    }
    
    /**
     * 从数据库读取 API Key（使用平台库）
     * @return API Key
     */
    private String getApiKey() {
        try {
            String sql = "SELECT ACCESSKEYSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
            List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
            
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
     * 从数据库获取 AI 模型配置（使用平台库）
     * @return 模型配置 Map（包含 model 和 apiEndpoint）
     */
    private Map<String, String> getAIModelConfig() {
        Map<String, String> config = new HashMap<>();
        
        String sql = "SELECT ACCESSKEYID, ACCESSKEYSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
        List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
        
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("PRODUCT_APPKEY 表中没有配置 ALI_QWEN 的 API Key");
        }
        
        Map<String, Object> row = list.get(0);
        String model = (String) row.get("ACCESSKEYID");
        String apiKey = (String) row.get("ACCESSKEYSECRET");
        
        if (model == null || model.trim().isEmpty()) {
            throw new RuntimeException("PRODUCT_APPKEY 表中 ACCESSKEYID 为空");
        }
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("PRODUCT_APPKEY 表中 ACCESSKEYSECRET 为空");
        }
        
        config.put("model", model);
        config.put("apiKey", apiKey);
        
        // 根据模型类型设置 API 端点
        String apiEndpoint = getAPIEndpoint(model);
        config.put("apiEndpoint", apiEndpoint);
        
        return config;
    }
    
    /**
     * 根据模型名称获取 API 端点
     * @param model 模型名称
     * @return API 端点 URL
     */
    private String getAPIEndpoint(String model) {
        if (model == null || model.trim().isEmpty()) {
            throw new RuntimeException("模型名称为空");
        }
        
        // qwen-plus 使用纯文本模型端点
        if ("qwen-plus".equalsIgnoreCase(model)) {
            return "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
        }
        
        // qwen3 开头的模型使用多模态模型端点
        if (model.toLowerCase().startsWith("qwen3")) {
            return "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";
        }
        
        // 其他未知模型默认使用多模态端点
        return "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";
    }
    
    // 模型切换消息
    private String modelSwitchMessage = null;
    
    /**
     * 根据自然语言生成 SQL（不带对话历史，兼容旧接口）
     * @param question 用户问题
     * @return SQL 语句
     */
    public String generateSQL(String question) {
        return generateSQL(question, new java.util.ArrayList<>());
    }
    
    /**
     * 根据自然语言生成 SQL（带对话历史）
     * @param question 用户问题
     * @param history 对话历史
     * @return SQL 语句
     */
    public String generateSQL(String question, List<com.report.dto.ConversationContext.Dialogue> history) {
        // 1. 读取表结构（使用商家库）
        String schema = getAllTablesSchema();
        
        // 2. 构建 Prompt（带对话历史）
        String prompt = buildPromptWithHistory(schema, question, history);
        
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
     * 获取模型切换消息（如果有）
     */
    public String getModelSwitchMessage() {
        String msg = modelSwitchMessage;
        modelSwitchMessage = null; // 清除
        return msg;
    }
    
    /**
     * 重新生成 SQL（修正语法错误）
     * @param question 用户问题
     * @param originalSQL 原始失败的 SQL
     * @return 修正后的 SQL
     */
    public String regenerateSQL(String question, String originalSQL) {
        return regenerateSQL(question, originalSQL, new java.util.ArrayList<>());
    }
    
    /**
     * 重新生成 SQL（修正语法错误，带对话历史）
     * @param question 用户问题
     * @param originalSQL 原始失败的 SQL
     * @param history 对话历史
     * @return 修正后的 SQL
     */
    public String regenerateSQL(String question, String originalSQL, List<com.report.dto.ConversationContext.Dialogue> history) {
        // 1. 读取表结构（使用商家库）
        String schema = getAllTablesSchema();
        
        // 2. 构建修正 Prompt（带对话历史）
        String prompt = buildPromptWithHistory(schema, question, history);
        prompt += "\n\n⚠️ 注意：以下 SQL 在 Oracle 11g 上执行失败，语法不正确：\n";
        prompt += "```sql\n" + originalSQL + "\n```\n";
        prompt += "\n请修正为 Oracle 11g 兼容的语法，注意：\n";
        prompt += "1. 不能使用 FETCH FIRST n ROWS ONLY（这是 Oracle 12c 语法）\n";
        prompt += "2. 使用 SELECT * FROM (SELECT ... WHERE ROWNUM <= n) 替代\n";
        prompt += "3. 不能使用 OFFSET ... FETCH（这是 Oracle 12c 语法）\n";
        prompt += "4. 确保所有语法都是 Oracle 11g 支持的\n";
        prompt += "\n只返回修正后的 SQL，不要任何解释：";
        
        // 3. 调用 AI API
        String sql = callAI(prompt);
        
        // 4. 清理 SQL（移除 markdown 标记）
        sql = cleanSQL(sql);
        
        System.out.println("🔧 修正 SQL - 原始：" + originalSQL.substring(0, Math.min(100, originalSQL.length())) + "...");
        System.out.println("🔧 修正 SQL - 修正后：" + sql.substring(0, Math.min(100, sql.length())) + "...");
        
        return sql;
    }
    
    /**
     * 从数据库读取角色定义（使用平台库）
     */
    private String getRoleDefinition() {
        if (roleDefinitionCache != null && (System.currentTimeMillis() - cacheTime) < CACHE_TTL_MS) {
            return roleDefinitionCache;
        }
        
        try {
            String sql = "SELECT REQUIREMENT FROM AI_PROMPT_REQUIREMENTS WHERE CATEGORY = 'ROLE' AND ENABLED = 'Y' ORDER BY SORT_ORDER";
            List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
            
            StringBuilder roleDef = new StringBuilder();
            for (Map<String, Object> row : list) {
                String req = (String) row.get("REQUIREMENT");
                if (req != null && !req.trim().isEmpty()) {
                    if (roleDef.length() > 0) {
                        roleDef.append("\n");
                    }
                    roleDef.append(req);
                }
            }
            
            // 如果没有配置，使用默认值
            if (roleDef.length() == 0) {
                roleDef.append("你是一个专业的 Oracle SQL 专家，请根据以下数据库表结构，将用户的自然语言问题转换为 Oracle SQL 查询。");
            }
            
            roleDefinitionCache = roleDef.toString();
            cacheTime = System.currentTimeMillis();
            return roleDefinitionCache;
            
        } catch (Exception e) {
            System.err.println("⚠️ 读取角色定义失败：" + e.getMessage());
            roleDefinitionCache = "你是一个专业的 Oracle SQL 专家，请根据以下数据库表结构，将用户的自然语言问题转换为 Oracle SQL 查询。";
            cacheTime = System.currentTimeMillis();
            return roleDefinitionCache;
        }
    }
    
    /**
     * 从数据库读取要求列表（使用平台库）
     */
    private List<String> getRequirements() {
        if (requirementsCache != null && (System.currentTimeMillis() - cacheTime) < CACHE_TTL_MS) {
            return requirementsCache;
        }
        
        try {
            String sql = "SELECT REQUIREMENT FROM AI_PROMPT_REQUIREMENTS WHERE CATEGORY != 'ROLE' AND ENABLED = 'Y' ORDER BY SORT_ORDER";
            List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
            
            List<String> requirements = new ArrayList<>();
            for (Map<String, Object> row : list) {
                String req = (String) row.get("REQUIREMENT");
                if (req != null && !req.trim().isEmpty()) {
                    requirements.add(req);
                }
            }
            
            // 如果没有配置，使用默认值
            if (requirements.isEmpty()) {
                requirements.add("只返回 SQL 语句，不要任何解释");
                requirements.add("只使用 SELECT 语句，禁止使用 INSERT、UPDATE、DELETE、DROP 等");
                requirements.add("使用 Oracle 语法（如 NVL、TRUNC、TO_CHAR、TO_DATE 等）");
                requirements.add("如果问题不明确，返回最简单的查询");
                requirements.add("确保 SQL 可以执行");
                requirements.add("只查询必要的字段，不要 SELECT *");
                requirements.add("日期字段如果是字符串类型（如 BDATE），使用 TO_DATE(BDATE, 'YYYY-MM-DD HH24:MI:SS') 转换");
                requirements.add("使用 TRUNC(SYSDATE) 表示今天");
                requirements.add("聚合查询（SUM、COUNT 等）使用 NVL 处理 NULL 值，如 NVL(SUM(字段), 0)");
            }
            
            requirementsCache = requirements;
            cacheTime = System.currentTimeMillis();
            return requirements;
            
        } catch (Exception e) {
            System.err.println("⚠️ 读取要求列表失败：" + e.getMessage());
            
            // 返回默认值
            List<String> defaults = new ArrayList<>();
            defaults.add("只返回 SQL 语句，不要任何解释");
            defaults.add("只使用 SELECT 语句，禁止使用 INSERT、UPDATE、DELETE、DROP 等");
            defaults.add("使用 Oracle 语法（如 NVL、TRUNC、TO_CHAR、TO_DATE 等）");
            defaults.add("如果问题不明确，返回最简单的查询");
            defaults.add("确保 SQL 可以执行");
            defaults.add("只查询必要的字段，不要 SELECT *");
            defaults.add("日期字段如果是字符串类型（如 BDATE），使用 TO_DATE(BDATE, 'YYYY-MM-DD HH24:MI:SS') 转换");
            defaults.add("使用 TRUNC(SYSDATE) 表示今天");
            defaults.add("聚合查询（SUM、COUNT 等）使用 NVL 处理 NULL 值，如 NVL(SUM(字段), 0)");
            
            requirementsCache = defaults;
            cacheTime = System.currentTimeMillis();
            return defaults;
        }
    }
    
    /**
     * 刷新 Prompt 配置缓存（立刻生效）
     */
    public void refreshPromptCache() {
        roleDefinitionCache = null;
        requirementsCache = null;
        cacheTime = 0;
        System.out.println("✅ Prompt 配置缓存已刷新");
    }
    
    /**
     * 构建 Prompt（不带对话历史，兼容旧接口）
     */
    private String buildPrompt(String schema, String question) {
        return buildPromptWithHistory(schema, question, new java.util.ArrayList<>());
    }
    
    /**
     * 构建带对话历史的 Prompt
     */
    private String buildPromptWithHistory(String schema, String question, 
                                          List<com.report.dto.ConversationContext.Dialogue> history) {
        StringBuilder prompt = new StringBuilder();
        
        // 1. 角色定义（从数据库读取）
        prompt.append(getRoleDefinition());
        prompt.append("\n\n");
        
        // 2. 对话历史（如果有）
        if (!history.isEmpty()) {
            prompt.append("## 对话历史\n\n");
            for (int i = 0; i < history.size(); i++) {
                com.report.dto.ConversationContext.Dialogue d = history.get(i);
                prompt.append(i + 1).append(". 用户：").append(d.question).append("\n");
                prompt.append("   AI：").append(d.sql).append("\n\n");
            }
        }
        
        // 3. 当前问题
        prompt.append("## 当前问题\n\n");
        prompt.append(question).append("\n\n");
        
        // 4. 表结构（动态获取）
        prompt.append("## 数据库表结构\n\n");
        prompt.append(schema);
        
        // 5. 要求（从数据库读取）
        prompt.append("\n\n## 要求\n\n");
        List<String> requirements = getRequirements();
        for (int i = 0; i < requirements.size(); i++) {
            prompt.append((i + 1))
                  .append(". ")
                  .append(requirements.get(i))
                  .append("\n");
        }
        
        // 6. 特殊说明（仅在有历史时添加）
        if (!history.isEmpty()) {
            prompt.append("\n⚠️ 注意：请根据对话历史理解用户的意图，");
            prompt.append("特别注意代词（如'它'、'这个'、'上周'等）的指代对象。\n");
        }
        
        // 7. 结尾
        prompt.append("\n## SQL\n");
        
        return prompt.toString();
    }
    
    /**
     * 调用 AI API（支持重试和模型切换）
     */
    private String callAI(String prompt) {
        return callAIWithRetry(prompt, false);
    }
    
    /**
     * 调用 AI API（支持重试和模型切换）
     */
    private String callAIWithRetry(String prompt, boolean isRetry) {
        try {
            // 从数据库获取模型配置（使用平台库）
            Map<String, String> config = getAIModelConfig();
            String model = config.get("model");
            String apiKey = config.get("apiKey");
            
            // 使用 OpenAI 兼容模式端点
            String apiEndpoint = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new RuntimeException("API Key 未配置，请在 PRODUCT_APPKEY 表中添加 PLATFORM='ALI_QWEN'的记录");
            }
            
            // 构建请求体（OpenAI 兼容格式）
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            
            // messages 数组（OpenAI 格式）
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            
            // 构建 HTTP 请求
            RequestBody body = RequestBody.create(
                requestBody.toJSONString(),
                MediaType.parse("application/json")
            );
            
            Request request = new Request.Builder()
                .url(apiEndpoint)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
            
            // 发送请求
            try (Response response = httpClient.newCall(request).execute()) {
                int statusCode = response.code();
                System.out.println("📡 HTTP 状态码：" + statusCode);
                
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "无响应内容";
                    System.err.println("❌ API 调用失败：" + statusCode);
                    System.err.println("错误响应：" + errorBody);
                    
                    // 检查是否返回 403（token 耗尽）
                    if (statusCode == 403) {
                        System.err.println("⚠️ 模型 " + model + " token 耗尽（403），尝试切换模型...");
                        
                        try {
                            // 切换模型（自动获取下一个可用模型）
                            String switchMsg = aiModelService.switchModel(model);
                            System.out.println("✅ " + switchMsg);
                            
                            // 重试调用（使用新模型）
                            return callAIWithRetry(prompt, true);
                        } catch (RuntimeException e) {
                            System.err.println("❌ " + e.getMessage());
                            throw e;
                        }
                    }
                    
                    // 其他错误直接抛出
                    throw new IOException("API 调用失败：" + statusCode + " - " + errorBody);
                }
                
                String responseBody = response.body().string();
                System.out.println("✅ API 响应：" + responseBody.substring(0, Math.min(500, responseBody.length())));
                
                JSONObject result = JSON.parseObject(responseBody);
                
                // 解析响应（OpenAI 兼容格式）
                JSONArray choices = result.getJSONArray("choices");
                if (choices == null || choices.size() == 0) {
                    throw new RuntimeException("API 响应格式错误：缺少 choices 字段");
                }
                
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                if (message == null) {
                    throw new RuntimeException("API 响应格式错误：缺少 message 字段");
                }
                
                String text = message.getString("content");
                if (text == null || text.trim().isEmpty()) {
                    throw new RuntimeException("API 响应中未找到文本内容");
                }
                
                System.out.println("✅ 解析后的文本：" + text.substring(0, Math.min(100, text.length())) + "...");
                return text;
            }
            
        } catch (Exception e) {
            System.err.println("❌ AI 调用异常：" + e.getMessage());
            e.printStackTrace();
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
        
        // 3. 从 AI_TABLE_FILTER 表读取允许的表（使用平台库）
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
     * 从 AI_TABLE_FILTER 表读取允许的表（使用平台库）
     * @return 允许的表名数组
     */
    private String[] getAllowedTables() {
        String sql = "SELECT TABLE_NAME FROM AI_TABLE_FILTER WHERE ENABLED = 'Y' ORDER BY SORT_ORDER";
        List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
        
        List<String> allowedTablesList = new ArrayList<>();
        for (Map<String, Object> row : list) {
            String tableName = (String) row.get("TABLE_NAME");
            if (tableName != null && !tableName.trim().isEmpty()) {
                allowedTablesList.add(tableName);
            }
        }
        
        return allowedTablesList.toArray(new String[0]);
    }
}
