# REPORT 工程部署技能

## 🎯 适用范围

**仅对 `/home/admin/.openclaw/workspace/REPORT` 工程有效**

## 📋 功能

### 1. 上传 Git 前自动处理

```bash
# 自动注释数据库配置
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources
sed -i 's/^  # url:/  # url:/' application.yml
sed -i 's/^    url:/    # url:/' application.yml
sed -i 's/^    username:/    # username:/' application.yml
sed -i 's/^    password:/    # password:/' application.yml

# 提交并推送
cd /home/admin/.openclaw/workspace
git add -A
git commit -m "auto: 安全上传（数据库配置已注释）"
git push origin master
```

### 2. 部署时自动恢复配置

```bash
# 自动取消注释数据库配置
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources
sed -i 's/^    # url:/    url:/' application.yml
sed -i 's/^    # username:/    username:/' application.yml
sed -i 's/^    # password:/    password:/' application.yml

# 重启服务
sudo systemctl restart report-service
```

## 🔐 数据库配置（仅供部署时使用）

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
    username: pos
    password: pos_2020
```

## ⚠️ 注意事项

1. **上传 Git 前必须执行**：自动注释数据库配置
2. **部署后必须执行**：自动恢复数据库配置
3. **此技能不适用于其他工程**

## 📝 使用示例

### 上传 Git
```bash
# 在 REPORT 工程目录执行
cd /home/admin/.openclaw/workspace/REPORT
# 触发技能：自动注释配置并提交
```

### 本地部署
```bash
# 在 REPORT 工程目录执行
cd /home/admin/.openclaw/workspace/REPORT
# 触发技能：自动恢复配置并重启
```

---

*技能创建时间：2026-04-07*
*适用工程：REPORT 工程专用*
