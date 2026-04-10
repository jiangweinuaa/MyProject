# 阿里云商品识别 API 接入指南

## 📋 当前状态

- ✅ **OSS 图片上传**: 已完成
- ✅ **API 调用框架**: 已搭建
- ⏳ **真实 API 接入**: 需要配置

---

## 🔧 接入步骤

### 步骤 1: 开通阿里云服务

1. **访问阿里云控制台**: https://home.console.aliyun.com/
2. **开通服务**: 
   - 视觉智能开放平台 (Vision Intelligence)
   - 或者图像识别服务
3. **获取信息**:
   - AccessKey ID (已有)
   - AccessKey Secret (已有)
   - API Endpoint (如：`vision.cn-shanghai.aliyuncs.com`)

### 步骤 2: 查看 API 文档

**参考文档**:
- [视觉智能开放平台 API](https://help.aliyun.com/product/44920.html)
- [商品识别 API](https://help.aliyun.com/document_detail/172277.html)
- [OpenAPI 调用方式](https://help.aliyun.com/document_detail/66186.html)

### 步骤 3: 修改代码

**文件**: `src/main/java/com/report/util/AliyunVisionClient.java`

**找到 `recognizeProduct()` 方法**，替换为真实 API 调用：

#### 方式一：使用官方 SDK（推荐）

```java
// 1. 添加依赖（已在 pom.xml 中添加）
// 需要确认具体的 SDK 包名，参考官方文档

// 2. 修改 recognizeProduct() 方法
public Map<String, Object> recognizeProduct(String imageUrl) {
    String accessKeyId = configUtil.getAccessKeyId();
    String accessKeySecret = configUtil.getAccessKeySecret();
    
    Map<String, Object> result = new HashMap<>();
    
    try {
        // 示例代码（需要根据实际 SDK 调整）
        // 1. 创建客户端
        Client client = new Client(Config.build()
            .setAccessKeyId(accessKeyId)
            .setAccessKeySecret(accessKeySecret)
            .setEndpoint("vision.cn-shanghai.aliyuncs.com"));
        
        // 2. 构建请求
        RecognizeProductRequest request = new RecognizeProductRequest();
        request.setImageURL(imageUrl);
        
        // 3. 调用 API
        RecognizeProductResponse response = client.recognizeProduct(request);
        
        // 4. 解析结果
        result.put("success", true);
        result.put("pluno", response.getBody().getProductId());
        result.put("productName", response.getBody().getProductName());
        result.put("category", response.getBody().getCategory());
        result.put("confidence", response.getBody().getConfidence());
        
        return result;
        
    } catch (Exception e) {
        e.printStackTrace();
        result.put("success", false);
        result.put("message", "识别失败：" + e.getMessage());
        return result;
    }
}
```

#### 方式二：使用 HTTP 调用（通用）

代码已包含 HTTP 调用框架，需要确认：
1. API 路径（如：`/api/v1/products/recognize`）
2. Action 名称（如：`RecognizeProduct`）
3. API 版本（如：`2020-09-01`）
4. 返回格式解析

---

## 📝 需要的配置信息

请在阿里云控制台确认以下信息：

| 配置项 | 说明 | 示例 |
|--------|------|------|
| **API Endpoint** | API 服务地址 | `vision.cn-shanghai.aliyuncs.com` |
| **API 路径** | 具体 API 路径 | `/api/v1/products/recognize` |
| **Action** | API 动作名 | `RecognizeProduct` |
| **Version** | API 版本 | `2020-09-01` |
| **返回格式** | 响应数据结构 | JSON |

---

## 🧪 测试方法

### 1. 编译打包

```bash
cd /home/admin/.openclaw/workspace/REPORT
mvn clean package -DskipTests
```

### 2. 重启服务

```bash
pkill -f report-service
nohup java -Xms256m -Xmx512m -jar target/report-service-1.0.0-SNAPSHOT.jar > logs/app.log 2>&1 &
```

### 3. 测试识别

```bash
curl -X POST http://localhost:8110/api/product/recognize \
  -F "image=@/path/to/test.jpg"
```

### 4. 查看日志

```bash
tail -f logs/app.log | grep -E "识别|API"
```

---

## ⚠️ 注意事项

1. **API 费用**: 商品识别 API 按调用次数计费
2. **并发限制**: 注意 API 的 QPS 限制
3. **图片格式**: 确保图片格式符合要求（JPG/PNG）
4. **图片大小**: 注意 API 对图片大小的限制
5. **网络延迟**: OSS 上传和 API 调用都需要网络时间

---

## 📞 需要帮助

如果你能提供以下信息，我可以帮你完成代码：

1. **API 文档链接**
2. **SDK 包名**（如果使用 SDK）
3. **API 请求/响应示例**

或者你可以：
- 在阿里云控制台查看 API 文档
- 使用 API 调试工具测试
- 将文档内容发给我，我来写代码

---

**当前状态**: 框架已就绪，等待 API 配置信息  
**更新时间**: 2026-04-09 09:40
