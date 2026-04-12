#!/usr/bin/env python3
import http.server
import socketserver
import os
import time

# 切换到 dist 目录
os.chdir(os.path.join(os.path.dirname(__file__), 'dist'))

class CORSHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        # 禁止缓存 HTML 文件
        self.send_header('Cache-Control', 'no-cache, no-store, must-revalidate')
        self.send_header('Pragma', 'no-cache')
        self.send_header('Expires', '0')
        # CORS 头
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        http.server.SimpleHTTPRequestHandler.end_headers(self)
    
    def serve_file(self, path, content_type=None):
        # 对 HTML 文件强制禁用缓存
        if path.endswith('.html'):
            self.send_header('Cache-Control', 'no-cache, no-store, must-revalidate')
            self.send_header('Pragma', 'no-cache')
            self.send_header('Expires', '0')
        super().serve_file(path, content_type)
    
    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()
    
    def do_POST(self):
        self.send_response(200)
        self.end_headers()

PORT = 8081

class ReuseAddrTCPServer(socketserver.TCPServer):
    allow_reuse_address = True

with ReuseAddrTCPServer(("", PORT), CORSHandler) as httpd:
    print(f"Serving at port {PORT}")
    httpd.serve_forever()
