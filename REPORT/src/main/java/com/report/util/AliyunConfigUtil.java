package com.report.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里云配置工具类
 * 从数据库 PRODUCT_APPKEY 表获取 ACCESSKEYID 和 ACCESSKEYSECRET
 */
@Component
public class AliyunConfigUtil {
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 从数据库获取阿里云配置
     * SQL: SELECT ACCESSKEYID as APPKEY, ACCESSKEYSECRET as APPSECRET from PRODUCT_APPKEY where PLATFORM = 'ALI'
     * SQL: SELECT ACCESSKEYID as bucket, ACCESSKEYSECRET as endpoint from PRODUCT_APPKEY where PLATFORM = 'ALI_OSS'
     * @return 配置 Map (accessKeyId, accessKeySecret, bucket, endpoint)
     */
    public Map<String, String> getAliyunConfig() {
        Map<String, String> config = new HashMap<>();
        
        if (jdbcTemplate == null) {
            return config;
        }
        
        try {
            // 从 PRODUCT_APPKEY 表获取阿里云 AccessKey
            String sql = "SELECT ACCESSKEYID as APPKEY, ACCESSKEYSECRET as APPSECRET " +
                        "FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI'";
            
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            
            if (rows != null && !rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                
                String appKey = row.get("APPKEY") != null ? row.get("APPKEY").toString() : null;
                String appSecret = row.get("APPSECRET") != null ? row.get("APPSECRET").toString() : null;
                
                if (appKey != null) {
                    config.put("accessKeyId", appKey.trim());
                }
                if (appSecret != null) {
                    config.put("accessKeySecret", appSecret.trim());
                }
            }
            
        } catch (Exception e) {
            System.err.println("获取阿里云配置失败：" + e.getMessage());
            e.printStackTrace();
        }
        
        return config;
    }
    
    /**
     * 从数据库获取阿里云 OSS 配置
     * SQL: SELECT ACCESSKEYID as bucket, ACCESSKEYSECRET as endpoint from PRODUCT_APPKEY where PLATFORM = 'ALI_OSS'
     * @return OSS 配置 Map (bucket, endpoint)
     */
    public Map<String, String> getAliyunOssConfig() {
        Map<String, String> config = new HashMap<>();
        
        if (jdbcTemplate == null) {
            return config;
        }
        
        try {
            // 从 PRODUCT_APPKEY 表获取 OSS 配置
            String sql = "SELECT ACCESSKEYID as bucket, ACCESSKEYSECRET as endpoint " +
                        "FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_OSS'";
            
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            
            if (rows != null && !rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                
                String bucket = row.get("bucket") != null ? row.get("bucket").toString() : null;
                String endpoint = row.get("endpoint") != null ? row.get("endpoint").toString() : null;
                
                if (bucket != null) {
                    config.put("bucket", bucket.trim());
                }
                if (endpoint != null) {
                    config.put("endpoint", endpoint.trim());
                }
            }
            
        } catch (Exception e) {
            System.err.println("获取阿里云 OSS 配置失败：" + e.getMessage());
            e.printStackTrace();
        }
        
        return config;
    }
    
    /**
     * 获取 OSS Bucket 名称
     * @return Bucket 名称
     */
    public String getOssBucket() {
        return getAliyunOssConfig().get("bucket");
    }
    
    /**
     * 获取 OSS Endpoint
     * @return Endpoint
     */
    public String getOssEndpoint() {
        return getAliyunOssConfig().get("endpoint");
    }
    
    /**
     * 获取 AccessKeyId (APPKEY)
     * @return AccessKeyId
     */
    public String getAccessKeyId() {
        return getAliyunConfig().get("accessKeyId");
    }
    
    /**
     * 获取 AccessKeySecret (APPSECRET)
     * @return AccessKeySecret
     */
    public String getAccessKeySecret() {
        return getAliyunConfig().get("accessKeySecret");
    }
    
    /**
     * 检查阿里云配置是否完整
     * @return true 如果配置完整
     */
    public boolean isConfigComplete() {
        Map<String, String> config = getAliyunConfig();
        return config.get("accessKeyId") != null && 
               config.get("accessKeySecret") != null &&
               !config.get("accessKeyId").trim().isEmpty() &&
               !config.get("accessKeySecret").trim().isEmpty();
    }
}
