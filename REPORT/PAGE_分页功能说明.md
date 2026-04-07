# 分页功能说明

## 服务规范

### 请求格式
```json
{
  "serviceId": "ServiceName",
  "request": {
    "field1": "20260401",
    "field2": "20260407"
  },
  "sign": {
    "key": "apicode",
    "sign": "",
    "token": "5E99F26A391F4E1293FCC01BFD3ACD9F"
  },
  "pageNumber": 1,
  "pageSize": 20
}
```

### 分页规则
- `pageNumber` 和 `pageSize` 任一为空或 0 → 不分页，返回全部数据
- `pageNumber > 0` 且 `pageSize > 0` → 分页查询

### 响应格式
```json
{
  "datas": {...},
  "success": true,
  "serviceStatus": "000",
  "serviceDescription": "查询成功",
  "totalRecords": 100,
  "totalPages": 5,
  "pageNumber": 1,
  "pageSize": 20,
  "sign": null
}
```

### 分页字段说明
| 字段 | 说明 | 不分页时的值 |
|------|------|-------------|
| totalRecords | 总记录数 | 实际记录数 |
| totalPages | 总页数 | 1（有数据）或 0（无数据） |
| pageNumber | 当前页码 | 1 |
| pageSize | 每页大小 | 0 |

## 已实现的服务

✅ DaySaleQuery - 每日销售查询

## 待完善的服务
- queryMember
- queryReport  
- DayShopGoodsQuery
- DayChannelQuery
- DayShopChannelQuery
- StockQuery
- StockSumQuery
- GetStockColumns
- AllEidQuery

## 测试示例

### 分页查询（每页 10 条，第 1 页）
```bash
curl -X POST http://localhost:8110/api/service \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId": "DaySaleQuery",
    "request": {"startDate": "20250101", "endDate": "20261231"},
    "sign": {"key": "digiwin", "sign": "", "token": ""},
    "pageNumber": 1,
    "pageSize": 10
  }'
```

### 不分页查询
```bash
curl -X POST http://localhost:8110/api/service \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId": "DaySaleQuery",
    "request": {"startDate": "20250101", "endDate": "20261231"},
    "sign": {"key": "digiwin", "sign": "", "token": ""}
  }'
```

## 后端实现要点

1. ServiceRequest 添加了 pageNumber 和 pageSize 字段
2. ServiceResponse 已有分页字段（totalRecords, totalPages, pageNumber, pageSize）
3. buildPaginatedResult 方法处理 Oracle 分页 SQL
4. 分页使用 Oracle 的 ROWNUM 语法

## 注意事项

- 分页 SQL 使用 Oracle 语法（ROWNUM）
- 页码从 1 开始
- 如果请求页码超过总页数，自动调整为最后一页
