package com.report.util;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 工具类（带本地缓存）
 */
public class TokenUtil {

    /**
     * Token 缓存：token -> {eid, expireTime}
     */
    private static final Map<String, TokenCache> TOKEN_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 缓存过期时间（5 分钟）
     */
    private static final long CACHE_EXPIRE_MS = 5 * 60 * 1000;

    /**
     * Token 缓存对象
     */
    private static class TokenCache {
        String eid;
        long expireTime;
        
        TokenCache(String eid, long expireTime) {
            this.eid = eid;
            this.expireTime = expireTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    /**
     * 从 token 中解析 OPNO（用户 ID，带缓存）
     * @param jdbcTemplate 数据库连接
     * @param token token 字符串
     * @return OPNO（用户 ID），如果 token 无效返回 "default_user"
     */
    public static String getOpnoFromToken(JdbcTemplate jdbcTemplate, String token) {
        if (token == null || token.trim().isEmpty()) {
            return "default_user";
        }
        
        // 1. 检查缓存
        TokenCache cache = TOKEN_CACHE.get(token);
        if (cache != null && !cache.isExpired()) {
            return cache.eid;  // 缓存中存储的是 OPNO
        }
        
        // 2. 缓存失效或不存在，查询数据库
        try {
            String querySql = "SELECT JSON FROM PLATFORM_TOKEN WHERE KEY = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(querySql, token);
            
            if (result != null && result.get("JSON") != null) {
                String jsonStr = result.get("JSON").toString();
                // 解析 JSON 获取 OPNO（用户 ID）
                // JSON 格式：{"OPNO":"admin","EID":"99","IP":"unknown"}
                if (jsonStr.contains("\"OPNO\"")) {
                    int opnoStart = jsonStr.indexOf("\"OPNO\"") + 8; // "OPNO":" 的长度
                    int opnoEnd = jsonStr.indexOf("\"", opnoStart);
                    if (opnoEnd > opnoStart) {
                        String opno = jsonStr.substring(opnoStart, opnoEnd);
                        opno = opno != null && !opno.isEmpty() ? opno : "default_user";
                        
                        // 3. 更新缓存（使用不同的键：OPNO_前缀）
                        TOKEN_CACHE.put("OPNO_" + token, new TokenCache(opno, System.currentTimeMillis() + CACHE_EXPIRE_MS));
                        
                        return opno;
                    }
                }
            }
        } catch (Exception e) {
            // 查询失败，返回默认用户
        }
        
        return "default_user";
    }
    
    /**
     * 从 token 中解析 EID（带缓存）- 保留兼容
     * @param jdbcTemplate 数据库连接
     * @param token token 字符串
     * @return EID，如果 token 无效返回 "99"
     */
    public static String getEidFromToken(JdbcTemplate jdbcTemplate, String token) {
        if (token == null || token.trim().isEmpty()) {
            return "99";
        }
        
        // 1. 检查缓存（使用不同的键：EID_前缀）
        TokenCache cache = TOKEN_CACHE.get("EID_" + token);
        if (cache != null && !cache.isExpired()) {
            return cache.eid;
        }
        
        // 2. 缓存失效或不存在，查询数据库
        try {
            String querySql = "SELECT JSON FROM PLATFORM_TOKEN WHERE KEY = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(querySql, token);
            
            if (result != null && result.get("JSON") != null) {
                String jsonStr = result.get("JSON").toString();
                System.out.println("✅ Token JSON: " + jsonStr);
                // 解析 JSON 获取 EID
                // JSON 格式：{"OPNO":"admin","EID":"99","IP":"unknown"}
                if (jsonStr.contains("\"EID\"")) {
                    int eidStart = jsonStr.indexOf("\"EID\"") + 7; // "EID":" 的长度
                    int eidEnd = jsonStr.indexOf("\"", eidStart);
                    if (eidEnd > eidStart) {
                        String eid = jsonStr.substring(eidStart, eidEnd);
                        System.out.println("✅ 解析的 EID: " + eid);
                        eid = eid != null && !eid.isEmpty() ? eid : "99";
                        
                        // 3. 更新缓存（使用不同的键：EID_前缀）
                        TOKEN_CACHE.put("EID_" + token, new TokenCache(eid, System.currentTimeMillis() + CACHE_EXPIRE_MS));
                        
                        return eid;
                    }
                }
            }
        } catch (Exception e) {
            // 查询失败，返回默认 EID
        }
        
        return "99";
    }
    
    /**
     * 清除缓存（token 失效时调用）
     * @param token token 字符串
     */
    public static void invalidateCache(String token) {
        if (token != null) {
            TOKEN_CACHE.remove(token);
        }
    }
    
    /**
     * 清除所有缓存
     */
    public static void clearCache() {
        TOKEN_CACHE.clear();
    }
}
