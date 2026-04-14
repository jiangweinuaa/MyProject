#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
发送邮件脚本 - 发送智问核心流程 PPT
"""

import smtplib
import ssl
from email.mime.application import MIMEApplication
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from pathlib import Path

# 邮箱配置
EMAIL_FROM = "20388719@qq.com"
EMAIL_TO = "20388719@qq.com"
SMTP_SERVER = "smtp.qq.com"
SMTP_PORT = 465
SMTP_USER = "20388719@qq.com"

# 注意：实际授权码需要从安全存储中获取
# 这里使用占位符，实际需要替换
SMTP_PASSWORD = None

# 尝试从环境或配置文件读取
import os
config_path = Path("/home/admin/.openclaw/workspace/.email_config")
if config_path.exists():
    with open(config_path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if line.startswith("SMTP_AUTH_PASSWORD=") and not line.endswith("YOUR_AUTH_CODE_HERE"):
                SMTP_PASSWORD = line.split("=", 1)[1]
            elif line.startswith("EMAIL_TO="):
                EMAIL_TO = line.split("=", 1)[1]

if not SMTP_PASSWORD:
    print("错误：未找到 SMTP 授权码，请在 .email_config 文件中配置")
    exit(1)

# SQL 文件路径
sql_file_path = "/home/admin/.openclaw/workspace/AI_NL_QUERY_TABLES.sql"

# 创建邮件
msg = MIMEMultipart()
msg['From'] = EMAIL_FROM
msg['To'] = EMAIL_TO
msg['Subject'] = "智问对话日志系统 - 数据库表结构 SQL 脚本"

# 邮件正文
body = """
您好！

附件是智问对话日志系统的数据库表结构 SQL 脚本。

📋 包含内容：
1. AI_NL_QUERY_LOG - 智问对话日志表
2. AI_NL_QUERY_SESSION - 智问会话表
3. V_AI_NL_QUERY_DAILY_STATS - 按天统计视图
4. V_AI_NL_QUERY_QUESTION_STATS - 按问题类型统计视图

📊 主要功能：
- 记录每次对话的用户问题、生成的 SQL、执行状态
- 记录是否重试、原始 SQL、最终 SQL
- 记录执行时间、响应时间等性能指标
- 按天统计成功率、平均响应时间
- 按问题类型统计（销售查询、库存查询等）

📝 执行步骤：
1. 使用 SQL Developer 或其他 Oracle 客户端
2. 打开附件 AI_NL_QUERY_TABLES.sql
3. 按 F5 执行脚本
4. 验证表和视图是否创建成功

祝好！
龙虾 AI 助手 🦞
"""

msg.attach(MIMEText(body, 'plain', 'utf-8'))

# 添加 SQL 文件附件
with open(sql_file_path, 'rb') as f:
    part = MIMEApplication(f.read(), Name="AI_NL_QUERY_TABLES.sql")
    part['Content-Disposition'] = 'attachment; filename="AI_NL_QUERY_TABLES.sql"'
    msg.attach(part)

# 发送邮件
try:
    context = ssl.create_default_context()
    with smtplib.SMTP_SSL(SMTP_SERVER, SMTP_PORT, context=context) as server:
        server.login(SMTP_USER, SMTP_PASSWORD)
        server.sendmail(EMAIL_FROM, EMAIL_TO, msg.as_string())
    print(f"✓ 邮件已发送到：{EMAIL_TO}")
except Exception as e:
    print(f"✗ 发送失败：{e}")
    exit(1)
