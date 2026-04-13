#!/bin/bash
# 编译后自动复制 HTML 文件到 dist 目录

cd /home/admin/.openclaw/workspace/REPORTUI

echo "📦 复制 HTML 文件到 dist 目录..."

if [ -f "retrain.html" ]; then
  cp retrain.html dist/
  echo "✅ retrain.html 已复制"
fi

if [ -f "smart-query.html" ]; then
  cp smart-query.html dist/
  echo "✅ smart-query.html 已复制"
fi

echo "✅ 复制完成！"
ls -la dist/*.html 2>/dev/null || echo "📄 HTML 文件列表："
