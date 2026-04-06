#!/usr/bin/env python3
import http.server
import socketserver
import os

# 切换到 dist 目录
os.chdir(os.path.join(os.path.dirname(__file__), 'dist'))

class CORSHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        http.server.SimpleHTTPRequestHandler.end_headers(self)
    
    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()
    
    def do_POST(self):
        self.send_response(200)
        self.end_headers()

PORT = 8081

socketserver.TCPServer.allow_reuse_address = True

with socketserver.TCPServer(("", PORT), CORSHandler) as httpd:
    print(f"Serving at port {PORT}")
    httpd.serve_forever()
