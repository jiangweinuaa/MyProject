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
 * 表结构向量嵌入服务
 * 功能：
 * 1. 将表结构信息向量化并存储到 AI_TABLE_EMBEDDING
 * 2. 根据用户问题检索相关表
 * 
 * 向量模型：DashScope text-embedding-v3
 * 存储：Oracle CLOB 字段
 * 检索：Java 余弦相似度计算（表数量少，不需要专用向量库）
 */
@Service
public class SchemaEmbeddingService {

    @Autowired
    private JdbcTemplate platformJdbcTemplate;

    @Autowired
    private MerchantDataSourceManager dataSourceManager;

    private static final String EMBEDDING_MODEL = "text-embedding-v3";
    private static final int TOP_K = 10;  // 默认返回 Top 10 张表

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build();

    /**
     * 重建所有表的向量（全量）
     */
    public Map<String, Object> rebuildAll() {
        Map<String, Object> result = new HashMap<>();
        List<String> success = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        // 1. 查询 AI_TABLE_FILTER 中启用的表
        String tablesSql = "SELECT TABLE_NAME, TABLE_COMMENT FROM AI_TABLE_FILTER WHERE ENABLED = 'Y' ORDER BY SORT_ORDER";
        List<Map<String, Object>> tables = platformJdbcTemplate.queryForList(tablesSql);
        System.out.println("📋 找到 " + tables.size() + " 张需要向量化的表");

        // 2. 逐个生成向量
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("TABLE_NAME");
            String tableComment = (String) table.get("TABLE_COMMENT");
            String eid = "99";

            try {
                System.out.println("🔄 正在向量化表: " + tableName);
                embedAndSave(tableName, tableComment, eid);
                success.add(tableName);
                System.out.println("✅ " + tableName + " 向量化完成");
            } catch (Exception e) {
                System.err.println("❌ " + tableName + " 向量化失败: " + e.getMessage());
                failed.add(tableName + " (" + e.getMessage() + ")");
            }
        }

