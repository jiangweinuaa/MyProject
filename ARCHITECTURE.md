# REPORT 系统技术架构文档

## 📁 项目结构

### 1. REPORT（后端服务）

```
REPORT/
├── pom.xml                          # Maven 配置文件
├── src/main/java/com/report/
│   ├── ReportServiceApplication.java    # Spring Boot 启动类
│   ├── config/
│   │   └── CorsConfig.java              # 跨域配置
│   ├── controller/
│   │   ├── ServiceController.java       # 统一服务入口
│   │   └── DbTestController.java        # 数据库测试接口
│   ├── service/
│   │   ├── LoginService.java            # 登录服务接口
│   │   ├── ReportService.java           # 报表服务接口
│   │   └── impl/
│   │       ├── LoginServiceImpl.java    # 登录服务实现
│   │       └── ReportServiceImpl.java   # 报表服务实现（含分页）
│   ├── dto/
│   │   ├── ServiceRequest.java          # 服务请求 DTO
│   │   ├── ServiceResponse.java         # 服务响应 DTO（含分页字段）
│   │   ├── SignInfo.java                # 签名信息 DTO
│   │   ├── TokenInfo.java               # Token 信息 DTO
│   │   ├── LoginRequest.java            # 登录请求 DTO
│   │   ├── LoginResponse.java           # 登录响应 DTO
│   │   ├── DaySaleQueryRequest.java     # 销售查询请求 DTO
│   │   ├── DayShopGoodsQueryRequest.java # 门店商品查询请求 DTO
│   │   └── DaySaleData.java             # 销售数据 DTO
│   ├── util/
│   │   └── SignUtil.java                # 签名工具类（MD5/AES）
│   └── exception/
├── src/main/resources/
│   └── application.yml                  # Spring Boot 配置文件
└── logs/                                # 日志目录
```

### 2. REPORTUI（前端项目）

```
REPORTUI/
├── package.json                         # Node.js 依赖配置
├── vite.config.js                       # Vite 构建配置
├── index.html                           # 入口 HTML
├── src/
│   ├── main.js                          # Vue 应用入口
│   ├── App.vue                          # 根组件
│   ├── api/
│   │   ├── request.js                   # Axios 请求封装
│   │   ├── auth.js                      # 认证相关 API
│   │   └── report.js                    # 报表服务 API
│   ├── router/
│   │   └── index.js                     # Vue Router 路由配置
│   ├── store/                           # Pinia 状态管理
│   ├── layout/
│   │   └── index.vue                    # 布局组件（侧边栏、顶部导航）
│   ├── views/
│   │   ├── Login.vue                    # 登录页
│   │   ├── SalesAnalysis.vue            # 销售分析页
│   │   ├── GoodsAnalysis.vue            # 商品销售分析页
│   │   ├── StockSummary.vue             # 库存分析页
│   │   └── StockAnalysis.vue            # 商品库存分析页
│   ├── components/                      # 公共组件
│   └── assets/                          # 静态资源
└── dist/                                # 构建输出目录
```

---

## 🛠️ 技术栈

### 后端技术栈（REPORT）

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 1.8 | JDK 版本 |
| **Spring Boot** | 2.7.18 | 应用框架 |
| **Spring Data JPA** | 2.7.18 | 数据持久层 |
| **Oracle JDBC** | 21.9.0.0 | Oracle 数据库驱动 |
| **FastJSON2** | 2.0.43 | JSON 序列化/反序列化 |
| **Hutool** | 5.8.32 | Java 工具库 |
| **Lombok** | - | 简化 Java 代码（可选） |
| **Maven** | - | 项目构建工具 |

**核心技术：**
- RESTful API 设计
- 统一服务入口（ServiceController）
- 分页查询（Oracle ROWNUM）
- MD5/AES 签名验证
- 跨域支持（CORS）

