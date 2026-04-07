# 🚀 REPORT 工程部署操作指南

## ⚠️ 重要安全提示

**数据库配置信息绝对不能上传到 Git！**

---

## 📋 标准操作流程

### 1️⃣ 上传 Git 前（移除敏感配置）

```bash
# 1. 编辑配置文件
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources
vim application.yml

# 2. 注释或删除数据库配置
spring:
  datasource:
    # url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
    # username: pos
    # password: pos_2020
    driver-class-name: oracle.jdbc.OracleDriver

# 3. 保存并提交
cd /home/admin/.openclaw/workspace
git add -A
git commit -m "feat: xxx"
git push origin master
```

### 2️⃣ 部署到本地/生产环境（添加数据库配置）

```bash
# 1. 编辑配置文件
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources
vim application.yml

# 2. 添加数据库配置
spring:
  datasource:
    url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
    username: pos
    password: pos_2020
    driver-class-name: oracle.jdbc.OracleDriver

# 3. 重启服务
sudo systemctl restart report-service
```

---

## 🔐 推荐方案：使用配置文件模板

### 创建配置模板

```bash
# 1. 创建模板文件（不包含密码）
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources
cp application.yml application.yml.template

# 2. 编辑模板，移除敏感信息
vim application.yml.template
# 修改为：
spring:
  datasource:
    url: jdbc:oracle:thin:@//YOUR_HOST:1521/YOUR_DB
    username: YOUR_USERNAME
    password: YOUR_PASSWORD  # 部署时填写真实密码
    driver-class-name: oracle.jdbc.OracleDriver
```

### 部署时操作

```bash
# 1. 从模板创建配置文件
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources
cp application.yml.template application.yml

# 2. 编辑配置文件，填入真实数据库信息
vim application.yml

# 3. 重启服务
sudo systemctl restart report-service
```

### Git 配置

```bash
# .gitignore 中添加
echo "src/main/resources/application.yml" >> /home/admin/.openclaw/workspace/.gitignore

# 但保留模板文件
echo "!src/main/resources/application.yml.template" >> /home/admin/.openclaw/workspace/.gitignore
```

---

## 📝 数据库配置信息（仅供本地部署使用）

| 配置项 | 值 |
|--------|-----|
| **主机** | 47.99.153.144 |
| **端口** | 1521 |
| **服务名** | posdb |
| **用户名** | pos |
| **密码** | pos_2020 |
| **驱动** | oracle.jdbc.OracleDriver |

### 完整 JDBC URL
```
jdbc:oracle:thin:@//47.99.153.144:1521/posdb
```

### application.yml 配置示例
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//47.99.153.144:1521/posdb
    username: pos
    password: pos_2020
    driver-class-name: oracle.jdbc.OracleDriver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 30000
      connection-timeout: 30000
      max-lifetime: 1800000
```

---

## ✅ 每次上传 Git 检查清单

- [ ] 检查 `application.yml` 中的数据库密码是否已移除
- [ ] 检查是否有其他敏感信息（API Key、Token 等）
- [ ] 确认 `.gitignore` 配置正确
- [ ] 提交信息清晰明确
- [ ] 推送成功后验证远程仓库

---

## 🛠️ 快速命令

### 上传 Git（安全方式）
```bash
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources
# 手动编辑 application.yml，注释掉密码
vim application.yml
cd /home/admin/.openclaw/workspace
git add -A && git commit -m "xxx" && git push
```

### 部署服务（添加配置）
```bash
cd /home/admin/.openclaw/workspace/REPORT/src/main/resources
# 手动编辑 application.yml，添加密码
vim application.yml
sudo systemctl restart report-service
```

---

*最后更新：2026-04-07*
