---
name: report-service-creator
description: REPORT 工程服务创建规范。用于在 REPORT 系统中开发和创建新的业务查询服务。
             使用场景：当需要为 REPORT 系统添加新的数据查询服务时（如销售查询、库存查询、商品分析等）。
             本规范定义了服务路由、Token 验证、SQL 过滤、分页处理等标准流程。
---

# REPORT 工程服务创建规范

## 适用范围

本规范适用于 REPORT 报表系统中所有业务查询服务的开发。

## 核心原则

1. **统一路由**：所有服务通过 `/api/service` 统一入口
2. **服务名路由**：通过 `serviceId` 字段区分不同服务
3. **Token 验证**：统一 Token 解析和验证
4. **EID 过滤**：SQL 中必须添加 EID 过滤（除非特别指定）
5. **分页支持**：参考 DaySaleQuery 服务的分页方法

---

## 请求规范

### 标准请求格式

```json
{
  "serviceId": "DaySaleQuery",
  "request": {
    "startDate": "20260401",
    "endDate": "20260407"
  },
  "sign": {
    "key": "",
    "sign": "",
    "token": "5E99F26A391F4E1293FCC01BFD3ACD9F"
  },
  "pageNumber": 1,
  "pageSize": 20
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| serviceId | String | 是 | 服务名称（用于路由） |
| request | Object | 是 | 业务参数 |
| sign.key | String | 否 | 签名密钥 |
| sign.sign | String | 否 | 签名值 |
| sign.token | String | 是 | 认证 Token |
| pageNumber | Integer | 否 | 页码（默认 1） |
| pageSize | Integer | 否 | 每页数量（默认 20） |

---

## 响应规范

### 标准响应格式

```json
{
  "datas": {
    "startDate": "20260401"
  },
  "success": true,
  "serviceStatus": "000",
  "serviceDescription": "查询成功，共 8 个品类",
  "totalRecords": 8,
  "totalPages": 1,
  "pageNumber": 1,
  "pageSize": 20,
  "sign": null
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| datas | Object | 业务数据 |
| success | Boolean | 是否成功 |
| serviceStatus | String | 服务状态码（000=成功） |
| serviceDescription | String | 服务描述信息 |
| totalRecords | Integer | 总记录数 |
| totalPages | Integer | 总页数 |
| pageNumber | Integer | 当前页码 |
| pageSize | Integer | 每页数量 |
| sign | Object | 签名信息（通常为 null） |

---

## 实现步骤

### 步骤 1：创建 Controller

**文件位置**：`src/main/java/com/report/controller/`

```java
package com.report.controller;

import com.report.service.DaySaleQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 日报销售查询 Controller
 */
@RestController
@RequestMapping("/api/service")
@CrossOrigin(origins = "*")
public class DaySaleQueryController {
    
    @Autowired
    private DaySaleQueryService daySaleQueryService;
    
    /**
     * 统一服务入口
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping
    public Map<String, Object> execute(@RequestBody Map<String, Object> request) {
        // 1. 提取 serviceId
        String serviceId = (String) request.get("serviceId");
        
        // 2. 验证 serviceId 是否匹配
        if (!"DaySaleQuery".equals(serviceId)) {
            return Map.of(
                "success", false,
                "serviceStatus", "404",
                "serviceDescription", "服务不存在：" + serviceId
            );
        }
        
        // 3. Token 验证
        Map<String, Object> sign = (Map<String, Object>) request.get("sign");
        String token = (String) sign.get("token");
        
        if (!daySaleQueryService.validateToken(token)) {
            return Map.of(
                "success", false,
                "serviceStatus", "401",
                "serviceDescription", "Token 验证失败"
            );
        }
        
        // 4. 提取业务参数
        Map<String, Object> businessParams = (Map<String, Object>) request.get("request");
        Integer pageNumber = (Integer) request.getOrDefault("pageNumber", 1);
        Integer pageSize = (Integer) request.getOrDefault("pageSize", 20);
        
        // 5. 执行查询
        return daySaleQueryService.query(businessParams, pageNumber, pageSize);
    }
}
```

---

### 步骤 2：创建 Service

**文件位置**：`src/main/java/com/report/service/`

```java
package com.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 日报销售查询 Service
 */
@Service
public class DaySaleQueryService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${report.eid:DEFAULT}")
    private String defaultEid;
    
    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        // TODO: 实现 Token 验证逻辑
        // 可以从数据库或配置文件中验证
        return token != null && !token.isEmpty();
    }
    
    /**
     * 查询日报销售数据
     * @param params 业务参数
     * @param pageNumber 页码
     * @param pageSize 每页数量
     * @return 查询结果
     */
    public Map<String, Object> query(Map<String, Object> params, 
                                      Integer pageNumber, 
                                      Integer pageSize) {
        try {
            // 1. 提取参数
            String startDate = (String) params.get("startDate");
            String endDate = (String) params.get("endDate");
            
            // 2. 构建 SQL（带 EID 过滤）
            String countSql = buildCountSql();
            String dataSql = buildDataSql(pageNumber, pageSize);
            
            // 3. 查询总数
            Integer totalRecords = jdbcTemplate.queryForObject(
                countSql, 
                Integer.class, 
                startDate, endDate, defaultEid
            );
            
            // 4. 查询数据
            List<Map<String, Object>> data = jdbcTemplate.queryForList(
                dataSql, 
                startDate, endDate, defaultEid
            );
            
            // 5. 计算分页
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            // 6. 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("datas", buildDatas(data, startDate));
            response.put("success", true);
            response.put("serviceStatus", "000");
            response.put("serviceDescription", "查询成功，共 " + totalRecords + " 条记录");
            response.put("totalRecords", totalRecords);
            response.put("totalPages", totalPages);
            response.put("pageNumber", pageNumber);
            response.put("pageSize", pageSize);
            response.put("sign", null);
            
            return response;
            
        } catch (Exception e) {
            return error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 构建总数 SQL（带 EID 过滤）
     */
    private String buildCountSql() {
        return """
            SELECT COUNT(*) AS TOTAL
            FROM SALES_DAY
            WHERE SALE_DATE BETWEEN ? AND ?
            AND EID = ?
            """;
    }
    
    /**
     * 构建数据 SQL（带 EID 过滤和分页）
     */
    private String buildDataSql(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        
        return """
            SELECT 
                CATEGORY_NAME,
                SUM(SALE_QTY) AS TOTAL_QTY,
                SUM(SALE_AMT) AS TOTAL_AMT
            FROM SALES_DAY
            WHERE SALE_DATE BETWEEN ? AND ?
            AND EID = ?
            GROUP BY CATEGORY_NAME
            ORDER BY TOTAL_AMT DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;
    }
    
    /**
     * 构建返回数据
     */
    private Map<String, Object> buildDatas(List<Map<String, Object>> data, 
                                            String startDate) {
        Map<String, Object> datas = new HashMap<>();
        datas.put("startDate", startDate);
        datas.put("items", data);
        return datas;
    }
    
    /**
     * 错误响应
     */
    private Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("serviceStatus", "500");
        response.put("serviceDescription", message);
        response.put("datas", null);
        response.put("totalRecords", 0);
        response.put("totalPages", 0);
        response.put("pageNumber", 1);
        response.put("pageSize", 20);
        response.put("sign", null);
        return response;
    }
}
```

---

## 关键要点

### 1. 路由规则

- **统一入口**：`/api/service`
- **服务识别**：通过 `serviceId` 字段区分
- **Controller 职责**：路由分发、Token 验证、参数提取

### 2. Token 验证

```java
public boolean validateToken(String token) {
    // 方案 1：数据库验证
    String sql = "SELECT COUNT(*) FROM SYS_TOKEN WHERE TOKEN = ? AND EXPIRE_TIME > SYSDATE";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, token);
    return count != null && count > 0;
    
    // 方案 2：配置文件验证
    // return config.getToken().equals(token);
    
    // 方案 3：JWT 验证
    // return JwtUtil.validate(token);
}
```

### 3. EID 过滤

**必须添加**（除非特别指定）：

```sql
-- 错误示例（缺少 EID 过滤）
SELECT * FROM SALES WHERE SALE_DATE = ?

-- 正确示例
SELECT * FROM SALES WHERE SALE_DATE = ? AND EID = ?
```

**EID 来源**：
- 从 Token 中解析
- 从配置文件中读取
- 从请求参数中提取

### 4. 分页处理

**Oracle 分页语法**：

```sql
-- Oracle 12c+
SELECT * FROM TABLE_NAME
ORDER BY COLUMN_NAME
OFFSET ? ROWS FETCH NEXT ? ROWS ONLY

-- Oracle 11g 及以下
SELECT * FROM (
    SELECT t.*, ROWNUM rn 
    FROM (
        SELECT * FROM TABLE_NAME ORDER BY COLUMN_NAME
    ) t 
    WHERE ROWNUM <= ?
) 
WHERE rn > ?
```

**分页计算**：

```java
int offset = (pageNumber - 1) * pageSize;
int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
```

---

## 示例服务

### 示例 1：商品销量查询

```java
@Service
public class ProductSaleQueryService {
    
    public Map<String, Object> query(Map<String, Object> params, 
                                      Integer pageNumber, 
                                      Integer pageSize) {
        String pluno = (String) params.get("pluno");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        
        String sql = """
            SELECT 
                SALE_DATE,
                SUM(SALE_QTY) AS TOTAL_QTY,
                SUM(SALE_AMT) AS TOTAL_AMT
            FROM SALES
            WHERE PLUNO = ?
            AND SALE_DATE BETWEEN ? AND ?
            AND EID = ?
            GROUP BY SALE_DATE
            ORDER BY SALE_DATE
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;
        
        // 执行查询...
    }
}
```

### 示例 2：库存查询

```java
@Service
public class StockQueryService {
    
    public Map<String, Object> query(Map<String, Object> params, 
                                      Integer pageNumber, 
                                      Integer pageSize) {
        String pluno = (String) params.get("pluno");
        
        String sql = """
            SELECT 
                STOCK_ID,
                STOCK_QTY,
                STOCK_DATE
            FROM STOCK
            WHERE PLUNO = ?
            AND EID = ?
            ORDER BY STOCK_DATE DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;
        
        // 执行查询...
    }
}
```

---

## 测试验证

### 测试请求

```bash
curl -X POST "http://47.100.138.89:8110/api/service" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId": "DaySaleQuery",
    "request": {
      "startDate": "20260401",
      "endDate": "20260407"
    },
    "sign": {
      "token": "5E99F26A391F4E1293FCC01BFD3ACD9F"
    },
    "pageNumber": 1,
    "pageSize": 20
  }'
```

### 预期响应

```json
{
  "datas": {
    "startDate": "20260401",
    "items": [...]
  },
  "success": true,
  "serviceStatus": "000",
  "serviceDescription": "查询成功，共 8 条记录",
  "totalRecords": 8,
  "totalPages": 1,
  "pageNumber": 1,
  "pageSize": 20,
  "sign": null
}
```

---

## 常见问题

### Q1：如何添加不需要 EID 过滤的服务？

**A**：在 Service 中明确说明：

```java
// 特殊服务，不需要 EID 过滤
String sql = "SELECT * FROM PUBLIC_CONFIG WHERE ENABLED = 'Y'";
```

### Q2：Token 从哪里来？

**A**：Token 来源有三种方式：
1. 从数据库表 `SYS_TOKEN` 中生成和验证
2. 从配置文件 `application.yml` 中读取
3. 从 JWT Token 中解析

### Q3：如何处理大数据量分页？

**A**：使用游标分页或限制最大页数：

```java
// 限制最大页数
if (pageNumber > 100) {
    pageNumber = 100;
}

// 或使用游标分页
String sql = "SELECT * FROM TABLE WHERE ID > ? ORDER BY ID LIMIT ?";
```

---

## 参考文档

- **DaySaleQuery 服务**：`src/main/java/com/report/service/DaySaleQueryService.java`
- **Oracle 分页语法**：https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/SELECT.html
- **Spring Boot 最佳实践**：https://spring.io/guides

---

**文档版本**：v1.0  
**创建时间**：2026-04-13  
**适用工程**：REPORT 报表系统