### 前端技术栈（REPORTUI）

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue.js** | 3.4.21 | 前端框架 |
| **Vite** | 5.2.6 | 构建工具 |
| **Vue Router** | 4.3.0 | 路由管理 |
| **Pinia** | 2.1.7 | 状态管理 |
| **Element Plus** | 2.6.1 | UI 组件库 |
| **Element Plus Icons** | 2.3.1 | 图标库 |
| **Axios** | 1.6.8 | HTTP 请求库 |
| **ECharts** | 6.0.0 | 图表库 |

**核心技术：**
- Vue 3 Composition API（`<script setup>`）
- 响应式布局（移动端 + PC 端）
- 路由守卫（登录验证）
- Axios 拦截器（Token 自动注入）
- ECharts 数据可视化

---

## 🏗️ 架构设计

### 后端架构

```
┌─────────────────────────────────────────┐
│           Client (Browser/Mobile)       │
└─────────────────┬───────────────────────┘
                  │ HTTP/HTTPS
                  ▼
┌─────────────────────────────────────────┐
│         ServiceController               │  ← 统一入口
│  POST /api/service                      │
│  POST /api/service/{serviceId}          │
└─────────────────┬───────────────────────┘
                  │ 路由分发
                  ▼
┌─────────────────────────────────────────┐
│        ReportServiceImpl                │  ← 业务逻辑层
│  - daySaleQuery()                       │
│  - dayShopGoodsQuery()                  │
│  - stockQuery()                         │
│  - stockSumQuery()                      │
│  - dayChannelQuery()                    │
│  - dayShopChannelQuery()                │
│  - allEidQuery()                        │
└─────────────────┬───────────────────────┘
                  │ JPA/JDBC
                  ▼
┌─────────────────────────────────────────┐
│        Oracle Database (11g)            │  ← 数据层
│  - DCP_SALE                             │
│  - DCP_SALE_DETAIL                      │
│  - DCP_STOCK                            │
│  - DCP_ORG_LANG                         │
│  - DCP_GOODS_LANG                       │
│  - PLATFORM_TOKEN                       │
│  - PLATFORM_STAFFS                      │
└─────────────────────────────────────────┘
```

**分层架构：**
1. **Controller 层**：接收 HTTP 请求，参数校验
2. **Service 层**：业务逻辑处理，分页查询
3. **DAO 层**：JPA/JDBC 数据访问
4. **DTO 层**：数据传输对象

### 前端架构

```
┌─────────────────────────────────────────┐
│              App.vue                    │  ← 根组件
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│           Router (Vue Router)           │  ← 路由管理
│  /login                                 │
│  /sales-analysis                        │
│  /goods-analysis                        │
│  /stock-summary                         │
│  /stock-analysis                        │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│           Layout Component              │  ← 布局组件
│  - Sidebar (菜单)                        │
│  - Header (顶部导航)                     │
│  - Content (内容区)                      │
└─────────────────┬───────────────────────┘
                  │
        ┌─────────┼─────────┐
        ▼         ▼         ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│  Views   │ │  Store   │ │   API    │
│  页面组件 │ │  Pinia   │ │  Axios   │
└──────────┘ └──────────┘ └──────────┘
```

**组件架构：**
1. **Layout**：全局布局（侧边栏、导航栏）
2. **Views**：页面级组件（登录、销售分析、商品分析等）
3. **Store**：全局状态管理（Pinia）
4. **API**：统一 API 封装（Axios 拦截器）

---

## 📡 API 接口规范

### 统一请求格式

```json
{
  "serviceId": "ServiceName",
  "request": {
    "field1": "value1",
    "field2": "value2"
  },
  "sign": {
    "key": "apicode",
    "sign": "md5 签名",
    "token": "登录 token"
  },
  "pageNumber": 1,
  "pageSize": 20
}
```

### 统一响应格式

