package com.report.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 阿里云配置工具类
 * 从数据库读取阿里云相关配置
 */
@Component
public class AliyunConfigUtil {
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 获取阿里云 OSS Bucket
     */
    public String getOssBucket() {
        return getConfigValue("ALI_OSS", "BUCKET");
    }
    
    /**
     * 获取阿里云 OSS Endpoint
     */
    public String getOssEndpoint() {
        return getConfigValue("ALI_OSS", "ENDPOINT");
    }
    
    /**
     * 获取阿里云 AccessKeyId
     */
    public String getAccessKeyId() {
        return getConfigValue("ALI", "ACCESSKEYID");
    }
    
    /**
     * 获取阿里云 AccessKeySecret
     */
    public String getAccessKeySecret() {
        return getConfigValue("ALI", "ACCESSKEYSECRET");
    }
    
    /**
     * 获取腾讯云 SecretId
     */
    public String getTencentSecretId() {
        return getConfigValue("TENCENT", "SECRETID");
    }
    
    /**
     * 获取腾讯云 SecretKey
     */
    public String getTencentSecretKey() {
        return getConfigValue("TENCENT", "SECRETKEY");
    }
    
    /**
     * 从数据库读取配置值
     */
    private String getConfigValue(String platform, String keyName) {
        if (jdbcTemplate == null) {
            return null;
        }
        
        try {
            String sql = "SELECT " + keyName + " FROM PRODUCT_APPKEY WHERE PLATFORM = ?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, platform);
            
            if (list != null && !list.isEmpty()) {
                Object value = list.get(0).get(keyName);
                return value != null ? value.toString() : null;
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("读取配置失败：" + platform + "." + keyName + " - " + e.getMessage());
            return null;
        }
    }
}
