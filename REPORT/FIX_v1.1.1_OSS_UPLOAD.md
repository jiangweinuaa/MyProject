# v1.1.1 修复说明 - OSS 上传顺序问题

## 🐛 问题描述

**现象**: 上传学习图片后，本地能看到图片，但 OSS_IMAGE_URL 字段为空，阿里云 OSS 看不到图片。

**原因**: MultipartFile 的临时文件在调用 `transferTo()` 保存到本地后就被删除了，导致后续上传 OSS 时文件已不存在。

**错误日志**:
```
OSS 上传失败：/tmp/tomcat.8110.../upload_xxx.tmp (No such file or directory)
java.io.FileNotFoundException: ... (No such file or directory)
```

---

## ✅ 修复方案

### 修改内容

**文件**: `ProductRecognitionServiceImpl.java`

**修改前**:
```java
// 1. 先保存本地
request.getImage().transferTo(new java.io.File(filePath));

// 2. 再上传 OSS（此时文件已被删除）❌
ossImageUrl = aliyunVisionClient.uploadImageToOSS(request.getImage(), request.getPluno());
```

**修改后**:
```java
// 1. 先上传 OSS（此时 MultipartFile 还在内存/临时文件中）✅
ossImageUrl = aliyunVisionClient.uploadImageToOSS(request.getImage(), request.getPluno());

// 2. 再保存本地
request.getImage().transferTo(new java.io.File(filePath));
```

---

## 📦 版本信息

- **版本号**: v1.1.1
- **修复时间**: 2026-04-09 09:16
- **构建时间**: 2026-04-09 09:16:41
- **JAR 文件**: `target/report-service-1.0.0-SNAPSHOT.jar`

---

## 🧪 测试步骤

### 1. 确认服务已启动

```bash
curl http://localhost:8110/api/product/training-stats
```

预期返回：
```json
{
  "success": true,
  "message": "查询成功"
}
```

### 2. 上传测试图片

1. 访问前端页面：`http://47.100.138.89:8081`
2. 进入"商品训练"页面
3. 填写商品信息（品号、名称、类别）
4. 点击"添加照片"上传图片
5. 点击"提交到训练库"

### 3. 验证结果

#### 检查日志

```bash
tail -f /home/admin/.openclaw/workspace/REPORT/logs/app.log
```

**成功日志**:
```
✅ 图片已上传到阿里云 OSS: https://product-training-images.oss-cn-shanghai.aliyuncs.com/...
```

**失败日志**:
```
⚠️ 阿里云 OSS 上传失败：...
```

#### 检查数据库

```sql
-- 查看最新记录
SELECT SAMPLE_ID, PLUNO, IMAGE_URL, OSS_IMAGE_URL, CREATED_TIME 
FROM PRODUCT_TRAINING_SAMPLES 
ORDER BY CREATED_TIME DESC 
FETCH FIRST 5 ROWS ONLY;
```

**预期结果**: `OSS_IMAGE_URL` 字段应该有值（以 `https://` 开头）

#### 检查 OSS

登录阿里云 OSS 控制台，查看 Bucket 中是否有以下路径的文件：
```
product-training/{pluno}/{uuid}.jpg
```

---

## 🔍 故障排查

### 如果 OSS_IMAGE_URL 仍为空

#### 1. 检查数据库配置

```sql
SELECT * FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_OSS';
```

确保有记录且字段不为空：
- `ACCESSKEYID` (Bucket 名称)
- `ACCESSKEYSECRET` (Endpoint)

#### 2. 检查日志中的具体错误

```bash
grep -E "OSS|Aliyun|上传" /home/admin/.openclaw/workspace/REPORT/logs/app.log | tail -20
```

**常见错误**:

| 错误信息 | 原因 | 解决方案 |
|----------|------|----------|
| `AccessKey 未配置` | PRODUCT_APPKEY 表无数据 | 插入 OSS 配置 |
| `Bucket 不存在` | Bucket 名称错误 | 检查 ACCESSKEYID 字段 |
| `网络超时` | 服务器无法访问 OSS | 检查网络/安全组 |
| `权限拒绝` | AccessKey 无权限 | 检查 RAM 权限配置 |

#### 3. 手动测试 OSS 连接

```bash
# 安装 ossutil（可选）
wget http://gosspublic.alicdn.com/ossutil/1.7.0/ossutil64
chmod +x ossutil64
./ossutil64 config -e oss-cn-shanghai.aliyuncs.com -i {ACCESSKEYID} -k {ACCESSKEYSECRET}
./ossutil64 ls oss://{bucket}/product-training/
```

---

## 📊 修复验证清单

- [ ] 服务已重启（v1.1.1）
- [ ] 上传测试图片成功
- [ ] 本地图片保存成功
- [ ] 日志显示 `✅ 图片已上传到阿里云 OSS`
- [ ] 数据库 `OSS_IMAGE_URL` 字段有值
- [ ] 阿里云 OSS 控制台能看到图片

---

## 🔄 回滚方案

如果修复后出现问题，可以回滚到 v1.1.0：

```bash
# 停止服务
pkill -f report-service

# 如果有备份，恢复旧版本
# cp target/report-service-1.0.0-SNAPSHOT.jar.bak target/report-service-1.0.0-SNAPSHOT.jar

# 重启服务
./deploy.sh start
```

---

**修复人**: AI Assistant  
**修复时间**: 2026-04-09 09:16  
**状态**: ✅ 已部署，待测试验证
