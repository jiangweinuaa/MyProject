# 发布说明 - v1.1.0 阿里云商品识别集成

## 📦 版本信息

- **版本号**: v1.1.0
- **发布时间**: 2026-04-09
- **构建时间**: 2026-04-09 09:09:25
- **JAR 文件**: `target/report-service-1.0.0-SNAPSHOT.jar` (50MB)

---

## ✨ 新增功能

### 1. 阿里云 OSS 图片上传

- 商品训练图片自动上传到阿里云 OSS
- 本地和 OSS 双存储，确保数据安全
- OSS 上传失败不影响本地保存

### 2. 阿里云商品识别 API 集成

- 支持调用阿里云视觉智能开放平台 API
- 从数据库动态获取 AccessKey 配置
- 预留商品识别接口，后续可快速接入

### 3. 数据库配置管理

- 所有阿里云配置统一存储在 `PRODUCT_APPKEY` 表
- 支持多平台配置（ALI - 商品识别，ALI_OSS - OSS 存储）
- 无需修改配置文件，部署更灵活

---

## 🔧 技术改动

### 新增文件

| 文件 | 说明 |
|------|------|
| `src/main/java/com/report/util/AliyunConfigUtil.java` | 阿里云配置工具类 |
| `src/main/java/com/report/util/AliyunVisionClient.java` | 阿里云视觉智能客户端 |
| `ALIYUN_INTEGRATION.md` | 集成说明文档 |
| `数据库配置脚本.sql` | 数据库配置 SQL 脚本 |

### 修改文件

| 文件 | 修改内容 |
|------|----------|
| `src/main/java/com/report/service/impl/ProductRecognitionServiceImpl.java` | 增加 OSS 上传逻辑 |
| `src/main/resources/application.yml` | 移除硬编码配置 |
| `pom.xml` | 添加阿里云 OSS SDK 依赖 |

### 数据库变更

```sql
-- 新增字段
ALTER TABLE PRODUCT_TRAINING_SAMPLES ADD OSS_IMAGE_URL VARCHAR2(1000);

-- 新增配置（需手动执行）
-- PLATFORM='ALI' - 商品识别 API 配置
-- PLATFORM='ALI_OSS' - OSS 存储配置
```

---

## 🚀 部署步骤

### 1. 停止当前服务

```bash
cd /home/admin/.openclaw/workspace/REPORT
./deploy.sh stop
```

### 2. 备份旧版本（可选）

```bash
cp target/report-service-1.0.0-SNAPSHOT.jar \
   target/report-service-1.0.0-SNAPSHOT.jar.bak.$(date +%Y%m%d)
```

### 3. 确认数据库配置已执行

```sql
-- 验证表结构
SELECT COLUMN_NAME, DATA_TYPE 
FROM USER_TAB_COLUMNS 
WHERE TABLE_NAME = 'PRODUCT_TRAINING_SAMPLES' 
AND COLUMN_NAME = 'OSS_IMAGE_URL';

-- 验证配置数据
SELECT * FROM PRODUCT_APPKEY WHERE PLATFORM IN ('ALI', 'ALI_OSS');
```

### 4. 启动新版本

```bash
./deploy.sh start
```

### 5. 验证服务

```bash
# 检查服务状态
./deploy.sh status

# 查看日志
tail -f logs/app.log

# 测试接口
curl -X GET http://localhost:8110/api/product/training-stats
```

---

## 📋 测试清单

### 功能测试

- [ ] 商品训练图片提交成功
- [ ] 图片保存到本地路径
- [ ] 图片上传到 OSS 成功
- [ ] 数据库记录包含 `OSS_IMAGE_URL`
- [ ] 日志中显示 OSS 上传成功信息

### 异常测试

- [ ] OSS 配置缺失时，本地保存仍正常
- [ ] OSS 上传失败时，错误日志记录
- [ ] AccessKey 错误时，返回友好提示

### 性能测试

- [ ] 单张图片上传时间 < 3 秒
- [ ] 批量上传 10 张图片无内存溢出
- [ ] 数据库连接池正常

---

## 🔍 日志示例

### 成功上传

```
✅ 图片已上传到阿里云 OSS: https://product-training-images.oss-cn-shanghai.aliyuncs.com/product-training/12345/abc123.jpg
```

### OSS 配置缺失

```
⚠️ 阿里云 OSS 配置未完整，请检查 PRODUCT_APPKEY 表 (PLATFORM='ALI_OSS')
```

---

## 📞 回滚方案

如果新版本出现问题，快速回滚：

```bash
# 停止服务
./deploy.sh stop

# 恢复旧版本
cp target/report-service-1.0.0-SNAPSHOT.jar.bak.20260409 \
   target/report-service-1.0.0-SNAPSHOT.jar

# 启动旧版本
./deploy.sh start
```

---

## 📝 注意事项

1. **数据库配置必须先执行** - 确保 `PRODUCT_APPKEY` 表有正确的配置
2. **OSS Bucket 权限** - 确保 AccessKey 有 OSS 写入权限
3. **网络连通性** - 服务器需要能访问阿里云 OSS API
4. **日志监控** - 部署后密切关注 `logs/app.log`

---

## 🔗 相关文档

- [ALIYUN_INTEGRATION.md](./ALIYUN_INTEGRATION.md) - 详细集成说明
- [数据库配置脚本.sql](./数据库配置脚本.sql) - 完整 SQL 脚本

---

**发布人**: AI Assistant  
**审核人**: [待填写]  
**批准人**: [待填写]
