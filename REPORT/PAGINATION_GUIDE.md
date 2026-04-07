# 分页功能使用指南

## 📋 统一分页方法

所有分页查询统一使用 `buildPaginatedResult` 方法。

### 方法签名

```java
private Map<String, Object> buildPaginatedResult(
    String sql,           // 原始 SQL（必须包含 ORDER BY）
    Integer pageNumber,   // 页码（从 1 开始）
    Integer pageSize,     // 每页大小
    String countSql,      // COUNT SQL
    Object... sqlParams   // SQL 参数
)
```

### 返回结果

```java
Map<String, Object> pageData = buildPaginatedResult(...);
pageData.get("list");         // List<Map<String, Object>> 当前页数据
pageData.get("totalRecords"); // Integer 总记录数
pageData.get("totalPages");   // Integer 总页数
pageData.get("pageNumber");   // Integer 当前页码
pageData.get("pageSize");     // Integer 每页大小
```

---

## 🎯 使用步骤

### 1️⃣ 构建原始 SQL（必须包含 ORDER BY）

```java
StringBuilder sqlBuilder = new StringBuilder();
sqlBuilder.append("select a.SHOPID,a.SALENO, ... ");
sqlBuilder.append("FROM DCP_SALE a ");
sqlBuilder.append("where a.EID = ? AND a.BDATE>=? and a.BDATE <= ? ");
sqlBuilder.append("order by a.SDATE,a.STIME,a.SHOPID,a.SALENO");  // ⚠️ 必须有 ORDER BY

String sql = sqlBuilder.toString();
```

### 2️⃣ 构建 COUNT SQL

```java
StringBuilder countSqlBuilder = new StringBuilder();
countSqlBuilder.append("SELECT COUNT(*) FROM DCP_SALE a ");
countSqlBuilder.append("where a.EID = ? AND a.BDATE>=? and a.BDATE <= ?");

String countSql = countSqlBuilder.toString();
```

### 3️⃣ 准备 SQL 参数

```java
List<Object> sqlParams = new java.util.ArrayList<>();
sqlParams.add(eid);
sqlParams.add(startDate);
sqlParams.add(endDate);
```

### 4️⃣ 调用分页方法

```java
Map<String, Object> pageData = buildPaginatedResult(
    sql, pageNumber, pageSize, countSql, sqlParams.toArray()
);

List<Map<String, Object>> resultList = 
    (List<Map<String, Object>>) pageData.get("list");
```

### 5️⃣ 设置响应分页信息

```java
ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
response.setDatas(resultData);
response.setTotalRecords((Integer) pageData.get("totalRecords"));
response.setTotalPages((Integer) pageData.get("totalPages"));
response.setPageNumber((Integer) pageData.get("pageNumber"));
response.setPageSize((Integer) pageData.get("pageSize"));
```

---

## 📊 分页参数说明

### 前端传递参数

```json
{
  "serviceId": "DcpSaleQty",
  "request": {...},
  "sign": {...},
  "pageNumber": 1,    // 页码（从 1 开始）
  "pageSize": 20      // 每页大小（0 或 10000 表示不分页）
}
```

### 分页规则

| pageNumber | pageSize | 行为 |
|-----------|----------|------|
| null/0 | null/0 | 不分页，返回全部 |
| 1 | 10 | 第 1 页，每页 10 条 |
| 2 | 20 | 第 2 页，每页 20 条 |
| 1 | 10000 | 不分页（前端"不分页"选项） |

### Oracle 分页 SQL（三层嵌套）

```sql
select * from (
  SELECT rownum as NUM, ALLTABLE.* FROM (
    -- 原始 SQL（包含 ORDER BY）
  ) ALLTABLE
) where NUM >= ? AND NUM <= ?
```

**示例：**
- 第 1 页（10 条）：`NUM >= 1 AND NUM <= 10`
- 第 2 页（10 条）：`NUM >= 11 AND NUM <= 20`
- 第 3 页（10 条）：`NUM >= 21 AND NUM <= 30`

---

## ✅ 已使用分页的服务

| 服务 ID | 方法名 | 状态 |
|--------|--------|------|
| DaySaleQuery | daySaleQuery() | ✅ 已实现 |
| DayShopGoodsQuery | dayShopGoodsQuery() | ✅ 已实现 |
| DayChannelQuery | dayChannelQuery() | ✅ 已实现 |
| DayShopChannelQuery | dayShopChannelQuery() | ✅ 已实现 |
| StockSumQuery | stockSumQuery() | ✅ 已实现 |
| StockQuery | stockQuery() | ✅ 已实现 |
| DcpSaleQty | dcpSaleQty() | ✅ 已实现 |

---

## 📝 代码模板

```java
private ServiceResponse<?> xxxQuery(Object params, Integer pageNumber, Integer pageSize) {
    if (jdbcTemplate == null) {
        return ServiceResponse.error("500", "数据库未连接");
    }

    try {
        // 1. 提取参数
        String startDate = "...";
        String endDate = "...";
        
        if (params instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) params;
            if (paramMap.get("startDate") != null) {
                startDate = paramMap.get("startDate").toString();
            }
            // ...
        }

        // 2. 构建 SQL（必须包含 ORDER BY）
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select ... ");
        sqlBuilder.append("FROM ... ");
        sqlBuilder.append("where ... ");
        sqlBuilder.append("order by ...");  // ⚠️ 必须有
        
        String sql = sqlBuilder.toString();
        
        // 3. 构建 COUNT SQL
        String countSql = "SELECT COUNT(*) FROM ... where ...";
        
        // 4. 准备参数
        List<Object> sqlParams = new java.util.ArrayList<>();
        sqlParams.add(...);
        
        // 5. 调用分页方法
        Map<String, Object> pageData = buildPaginatedResult(
            sql, pageNumber, pageSize, countSql, sqlParams.toArray()
        );
        List<Map<String, Object>> resultList = 
            (List<Map<String, Object>>) pageData.get("list");

        // 6. 构建响应
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("list", resultList);
        
        ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
        response.setDatas(resultData);
        
        // 7. 设置分页信息
        response.setTotalRecords((Integer) pageData.get("totalRecords"));
        response.setTotalPages((Integer) pageData.get("totalPages"));
        response.setPageNumber((Integer) pageData.get("pageNumber"));
        response.setPageSize((Integer) pageData.get("pageSize"));

        return response;

    } catch (Exception e) {
        e.printStackTrace();
        return ServiceResponse.error("500", "查询失败：" + e.getMessage());
    }
}
```

---

## ⚠️ 注意事项

1. **ORDER BY 必须存在** - 原始 SQL 必须包含 ORDER BY 子句，否则分页结果无序
2. **COUNT SQL 不需要 ORDER BY** - COUNT SQL 只统计数量，不需要排序
3. **参数顺序一致** - sqlParams 的参数顺序必须与 SQL 中的 `?` 顺序一致
4. **不分页逻辑** - `pageNumber` 或 `pageSize` 为 null/0 时，返回全部数据
5. **页码越界处理** - 自动调整为最后一页

---

*文档更新时间：2026-04-07*
