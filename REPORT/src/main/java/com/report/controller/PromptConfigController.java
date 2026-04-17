package com.report.controller;

import com.report.service.AISQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 智问 Prompt 要求配置管理 Controller
 */
@RestController
@RequestMapping("/api/prompt-config")
@CrossOrigin(origins = "*")
public class PromptConfigController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private AISQLService aiSQLService;
    
    /**
     * 获取要求列表
     */
    @GetMapping("/requirements")
    public Map<String, Object> getRequirements() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = "SELECT SORT_ORDER, REQUIREMENT, CATEGORY, ENABLED, CREATED_TIME, UPDATED_TIME, REMARK FROM AI_PROMPT_REQUIREMENTS ORDER BY SORT_ORDER";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            
            response.put("success", true);
            response.put("data", list);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 添加要求
     */
    @PostMapping("/requirements")
    public Map<String, Object> addRequirement(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String requirement = (String) request.get("requirement");
            String category = (String) request.getOrDefault("category", "GENERAL");
            String remark = (String) request.getOrDefault("remark", "");
            Number sortOrder = (Number) request.getOrDefault("sortOrder", 99);
            
            String sql = "INSERT INTO AI_PROMPT_REQUIREMENTS (REQUIREMENT, CATEGORY, ENABLED, SORT_ORDER, REMARK, CREATED_TIME) VALUES (?, ?, 'Y', ?, ?, SYSDATE)";
            jdbcTemplate.update(sql, requirement, category, sortOrder, remark);
            
            // 刷新缓存
            aiSQLService.refreshPromptCache();
            
            response.put("success", true);
            response.put("message", "添加成功");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 更新要求
     */
    @PutMapping("/requirements/{sortOrder}")
    public Map<String, Object> updateRequirement(@PathVariable Number sortOrder, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String requirement = (String) request.get("requirement");
            String category = (String) request.get("category");
            String enabled = (String) request.get("enabled");
            Number newSortOrder = (Number) request.get("sortOrder");
            String remark = (String) request.get("remark");
            
            StringBuilder sql = new StringBuilder("UPDATE AI_PROMPT_REQUIREMENTS SET ");
            List<Object> params = new ArrayList<>();
            
            if (requirement != null) {
                sql.append("REQUIREMENT = ?, ");
                params.add(requirement);
            }
            if (category != null) {
                sql.append("CATEGORY = ?, ");
                params.add(category);
            }
            if (enabled != null) {
                sql.append("ENABLED = ?, ");
                params.add(enabled);
            }
            if (newSortOrder != null && !newSortOrder.equals(sortOrder)) {
                sql.append("SORT_ORDER = ?, ");
                params.add(newSortOrder);
            }
            if (remark != null) {
                sql.append("REMARK = ?, ");
                params.add(remark);
            }
            
            sql.append("UPDATED_TIME = SYSDATE WHERE SORT_ORDER = ?");
            params.add(sortOrder);
            
            jdbcTemplate.update(sql.toString(), params.toArray());
            
            // 刷新缓存
            aiSQLService.refreshPromptCache();
            
            response.put("success", true);
            response.put("message", "更新成功");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 删除要求
     */
    @DeleteMapping("/requirements/{sortOrder}")
    public Map<String, Object> deleteRequirement(@PathVariable Number sortOrder) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = "DELETE FROM AI_PROMPT_REQUIREMENTS WHERE SORT_ORDER = ?";
            jdbcTemplate.update(sql, sortOrder);
            
            // 刷新缓存
            aiSQLService.refreshPromptCache();
            
            response.put("success", true);
            response.put("message", "删除成功");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 刷新缓存（立刻生效）
     */
    @PostMapping("/refresh")
    public Map<String, Object> refreshCache() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            aiSQLService.refreshPromptCache();
            
            response.put("success", true);
            response.put("message", "缓存已刷新，配置立刻生效");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 获取表结构配置列表（AI_TABLE_FILTER）
     */
    @GetMapping("/table-filter")
    public Map<String, Object> getTableFilter() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = "SELECT TABLE_NAME, TABLE_COMMENT, ENABLED, SORT_ORDER FROM AI_TABLE_FILTER ORDER BY SORT_ORDER";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            
            response.put("success", true);
            response.put("data", list);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 添加表结构配置
     */
    @PostMapping("/table-filter")
    public Map<String, Object> addTableFilter(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String tableName = (String) request.get("tableName");
            String tableComment = (String) request.get("tableComment");
            String enabled = (String) request.getOrDefault("enabled", "Y");
            Number sortOrder = (Number) request.getOrDefault("sortOrder", 99);
            
            String sql = "INSERT INTO AI_TABLE_FILTER (TABLE_NAME, TABLE_COMMENT, ENABLED, SORT_ORDER, CREATED_TIME) VALUES (?, ?, ?, ?, SYSDATE)";
            jdbcTemplate.update(sql, tableName, tableComment, enabled, sortOrder);
            
            // 刷新缓存
            aiSQLService.refreshPromptCache();
            
            response.put("success", true);
            response.put("message", "添加成功");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 更新表结构配置
     */
    @PutMapping("/table-filter/{tableName}")
    public Map<String, Object> updateTableFilter(@PathVariable String tableName, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String tableComment = (String) request.get("tableComment");
            String enabled = (String) request.get("enabled");
            Number newSortOrder = (Number) request.get("sortOrder");
            
            StringBuilder sql = new StringBuilder("UPDATE AI_TABLE_FILTER SET ");
            List<Object> params = new ArrayList<>();
            
            if (tableComment != null) {
                sql.append("TABLE_COMMENT = ?, ");
                params.add(tableComment);
            }
            if (enabled != null) {
                sql.append("ENABLED = ?, ");
                params.add(enabled);
            }
            if (newSortOrder != null) {
                sql.append("SORT_ORDER = ?, ");
                params.add(newSortOrder);
            }
            
            // 删除末尾的逗号和空格
            String sqlStr = sql.toString();
            if (sqlStr.endsWith(", ")) {
                sqlStr = sqlStr.substring(0, sqlStr.length() - 2);
            }
            
            sqlStr += " WHERE TABLE_NAME = ?";
            params.add(tableName);
            
            jdbcTemplate.update(sqlStr, params.toArray());
            
            // 刷新缓存
            aiSQLService.refreshPromptCache();
            
            response.put("success", true);
            response.put("message", "更新成功");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 删除表结构配置
     */
    @DeleteMapping("/table-filter/{tableName}")
    public Map<String, Object> deleteTableFilter(@PathVariable String tableName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = "DELETE FROM AI_TABLE_FILTER WHERE TABLE_NAME = ?";
            jdbcTemplate.update(sql, tableName);
            
            // 刷新缓存
            aiSQLService.refreshPromptCache();
            
            response.put("success", true);
            response.put("message", "删除成功");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
}
