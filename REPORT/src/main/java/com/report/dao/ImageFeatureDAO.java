package com.report.dao;

import com.report.dto.ImageFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 图像特征数据访问对象
 */
@Repository
public class ImageFeatureDAO {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 保存图像特征
     */
    public void saveFeature(ImageFeature feature) {
        String sql = "INSERT INTO PRODUCT_IMAGE_FEATURES (" +
                    "FEATURE_ID, PLUNO, IMAGE_URL, OSS_IMAGE_URL, " +
                    "FEATURE_VECTOR, FEATURE_DIMENSION, MODEL_VERSION, " +
                    "CREATED_BY, REMARK) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            // float[] 转 byte[]
            float[] features = feature.getFeatureVector();
            byte[] featureBytes = new byte[features.length * 4];
            java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(featureBytes);
            for (float f : features) {
                buffer.putFloat(f);
            }
            
            // 使用 Spring 的 JdbcTemplate 直接插入 byte[] 到 BLOB 字段
            jdbcTemplate.update(sql,
                feature.getFeatureId(),
                feature.getPluno(),
                feature.getImageUrl(),
                feature.getOssImageUrl(),
                featureBytes,  // 直接传入 byte[]，Spring 会自动处理
                feature.getFeatureDimension(),
                feature.getModelVersion(),
                feature.getCreatedBy(),
                feature.getRemark()
            );
            
            System.out.println("✅ 特征保存成功，维度：" + features.length);
        } catch (Exception e) {
            System.err.println("❌ 保存特征失败：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("保存特征失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 查询商品的所有特征
     */
    public List<Map<String, Object>> getFeaturesByPluno(String pluno) {
        String sql = "SELECT FEATURE_ID, PLUNO, IMAGE_URL, FEATURE_DIMENSION, " +
                    "CREATED_TIME, CREATED_BY FROM PRODUCT_IMAGE_FEATURES " +
                    "WHERE PLUNO = ? ORDER BY CREATED_TIME DESC";
        return jdbcTemplate.queryForList(sql, pluno);
    }
    
    /**
     * 查询所有特征（用于构建索引）
     */
    public List<Map<String, Object>> getAllFeatures() {
        String sql = "SELECT FEATURE_ID, PLUNO, FEATURE_VECTOR " +
                    "FROM PRODUCT_IMAGE_FEATURES " +
                    "WHERE FEATURE_VECTOR IS NOT NULL";
        return jdbcTemplate.queryForList(sql);
    }
    
    /**
     * 更新商品特征数量
     */
    public void updateFeatureCount(String pluno) {
        String sql = "UPDATE PRODUCT_FEATURES SET " +
                    "IMAGE_FEATURE_COUNT = (SELECT COUNT(*) FROM PRODUCT_IMAGE_FEATURES WHERE PLUNO = ?), " +
                    "HAS_IMAGE_FEATURES = CASE WHEN (SELECT COUNT(*) FROM PRODUCT_IMAGE_FEATURES WHERE PLUNO = ?) > 0 THEN 'Y' ELSE 'N' END, " +
                    "LAST_FEATURE_UPDATE = SYSDATE " +
                    "WHERE PLUNO = ?";
        jdbcTemplate.update(sql, pluno, pluno, pluno);
    }
}
