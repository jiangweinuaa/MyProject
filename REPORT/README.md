# REPORT Service

RESTful 报表服务，基于 Spring Boot 2.7 + JDK 1.8

## 技术栈

- **JDK**: 1.8
- **Spring Boot**: 2.7.18
- **JSON**: FastJSON2
- **工具库**: Hutool
- **构建工具**: Maven

## 快速开始

### 1. 环境要求
```bash
java -version  # 需要 Java 8
mvn -version   # 需要 Maven 3.6+
```

### 2. 构建项目
```bash
cd REPORT
mvn clean package -DskipTests
```

### 3. 运行服务
```bash
java -jar target/report-service-1.0.0-SNAPSHOT.jar
```

或
```bash
mvn spring-boot:run
```

### 4. 访问服务
服务启动后访问：http://localhost:8080

## API 接口

### 统一服务入口

**POST** `/api/service`

请求格式：
```json
{
  "serviceId": "QueryMember",
  "request": {
    "mobile": "13952010514",
    "withAccount": "1"
  },
  "sign": {
    "key": "digiwin",
    "sign": "280effda5c775cfc193b794f89bd741d"
  }
}
```

响应格式：
```json
{
  "datas": {
    "memberId": "6220013808",
    "mobile": "13952010514",
    "name": "微信用户",
    ...
  },
  "success": true,
  "serviceStatus": "000",
  "serviceDescription": "服务执行成功",
  "sign": {
    "key": "digiwin",
    "sign": "0d20ef4bc1f2510d790bca040bad16a5"
  }
}
```

### 直接调用服务

**POST** `/api/service/{serviceId}`

```bash
curl -X POST http://localhost:8080/api/service/QueryMember \
  -H "Content-Type: application/json" \
  -d '{"mobile":"13952010514","withAccount":"1"}'
```

**GET** `/api/service/{serviceId}`

```bash
curl -X GET "http://localhost:8080/api/service/QueryMember?mobile=13952010514&withAccount=1"
```

### 健康检查

**GET** `/api/health`

```bash
curl http://localhost:8080/api/health
```

## 项目结构

```
REPORT/
├── src/main/java/com/report/
│   ├── ReportServiceApplication.java    # 启动类
│   ├── controller/
│   │   └── ServiceController.java       # REST 控制器
│   ├── service/
│   │   ├── ReportService.java           # 服务接口
│   │   └── impl/
│   │       └── ReportServiceImpl.java   # 服务实现
│   ├── dto/
│   │   ├── ServiceRequest.java          # 请求封装
│   │   ├── ServiceResponse.java         # 响应封装
│   │   └── SignInfo.java                # 签名信息
│   ├── util/
│   │   └── SignUtil.java                # 签名工具
│   └── config/                          # 配置类
├── src/main/resources/
│   └── application.yml                  # 配置文件
└── pom.xml                              # Maven 配置
```

## 扩展新服务

1. 在 `ReportServiceImpl` 中添加新的服务方法
2. 在 `execute` 方法的 switch 中添加路由

```java
case "YourService":
    return yourService(params);
```

## 配置说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| server.port | 8080 | 服务端口 |
| report.default-key | digiwin | 默认签名密钥 |
| report.sign-verify | false | 是否验证签名 |
| report.timeout | 30000 | 服务超时 (ms) |

## 签名算法

MD5 签名：`MD5(content + key)`

```java
String sign = SignUtil.generateSign(content, key);
```

## License

MIT
