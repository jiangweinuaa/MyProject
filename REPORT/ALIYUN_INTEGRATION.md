# 阿里云商品识别集成 - 实现说明

## ✅ 已完成的代码修改

### 1. 新增工具类

#### `AliyunConfigUtil.java`
- 位置：`src/main/java/com/report/util/AliyunConfigUtil.java`
- 功能：从数据库 `PRODUCT_APPKEY` 表获取阿里云 AccessKey
- SQL：`SELECT ACCESSKEYID as APPKEY, ACCESSKEYSECRET as APPSECRET FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI'`

#### `AliyunVisionClient.java`
- 位置：`src/main/java/com/report/util/AliyunVisionClient.java`
- 功能：
  - `uploadImageToOSS()` - 上传图片到阿里云 OSS
  - `recognizeProduct()` - 调用阿里云商品识别 API

### 2. 修改服务类

#### `ProductRecognitionServiceImpl.java`
- 位置：`src/main/java/com/report/service/impl/ProductRecognitionServiceImpl.java`
- 修改内容：
  - 注入 `AliyunVisionClient`
  - 修改 `submitTrainingData()` 方法，增加 OSS 上传逻辑
  - 保存本地图片的同时，同步上传到阿里云 OSS

### 3. 修改配置文件

#### `pom.xml`
- 添加阿里云 OSS SDK 依赖：
```xml
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.1</version>
</dependency>
```

#### `application.yml`
- 添加阿里云 OSS 配置：
```yaml
aliyun:
  oss:
    bucket: product-training-images
    endpoint: oss-cn-shanghai.aliyuncs.com
```

---

## 📋 需要执行的数据库操作

### 1️⃣ 修改 `PRODUCT_TRAINING_SAMPLES` 表

**添加 OSS 图片 URL 字段：**

```sql
-- 添加 OSS_IMAGE_URL 字段
ALTER TABLE PRODUCT_TRAINING_SAMPLES ADD (
    OSS_IMAGE_URL VARCHAR2(1000)
);

-- 添加字段注释
COMMENT ON COLUMN PRODUCT_TRAINING_SAMPLES.OSS_IMAGE_URL IS '阿里云 OSS 图片 URL';
```

### 2️⃣ 配置阿里云 AccessKey（商品识别用）

确保 `PRODUCT_APPKEY` 表中有以下数据（PLATFORM='ALI'）：

```sql
-- 查询现有配置
SELECT * FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI';

-- 如果没有，插入数据
INSERT INTO PRODUCT_APPKEY (ACCESSKEYID, ACCESSKEYSECRET, PLATFORM) 
VALUES ('你的 AccessKey ID', '你的 AccessKey Secret', 'ALI');
```

### 3️⃣ 配置阿里云 OSS（图片存储用）

确保 `PRODUCT_APPKEY` 表中有以下数据（PLATFORM='ALI_OSS'）：

```sql
-- 查询现有配置
SELECT * FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_OSS';

-- 插入或更新 OSS 配置
-- ACCESSKEYID 存储 Bucket 名称
-- ACCESSKEYSECRET 存储 Endpoint
MERGE INTO PRODUCT_APPKEY T
USING DUAL
ON (T.PLATFORM = 'ALI_OSS')
WHEN MATCHED THEN
  UPDATE SET T.ACCESSKEYID = 'your-bucket-name',
             T.ACCESSKEYSECRET = 'oss-cn-shanghai.aliyuncs.com'
WHEN NOT MATCHED THEN
  INSERT (ACCESSKEYID, ACCESSKEYSECRET, PLATFORM)
  VALUES ('your-bucket-name', 'oss-cn-shanghai.aliyuncs.com', 'ALI_OSS');
```

**示例数据：**
```sql
-- 上海区域
INSERT INTO PRODUCT_APPKEY (ACCESSKEYID, ACCESSKEYSECRET, PLATFORM) 
VALUES ('product-training-images', 'oss-cn-shanghai.aliyuncs.com', 'ALI_OSS');

-- 杭州区域
INSERT INTO PRODUCT_APPKEY (ACCESSKEYID, ACCESSKEYSECRET, PLATFORM) 
VALUES ('product-training-images', 'oss-cn-hangzhou.aliyuncs.com', 'ALI_OSS');
```

---

## 🔧 配置说明

### 数据库配置说明

所有阿里云配置都存储在 `PRODUCT_APPKEY` 表中：

| PLATFORM | ACCESSKEYID 字段 | ACCESSKEYSECRET 字段 | 用途 |
|----------|-----------------|---------------------|------|
| `ALI` | AccessKey ID | AccessKey Secret | 商品识别 API |
| `ALI_OSS` | OSS Bucket 名称 | OSS Endpoint | 图片存储 |

### OSS Endpoint 选择

根据 Bucket 所在区域选择 Endpoint：

| 区域 | Endpoint |
|------|----------|
| 华东 1（上海） | oss-cn-shanghai.aliyuncs.com |
| 华东 2（杭州） | oss-cn-hangzhou.aliyuncs.com |
| 华北 1（青岛） | oss-cn-qingdao.aliyuncs.com |
| 华北 2（北京） | oss-cn-beijing.aliyuncs.com |
| 华南 1（深圳） | oss-cn-shenzhen.aliyuncs.com |

---

## 🚀 部署步骤

### 1. 安装依赖

```bash
cd /home/admin/.openclaw/workspace/REPORT
mvn clean install
```

### 2. 执行数据库脚本

联系数据库管理员执行上述 SQL 脚本。

### 3. 配置 AccessKey

在 `PRODUCT_APPKEY` 表中插入阿里云 AccessKey。

### 4. 重启服务

```bash
# 停止服务
sudo systemctl stop report-service

# 启动服务
sudo systemctl start report-service

# 查看日志
tail -f /home/admin/.openclaw/workspace/REPORT/logs/application.log
```

---

## 📝 使用说明

### 提交训练数据

前端调用 `/api/product/submit-training` 接口时：

1. 图片保存到本地：`/home/admin/.openclaw/workspace/REPORT/upload/products/{pluno}/`
2. 同时上传到阿里云 OSS：`oss://{bucket}/product-training/{pluno}/{uuid}.jpg`
3. 数据库记录包含：
   - `IMAGE_URL` - 本地图片路径
   - `OSS_IMAGE_URL` - 阿里云 OSS 图片 URL

### 返回结果示例

```json
{
  "success": true,
  "sampleId": "abc123...",
  "imageUrl": "/upload/products/12345/1234567890.jpg",
  "ossImageUrl": "https://product-training-images.oss-cn-shanghai.aliyuncs.com/product-training/12345/abc123.jpg",
  "message": "提交成功"
}
```

---

## ⚠️ 注意事项

1. **OSS 上传失败不影响本地保存**
   - 如果 OSS 上传失败，图片仍会保存到本地
   - 错误会记录到日志中

2. **商品识别 API 待接入**
   - `AliyunVisionClient.recognizeProduct()` 方法目前是模拟实现
   - 需要根据阿里云实际 API 文档完善

3. **AccessKey 安全**
   - 确保 `PRODUCT_APPKEY` 表有访问控制
   - 建议使用 RAM 子账号的 AccessKey

---

## 📚 参考文档

- [阿里云 OSS Java SDK](https://help.aliyun.com/document_detail/32008.html)
- [阿里云视觉智能开放平台](https://help.aliyun.com/product/44920.html)
