# MySQL 8.0 数据库配置

## 🐬 数据库信息

| 配置项 | 值 |
|--------|-----|
| **版本** | MySQL 8.0.44 |
| **主机** | localhost |
| **端口** | 3306 |
| **数据库名** | openclaw_db |
| **用户名** | openclaw_user |
| **密码** | j7gLZHHYGcAeOTcy |
| **字符集** | utf8mb4 |
| **排序规则** | utf8mb4_unicode_ci |

---

## 🔧 连接示例

### MySQL 命令行
```bash
mysql -h localhost -u openclaw_user -p openclaw_db
# 密码：j7gLZHHYGcAeOTcy
```

### JDBC 连接字符串
```
jdbc:mysql://localhost:3306/openclaw_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
```

### Node.js (mysql2)
```javascript
const connection = mysql.createConnection({
  host: 'localhost',
  port: 3306,
  user: 'openclaw_user',
  password: 'j7gLZHHYGcAeOTcy',
  database: 'openclaw_db'
});
```

### Java (Spring Boot)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/openclaw_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=openclaw_user
spring.datasource.password=j7gLZHHYGcAeOTcy
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

---

## 🔐 Root 账户

| 配置项 | 值 |
|--------|-----|
| **用户名** | root |
| **密码** | j7gLZHHYGcAeOTcy |
| **主机** | localhost |

---

## 🛠️ 服务管理

```bash
# 启动
sudo systemctl start mysqld

# 停止
sudo systemctl stop mysqld

# 重启
sudo systemctl restart mysqld

# 状态
sudo systemctl status mysqld

# 开机自启
sudo systemctl enable mysqld
```

---

## 📝 备注

- 创建时间：2026-03-11
- 服务器：47.100.138.89
- 此文件包含敏感信息，请妥善保管

---

⚠️ **安全提示：** 
- 生产环境请修改默认密码
- 不要将此文件提交到版本控制
- 建议定期备份数据库