        result.put("success", true);
        result.put("total", tables.size());
        result.put("successCount", success.size());
        result.put("failedCount", failed.size());
        result.put("successTables", success);
        result.put("failedTables", failed);
        return result;
    }

    /**
     * 根据用户问题检索相关表
     */
    public Map<String, Object> findRelevantTables(String question) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 对用户问题进行向量化
            double[] questionVector = getEmbedding(question);
            if (questionVector == null || questionVector.length == 0) {
                result.put("success", false);
                result.put("message", "向量化失败，请检查 API 配置");
                return result;
            }

            // 2. 查询所有已向量化的表
            String sql = "SELECT TABLE_NAME, TABLE_DESC, VECTOR_DATA FROM AI_TABLE_EMBEDDING";
            List<Map<String, Object>> rows = platformJdbcTemplate.queryForList(sql);

            // 3. 计算余弦相似度
            List<TableSimilarity> similarities = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                String tableName = (String) row.get("TABLE_NAME");
                String tableDesc = (String) row.get("TABLE_DESC");
                String vectorData = clobToString(row.get("VECTOR_DATA"));

                if (vectorData != null && !vectorData.isEmpty()) {
                    try {
                        double[] tableVector = parseVector(vectorData);
                        double score = cosineSimilarity(questionVector, tableVector);
                        similarities.add(new TableSimilarity(tableName, tableDesc, score));
                    } catch (Exception e) {
                        // 向量解析失败，跳过
                    }
                }
            }

            // 4. 按相似度排序，取 Top K
            similarities.sort((a, b) -> Double.compare(b.score, a.score));
            int limit = Math.min(TOP_K, similarities.size());

            List<Map<String, Object>> tables = new ArrayList<>();
            for (int i = 0; i < limit; i++) {
                TableSimilarity ts = similarities.get(i);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("tableName", ts.tableName);
                item.put("tableDesc", ts.tableDesc);
                item.put("similarity", Math.round(ts.score * 10000.0) / 100.0);  // 保留 2 位小数
                tables.add(item);
            }

            result.put("success", true);
            result.put("question", question);
            result.put("totalTables", similarities.size());
            result.put("tables", tables);
            result.put("vectorDim", questionVector.length);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检索失败：" + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取已向量化的表列表
     */
    public Map<String, Object> getEmbeddingStatus() {
        Map<String, Object> result = new HashMap<>();

        // 已向量化的表
        String sql = "SELECT TABLE_NAME, TABLE_DESC, MODEL_ID, UPDATED_TIME FROM AI_TABLE_EMBEDDING ORDER BY TABLE_NAME";
        List<Map<String, Object>> embedded = platformJdbcTemplate.queryForList(sql);

        // AI_TABLE_FILTER 中启用的表
        String filterSql = "SELECT TABLE_NAME, TABLE_COMMENT FROM AI_TABLE_FILTER WHERE ENABLED = 'Y' ORDER BY TABLE_NAME";
        List<Map<String, Object>> allTables = platformJdbcTemplate.queryForList(filterSql);

        // 计算差集
        Set<String> embeddedNames = new HashSet<>();
        for (Map<String, Object> row : embedded) {
            embeddedNames.add((String) row.get("TABLE_NAME"));
        }

        List<String> notEmbedded = new ArrayList<>();
        for (Map<String, Object> row : allTables) {
            if (!embeddedNames.contains(row.get("TABLE_NAME"))) {
                notEmbedded.add((String) row.get("TABLE_NAME"));
            }
        }

        result.put("success", true);
        result.put("totalInFilter", allTables.size());
        result.put("embeddedCount", embedded.size());
        result.put("notEmbeddedCount", notEmbedded.size());
        result.put("notEmbeddedTables", notEmbedded);
        result.put("embeddedTables", embedded);

        return result;
    }

    // ==================== 内部方法 ====================

    /**
     * 为单个表生成向量并存储
     */
    private void embedAndSave(String tableName, String tableComment, String eid) throws Exception {
        // 1. 构建表描述文本（表名 + 注释 + 字段信息）
        String tableDesc = buildTableDesc(tableName, tableComment);
        String allColumnsDesc = buildAllColumnsDesc(tableName);

        // 2. 调用 DashScope 生成向量
        double[] vector = getEmbedding(allColumnsDesc);
        if (vector == null || vector.length == 0) {
            throw new RuntimeException("向量化失败，API 无返回");
        }

        String vectorJson = JSON.toJSONString(vector);

        // 3. 存储到数据库（先查是否存在，避免 MERGE 的 LOB 参数顺序问题）
        String checkSql = "SELECT COUNT(*) FROM AI_TABLE_EMBEDDING WHERE TABLE_NAME = ? AND EID = ?";
        Integer count = platformJdbcTemplate.queryForObject(checkSql, Integer.class, tableName, eid);

        if (count != null && count > 0) {
            String updateSql = "UPDATE AI_TABLE_EMBEDDING SET TABLE_DESC = ?, MODEL_ID = ?, ALL_COLUMNS_DESC = ?, VECTOR_DATA = ?, UPDATED_TIME = SYSDATE WHERE TABLE_NAME = ? AND EID = ?";
            platformJdbcTemplate.update(updateSql, tableDesc, EMBEDDING_MODEL, allColumnsDesc, vectorJson, tableName, eid);
        } else {
            String insertSql = "INSERT INTO AI_TABLE_EMBEDDING (TABLE_NAME, EID, TABLE_DESC, MODEL_ID, UPDATED_TIME, ALL_COLUMNS_DESC, VECTOR_DATA) VALUES (?, ?, ?, ?, SYSDATE, ?, ?)";
            platformJdbcTemplate.update(insertSql, tableName, eid, tableDesc, EMBEDDING_MODEL, allColumnsDesc, vectorJson);
        }
    }

    /**
     * 构建表简要描述
     */
    private String buildTableDesc(String tableName, String tableComment) {
        StringBuilder sb = new StringBuilder();
        sb.append("表名：").append(tableName);
        if (tableComment != null && !tableComment.isEmpty()) {
            sb.append("，注释：").append(tableComment);
        }
        return sb.toString();
    }

    /**
     * 构建完整表结构描述（用于 embedding）
     */
    private String buildAllColumnsDesc(String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("表名：").append(tableName).append("\n");

        // 先从 AI_TABLE_FILTER 获取表注释（兜底）
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
            System.err.println("⚠️ 读取 " + tableName + " 表注释失败: " + e.getMessage());
        }

        // 尝试从商家库读取字段信息（fallback 到平台库，与智问逻辑一致）
        JdbcTemplate businessJdbc = dataSourceManager.getMerchantJdbcTemplate();
        if (businessJdbc == null) {
            businessJdbc = platformJdbcTemplate;
            System.out.println("🔍 " + tableName + " - 使用平台库读取表结构");
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
                    }
                    String desc = sb.toString();
                    System.out.println("🔍 " + tableName + " - 描述长度: " + desc.length() + " 字符");
                    return desc;
                }
            } catch (Exception e) {
                System.err.println("⚠️ 读取 " + tableName + " 字段失败: " + e.getMessage());
            }

        // 降级：使用表名作为 embedding 输入
        sb.append("字段列表：（暂无法读取）");
        String desc = sb.toString();
        System.out.println("⚠️ " + tableName + " 降级 - 描述长度: " + desc.length() + " 字符");
        return desc;
    }

    /**
     * 调用 DashScope Embedding API
     */
    private double[] getEmbedding(String text) throws IOException {
        // 1. 获取 API Key
        String apiKeySql = "SELECT ACCESSKEYSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_QWEN'";
        List<Map<String, Object>> list = platformJdbcTemplate.queryForList(apiKeySql);
        String apiKey = "";
        if (list != null && !list.isEmpty()) {
            apiKey = (String) list.get(0).get("ACCESSKEYSECRET");
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("⚠️ 无法获取 Embedding API Key");
            return new double[0];
        }

        // 2. 构建请求
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", EMBEDDING_MODEL);
        JSONArray inputArr = new JSONArray();
        inputArr.add(text);
        requestBody.put("input", inputArr);

        RequestBody body = RequestBody.create(
            requestBody.toJSONString(),
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
                String errorBody = response.body() != null ? response.body().string() : "无响应";
                System.err.println("❌ Embedding API 失败：" + response.code() + " " + errorBody);
                return new double[0];
            }

            String responseBody = response.body().string();
            JSONObject result = JSON.parseObject(responseBody);
            JSONArray dataArray = result.getJSONArray("data");
            if (dataArray != null && dataArray.size() > 0) {
                JSONObject firstData = dataArray.getJSONObject(0);
                JSONArray embedding = firstData.getJSONArray("embedding");
                double[] vector = new double[embedding.size()];
                for (int i = 0; i < embedding.size(); i++) {
                    vector[i] = embedding.getDoubleValue(i);
                }
                return vector;
            }
        }

        return new double[0];
    }

    /**
     * 余弦相似度
     */
    private double cosineSimilarity(double[] a, double[] b) {
        if (a.length != b.length) return 0;

        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) return 0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * 解析向量 JSON 字符串
     */
    private double[] parseVector(String json) {
        JSONArray arr = JSON.parseArray(json);
        double[] vector = new double[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            vector[i] = arr.getDoubleValue(i);
        }
        return vector;
    }

    /**
     * CLOB 转 String
     */
    private String clobToString(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        return obj.toString();
    }

    /**
     * 表相似度内部类
     */
    private static class TableSimilarity {
        String tableName;
        String tableDesc;
        double score;

        TableSimilarity(String tableName, String tableDesc, double score) {
            this.tableName = tableName;
            this.tableDesc = tableDesc;
            this.score = score;
        }
    }
}
