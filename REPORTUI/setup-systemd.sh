#!/bin/bash

# 创建 systemd 服务文件
sudo tee /etc/systemd/system/report-frontend.service > /dev/null << 'EOF'
[Unit]
Description=REPORT Frontend Server
After=network.target

[Service]
Type=simple
User=admin
WorkingDirectory=/home/admin/.openclaw/workspace/REPORTUI/dist
ExecStart=/usr/bin/python3 /home/admin/.openclaw/workspace/REPORTUI/dist/../cors-server.py
Restart=always
RestartSec=5
StandardOutput=append:/home/admin/.openclaw/workspace/REPORTUI/logs/cors.log
StandardError=append:/home/admin/.openclaw/workspace/REPORTUI/logs/cors.log

[Install]
WantedBy=multi-user.target
EOF

# 重载 systemd 配置
sudo systemctl daemon-reload

# 启用并启动服务
sudo systemctl enable report-frontend
sudo systemctl start report-frontend

# 查看服务状态
sudo systemctl status report-frontend --no-pager

echo ""
echo "✅ 前端服务已配置为 systemd 服务"
echo "📝 日志文件：/home/admin/.openclaw/workspace/REPORTUI/logs/cors.log"
echo ""
echo "常用命令："
echo "  查看状态：sudo systemctl status report-frontend"
echo "  重启服务：sudo systemctl restart report-frontend"
echo "  停止服务：sudo systemctl stop report-frontend"
echo "  查看日志：tail -f /home/admin/.openclaw/workspace/REPORTUI/logs/cors.log"
