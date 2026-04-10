# ✅ 阿里云商品分类 API 接入完成

## 📦 版本信息

- **版本**: v1.2.2
- **时间**: 2026-04-09 10:04
- **状态**: ✅ 已接入真实 API

---

## 🎯 对比结果

### 文档信息 vs 之前实现

| 项目 | 文档要求 | 之前实现 | 当前状态 |
|------|----------|----------|----------|
| **API 名称** | `ClassifyCommodity` | `RecognizeProduct` | ✅ 已修正 |
| **Endpoint** | `goodstech.cn-shanghai.aliyuncs.com` | `vision.cn-shanghai.aliyuncs.com` | ✅ 已修正 |
| **Version** | `2019-12-30` | `2019-12-30` | ✅ 正确 |
| **参数** | `ImageURL` | `ImageURL` | ✅ 正确 |
| **返回格式** | `Data.Categories[0]` | 通用 JSON | ✅ 已调整 |

---

## 🔧 修改内容

### 1. AliyunVisionClient.java

**修改 `recognizeProduct()` 方法**:

```java
// 正确的 Endpoint
String host = "goodstech.cn-shanghai.aliyuncs.com";

// 正确的 Action
params.put("Action", "ClassifyCommodity");

// 解析返回的 CategoryName 和 Score
String categoryName = extractFromJson(responseBody, "CategoryName");
String score = extractFromJson(responseBody, "Score");

result.put("pluno", "ALI-" + categoryId);
result.put("productName", categoryName);
result.put("confidence", score);
```

### 2. callOpenApi() 方法

```java
// 添加正确的公共参数
params.put("Action", "ClassifyCommodity");
params.put("Version", "2019-12-30");
```

---

## 📊 API 返回格式

### 请求示例

```
POST https://goodstech.cn-shanghai.aliyuncs.com/
?Action=ClassifyCommodity
&Version=2019-12-30
&ImageURL=http://xxx.jpg
&AccessKeyId=xxx
&Timestamp=xxx
&Signature=xxx
```

### 返回示例

```json
{
  "RequestId": "87C5AF93-F641-54C2-873D-0501E637489C",
  "Data": {
    "Categories": [
      {
        "CategoryId": "584",
        "CategoryName": "旁轴相机",
        "Score": 0.417248
      },
      {
        "CategoryId": "4856",
        "CategoryName": "数码单反",
        "Score": 0.309264
      }
    ]
  }
}
```

### 当前解析逻辑

提取第一个（最佳匹配）结果：
- `CategoryId` → 用于生成品号
- `CategoryName` → 商品名称
- `Score` → 置信度

---

## 🧪 测试步骤

### 1. 接口测试

```bash
curl -X POST http://localhost:8110/api/product/recognize \
  -F "image=@/path/to/test.jpg"
```

**预期返回**:
```json
{
  "success": true,
  "datas": {
    "pluno": "ALI-584",
    "productName": "旁轴相机",
    "category": "旁轴相机",
    "confidence": 0.417,
    "recognitionSource": "ALIYUN"
  },
  "message": "识别成功"
}
```

### 2. 前端测试

1. 访问：`http://47.100.138.89:8081`
2. 进入"🔍 商品识别"页面
3. 拍照或选择图片
4. 查看识别结果（真实 API 返回）

### 3. 日志验证

```bash
tail -f logs/app.log | grep -E "识别|API|goodstech"
```

**预期日志**:
```
✅ 识别图片已上传到 OSS
🔍 开始调用阿里云商品分类 API...
📷 图片 URL: https://xxx.jpg
📡 请求 URL: https://goodstech.cn-shanghai.aliyuncs.com/?...
🔍 商品分类 API 响应：{...}
🎯 识别完成：类目=旁轴相机，置信度=0.417
```

### 4. 数据库验证

```sql
SELECT LOG_ID, RECOGNIZED_PLUNO, RECOGNIZED_NAME, CONFIDENCE, CREATED_TIME 
FROM PRODUCT_RECOGNITION_LOGS 
ORDER BY CREATED_TIME DESC 
FETCH FIRST 10 ROWS ONLY;
```

---

## ⚠️ 重要说明

### 1. 服务开通

确保已在阿里云开通：
- **服务**: 商品理解服务 (goodstech)
- **API**: ClassifyCommodity (商品分类)
- **权限**: 已提交申请并通过

### 2. AccessKey 配置

```sql
SELECT * FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI';
```

确保：
- AccessKey ID 正确
- AccessKey Secret 正确
- 账号有 `AliyunVIAPIFullAccess` 权限

### 3. 支持的类目

- 支持超过 6000 种商品类目
- 包括：服饰鞋包、3C 数码、家居用品等
- 完整类目列表：https://viapi-demo.oss-cn-shanghai.aliyuncs.com/doc/category.txt

### 4. 图片要求

- **格式**: JPG、JPEG、PNG、BMP
- **大小**: 不超过 3 MB
- **URL**: 不能包含中文字符
- **推荐**: 使用上海地域 OSS 链接

### 5. 计费说明

- 该 API 为**付费接口**
- 免费体验请前往：https://vision.aliyun.com/experience
- 详细计费：https://help.aliyun.com/zh/viapi/product-overview/billing-is-introduced-6

---

## 🔍 故障排查

### 问题 1: API 调用失败

**错误**: `UnknownHostException: goodstech.cn-shanghai.aliyuncs.com`

**解决**:
- 检查服务器网络
- 确认 DNS 解析正常
- 检查防火墙规则

### 问题 2: 权限错误

**错误**: `InvalidAccessKeyId.NotFound`

**解决**:
- 确认 AccessKey 正确
- 确认已开通商品理解服务
- 确认已提交 API 使用申请

### 问题 3: 返回结果为空

**检查**:
- 图片 URL 是否可访问
- 图片是否符合要求（格式、大小）
- 图片中是否包含可识别的商品

---

## 📈 后续优化

### 阶段一（当前）
- ✅ 接入真实 API
- ✅ 返回商品类目
- ⏳ 测试识别准确率

### 阶段二
- 映射商品类目到本地品号体系
- 记录识别结果用于分析
- 优化识别准确率

### 阶段三
- 混合识别（本地训练库 + 阿里云）
- 成本优化（缓存识别结果）
- 自定义商品训练

---

## 🔗 参考文档

| 文档 | 链接 |
|------|------|
| **API 文档** | https://help.aliyun.com/document_detail/151536.html |
| **商品理解服务** | https://vision.aliyun.com/goodstech |
| **类目列表** | https://viapi-demo.oss-cn-shanghai.aliyuncs.com/doc/category.txt |
| **OpenAPI Explorer** | https://api.aliyun.com/#product=goodstech&api=ClassifyCommodity |
| **示例代码** | https://help.aliyun.com/zh/viapi/use-cases/classification-of-goods-1 |

---

## 🎯 测试清单

- [ ] 服务已启动（端口 8110）
- [ ] AccessKey 配置正确
- [ ] 阿里云服务已开通
- [ ] API 调用成功
- [ ] 返回商品类目
- [ ] 前端展示正常
- [ ] 日志记录正常
- [ ] 数据库记录正常

---

**完成时间**: 2026-04-09 10:04  
**状态**: ✅ 真实 API 已接入，待测试验证  
**下一步**: 使用实际商品图片测试识别准确率
