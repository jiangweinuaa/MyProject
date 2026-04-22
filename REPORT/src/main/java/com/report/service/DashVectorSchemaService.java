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

/**
 * DashVector Schema 检索服务（测试版）
 */
@Service
public class DashVectorSchemaService {

    @Autowired
    private JdbcTemplate platformJdbcTemplate;

    @Autowired
    private MerchantDataSourceManager dataSourceManager;

    private static final String COLLECTION_NAME = "table_schema";
    private static final String EMBEDDING_MODEL = "text-embedding-v4";
    private static final int DIMENSIONS = 1024;
    private static final int TOP_K = 10;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build();

    private Map<String, String> getDashVectorConfig() {
        try {
            String sql = "SELECT ACCESSKEYID as APIKEY, ACCESSKEYSECRET as ENDPOINT FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_DASHVECTOR'";
            List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
            if (list != null && !list.isEmpty()) {
                Map<String, Object> row = list.get(0);
                String apiKey = (String) row.get("APIKEY");
                String endpoint = (String) row.get("ENDPOINT");
                if (apiKey != null && endpoint != null) {
                    Map<String, String> config = new HashMap<>();
                    config.put("apiKey", apiKey);
                    config.put("endpoint", endpoint);
                    return config;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ 读取 DashVector 配置失败: " + e.getMessage());
        }
        return null;
    }

    public boolean isConfigured() {
        return getDashVectorConfig() != null;
    }

    private String getEndpoint() {
        Map<String, String> config = getDashVectorConfig();
        return config != null ? config.get("endpoint") : null;
    }

    private String getApiKey() {
        Map<String, String> config = getDashVectorConfig();
        return config != null ? config.get("apiKey") : null;
    }

    /**
     * 获取状态（不依赖 Collection 是否存在）
     */
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        boolean hasConfig = isConfigured();
        result.put("hasConfig", hasConfig);
        
        if (!hasConfig) {
            result.put("collectionExists", false);
            result.put("docCount", 0);
            result.put("message", "未配置 ALI_DASHVECTOR，请在 PRODUCT_APPKEY 表中配置");
            return result;
        }

        try {
            String url = getEndpoint() + "/v1/collections/" + COLLECTION_NAME + "/stats";
            String body = httpGet(url);
            if (body != null) {
                JSONObject json = JSON.parseObject(body);
                if (json.getInteger("code") == 0) {
                    JSONObject data = json.getJSONObject("data");
                    result.put("collectionExists", true);
                    result.put("docCount", data != null ? data.getInteger("doc_count") : 0);
                    return result;
                } else if (json.getInteger("code") == -2021) {
                    // Collection 不存在
                    result.put("collectionExists", false);
                    result.put("docCount", 0);
                    return result;
                } else {
                    result.put("collectionExists", false);
                    result.put("docCount", 0);
                    result.put("message", "DashVector 返回: code=" + json.getInteger("code") + ", msg=" + json.getString("message"));
                    return result;
                }
            }
            result.put("collectionExists", false);
            result.put("docCount", 0);
        } catch (Exception e) {
            result.put("collectionExists", false);
            result.put("docCount", 0);
            result.put("message", "连接失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 重建向量库（Collection 不存在时自动创建）
     */
    public Map<String, Object> rebuildAll() {
        Map<String, Object> result = new HashMap<>();
        List<String> success = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        try {
            if (!isConfigured()) {
                result.put("success", false);
                result.put("message", "DashVector 未配置");
                return result;
            }

            // 1. 确保 Collection 存在
            ensureCollection();

            // 2. 查询需要向量化的表
            String tablesSql = "SELECT TABLE_NAME, TABLE_COMMENT FROM AI_TABLE_FILTER WHERE ENABLED = 'Y' ORDER BY SORT_ORDER";
            List<Map<String, Object>> tables = platformJdbcTemplate.queryForList(tablesSql);
            System.out.println("📋 DashVector: 找到 " + tables.size() + " 张表需要向量化");

            // 3. 逐个向量化并插入（DashVector 支持 upsert，同 id 自动覆盖）
            List<JSONObject> docs = new ArrayList<>();
            for (Map<String, Object> table : tables) {
                String tableName = (String) table.get("TABLE_NAME");
                String tableComment = (String) table.get("TABLE_COMMENT");

                try {
                    System.out.println("🔄 DashVector: 正在处理 " + tableName);

                    // 构建表结构描述
                    String desc = buildTableDesc(tableName);

                    // 防御：空描述跳过向量化
                    if (desc == null || desc.trim().isEmpty()) {
                        System.err.println("⚠️ " + tableName + " 描述为空，跳过向量化");
                        failed.add(tableName + " (描述为空)");
                        continue;
                    }
                    System.out.println("📝 " + tableName + " 描述长度: " + desc.length() + " 字符");

                    // 生成向量
                    float[] vector = getEmbedding(desc);
                    if (vector == null || vector.length == 0) {
                        failed.add(tableName + " (Embedding 失败，请检查日志)");
                        continue;
                    }
                    System.out.println("✅ " + tableName + " 向量维度: " + vector.length);

                    // 构建 Doc
                    JSONObject doc = new JSONObject();
                    doc.put("id", tableName);
                    doc.put("vector", toFloatList(vector));

                    JSONObject fields = new JSONObject();
                    fields.put("table_name", tableName);
                    fields.put("table_comment", tableComment != null ? tableComment : "");
                    fields.put("schema_desc", desc);
                    doc.put("fields", fields);

                    docs.add(doc);
                    success.add(tableName);
                } catch (Exception e) {
                    System.err.println("❌ DashVector: " + tableName + " 失败: " + e.getMessage());
                    e.printStackTrace();
                    failed.add(tableName + " (" + e.getMessage() + ")");
                }
            }

            // 4. 批量 upsert 到 DashVector（使用 upsert 模式，同 id 自动覆盖）
            if (!docs.isEmpty()) {
                String endpoint = getEndpoint();
                JSONObject req = new JSONObject();
                req.put("docs", docs);
                req.put("mode", "upsert");
                String resp = httpPost(endpoint + "/v1/collections/" + COLLECTION_NAME + "/docs", req.toJSONString());
                System.out.println("📤 DashVector 批量 upsert 结果: " + resp);

                // 检查 upsert 是否成功
                if (resp != null) {
                    try {
                        JSONObject respJson = JSON.parseObject(resp);
                        int code = respJson.getInteger("code");
                        if (code != 0) {
                            String errMsg = respJson.getString("message");
                            System.err.println("❌ DashVector upsert 失败: code=" + code + ", " + errMsg);
                            result.put("success", false);
                            result.put("message", "DashVector upsert 失败: " + errMsg);
                            return result;
                        }
                        // 检查每个 doc 的操作结果
                        JSONArray output = respJson.getJSONArray("output");
                        if (output != null && !output.isEmpty()) {
                            int failCount = 0;
                            for (int i = 0; i < output.size(); i++) {
                                JSONObject item = output.getJSONObject(i);
                                if (item.getInteger("code") != 0) {
                                    failCount++;
                                    String docId = item.getString("id");
                                    String msg = item.getString("message");
                                    System.err.println("⚠️ doc[" + docId + "] 失败: " + msg);
                                }
                            }
                            if (failCount > 0) {
                                System.err.println("❌ DashVector upsert: " + failCount + "/" + output.size() + " 个文档失败");
                                result.put("success", false);
                                result.put("message", failCount + " 个文档 upsert 失败，请检查日志");
                                return result;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("⚠️ 解析 DashVector 响应失败: " + e.getMessage());
                    }
                }
            }

            result.put("success", true);
            result.put("total", tables.size());
            result.put("successCount", success.size());
            result.put("failedCount", failed.size());
            result.put("successTables", success);
            result.put("failedTables", failed);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "重建失败: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 检索相关表
     */
    public Map<String, Object> retrieve(String question) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (!isConfigured()) {
                result.put("success", false);
                result.put("message", "DashVector 未配置");
                return result;
            }

            // 1. 问题向量化
            float[] queryVector = getEmbedding(question);
            if (queryVector == null || queryVector.length == 0) {
                result.put("success", false);
                result.put("message", "问题向量化失败");
                return result;
            }

            // 2. DashVector 检索
            String endpoint = getEndpoint();
            JSONObject req = new JSONObject();
            req.put("vector", toFloatList(queryVector));
            req.put("topk", TOP_K);
            req.put("include_vector", false);
            req.put("include_fields", true);

            String resp = httpPost(endpoint + "/v1/collections/" + COLLECTION_NAME + "/query", req.toJSONString());

            List<Map<String, Object>> tables = new ArrayList<>();
            if (resp != null) {
                JSONObject json = JSON.parseObject(resp);
                if (json.getInteger("code") == 0) {
                    JSONArray data = json.getJSONArray("data");
                    if (data != null) {
                        for (int i = 0; i < data.size(); i++) {
                            JSONObject doc = data.getJSONObject(i);
                            JSONObject fields = doc.getJSONObject("fields");
                            Map<String, Object> item = new LinkedHashMap<>();
                            item.put("tableName", fields != null ? fields.getString("table_name") : doc.getString("id"));
                            item.put("tableComment", fields != null ? fields.getString("table_comment") : "");
                            item.put("similarity", Math.round(doc.getDoubleValue("score") * 10000.0) / 100.0);
                            item.put("schemaDesc", fields != null ? fields.getString("schema_desc") : "");
                            tables.add(item);
                        }
                    }
                }
            }

            result.put("success", true);
            result.put("question", question);
            result.put("tables", tables);
            result.put("vectorDim", queryVector.length);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检索失败: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private void ensureCollection() throws IOException {
        String endpoint = getEndpoint();
        String body = httpGet(endpoint + "/v1/collections/" + COLLECTION_NAME);
        if (body != null) {
            JSONObject json = JSON.parseObject(body);
            if (json.getInteger("code") == 0) return; // 已存在
        }

        // 创建 Collection
        JSONObject req = new JSONObject();
        req.put("name", COLLECTION_NAME);
        req.put("dimension", DIMENSIONS);
        req.put("metric", "cosine");

        String resp = httpPost(endpoint + "/v1/collections", req.toJSONString());
        System.out.println("📦 DashVector Collection 创建结果: " + resp);
    }

    private String buildTableDesc(String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("数据库表名：").append(tableName).append("\n");

        // 从 AI_TABLE_FILTER 获取表注释
        try {
            String filterSql = "SELECT TABLE_COMMENT FROM AI_TABLE_FILTER WHERE TABLE_NAME = ?";
            List<Map<String, Object>> rows = platformJdbcTemplate.queryForList(filterSql, tableName);
            if (!rows.isEmpty()) {
                String tableComment = (String) rows.get(0).get("TABLE_COMMENT");
                if (tableComment != null && !tableComment.isEmpty()) {
                    sb.append("表注释：").append(tableComment).append("\n");
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ 读取 " + tableName + " 注释失败: " + e.getMessage());
        }

        // 尝试从商家库读取字段信息（fallback 到平台库，与智问逻辑一致）
        JdbcTemplate businessJdbc = dataSourceManager.getMerchantJdbcTemplate();
        if (businessJdbc == null) {
            businessJdbc = platformJdbcTemplate;
            System.out.println("🔍 " + tableName + " - 商家库未配置，使用平台库读取表结构");
        } else {
            System.out.println("🔍 " + tableName + " - 使用商家库读取表结构");
        }
        
        sb.append("字段列表：\n");
        String columnsSql = "SELECT c.COLUMN_NAME, c.DATA_TYPE, c.NULLABLE, cm.COMMENTS " +
                           "FROM USER_TAB_COLUMNS c " +
                           "LEFT JOIN USER_COL_COMMENTS cm ON c.TABLE_NAME = cm.TABLE_NAME AND c.COLUMN_NAME = cm.COLUMN_NAME " +
                           "WHERE c.TABLE_NAME = ? ORDER BY c.COLUMN_ID";

        try {
            List<Map<String, Object>> columns = businessJdbc.queryForList(columnsSql, tableName);
            System.out.println("🔍 " + tableName + " - 读取到 " + (columns != null ? columns.size() : "null") + " 个字段");
            if (columns != null && !columns.isEmpty()) {
                int count = 0;
                for (Map<String, Object> col : columns) {
                    String colName = (String) col.get("COLUMN_NAME");
                    String dataType = (String) col.get("DATA_TYPE");
                    String nullable = (String) col.get("NULLABLE");
                    String comment = (String) col.get("COMMENTS");

                    sb.append("  ").append(colName);
                    sb.append(" (").append(dataType).append(")");
                    if (comment != null && !comment.isEmpty()) {
                        sb.append("：").append(comment);
                    }
                    if ("Y".equals(nullable)) {
                        sb.append(" [可空]");
                    }
                    sb.append("\n");
                    count++;
                }
                String desc = sb.toString();
                System.out.println("✅ " + tableName + " 描述长度: " + desc.length() + " 字符, 前100: " + desc.substring(0, Math.min(100, desc.length())));
                return desc;
            } else {
                System.out.println("⚠️ " + tableName + " - 平台库中未找到该表字段");
            }
        } catch (Exception e) {
            System.err.println("⚠️ 读取 " + tableName + " 字段失败: " + e.getMessage());
        }

        // 降级：使用基本信息作为描述
        sb.append("字段列表：暂无法读取，表名为 ").append(tableName);
        String desc = sb.toString();
        System.out.println("⚠️ " + tableName + " 降级描述长度: " + desc.length() + " 字符: " + desc);
        return desc;
    }

    private float[] getEmbedding(String text) throws IOException {
        String apiKey = getDashScopeApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("⚠️ 无法获取 DashScope API Key");
            return new float[0];
        }

        JSONArray inputArray = new JSONArray();
        inputArray.add(text);
        JSONObject req = new JSONObject();
        req.put("model", EMBEDDING_MODEL);
        req.put("input", inputArray);
        req.put("dimensions", DIMENSIONS);

        RequestBody body = RequestBody.create(
            req.toJSONString(),
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url("https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings")
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String err = response.body() != null ? response.body().string() : "无响应";
                System.err.println("❌ Embedding 失败: " + response.code() + " " + err);
                return new float[0];
            }

            String responseBody = response.body().string();
            JSONObject result = JSON.parseObject(responseBody);
            JSONArray dataArray = result.getJSONArray("data");
            if (dataArray != null && dataArray.size() > 0) {
                JSONArray embedding = dataArray.getJSONObject(0).getJSONArray("embedding");
                float[] vector = new float[embedding.size()];
                for (int i = 0; i < embedding.size(); i++) {
                    vector[i] = embedding.getFloatValue(i);
                }
                return vector;
            }
        }
        return new float[0];
    }

    private String getDashScopeApiKey() {
        try {
            String sql = "SELECT ACCESSKEYSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
            List<Map<String, Object>> list = platformJdbcTemplate.queryForList(sql);
            if (list != null && !list.isEmpty()) {
                return (String) list.get(0).get("ACCESSKEYSECRET");
            }
        } catch (Exception e) {}
        return null;
    }

    private List<Float> toFloatList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float v : arr) list.add(v);
        return list;
    }

    private String httpGet(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .addHeader("dashvector-auth-token", getApiKey())
            .get()
            .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : null;
        }
    }

    private String httpPost(String url, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .addHeader("dashvector-auth-token", getApiKey())
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : null;
        }
    }
}
