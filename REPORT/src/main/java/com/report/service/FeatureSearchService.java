package com.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;

/**
 * 特征检索服务
 * 使用向量相似度匹配商品
 */
@Service
public class FeatureSearchService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 根据特征向量检索最相似的商品
     * @param queryFeatures 查询特征向量（768 维）
     * @param topK 返回前 K 个最相似的商品
     * @return 相似度列表（包含 PLUNO 和相似度）
     */
    public List<Map<String, Object>> searchSimilarProducts(float[] queryFeatures, int topK) {
        // 查询所有特征
        String sql = "SELECT FEATURE_ID, PLUNO, FEATURE_VECTOR FROM PRODUCT_IMAGE_FEATURES WHERE FEATURE_VECTOR IS NOT NULL";
        List<Map<String, Object>> features = jdbcTemplate.queryForList(sql);
        
        if (features == null || features.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 计算相似度
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (Map<String, Object> feature : features) {
            try {
                Object blobObj = feature.get("FEATURE_VECTOR");
                if (blobObj == null) {
                    continue;
                }
                
                // 读取 BLOB 数据（Oracle 可能返回 byte[] 或 Blob）
                byte[] bytes;
                if (blobObj instanceof byte[]) {
                    bytes = (byte[]) blobObj;
                } else if (blobObj instanceof Blob) {
                    Blob blob = (Blob) blobObj;
                    bytes = blob.getBytes(1, (int) blob.length());
                } else {
                    System.err.println("⚠️ 未知的 BLOB 类型：" + blobObj.getClass());
                    continue;
                }
                
                float[] storedFeatures = bytesToFloats(bytes);
                
                // 计算余弦相似度
                double similarity = cosineSimilarity(queryFeatures, storedFeatures);
                
                Map<String, Object> result = new HashMap<>();
                result.put("pluno", feature.get("PLUNO"));
                result.put("similarity", similarity);
                result.put("featureId", feature.get("FEATURE_ID"));
                
                results.add(result);
            } catch (SQLException e) {
                System.err.println("读取特征向量失败：" + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 按相似度降序排序
        results.sort((a, b) -> Double.compare((Double) b.get("similarity"), (Double) a.get("similarity")));
        
        // 返回前 K 个
        int limit = Math.min(topK, results.size());
        return results.subList(0, limit);
    }
    
    /**
     * 计算两个特征向量的余弦相似度
     * @param features1 特征向量 1
     * @param features2 特征向量 2
     * @return 余弦相似度（0-1，越大越相似）
     */
    public double cosineSimilarity(float[] features1, float[] features2) {
        if (features1 == null || features2 == null || 
            features1.length != features2.length) {
            return 0.0;
        }
        
        float dotProduct = 0.0f;
        float norm1 = 0.0f;
        float norm2 = 0.0f;
        
        for (int i = 0; i < features1.length; i++) {
            dotProduct += features1[i] * features2[i];
            norm1 += features1[i] * features1[i];
            norm2 += features2[i] * features2[i];
        }
        
        if (norm1 == 0.0f || norm2 == 0.0f) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    /**
     * byte[] 转 float[]
     */
    private float[] bytesToFloats(byte[] bytes) {
        float[] floats = new float[bytes.length / 4];
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(bytes);
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        
        for (int i = 0; i < floats.length; i++) {
            floats[i] = buffer.getFloat();
        }
        
        return floats;
    }
}