```json
{
  "datas": {
    "list": [...],
    "summary": {...}
  },
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

### 分页规则

| 条件 | 行为 |
|------|------|
| `pageNumber` 或 `pageSize` 为空/0 | 不分页，返回全部数据 |
| `pageNumber > 0` 且 `pageSize > 0` | 分页查询 |

**不分页时返回：**
```json
{
  "totalRecords": 50,
  "totalPages": 1,
  "pageNumber": 1,
  "pageSize": 0
}
```

**分页时返回：**
```json
{
  "totalRecords": 100,
  "totalPages": 5,
  "pageNumber": 1,
  "pageSize": 20
}
```

---

## 🔐 安全机制

### 后端安全

1. **Token 验证**
   - 登录生成 token，存入 `PLATFORM_TOKEN` 表
   - 请求时携带 token，后端验证有效性

2. **签名验证**
   - 使用 MD5 对请求参数签名
   - 防止请求被篡改

3. **CORS 配置**
   - 允许跨域访问
   - 生产环境可配置允许域

### 前端安全

1. **路由守卫**
   - 未登录用户重定向到登录页
   - Token 过期自动跳转登录

2. **Token 存储**
   - localStorage 存储 token
   - 退出登录自动清除

3. **请求拦截**
   - Axios 拦截器自动注入 token
   - 统一错误处理

---

## 📊 数据库设计

### 核心表结构

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `DCP_SALE` | 销售主表 | SHOPID, BDATE, TOT_AMT, ORDERCOUNT |
| `DCP_SALE_DETAIL` | 销售明细表 | PLUNO, AMT, BASEQTY |
| `DCP_STOCK` | 库存表 | ORGANIZATIONNO, PLUNO, QTY |
| `DCP_ORG_LANG` | 门店信息表 | ORGANIZATIONNO, ORG_NAME |
| `DCP_GOODS_LANG` | 商品信息表 | PLUNO, PLU_NAME |
| `PLATFORM_TOKEN` | Token 表 | KEY, JSON, UPDATE_TIME |
| `PLATFORM_STAFFS` | 员工/企业表 | EID, OPNO |

---

## 🚀 部署架构

### 后端部署

```bash
# 编译
mvn clean package -DskipTests

# 运行
java -jar target/report-service-1.0.0-SNAPSHOT.jar

# 或使用 systemd 服务
sudo systemctl start report-service
```

**配置文件：** `application.yml`
- 端口：8110
- 数据库：Oracle 11g
- 日志：文件日志

### 前端部署

```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build

# 预览
npm run preview
```

**构建输出：** `dist/` 目录
- 使用 Python SimpleHTTP 或 Nginx 部署
- 端口：8081

---

## 📈 性能优化

### 后端优化

1. **分页查询**
   - Oracle ROWNUM 分页
   - 避免全表扫描

2. **连接池**
   - HikariCP 连接池
   - 最大连接数：20

3. **索引优化**
   - 日期字段索引
   - 门店 ID 索引

### 前端优化

1. **代码分割**
   - Vite 自动代码分割
   - 路由懒加载

2. **资源优化**
   - ECharts 按需引入
   - 图片压缩

3. **缓存策略**
   - 禁止缓存 HTML
   - JS/CSS 文件 hash 缓存

---

## 📝 开发规范

### 后端规范

1. **命名规范**
   - Controller：`XxxController`
   - Service：`XxxService` / `XxxServiceImpl`
   - DTO：`XxxRequest` / `XxxResponse`

2. **异常处理**
   - 统一返回 `ServiceResponse`
   - 错误码：000 成功，其他失败

3. **日志规范**
   - 使用 SLF4J
   - 关键操作打印日志

### 前端规范

1. **组件命名**
   - 文件：`PascalCase.vue`
   - 组件名：`PascalCase`

2. **代码风格**
   - 使用 `<script setup>`
   - 组合式 API

3. **注释规范**
   - 组件说明
   - 复杂逻辑注释

---

## 📞 联系方式

- **项目地址**：`/home/admin/.openclaw/workspace/`
- **后端端口**：8110
- **前端端口**：8081
- **数据库**：Oracle 11g (47.99.153.144:1521/posdb)

---

*文档生成时间：2026-04-07*
