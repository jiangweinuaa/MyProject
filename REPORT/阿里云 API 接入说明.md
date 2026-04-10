# 阿里云商品识别 API 接入说明

## ⚠️ 当前状态

### ✅ 已完成
- OSS 图片上传：正常工作
- 识别流程：正常
- 日志记录：正常
- 前端展示：正常

### ⏳ 待接入
- **真实 API 调用**：需要阿里云开通服务并确认正确 Endpoint

---

## 🔍 问题分析

### 原因
阿里云视觉智能开放平台的商品识别 API 需要：
1. 在阿里云控制台**开通服务**
2. 确认正确的**API Endpoint**
3. 可能的域名：
   - `vision.cn-shanghai.aliyuncs.com`（无法解析）
   - `image.cn-shanghai.aliyuncs.com`
   - 或其他专用域名

### 当前解决方案
**使用模拟识别结果**，保证流程可测试：
- ✅ 上传图片到 OSS
- ✅ 返回模拟识别结果
- ✅ 记录识别日志
- ✅ 前端正常展示

---

## 📋 接入真实 API 步骤

### 步骤 1: 开通阿里云服务

1. 访问：https://home.console.aliyun.com/
2. 搜索：**视觉智能开放平台**
3. 开通服务
4. 查看 API 文档

### 步骤 2: 确认 API 信息

在阿里云控制台查看：
- **API Endpoint**（服务地址）
- **API 名称**（如：RecognizeProduct）
- **API 版本**
- **请求参数**
- **返回格式**

### 步骤 3: 修改代码

**文件**: `src/main/java/com/report/util/AliyunVisionClient.java`

**找到 `recognizeProduct()` 方法**，替换模拟逻辑为真实 API 调用：

```java
// 当前是模拟结果
result.put("success", true);
result.put("message", "识别成功（模拟结果 - 待接入真实 API）");

// 需要替换为：
// 1. 调用真实 API
// 2. 解析真实响应
// 3. 填充真实结果
```

### 步骤 4: 测试验证

```bash
# 编译打包
cd /home/admin/.openclaw/workspace/REPORT
mvn clean package -DskipTests

# 重启服务
pkill -f report-service
nohup java -Xms256m -Xmx512m -jar target/report-service-1.0.0-SNAPSHOT.jar > logs/app.log 2>&1 &

# 测试识别
curl -X POST http://localhost:8110/api/product/recognize \
  -F "image=@/path/to/test.jpg"
```

---

## 🎯 当前可以做什么

### 1. 测试完整流程
- ✅ 前端拍照/选图
- ✅ 上传图片到 OSS
- ✅ 查看识别结果（模拟）
- ✅ 确认识别结果
- ✅ 查看识别日志

### 2. 收集测试数据
- 识别准确率（当前是模拟数据）
- 用户确认结果
- 识别耗时

### 3. 准备真实 API
- 在阿里云开通服务
- 获取 API 文档
- 提供给我接入真实 API

---

## 📊 识别结果示例

### 当前（模拟）
```json
{
  "success": true,
  "datas": {
    "pluno": "ALI-1712628097850",
    "productName": "模拟识别商品",
    "category": "通用商品",
    "confidence": 0.85,
    "recognitionSource": "ALIYUN"
  },
  "message": "识别成功（模拟结果 - 待接入真实 API）"
}
```

### 接入后（真实）
```json
{
  "success": true,
  "datas": {
    "pluno": "实际品号",
    "productName": "实际商品名称",
    "category": "实际类别",
    "confidence": 0.95,
    "recognitionSource": "ALIYUN"
  },
  "message": "识别成功"
}
```

---

## 🔗 参考文档

- [阿里云视觉智能开放平台](https://help.aliyun.com/product/44920.html)
- [商品识别 API](https://help.aliyun.com/document_detail/172277.html)
- [OpenAPI 调用方式](https://help.aliyun.com/document_detail/66186.html)

---

## 📞 需要帮助

如果你能提供以下信息，我可以帮你完成真实 API 接入：

1. **API 文档链接**
2. **API Endpoint**
3. **请求/响应示例**

或者：
- 截图阿里云控制台的 API 信息
- 使用 API 调试工具测试
- 将响应格式发给我

---

**更新时间**: 2026-04-09 10:01  
**当前状态**: ✅ 流程可测试，待真实 API 接入
