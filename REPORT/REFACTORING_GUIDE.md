# REPORT 工程重构指南

## 🎯 重构目标

将 `ReportServiceImpl.java` 中的多个服务实现拆分到独立的类中，每个类只负责一个服务。

## 📁 新的目录结构

```
src/main/java/com/report/
├── service/
│   ├── ReportService.java          # 服务接口
│   └── impl/
│       ├── ServiceRouter.java      # 服务路由类
│       └── sales/                  # 销售相关服务
│           ├── DaySaleQueryServiceImpl.java    # 每日销售查询
│           └── DcpSaleQtyServiceImpl.java      # 商品销售明细
└── controller/
    └── ServiceController.java      # Controller（使用 ServiceRouter）
```

## 📝 已完成的工作

### 1. 修改服务接口
**文件**: `src/main/java/com/report/service/ReportService.java`

```java
public interface ReportService {
    ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize);
}
```

### 2. 创建独立服务实现
- ✅ `DaySaleQueryServiceImpl.java` - 每日销售查询
- ✅ `DcpSaleQtyServiceImpl.java` - 商品销售明细

### 3. 创建服务路由类
**文件**: `src/main/java/com/report/service/impl/ServiceRouter.java`

```java
@Service("serviceRouter")
public class ServiceRouter {
    @Autowired
    private DaySaleQueryServiceImpl daySaleQueryService;
    
    @Autowired
    private DcpSaleQtyServiceImpl dcpSaleQtyService;
    
    public ServiceResponse<?> route(ServiceRequest request) {
        switch (request.getServiceId()) {
            case "DaySaleQuery":
                return daySaleQueryService.execute(params, pageNumber, pageSize);
            case "DcpSaleQty":
                return dcpSaleQtyService.execute(params, pageNumber, pageSize);
            default:
                return ServiceResponse.error("999", "未知服务 ID");
        }
    }
}
```

### 4. 修改 Controller
**文件**: `src/main/java/com/report/controller/ServiceController.java`

```java
@Autowired
private ServiceRouter serviceRouter;

@PostMapping("/service")
public ServiceResponse<?> service(@RequestBody ServiceRequest request) {
    return serviceRouter.route(request);
}
```

## 🔄 待完成的工作

### 需要迁移的服务（共 7 个）

| 服务 ID | 原方法名 | 新类名 | 状态 |
|--------|---------|--------|------|
| DaySaleQuery | daySaleQuery() | DaySaleQueryServiceImpl | ✅ 已完成 |
| DcpSaleQty | dcpSaleQty() | DcpSaleQtyServiceImpl | ✅ 已完成 |
| DayShopGoodsQuery | dayShopGoodsQuery() | DayShopGoodsQueryServiceImpl | ⏳ 待创建 |
| DayChannelQuery | dayChannelQuery() | DayChannelQueryServiceImpl | ⏳ 待创建 |
| DayShopChannelQuery | dayShopChannelQuery() | DayShopChannelQueryServiceImpl | ⏳ 待创建 |
| StockSumQuery | stockSumQuery() | StockSumQueryServiceImpl | ⏳ 待创建 |
| StockQuery | stockQuery() | StockQueryServiceImpl | ⏳ 待创建 |

### 步骤

1. **为每个服务创建独立类**
   ```bash
   mkdir -p src/main/java/com/report/service/impl/sales
   mkdir -p src/main/java/com/report/service/impl/stock
   ```

2. **复制方法体到新类**
   - 从 `ReportServiceImpl.java` 复制对应方法
   - 添加到新的 ServiceImpl 类中

3. **在 ServiceRouter 中注册**
   ```java
   @Autowired
   private DayShopGoodsQueryServiceImpl dayShopGoodsQueryService;
   
   // 在 route() 方法中添加 case
   case "DayShopGoodsQuery":
       return dayShopGoodsQueryService.execute(params, pageNumber, pageSize);
   ```

4. **删除旧的 ReportServiceImpl**
   ```bash
   rm src/main/java/com/report/service/impl/ReportServiceImpl.java
   ```

## ✅ 重构优势

1. **单一职责** - 每个类只负责一个服务
2. **易于维护** - 修改某个服务不影响其他服务
3. **易于测试** - 可以单独测试每个服务
4. **代码清晰** - 类名即服务名，一目了然
5. **团队协作** - 不同开发者可以维护不同的服务类

## 📋 重构检查清单

- [ ] 所有服务都创建了独立类
- [ ] ServiceRouter 中注册了所有服务
- [ ] Controller 使用 ServiceRouter
- [ ] 删除了旧的 ReportServiceImpl
- [ ] 编译通过
- [ ] 测试通过
- [ ] 提交 Git

---

*重构开始时间：2026-04-07*
*当前进度：2/7 服务已完成*
