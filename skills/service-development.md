# 服务开发通用规范

## 📋 标准请求格式

所有服务都使用统一的 JSON 格式：

```json
{
  "serviceId": "DaySaleQuery",
  "request": {
    "startDate": "20260401",
    "endDate": "20260407",
    "shopId": "120021"
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
| `serviceId` | String | ✅ | 服务 ID，如 `DaySaleQuery`、`ShopSaleForecastQuery` |
| `request` | Object | ✅ | 业务参数对象 |
| `sign` | Object | ✅ | 签名信息（包含 token） |
| `pageNumber` | Integer | ❌ | 页码（默认 1，不分页服务可忽略） |
| `pageSize` | Integer | ❌ | 每页大小（默认 20，不分页服务可忽略） |

---

## 🔌 统一访问地址

**所有服务都通过统一入口访问：**

```
POST /api/service
```

**不要**为每个服务创建独立的 Controller 和 URL 路径！

---

## 📝 开发步骤

### 1. 创建服务实现类

位置：`src/main/java/com/report/service/impl/xxx/`

```java
package com.report.service.impl.xxx;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import com.report.service.impl.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("xxxQueryService")
public class XxxQueryServiceImpl extends BaseService implements ReportService {
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }
        
        try {
            // 从 token 解析 EID
            String eid = resolveEid(jdbcTemplate, params);
            if (eid == null || eid.isEmpty()) {
                return ServiceResponse.error("401", "未找到用户信息，请重新登录");
            }
            
            // 获取业务参数
            String startDate = getStringParam(params, "startDate", "");
            String endDate = getStringParam(params, "endDate", "");
            String shopId = getStringParam(params, "shopId", "");
            
            // 查询数据
            // ...
            
            return ServiceResponse.success(resultData);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }
}
```

**要点**：
- ✅ 继承 `BaseService`（获取 `resolveEid`、`getStringParam` 等工具方法）
- ✅ 实现 `ReportService` 接口
- ✅ 使用 `@Service` 注解
- ✅ 从 token 解析 EID
- ✅ 统一错误处理

---

### 2. 注册服务路由

文件：`src/main/java/com/report/service/impl/ServiceRouter.java`

```java
@Service("serviceRouter")
public class ServiceRouter {
    
    @Autowired(required = false)
    private XxxQueryServiceImpl xxxQueryService;
    
    public ServiceResponse<?> route(ServiceRequest request) {
        String serviceId = request.getServiceId();
        // ...
        
        switch (serviceId) {
            // ... 其他服务
            case "XxxQuery":
                return xxxQueryService != null ? 
                    xxxQueryService.execute(paramsMap, pageNumber, pageSize) : 
                    ServiceResponse.error("500", "服务未初始化");
            default:
                return ServiceResponse.error("999", "未知服务 ID: " + serviceId);
        }
    }
}
```

**要点**：
- ✅ 添加 `@Autowired` 注入服务
- ✅ 在 `switch` 中添加 `case` 分支
- ✅ 使用统一的 serviceId 命名

---

### 3. 前端调用

文件：`src/api/xxx.js`

```javascript
import request from './request'

/**
 * 查询 XXX 数据
 */
export function queryXxx(data) {
  return request({
    url: '/api/service',
    method: 'post',
    data: {
      serviceId: 'XxxQuery',
      request: data,
      sign: {
        key: '',
        sign: '',
        token: localStorage.getItem('token') || ''
      },
      pageNumber: 1,
      pageSize: 20
    }
  })
}
```

**要点**：
- ✅ 统一使用 `/api/service` 地址
- ✅ 通过 `serviceId` 区分不同服务
- ✅ 从 localStorage 获取 token

---

## 📚 现有服务列表

| serviceId | 实现类 | 说明 |
|-----------|--------|------|
| `DaySaleQuery` | `DaySaleQueryServiceImpl` | 每日销售查询 |
| `DcpSaleQuery` | `DcpSaleQueryServiceImpl` | DCP 销售查询 |
| `DayShopGoodsQuery` | `DayShopGoodsQueryServiceImpl` | 门店商品销售查询 |
| `DayChannelQuery` | `DayChannelQueryServiceImpl` | 渠道销售查询 |
| `DayShopChannelQuery` | `DayShopChannelQueryServiceImpl` | 门店渠道查询 |
| `StockSumQuery` | `StockSumQueryServiceImpl` | 库存汇总查询 |
| `StockQuery` | `StockQueryServiceImpl` | 库存查询 |
| `CategorySaleQuery` | `CategorySaleQueryServiceImpl` | 品类销售分析 |
| `AllEidQuery` | `AllEidQueryServiceImpl` | 所有 EID 查询 |
| `ShopSaleForecastQuery` | `ShopSaleForecastQueryServiceImpl` | 销售预估准确性分析 |

---

## ⚠️ 注意事项

### 1. 不要创建独立 Controller

❌ **错误做法**：
```java
@RestController
@RequestMapping("/api/xxx-query")
public class XxxQueryController {
    @PostMapping("/query")
    public ServiceResponse query(...) { ... }
}
```

✅ **正确做法**：
- 只创建 Service 实现类
- 在 `ServiceRouter` 中注册
- 通过 `/api/service` 统一访问

### 2. Token 解析

所有服务都应该从 token 解析 EID，不要从请求参数中直接获取：

```java
// ✅ 正确
String eid = resolveEid(jdbcTemplate, params);

// ❌ 错误
String eid = params.get("eid").toString();
```

### 3. 错误处理

统一使用 `ServiceResponse.error()` 返回错误：

```java
return ServiceResponse.error("500", "错误信息");
```

### 4. 分页处理

- 需要分页的服务：使用 `pageNumber` 和 `pageSize` 参数
- 不分页的服务：忽略这两个参数

---

## 🔧 常用工具方法

继承 `BaseService` 后可用的工具方法：

| 方法 | 说明 |
|------|------|
| `resolveEid(JdbcTemplate, Map)` | 从 token 解析 EID |
| `getStringParam(Map, String, String)` | 获取字符串参数 |
| `getIntParam(Map, String, int)` | 获取整数参数 |
| `buildPaginatedResult(...)` | 构建分页结果 |

---

## 📖 参考示例

- `DaySaleQueryServiceImpl.java` - 标准实现示例
- `ServiceRouter.java` - 路由注册示例
- `shopSaleForecast.js` - 前端调用示例

---

**创建时间**: 2026-04-09  
**最后更新**: 2026-04-09  
**维护人**: AI Assistant
