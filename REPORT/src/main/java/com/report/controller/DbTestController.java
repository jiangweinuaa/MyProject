package com.report.controller;

import com.report.dto.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库测试 Controller
 */
@RestController
@RequestMapping("/api/db")
public class DbTestController {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试数据库连接
     * GET /api/db/test
     */
    @GetMapping("/test")
    public ServiceResponse<?> testDatabase() {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "JdbcTemplate 未初始化，请检查数据库配置");
        }

        try {
            // 执行 Oracle 测试查询
            Timestamp dbTime = jdbcTemplate.queryForObject(
                "SELECT SYSDATE FROM DUAL", 
                Timestamp.class
            );

            Map<String, Object> result = new HashMap<>();
            result.put("dbTime", dbTime.toString());
            result.put("sql", "SELECT SYSDATE FROM DUAL");
            result.put("status", "connected");

            return ServiceResponse.success(result, "数据库连接成功");
        } catch (Exception e) {
            return ServiceResponse.error("500", "数据库连接失败：" + e.getMessage());
        }
    }

    /**
     * 测试数据库连接（详细版）
     * GET /api/db/check
     */
    @GetMapping("/check")
    public ServiceResponse<?> checkDatabase() {
        Map<String, Object> info = new HashMap<>();

        if (jdbcTemplate == null) {
            info.put("jdbcTemplate", "未初始化");
            info.put("error", "JdbcTemplate 未初始化");
            return ServiceResponse.error("500", "JdbcTemplate 未初始化");
        }

        info.put("jdbcTemplate", "已初始化");

        try {
            // 获取数据库时间
            Timestamp dbTime = jdbcTemplate.queryForObject(
                "SELECT SYSDATE FROM DUAL", 
                Timestamp.class
            );
            info.put("dbTime", dbTime.toString());
            info.put("connection", "成功");

            return ServiceResponse.success(info, "数据库检查通过");
        } catch (Exception e) {
            info.put("connection", "失败");
            info.put("error", e.getMessage());
            info.put("errorType", e.getClass().getSimpleName());
            return ServiceResponse.error("500", "数据库连接失败：" + e.getMessage());
        }
    }
}
