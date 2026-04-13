package com.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商品识别配置服务
 * 从数据库读取配置，支持缓存和动态刷新
 */
@Service
public class RecognitionConfigService {
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    // 配置缓存
    private final Map<String, String> configCache = new ConcurrentHashMap<>();
    
    // 默认配置
    private final Map<String, String> defaultConfig = new HashMap<>();
    
    @PostConstruct
    public void init() {
        // 初始化默认配置
        defaultConfig.put("VECTOR_HIGH_THRESHOLD", "0.85");
        defaultConfig.put("VECTOR_MIN_THRESHOLD", "0.60");
        defaultConfig.put("ENABLE_SALIENT_ROI", "true");
        defaultConfig.put("FEATURE_IMAGE_SIZE", "1024");
        defaultConfig.put("ENABLE_DETAIL_LOG", "true");
        
        // 加载数据库配置
        loadConfigFromDB();
    }
    
    /**
     * 从数据库加载配置
     */
    public void loadConfigFromDB() {
        if (jdbcTemplate == null) {
            System.out.println("⚠️ 数据库未连接，使用默认配置");
            configCache.putAll(defaultConfig);
            return;
        }
        
        try {
            String sql = "SELECT CONFIG_KEY, CONFIG_VALUE FROM PRODUCT_RECOGNITION_CONFIG";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            
            // 先加载默认配置
            configCache.putAll(defaultConfig);
            
            // 再覆盖数据库配置
            for (Map<String, Object> row : rows) {
                String key = (String) row.get("CONFIG_KEY");
                String value = (String) row.get("CONFIG_VALUE");
                configCache.put(key, value);
            }
            
            System.out.println("✅ 已加载 " + configCache.size() + " 个配置项");
            
        } catch (Exception e) {
            System.err.println("⚠️ 加载配置失败：" + e.getMessage());
            configCache.putAll(defaultConfig);
        }
    }
    
    /**
     * 获取配置值（字符串）
     * @param key 配置键
     * @return 配置值
     */
    public String getConfig(String key) {
        return configCache.getOrDefault(key, defaultConfig.get(key));
    }
    
    /**
     * 获取配置值（带默认值）
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public String getConfig(String key, String defaultValue) {
        return configCache.getOrDefault(key, defaultValue);
    }
    
    /**
     * 获取配置值（双精度）
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public double getDoubleConfig(String key, double defaultValue) {
        String value = getConfig(key);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 获取配置值（整数）
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public int getIntConfig(String key, int defaultValue) {
        String value = getConfig(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 获取配置值（布尔）
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public boolean getBooleanConfig(String key, boolean defaultValue) {
        String value = getConfig(key);
        return "true".equalsIgnoreCase(value);
    }
    
    /**
     * 刷新配置（重新从数据库加载）
     */
    public void refreshConfig() {
        loadConfigFromDB();
    }
}
