package com.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据源配置
 * 
 * 架构说明：
 * - 默认数据源（platformJdbcTemplate）：从 application.yml 读取，用于平台库
 * - 商家数据源（MerchantDataSourceManager）：动态创建，从 MERCHANT_DB_CONFIG 读取
 */
@Configuration
public class DataSourceConfig {

    /**
     * 平台 JdbcTemplate（使用 Spring Boot 默认数据源）
     * 用于读取：AI 配置、商家连接配置、商品识别配置、Token 验证等
     * 
     * @Primary 注解确保默认注入时使用平台库
     */
    @Bean("platformJdbcTemplate")
    @Primary
    public JdbcTemplate platformJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
