# v1.2.0 商品识别功能发布说明

## 📦 版本信息

- **版本号**: v1.2.0
- **发布时间**: 2026-04-09 09:32
- **构建时间**: 2026-04-09 09:31:52
- **JAR 文件**: `target/report-service-1.0.0-SNAPSHOT.jar` (50MB)
- **服务状态**: ✅ 运行中 (端口 8110)

---

## ✨ 新增功能

### 商品识别（方案一）

- ✅ 拍照/选择图片识别商品
- ✅ 调用阿里云视觉智能 API
- ✅ 返回商品品号、名称、类别、置信度
- ✅ 识别结果展示与确认
- ✅ 识别日志自动记录

---

## 🔧 修改内容

### 已修改文件

| 文件 | 修改内容 |
|------|----------|
| `AliyunVisionClient.java` | 完善 `recognizeProduct()` 方法 |
| `ProductRecognitionServiceImpl.java` | 实现 `recognize()` 和 `recognizeWithConfirmation()` |
| `ProductRecognitionResult.java` | 新增 `recognitionSource` 字段 |

### 新增文件

| 文件 | 说明 |
|------|------|
| `识别日志表.sql` | 识别日志表结构 |
| `商品识别功能实现说明.md` | 详细实现文档 |

### 数据库变更

**新增表**: `PRODUCT_RECOGNITION_LOGS`

```sql
CREATE TABLE PRODUCT_RECOGNITION_LOGS (
    LOG_ID VARCHAR2(64) PRIMARY KEY,
    IMAGE_URL VARCHAR2(1000),
    RECOGNIZED_PLUNO VARCHAR2(50),
    RECOGNIZED_NAME VARCHAR2(200),
    CONFIDENCE NUMBER(5,4),
    USER_CONFIRMED_PLUNO VARCHAR2(50),
    IS_CORRECT CHAR(1),
    CREATED_TIME DATE,
    CREATED_BY VARCHAR2(50)
);
```

---

## 🚀 部署步骤

### 1. 执行数据库脚本

```sql
@识别日志表.sql
```

### 2. 服务已自动重启

当前服务已启动并运行正常。

### 3. 验证服务

```bash
curl http://localhost:8110/api/product/training-stats
```

---

## 🧪 测试步骤

### 前端测试

1. **访问**: `http://47.100.138.89:8081`
2. **进入**: "商品识别"页面
3. **操作**: 拍照或选择图片
4. **结果**: 查看识别结果

### 接口测试

```bash
curl -X POST http://localhost:8110/api/product/recognize \
  -F "image=@/path/to/image.jpg"
```

**预期返回**:
```json
{
  "success": true,
  "datas": {
    "pluno": "商品品号",
    "productName": "商品名称",
    "category": "商品类别",
    "confidence": 0.95,
    "recognitionSource": "ALIYUN"
  },
  "message": "识别成功"
}
```

### 日志验证

```bash
tail -f logs/app.log | grep -E "识别|OSS|Aliyun"
```

**关键日志**:
```
✅ 识别图片已上传到 OSS: https://...
🔍 商品识别请求，图片 URL: ...
🎯 识别完成：品号=xxx, 置信度=0.xx
```

### 数据库验证

```sql
-- 查看最新识别记录
SELECT * FROM PRODUCT_RECOGNITION_LOGS 
ORDER BY CREATED_TIME DESC 
FETCH FIRST 10 ROWS ONLY;
```

---

## ⚠️ 重要说明

### 阿里云 API 待接入

当前版本返回**模拟识别结果**，需要：

1. **开通服务**: 在阿里云控制台开通商品识别服务
2. **获取文档**: 参考 [阿里云视觉智能开放平台](https://help.aliyun.com/product/44920.html)
3. **替换代码**: 修改 `AliyunVisionClient.java` 中的 TODO 部分

**示例代码位置**:
```
src/main/java/com/report/util/AliyunVisionClient.java
// TODO: 根据阿里云视觉智能开放平台的实际 API 实现商品识别
```

---

## 📊 识别流程

```
用户拍照/选图
    ↓
前端调用 /api/product/recognize
    ↓
上传图片到 OSS
    ↓
调用阿里云 API (待接入)
    ↓
返回识别结果
    ↓
用户确认
    ↓
记录识别日志
```

---

## 📝 后续优化

### 阶段一（当前）
- ✅ 基础识别功能
- ✅ 识别日志记录
- ⏳ 阿里云 API 接入

### 阶段二
- 识别准确率统计
- 用户反馈收集
- 热门识别商品排行

### 阶段三
- 本地模型训练
- 混合识别（本地 + 阿里云）

---

## 🔍 故障排查

### 问题 1: 识别返回空结果

**检查**:
1. OSS 配置：`SELECT * FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_OSS';`
2. 日志中的错误信息
3. 网络连接状态

### 问题 2: 日志表插入失败

**检查**:
1. 表是否已创建
2. 数据库连接状态
3. 字段类型匹配

---

## 📞 回滚方案

如需回滚到 v1.1.1：

```bash
# 停止服务
pkill -f report-service

# 恢复旧版本（如果有备份）
cp target/report-service-1.0.0-SNAPSHOT.jar.bak \
   target/report-service-1.0.0-SNAPSHOT.jar

# 重启服务
./deploy.sh start
```

---

**发布人**: AI Assistant  
**发布时间**: 2026-04-09 09:32  
**状态**: ✅ 已部署，待测试验证

---

## 📚 相关文档

- [商品识别功能实现说明.md](./商品识别功能实现说明.md) - 详细实现文档
- [识别日志表.sql](./识别日志表.sql) - 数据库脚本
- [FIX_v1.1.1_OSS_UPLOAD.md](./FIX_v1.1.1_OSS_UPLOAD.md) - OSS 上传修复说明
