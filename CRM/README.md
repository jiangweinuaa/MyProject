# CRM 会员卡查询微服务

## 📋 项目简介

基于 Spring Boot 3.2 + Spring Cloud 的会员卡查询微服务，提供 RESTful API 接口查询会员卡信息。

---

## 🏗️ 技术栈

| 组件 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.2.0 |
| Spring Cloud | 2023.0.0 |
| MySQL | 8.0 |
| Maven | 3.6+ |

---

## 📁 项目结构

```
CRM/
├── pom.xml                          # Maven 配置
├── README.md                        # 项目说明
└── src/
    ├── main/
    │   ├── java/com/crm/
    │   │   ├── CrmCardServiceApplication.java    # 启动类
    │   │   ├── controller/
    │   │   │   └── CrmCardController.java        # 控制器
    │   │   ├── service/
    │   │   │   ├── CrmCardService.java           # 服务接口
    │   │   │   └── impl/
    │   │   │       └── CrmCardServiceImpl.java   # 服务实现
    │   │   ├── repository/
    │   │   │   └── CrmCardRepository.java        # 数据访问层
    │   │   ├── entity/
    │   │   │   └── CrmCard.java                  # 实体类
    │   │   └── dto/
    │   │       ├── QueryCardRequest.java         # 请求 DTO
    │   │       ├── QueryCardResponse.java        # 响应 DTO
    │   │       └── ApiResponse.java              # 统一响应
    │   └── resources/
    │       └── application.yml                   # 配置文件
    └── test/java/com/crm/                        # 测试代码
```

---

## 🚀 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0

### 2. 数据库配置

确保 MySQL 已启动，数据库 `openclaw_db` 和表 `CRM_CARD` 已创建。

### 3. 编译项目

```bash
cd /home/admin/.openclaw/workspace/CRM
mvn clean package -DskipTests
```

### 4. 启动服务

```bash
java -jar target/crm-card-service-1.0.0-SNAPSHOT.jar
```

或使用 Maven：

```bash
mvn spring-boot:run
```

### 5. 访问服务

服务启动后访问：http://localhost:8080/api

---

## 📡 API 接口

### 查询卡片信息

**接口：** `POST /api/card/query`

**请求头：**
```
Content-Type: application/json
```

**请求体：**
```json
{
  "cardNo": "000001"
}
```

**响应：**
```json
{
  "cardId": 1,
  "cardNo": "000001",
  "cardSnNo": "SN20260311000001",
  "memberId": "M000001",
  "memberName": "测试会员",
  "amount": 10000.0000,
  "faceAmount": 10000.0000,
  "status": 1,
  "validDate": "2026-12-31",
  "createTime": "2026-03-11",
  "remark": "初始化测试卡",
  "code": 200,
  "message": "查询成功",
  ...
}
```

### 查询所有卡片信息

**接口：** `GET /api/card/queryAll`

**请求：** 无参数

**响应：**
```json
[
  {
    "cardId": 1,
    "cardNo": "000001",
    "cardSnNo": "SN20260311000001",
    "memberName": "测试会员",
    "amount": 10000.0000,
    "faceAmount": 10000.0000,
    "status": 1,
    "code": 200,
    "message": "查询成功",
    ...
  }
]
```

### 健康检查

**接口：** `GET /api/card/health`

**响应：**
```
CRM Card Service is running
```

---

## 🔧 配置说明

### application.yml

```yaml
server:
  port: 8080                    # 服务端口
  servlet:
    context-path: /api          # 上下文路径

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/openclaw_db
    username: openclaw_user
    password: j7gLZHHYGcAeOTcy
```

---

## 🧪 测试示例

### 使用 curl 测试

```bash
curl -X POST http://localhost:8080/api/card/query \
  -H "Content-Type: application/json" \
  -d '{"cardNo": "000001"}'
```

### 使用 Postman 测试

1. 方法：POST
2. URL：http://localhost:8080/api/card/query
3. Headers：Content-Type = application/json
4. Body (raw JSON)：
```json
{
  "cardNo": "000001"
}
```

---

## 📊 响应码说明

| 代码 | 说明 |
|------|------|
| 200 | 查询成功 |
| 400 | 请求参数错误 |
| 404 | 卡片不存在 |
| 500 | 服务器内部错误 |

---

## 📝 开发说明

### 添加新接口

1. 在 `controller` 包创建新的 Controller
2. 在 `service` 包定义接口和实现
3. 在 `dto` 包定义请求和响应 DTO

### 数据库变更

1. 更新 `entity/CrmCard.java`
2. 更新数据库表结构
3. 更新 `repository` 查询方法

---

## ⚠️ 注意事项

1. 生产环境请修改数据库密码
2. 建议配置日志级别和日志文件
3. 建议添加接口认证和限流
4. 建议配置连接池参数

---

## 📄 License

MIT License
