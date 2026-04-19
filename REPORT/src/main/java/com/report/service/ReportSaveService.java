package com.report.service;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 报表设计器保存服务
 */
@Service
public class ReportSaveService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 保存报表
     */
    public Map<String, Object> saveReport(Map<String, Object> request) {
        try {
            String reportId = generateReportId();
            String reportName = (String) request.get("name");
            String description = (String) request.get("description");
            String sql = (String) request.get("sql");
            Object chartConfig = request.get("chartConfig");
            Object charts = request.get("charts");  // 多图表
            Object layoutConfig = request.get("layout");
            Object variablesConfig = request.get("variables");
            String menuId = (String) request.get("menuId");
            Integer status = (Integer) request.get("status");
            String token = (String) request.get("token");
            
            // 从 token 解析用户 ID
            String userId = getUserIdFromToken(token);
            
            // 构建 SQL
            String saveSql = "MERGE INTO AI_REPORT_DEF t " +
                "USING DUAL " +
                "ON (t.REPORT_ID = ?) " +
                "WHEN MATCHED THEN " +
                "  UPDATE SET " +
                "    t.REPORT_NAME = ?, " +
                "    t.DESCRIPTION = ?, " +
                "    t.SQL_TEXT = ?, " +
                "    t.CHART_CONFIG = ?, " +
                "    t.LAYOUT_CONFIG = ?, " +
                "    t.VARIABLES_CONFIG = ?, " +
                "    t.STATUS = ?, " +
                "    t.MENU_ID = ?, " +
                "    t.UPDATED_TIME = SYSDATE " +
                "WHEN NOT MATCHED THEN " +
                "  INSERT ( " +
                "    REPORT_ID, REPORT_NAME, DESCRIPTION, SQL_TEXT, " +
                "    CHART_CONFIG, LAYOUT_CONFIG, VARIABLES_CONFIG, " +
                "    STATUS, MENU_ID, CREATED_BY, CREATED_TIME " +
                "  ) VALUES ( " +
                "    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE " +
                "  ) ";
            
            // 转换 JSON 对象为字符串
            // 优先使用 charts 数组，如果没有则使用单个 chartConfig
            String chartConfigJson = null;
            if (charts != null) {
                chartConfigJson = JSON.toJSONString(charts);  // 存储数组
            } else if (chartConfig != null) {
                chartConfigJson = JSON.toJSONString(chartConfig);  // 存储单个对象
            }
            
            String layoutConfigJson = layoutConfig != null ? JSON.toJSONString(layoutConfig) : null;
            String variablesConfigJson = variablesConfig != null ? JSON.toJSONString(variablesConfig) : null;
            
            // 执行保存
            jdbcTemplate.update(saveSql, 
                reportId, reportName, description, sql,
                chartConfigJson, layoutConfigJson, variablesConfigJson,
                status, menuId,
                reportId, reportName, description, sql,
                chartConfigJson, layoutConfigJson, variablesConfigJson,
                status, menuId, userId
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reportId", reportId);
            response.put("message", status == 200 ? "报表已发布" : "草稿已保存");
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "保存失败：" + e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
    
    /**
     * 获取菜单树
     */
    public Map<String, Object> getMenuTree() {
        try {
            String sql = "SELECT MENU_ID, MENU_NAME, PARENT_ID, SORT_ORDER, ICON, STATUS " +
                "FROM AI_REPORT_MENU " +
                "WHERE STATUS = 100 " +
                "ORDER BY PARENT_ID, SORT_ORDER ";
            
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            
            // 构建树形结构
            List<Map<String, Object>> tree = buildMenuTree(list, "0");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("menuTree", tree);
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取菜单失败：" + e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
    
    /**
     * 构建菜单树
     */
    private List<Map<String, Object>> buildMenuTree(List<Map<String, Object>> all, String parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        
        for (Map<String, Object> node : all) {
            if (parentId.equals(node.get("PARENT_ID"))) {
                Map<String, Object> newNode = new HashMap<>(node);
                
                // 递归查找子节点
                List<Map<String, Object>> children = buildMenuTree(all, (String) node.get("MENU_ID"));
                if (!children.isEmpty()) {
                    newNode.put("children", children);
                }
                
                tree.add(newNode);
            }
        }
        
        return tree;
    }
    
    /**
     * 生成报表 ID
     */
    private String generateReportId() {
        return "RPT_" + System.currentTimeMillis() + "_" + 
               UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
    
    /**
     * 从 token 解析用户 ID
     */
    private String getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return "anonymous";
        }
        try {
            // TODO: 实现 Token 解析
            // return TokenUtil.getOpnoFromToken(token);
            return "admin";
        } catch (Exception e) {
            return "anonymous";
        }
    }
    
    /**
     * 获取报表详情
     */
    public Map<String, Object> getReport(String reportId) {
        try {
            String sql = "SELECT * FROM AI_REPORT_DEF WHERE REPORT_ID = ? ";
            
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, reportId);
            if (list.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "报表不存在");
                return response;
            }
            
            Map<String, Object> report = list.get(0);
            
            // 安全解析 JSON 字段
            report.put("chartConfig", safeParseJson(report.get("CHART_CONFIG")));
            report.put("layoutConfig", safeParseJson(report.get("LAYOUT_CONFIG")));
            report.put("variablesConfig", safeParseJson(report.get("VARIABLES_CONFIG")));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("report", report);
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取报表失败：" + e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
    
    /**
     * 安全解析 JSON
     */
    private Object safeParseJson(Object value) {
        if (value == null) return null;
        
        String str = value.toString();
        if (str.trim().isEmpty()) return null;
        
        try {
            // 尝试解析为 JSON 对象
            if (str.trim().startsWith("{")) {
                return JSON.parseObject(str);
            }
            // 尝试解析为 JSON 数组
            if (str.trim().startsWith("[")) {
                return JSON.parseArray(str);
            }
            // 简单字符串，直接返回
            return str;
        } catch (Exception e) {
            // 解析失败，返回原始字符串
            return str;
        }
    }
    
    /**
     * 获取报表列表
     */
    public Map<String, Object> getReportList() {
        try {
            String sql = "SELECT REPORT_ID, REPORT_NAME, DESCRIPTION, STATUS, CREATED_BY, CREATED_TIME, UPDATED_TIME " +
                "FROM AI_REPORT_DEF " +
                "ORDER BY CREATED_TIME DESC ";
            
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reports", list);
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取报表列表失败：" + e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
    
    /**
     * 删除报表
     */
    public Map<String, Object> deleteReport(String reportId) {
        try {
            String sql = "DELETE FROM AI_REPORT_DEF WHERE REPORT_ID = ? ";
            jdbcTemplate.update(sql, reportId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "报表已删除");
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
}
