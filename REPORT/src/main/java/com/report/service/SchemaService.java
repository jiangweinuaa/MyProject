package com.report.service;

import com.report.config.MerchantDataSourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 数据库表结构服务
 * 用于 AI 生成 SQL 时提供表结构信息
 * 
 * 数据源使用规范：
 * - 平台库：AI_TABLE_FILTER（表过滤配置）
 * - 商家库：USER_TAB_COLUMNS、USER_COL_COMMENTS（实际表结构）
 */
@Service
public class SchemaService {
    
    /**
     * 平台 JdbcTemplate
     * 用于：AI_TABLE_FILTER 表过滤配置
     */
    @Autowired
    private JdbcTemplate platformJdbcTemplate;
    
    @Autowired
    private MerchantDataSourceManager dataSourceManager;
    
    /**
     * 获取所有表结构（用于 AI 生成 SQL）
     * 只返回 AI_TABLE_FILTER 表中配置的表
     * 
     * 数据源：
     * - AI_TABLE_FILTER：平台库
     * - USER_TAB_COLUMNS、USER_COL_COMMENTS：商家库
     * 
     * @return 表结构文本（Markdown 格式）
     */
    public String getAllTables() {
        StringBuilder schema = new StringBuilder();
        
        // 1. 查询过滤表中配置的表（使用平台库）
        String tablesSql = "SELECT f.TABLE_NAME, f.TABLE_COMMENT FROM AI_TABLE_FILTER f WHERE f.ENABLED = 'Y' ORDER BY f.SORT_ORDER";
        
        List<Map<String, Object>> tables = platformJdbcTemplate.queryForList(tablesSql);
        
        // 2. 获取商家 JdbcTemplate（读取实际表结构）
        JdbcTemplate businessJdbc = dataSourceManager.getMerchantJdbcTemplate();
        if (businessJdbc == null) {
            businessJdbc = platformJdbcTemplate; // 降级到平台库
        }
        
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("TABLE_NAME");
            String tableComment = (String) table.get("TABLE_COMMENT");
            
            schema.append("### 表：").append(tableName);
            if (tableComment != null && !tableComment.isEmpty()) {
                schema.append("（").append(tableComment).append("）");
            }
            schema.append("\n\n");
            
            // 3. 查询表的列（使用商家库）
            String columnsSql = "SELECT c.COLUMN_NAME, c.DATA_TYPE, c.DATA_LENGTH, cm.COMMENTS, c.NULLABLE FROM USER_TAB_COLUMNS c LEFT JOIN USER_COL_COMMENTS cm ON c.TABLE_NAME = cm.TABLE_NAME AND c.COLUMN_NAME = cm.COLUMN_NAME WHERE c.TABLE_NAME = ? ORDER BY c.COLUMN_ID";
            
            List<Map<String, Object>> columns = businessJdbc.queryForList(columnsSql, tableName);
            
            schema.append("| 字段名 | 类型 | 长度 | 注释 | 可空 |\n");
            schema.append("|--------|------|------|------|------|\n");
            
            for (Map<String, Object> col : columns) {
                String colName = (String) col.get("COLUMN_NAME");
                String dataType = (String) col.get("DATA_TYPE");
                Number dataLength = (Number) col.get("DATA_LENGTH");
                String comment = (String) col.get("COMMENTS");
                String nullable = (String) col.get("NULLABLE");
                
                schema.append("| ").append(colName);
                schema.append(" | ").append(dataType);
                schema.append(" | ").append(dataLength != null ? dataLength.intValue() : "");
                schema.append(" | ").append(comment != null ? comment : "");
                schema.append(" | ").append(nullable);
                schema.append(" |\n");
            }
            
            schema.append("\n");
        }
        
        return schema.toString();
    }
    
    /**
     * 获取指定表的结构
     * @param tableName 表名
     * @return 表结构文本
     */
    public String getTableSchema(String tableName) {
        // 使用商家 JdbcTemplate 读取表结构
        JdbcTemplate businessJdbc = dataSourceManager.getMerchantJdbcTemplate();
        if (businessJdbc == null) {
            businessJdbc = platformJdbcTemplate; // 降级到平台库
        }
        
        StringBuilder schema = new StringBuilder();
        
        String columnsSql = "SELECT c.COLUMN_NAME, c.DATA_TYPE, c.DATA_LENGTH, cm.COMMENTS, c.NULLABLE FROM USER_TAB_COLUMNS c LEFT JOIN USER_COL_COMMENTS cm ON c.TABLE_NAME = cm.TABLE_NAME AND c.COLUMN_NAME = cm.COLUMN_NAME WHERE c.TABLE_NAME = ? ORDER BY c.COLUMN_ID";
        
        List<Map<String, Object>> columns = businessJdbc.queryForList(columnsSql, tableName);
        
        schema.append("### 表：").append(tableName).append("\n\n");
        schema.append("| 字段名 | 类型 | 长度 | 注释 | 可空 |\n");
        schema.append("|--------|------|------|------|------|\n");
        
        for (Map<String, Object> col : columns) {
            String colName = (String) col.get("COLUMN_NAME");
            String dataType = (String) col.get("DATA_TYPE");
            Number dataLength = (Number) col.get("DATA_LENGTH");
            String comment = (String) col.get("COMMENTS");
            String nullable = (String) col.get("NULLABLE");
            
            schema.append("| ").append(colName);
            schema.append(" | ").append(dataType);
            schema.append(" | ").append(dataLength != null ? dataLength.intValue() : "");
            schema.append(" | ").append(comment != null ? comment : "");
            schema.append(" | ").append(nullable);
            schema.append(" |\n");
        }
        
        return schema.toString();
    }
}
