package com.report.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商家数据源管理器
 * 
 * 核心逻辑：
 * 1. 从 MERCHANT_DB_CONFIG 查询 STATUS=100 的配置
 * 2. 如果有配置，创建独立 HikariDataSource
 * 3. 如果没有配置，返回 null（调用方降级到平台库）
 * 4. 支持配置刷新和连接池清理
 */
@Component
public class MerchantDataSourceManager {

    @Autowired
    private JdbcTemplate platformJdbcTemplate;

    // 商家数据源缓存（单例，按 DEFAULT 键存储）
    private final Map<String, HikariDataSource> dataSourceCache = new ConcurrentHashMap<>();

    // 缓存最后加载时间（用于防抖）
    private long lastLoadTime = 0;
    private static final long LOAD_INTERVAL_MS = 5000; // 5 秒内不重复加载

    /**
     * 获取商家数据源
     * 
     * 规则：
     * 1. 从 MERCHANT_DB_CONFIG 查询启用的配置（STATUS=100）
     * 2. 按 CREATE_TIME 排序，取第一条
     * 3. 如果有配置，返回对应的数据源（带缓存）
     * 4. 如果没有配置，返回 null（使用默认平台库）
     * 
     * @return HikariDataSource 或 null
     */
    public HikariDataSource getMerchantDataSource() {
        // 防抖：5 秒内不重复加载
        long now = System.currentTimeMillis();
        if (now - lastLoadTime < LOAD_INTERVAL_MS) {
            HikariDataSource cached = dataSourceCache.get("DEFAULT");
            if (cached != null && !cached.isClosed()) {
                return cached;
            }
        }

        // 先查缓存
        HikariDataSource cached = dataSourceCache.get("DEFAULT");
        if (cached != null && !cached.isClosed()) {
            return cached;
        }

        // 从平台库读取配置（Oracle 11g 兼容语法）
        String sql = "SELECT DB_URL, DB_USERNAME, DB_PASSWORD, DRIVER_CLASS, EID " +
                     "FROM (SELECT DB_URL, DB_USERNAME, DB_PASSWORD, DRIVER_CLASS, EID " +
                     "FROM MERCHANT_DB_CONFIG WHERE STATUS = 100 " +
                     "ORDER BY CREATE_TIME ASC) " +
                     "WHERE ROWNUM = 1";

        try {
            Map<String, Object> config = platformJdbcTemplate.queryForMap(sql);
            
            String dbUrl = (String) config.get("DB_URL");
            String dbUsername = (String) config.get("DB_USERNAME");
            String dbPassword = (String) config.get("DB_PASSWORD");
            String driverClass = (String) config.get("DRIVER_CLASS");
            String eid = (String) config.get("EID");

            // 如果配置为空，返回 null
            if (dbUrl == null || dbUrl.trim().isEmpty()) {
                System.out.println("ℹ️ 商家库配置为空，使用平台库作为默认数据源");
                return null;
            }

            // 创建新数据源
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(dbUrl);
            hikariConfig.setUsername(dbUsername);
            hikariConfig.setPassword(dbPassword);
            hikariConfig.setDriverClassName(driverClass != null ? driverClass : "oracle.jdbc.OracleDriver");
            hikariConfig.setMaximumPoolSize(20);
            hikariConfig.setMinimumIdle(5);
            hikariConfig.setConnectionTimeout(30000);
            hikariConfig.setIdleTimeout(30000);
            hikariConfig.setMaxLifetime(1800000);
            hikariConfig.setPoolName("MerchantHikariCP-" + (eid != null ? eid : "DEFAULT"));
            
            // 测试连接
            hikariConfig.setConnectionTestQuery("SELECT 1 FROM DUAL");

            HikariDataSource newDs = new HikariDataSource(hikariConfig);
            
            // 关闭旧数据源
            if (cached != null && !cached.isClosed()) {
                cached.close();
            }
            
            dataSourceCache.put("DEFAULT", newDs);
            lastLoadTime = now;
            
            System.out.println("✅ 已创建商家数据源：" + dbUrl + " (EID=" + eid + ")");
            return newDs;

        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            // 没有查询到任何记录
            System.out.println("ℹ️ MERCHANT_DB_CONFIG 中没有启用的配置（STATUS=100），使用平台库作为默认数据源");
            return null;
            
        } catch (Exception e) {
            // 其他异常（数据库连接失败等）
            System.err.println("⚠️ 加载商家库配置失败：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取商家 JdbcTemplate
     * 
     * @return 商家 JdbcTemplate，如果没有配置则返回 null
     */
    public JdbcTemplate getMerchantJdbcTemplate() {
        HikariDataSource ds = getMerchantDataSource();
        return ds != null ? new JdbcTemplate(ds) : null;
    }

    /**
     * 刷新商家数据源（配置变更时调用）
     * 
     * 调用场景：
     * 1. 管理员修改了 MERCHANT_DB_CONFIG 配置
     * 2. 需要切换商家数据库
     * 3. 连接异常需要重建
     */
    public void refreshMerchantDataSource() {
        HikariDataSource ds = dataSourceCache.remove("DEFAULT");
        if (ds != null && !ds.isClosed()) {
            ds.close();
            System.out.println("🔄 已关闭商家数据源，下次请求将重新加载配置");
        }
        lastLoadTime = 0; // 重置防抖计时
    }

    /**
     * 检查商家数据源是否可用
     * 
     * @return true=可用，false=不可用或未配置
     */
    public boolean isMerchantDataSourceAvailable() {
        HikariDataSource ds = dataSourceCache.get("DEFAULT");
        if (ds == null || ds.isClosed()) {
            // 尝试加载一次
            ds = getMerchantDataSource();
        }
        return ds != null && !ds.isClosed();
    }

    /**
     * 获取当前数据源状态信息
     * 
     * @return 状态信息 Map
     */
    public Map<String, Object> getDataSourceStatus() {
        java.util.HashMap<String, Object> status = new java.util.HashMap<>();
        HikariDataSource ds = dataSourceCache.get("DEFAULT");
        
        if (ds != null && !ds.isClosed()) {
            status.put("status", "CONNECTED");
            status.put("poolName", ds.getPoolName());
            status.put("activeConnections", ds.getHikariPoolMXBean().getActiveConnections());
            status.put("idleConnections", ds.getHikariPoolMXBean().getIdleConnections());
            status.put("totalConnections", ds.getHikariPoolMXBean().getTotalConnections());
        } else {
            status.put("status", "NOT_CONFIGURED");
            status.put("message", "使用平台库作为默认数据源");
        }
        
        return status;
    }

    /**
     * 应用关闭时清理资源
     */
    @PreDestroy
    public void destroy() {
        for (HikariDataSource ds : dataSourceCache.values()) {
            if (ds != null && !ds.isClosed()) {
                ds.close();
                System.out.println("🔒 已关闭商家数据源连接池：" + ds.getPoolName());
            }
        }
        dataSourceCache.clear();
    }
}
