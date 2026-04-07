# Content-Type 支持说明

## 当前状态

后端服务已配置为接受多种 Content-Type，但 Spring Boot 的 `@RequestBody` 默认只自动解析 `application/json`。

## 支持的 Content-Type

### ✅ application/json（推荐）
```bash
curl -X POST http://47.100.138.89:8110/api/service \
  -H "Content-Type: application/json" \
  -d '{"serviceId":"DaySaleQuery","request":{"startDate":"20260401","endDate":"20260407"},"sign":{"key":"digiwin","sign":"","token":""}}'
```

### ⚠️ text/plain
需要使用 `application/json`，text/plain 需要额外配置。

## 解决方案

### 方案 1：使用 application/json（推荐）
所有工具都应该支持设置 `Content-Type: application/json` 头。

### 方案 2：如果需要 text/plain 支持
需要修改 Controller 手动解析 JSON 字符串，或者配置自定义的 HttpMessageConverter。

## 建议

**请使用 `Content-Type: application/json`**，这是标准的 REST API 调用方式，所有 HTTP 工具都支持。

如果你的工具不支持设置 Content-Type，请考虑：
1. 更换工具
2. 使用 curl/wget 等命令行工具
3. 使用编程语言的标准 HTTP 库
