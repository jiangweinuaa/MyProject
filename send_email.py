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

# PPT 文件路径
ppt_path = "/home/admin/.openclaw/workspace/智问泳道图_图形版.pptx"

# Prompt 日志文件路径
prompt_log_path = "/home/admin/.openclaw/workspace/prompt_log.txt"

# AISQLService.java 文件路径
aisql_service_path = "/home/admin/.openclaw/workspace/AISQLService.java"

# 创建邮件
msg = MIMEMultipart()
msg['From'] = EMAIL_FROM
msg['To'] = EMAIL_TO
msg['Subject'] = "智问系统核心流程 PPT"

# 邮件正文
body = """
您好！

附件包含以下文件：

1. prompt_log.txt - 智问系统最近一次 AI 调用的完整 Prompt 日志
2. 智问泳道图.pptx - 智问系统核心流程泳道图 PPT
3. AISQLService.java - 智问系统 AI SQL 生成服务源代码

配置说明：
- 角色定义来源：AI_PROMPT_REQUIREMENTS 表（CATEGORY = 'ROLE'）
- 要求列表来源：AI_PROMPT_REQUIREMENTS 表（CATEGORY != 'ROLE'）
- 模型配置来源：PRODUCT_APPKEY 表（ACCESSKEYID 字段存储模型名称）
- 数据库表结构：AI_TABLE_FILTER 表配置的表
- 缓存机制：60 秒 TTL
- 刷新方式：/api/prompt-config/refresh 接口

祝好！
龙虾 AI 助手 🦞
"""

msg.attach(MIMEText(body, 'plain', 'utf-8'))

# 添加 Prompt 日志附件
with open(prompt_log_path, 'rb') as f:
    part = MIMEApplication(f.read(), Name="prompt_log.txt")
    part['Content-Disposition'] = 'attachment; filename="prompt_log.txt"'
    msg.attach(part)

# 添加 PPT 附件
with open(ppt_path, 'rb') as f:
    part = MIMEApplication(f.read(), Name="智问泳道图.pptx")
    part['Content-Disposition'] = 'attachment; filename="智问泳道图.pptx"'
    msg.attach(part)

# 添加 AISQLService.java 附件
with open(aisql_service_path, 'rb') as f:
    part = MIMEApplication(f.read(), Name="AISQLService.java")
    part['Content-Disposition'] = 'attachment; filename="AISQLService.java"'
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
