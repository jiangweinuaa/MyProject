package com.report.service.impl;

import com.report.service.LoginService;
import com.report.dto.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 登录服务实现类
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    /**
     * 时间格式：yyyymmddhh24missff3
     */
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    @Override
    public ServiceResponse<?> login(String opno, String password, String ip) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            // TODO: 这里可以添加真实的密码验证逻辑
            // 目前暂时允许任意密码登录（生产环境请修改）
            
            // 生成 GUID 作为 token
            String token = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            
            // 构建 token 数据（包含 IP）
            Map<String, Object> tokenData = new HashMap<>();
            // 修复：当 opno 为空字符串时也使用默认值 "admin"
            String opnoValue = (opno != null && !opno.trim().isEmpty()) ? opno : "admin";
            tokenData.put("OPNO", opnoValue);
            tokenData.put("EID", "66");
            tokenData.put("IP", ip != null ? ip : "unknown");
            
            // 将 JSON 转为字符串
            String jsonString = "{\"OPNO\":\"" + tokenData.get("OPNO") + 
                               "\",\"EID\":\"" + tokenData.get("EID") + 
                               "\",\"IP\":\"" + tokenData.get("IP") + "\"}";
            
            // 获取当前时间（yyyymmddhh24missff3 格式）
            String updateTime = LocalDateTime.now().format(TIME_FORMATTER);
            
            // 插入到 PLATFORM_TOKEN 表
            String insertSql = "INSERT INTO PLATFORM_TOKEN (KEY, JSON, UPDATE_TIME) VALUES (?, ?, ?)";
            int rows = jdbcTemplate.update(insertSql, token, jsonString, updateTime);
            
            if (rows > 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                result.put("opno", tokenData.get("OPNO"));
                result.put("eid", tokenData.get("EID"));
                result.put("ip", tokenData.get("IP"));
                result.put("expireTime", "24h"); // TODO: 可配置过期时间
                
                return ServiceResponse.success(result, "登录成功");
            } else {
                return ServiceResponse.error("500", "登录失败：无法保存 token");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "登录失败：" + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<?> verifyToken(String token) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            if (token == null || token.trim().isEmpty()) {
                return ServiceResponse.error("401", "token 不能为空");
            }
            
            String querySql = "SELECT KEY, JSON, UPDATE_TIME FROM PLATFORM_TOKEN WHERE KEY = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(querySql, token);
            
            if (result != null) {
                return ServiceResponse.success(result, "token 有效");
            } else {
                return ServiceResponse.error("401", "token 无效或已过期");
            }
            
        } catch (Exception e) {
            return ServiceResponse.error("401", "token 无效：" + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<?> logout(String token) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            if (token == null || token.trim().isEmpty()) {
                return ServiceResponse.error("400", "token 不能为空");
            }
            
            String deleteSql = "DELETE FROM PLATFORM_TOKEN WHERE KEY = ?";
            int rows = jdbcTemplate.update(deleteSql, token);
            
            if (rows > 0) {
                return ServiceResponse.success(null, "退出登录成功");
            } else {
                return ServiceResponse.success(null, "退出登录成功（token 不存在）");
            }
            
        } catch (Exception e) {
            return ServiceResponse.error("500", "退出登录失败：" + e.getMessage());
        }
    }
}
