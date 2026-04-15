package com.report.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 工具类（带本地缓存）
 */
public class TokenUtil {

    /**
     * Token 缓存：token -> TokenInfo
     */
    private static final Map<String, TokenInfo> TOKEN_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 缓存过期时间（5 分钟）
     */
    private static final long CACHE_EXPIRE_MS = 5 * 60 * 1000;

    /**
     * Token 信息类（一次性解析所有字段）
     */
    private static class TokenInfo {
        String opno;      // 用户编号
        String eid;       // 企业编号
        String ip;        // IP 地址
        long expireTime;  // 缓存过期时间
        
        TokenInfo(String opno, String eid, String ip, long expireTime) {
            this.opno = opno;
            this.eid = eid;
            this.ip = ip;
            this.expireTime = expireTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    /**
     * 从 token 中解析 OPNO（用户编号，带缓存）
     * @param jdbcTemplate 数据库连接
     * @param token token 字符串
     * @return OPNO（用户编号），如果 token 无效返回 "default_user"
     */
    public static String getOpnoFromToken(JdbcTemplate jdbcTemplate, String token) {
        if (token == null || token.trim().isEmpty()) {
            return "default_user";
        }
        
        // 1. 检查缓存
        TokenInfo info = TOKEN_CACHE.get(token);
        if (info != null && !info.isExpired()) {
            return info.opno;
        }
        
        // 2. 缓存失效或不存在，解析 token
        TokenInfo newInfo = parseToken(jdbcTemplate, token);
        if (newInfo != null) {
            TOKEN_CACHE.put(token, newInfo);
            return newInfo.opno;
        }
        
        return "default_user";
    }
    
    /**
     * 从 token 中解析 EID（企业编号，带缓存）
     * @param jdbcTemplate 数据库连接
     * @param token token 字符串
     * @return EID（企业编号），如果 token 无效返回 "99"
     */
    public static String getEidFromToken(JdbcTemplate jdbcTemplate, String token) {
        if (token == null || token.trim().isEmpty()) {
            return "99";
        }
        
        // 1. 检查缓存
        TokenInfo info = TOKEN_CACHE.get(token);
        if (info != null && !info.isExpired()) {
            return info.eid;
        }
        
        // 2. 缓存失效或不存在，解析 token
        TokenInfo newInfo = parseToken(jdbcTemplate, token);
        if (newInfo != null) {
            TOKEN_CACHE.put(token, newInfo);
            return newInfo.eid;
        }
        
        return "99";
    }
    
    /**
     * 解析 Token（一次性解析所有字段）
     * @param jdbcTemplate 数据库连接
     * @param token token 字符串
     * @return TokenInfo，如果解析失败返回 null
     */
    private static TokenInfo parseToken(JdbcTemplate jdbcTemplate, String token) {
        try {
            String querySql = "SELECT JSON FROM PLATFORM_TOKEN WHERE KEY = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(querySql, token);
            
            if (result == null || result.get("JSON") == null) {
                return null;
            }
            
            String jsonStr = result.get("JSON").toString();
            System.out.println("✅ Token JSON: " + jsonStr);
            
            // 使用 FastJSON 解析（更可靠）
            JSONObject json = JSON.parseObject(jsonStr);
            String opno = json.getString("OPNO");
            String eid = json.getString("EID");
            String ip = json.getString("IP");
            
            System.out.println("✅ 解析结果 - OPNO: " + opno + ", EID: " + eid + ", IP: " + ip);
            
            // 创建 TokenInfo（一次性缓存所有字段）
            TokenInfo info = new TokenInfo(
                opno != null && !opno.isEmpty() ? opno : "default_user",
                eid != null && !eid.isEmpty() ? eid : "99",
                ip != null ? ip : "unknown",
                System.currentTimeMillis() + CACHE_EXPIRE_MS
            );
            
            return info;
            
        } catch (Exception e) {
            System.err.println("⚠️ 解析 Token 失败：" + e.getMessage());
            return null;
        }
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
