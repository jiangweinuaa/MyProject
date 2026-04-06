#!/bin/bash

# REPORT 系统部署脚本
# 用法：./deploy.sh [start|stop|restart|status]

REPORT_HOME="/home/admin/.openclaw/workspace/REPORT"
REPORTUI_HOME="/home/admin/.openclaw/workspace/REPORTUI"
BACKEND_PORT=8110
FRONTEND_PORT=8081

start_backend() {
    echo "启动后端服务..."
    cd $REPORT_HOME
    nohup java -Xms256m -Xmx512m -jar target/report-service-1.0.0-SNAPSHOT.jar > logs/app.log 2>&1 &
    sleep 5
    if ps aux | grep "report-service" | grep -v grep > /dev/null; then
        echo "✅ 后端服务已启动 (端口：$BACKEND_PORT)"
    else
        echo "❌ 后端服务启动失败"
        exit 1
    fi
}

stop_backend() {
    echo "停止后端服务..."
    pkill -f "report-service-1.0.0-SNAPSHOT.jar"
    sleep 2
    echo "✅ 后端服务已停止"
}

start_frontend() {
    echo "启动前端服务..."
    cd $REPORTUI_HOME/dist
    nohup python3 -m http.server $FRONTEND_PORT > logs/frontend.log 2>&1 &
    sleep 2
    echo "✅ 前端服务已启动 (端口：$FRONTEND_PORT)"
}

stop_frontend() {
    echo "停止前端服务..."
    pkill -f "http.server $FRONTEND_PORT"
    sleep 2
    echo "✅ 前端服务已停止"
}

check_status() {
    echo "=== 服务状态 ==="
    
    if ps aux | grep "report-service" | grep -v grep > /dev/null; then
        echo "✅ 后端服务：运行中 (端口：$BACKEND_PORT)"
    else
        echo "❌ 后端服务：未运行"
    fi
    
    if ps aux | grep "http.server $FRONTEND_PORT" | grep -v grep > /dev/null; then
        echo "✅ 前端服务：运行中 (端口：$FRONTEND_PORT)"
    else
        echo "❌ 前端服务：未运行"
    fi
    
    echo ""
    echo "访问地址:"
    echo "  前端：http://47.100.138.89:$FRONTEND_PORT"
    echo "  后端：http://47.100.138.89:$BACKEND_PORT"
}

case "$1" in
    start)
        start_backend
        start_frontend
        echo ""
        echo "🎉 部署完成！"
        check_status
        ;;
    stop)
        stop_frontend
        stop_backend
        echo ""
        echo "✅ 服务已停止"
        ;;
    restart)
        stop_frontend
        stop_backend
        sleep 2
        start_backend
        start_frontend
        echo ""
        echo "🎉 重启完成！"
        check_status
        ;;
    status)
        check_status
        ;;
    *)
        echo "用法：$0 {start|stop|restart|status}"
        exit 1
        ;;
esac
