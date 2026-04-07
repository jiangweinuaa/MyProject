# REPORT 工程安全部署技能

## 🎯 适用范围

**仅对 `/home/admin/.openclaw/workspace/REPORT` 工程有效**

## 📋 自动化流程

### 1️⃣ 上传 Git（自动注释数据库配置）

```bash
#!/bin/bash
# 文件名：git-upload-safe.sh

cd /home/admin/.openclaw/workspace/REPORT/src/main/resources

# 注释数据库配置
sed -i 's/^    url:/    # url:/' application.yml
sed -i 's/^    username:/    # username:/' application.yml
sed -i 's/^    password:/    # password:/' application.yml

echo "✅ 数据库配置已注释"

cd /home/admin/.openclaw/workspace
git add -A
git commit -m "auto: 安全上传（数据库配置已注释）"
git push origin master

echo "✅ 已安全上传到 Git"
```

### 2️⃣ 本地部署（自动恢复数据库配置）

```bash
#!/bin/bash
# 文件名：deploy-local.sh

cd /home/admin/.openclaw/workspace/REPORT/src/main/resources

# 恢复数据库配置
sed -i 's/^    # url:/    url:/' application.yml
sed -i 's/^    # username:/    username:/' application.yml
sed -i 's/^    # password:/    password:/' application.yml

echo "✅ 数据库配置已恢复"

# 重启服务
sudo systemctl restart report-service

echo "✅ 服务已重启"
```

## 🔐 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
    username: pos
    password: pos_2020
    driver-class-name: oracle.jdbc.OracleDriver
```

## ⚠️ 注意事项

1. **上传 Git 前必须执行**：自动注释数据库配置
2. **部署后必须执行**：自动恢复数据库配置
3. **此技能不适用于其他工程**

## 📝 使用示例

### 上传 Git
```bash
cd /home/admin/.openclaw/workspace/REPORT
# 执行技能：自动注释配置并提交
```

### 本地部署
```bash
cd /home/admin/.openclaw/workspace/REPORT
# 执行技能：自动恢复配置并重启
```

## 🛠️ 手动操作指南

### 上传 Git 前
```bash
# 1. 编辑配置文件
vim /home/admin/.openclaw/workspace/REPORT/src/main/resources/application.yml

# 2. 注释掉数据库配置（3 行）
datasource:
  # url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
  # username: pos
  # password: pos_2020

# 3. 提交
cd /home/admin/.openclaw/workspace
git add -A && git commit -m "安全上传" && git push
```

### 本地部署时
```bash
# 1. 编辑配置文件
vim /home/admin/.openclaw/workspace/REPORT/src/main/resources/application.yml

# 2. 取消注释数据库配置
datasource:
  url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
  username: pos
  password: pos_2020

# 3. 重启服务
sudo systemctl restart report-service
```

## ✅ 检查清单

上传 Git 前：
- [ ] 数据库 URL 已注释
- [ ] 数据库用户名已注释
- [ ] 数据库密码已注释
- [ ] 确认没有敏感信息

部署本地时：
- [ ] 数据库 URL 已恢复
- [ ] 数据库用户名已恢复
- [ ] 数据库密码已恢复
- [ ] 服务已重启
- [ ] 服务运行正常

---

*技能创建时间：2026-04-07*
*适用工程：REPORT 工程专用*
*最后更新：2026-04-07 18:02*
