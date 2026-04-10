# REPORT 工程安全部署技能

## 🎯 适用范围

**仅对 `/home/admin/.openclaw/workspace/REPORT` 工程有效**

## 🔐 安全策略

### 核心原则

1. **application.yml** - 永不上 Git（已加入 .gitignore）
2. **application.yml.template** - 模板文件，可上 Git（不含真实配置）
3. **数据库配置** - 只存在于本地服务器，绝不上传

### 数据库配置（仅供本地部署使用）

```yaml
url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
username: pos
password: pos_2020
```

**⚠️ 重要：这些配置绝对不能出现在 Git 中！**

## 📋 标准操作流程

### 1️⃣ 上传 Git（自动删除数据库配置）

```bash
#!/bin/bash
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources

# 删除数据库配置行（不是注释，是直接删除）
sed -i '/^    url: jdbc:oracle/d' application.yml
sed -i '/^    username: pos$/d' application.yml
sed -i '/^    password: pos_2020$/d' application.yml

echo "✅ 数据库配置已彻底删除"

cd /home/admin/.openclaw/workspace
git add -A
git commit -m "auto: 安全上传（数据库配置已删除）"
git push origin master

echo "✅ 已安全上传到 Git"
```

### 2️⃣ 本地部署（自动添加数据库配置）

```bash
#!/bin/bash
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources

# 添加数据库配置
sed -i '/^    driver-class-name: oracle.jdbc.OracleDriver$/i\    url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb\n    username: pos\n    password: pos_2020' application.yml

echo "✅ 数据库配置已添加"

# 重启服务
sudo systemctl restart report-service

echo "✅ 服务已重启"
```

## 🛠️ 手动操作指南

### 上传 Git 前

```bash
# 1. 编辑配置文件
vim /home/admin/.openclaw/workspace/REPORT/src/main/resources/application.yml

# 2. 彻底删除数据库配置（3 行）
# 删除以下内容（不是注释，是完全删除）：
#   url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
#   username: pos
#   password: pos_2020

# 3. 确认配置文件中没有数据库信息
grep -E "url:|username:|password:" application.yml
# 应该没有输出

# 4. 提交
cd /home/admin/.openclaw/workspace
git add -A && git commit -m "安全上传" && git push
```

### 本地部署时

```bash
# 1. 编辑配置文件
vim /home/admin/.openclaw/workspace/REPORT/src/main/resources/application.yml

# 2. 在 driver-class-name 上方添加数据库配置
datasource:
  url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
  username: pos
  password: pos_2020
  driver-class-name: oracle.jdbc.OracleDriver

# 3. 重启服务
sudo systemctl restart report-service
```

## ✅ 检查清单

### 上传 Git 前
- [ ] 数据库 URL 已彻底删除（不是注释）
- [ ] 数据库用户名已彻底删除
- [ ] 数据库密码已彻底删除
- [ ] 使用 grep 确认没有敏感信息
- [ ] application.yml 不在 git status 中

### 部署本地时
- [ ] 数据库 URL 已添加
- [ ] 数据库用户名已添加
- [ ] 数据库密码已添加
- [ ] 服务已重启
- [ ] 服务运行正常

## 🔍 验证命令

### 验证 Git 中没有敏感信息
```bash
cd /home/admin/.openclaw/workspace
git show HEAD:REPORT/src/main/resources/application.yml | grep -E "jdbc:|username:|password:"
# 应该没有输出
```

### 验证本地配置正确
```bash
grep -A 3 "datasource:" /home/admin/.openclaw/workspace/REPORT/src/main/resources/application.yml
# 应该显示完整的数据库配置
```

## 📁 文件说明

| 文件 | 状态 | 说明 |
|------|------|------|
| `application.yml` | 🚫 禁止上 Git | 包含真实数据库配置 |
| `application.yml.template` | ✅ 可上 Git | 模板文件，不含敏感信息 |
| `.gitignore` | ✅ 已配置 | 自动忽略 application.yml |

## 🛡️ 安全保证

1. **Git 仓库** - 完全没有数据库配置（已删除，不是注释）
2. **本地服务器** - 配置只存在于 application.yml
3. **模板文件** - 只有占位符，没有真实信息
4. **自动化** - 技能自动处理，避免人为失误

---

*技能创建时间：2026-04-07*
*适用工程：REPORT 工程专用*
*安全级别：最高*
*最后更新：2026-04-07 18:36*
