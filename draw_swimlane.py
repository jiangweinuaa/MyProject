#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
用 PIL/Pillow 绘制泳道图
"""

from PIL import Image, ImageDraw, ImageFont

# 创建图像
width = 1400
height = 900
img = Image.new('RGB', (width, height), color='white')
draw = ImageDraw.Draw(img)

# 颜色定义
swimlane_colors = [
    '#E3F2FD',  # 用户 - 浅蓝
    '#FFF3E0',  # 前端 - 浅橙
    '#F3E5F5',  # Controller - 浅紫
    '#E8F5E9',  # Service - 浅绿
    '#FFF8E1',  # AI API - 浅黄
    '#FAFAFA',  # 数据库 - 浅灰
]

text_color = '#333333'
line_color = '#999999'
arrow_color = '#2196F3'

# 泳道配置
swimlanes = [
    ('用户', 0),
    ('前端\nSmartQuery.vue', 1),
    ('Controller\nNLQueryController', 2),
    ('Service\nNLQueryService', 3),
    ('AI API\n通义千问', 4),
    ('数据库\nOracle', 5),
]

# 泳道宽度
swimlane_width = width // len(swimlanes)

# 绘制泳道背景
for i, (name, idx) in enumerate(swimlanes):
    x1 = i * swimlane_width
    x2 = (i + 1) * swimlane_width
    draw.rectangle([x1, 60, x2, height - 40], fill=swimlane_colors[idx])
    
    # 泳道标题
    draw.rectangle([x1, 10, x2, 60], fill=swimlane_colors[idx])
    draw.line([(x1, 60), (x2, 60)], fill=line_color, width=2)

# 绘制泳道分隔线
for i in range(1, len(swimlanes)):
    x = i * swimlane_width
    draw.line([(x, 10), (x, height - 40)], fill=line_color, width=2)

# 字体（使用默认字体）
try:
    title_font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 16)
    text_font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 14)
except:
    title_font = ImageFont.load_default()
    text_font = ImageFont.load_default()

# 绘制泳道标题
for i, (name, idx) in enumerate(swimlanes):
    x1 = i * swimlane_width
    x2 = (i + 1) * swimlane_width
    # 简单计算文本位置
    draw.text((x1 + 10, 20), name.split('\n')[0], fill=text_color, font=title_font)

# 流程步骤（y 坐标，泳道索引，文本）
steps = [
    (100, 0, "① 输入问题"),
    (150, 1, "② 显示「AI 思考中...」"),
    (200, 1, "③ POST /api/nl-query/query"),
    (250, 2, "④ query(question)"),
    (300, 3, "⑤ generateSQL()"),
    (350, 5, "⑥ 查询 AI_TABLE_FILTER"),
    (400, 5, "⑦ 查询 USER_TAB_COLUMNS"),
    (450, 4, "⑧ 调用通义千问 API"),
    (500, 4, "⑨ 返回生成的 SQL"),
    (550, 3, "⑩ validateSQL() 验证"),
    (600, 5, "⑪ 执行 SQL 查询"),
    (650, 3, "⑫ 返回 JSON 响应"),
    (700, 1, "⑬ 渲染表格 + SQL"),
    (750, 0, "⑭ 查看结果"),
]

# 绘制流程步骤
for y, lane_idx, text in steps:
    x = lane_idx * swimlane_width + 20
    
    # 绘制圆角矩形背景
    box_width = swimlane_width - 40
    box_height = 40
    draw.rounded_rectangle([x, y, x + box_width, y + box_height], radius=8, fill='white')
    
    # 绘制文本
    draw.text((x + 10, y + 12), text, fill=text_color, font=text_font)

# 绘制箭头连接
arrows = [
    (140, 0, 190, 1),   # ①→②
    (240, 1, 290, 2),   # ③→④
    (290, 2, 340, 3),   # ④→⑤
    (340, 3, 390, 5),   # ⑤→⑥
    (390, 5, 440, 5),   # ⑥→⑦
    (440, 5, 490, 4),   # ⑦→⑧
    (540, 4, 590, 3),   # ⑨→⑩
    (590, 3, 640, 5),   # ⑩→⑪
    (640, 5, 690, 3),   # ⑪→⑫
    (690, 3, 740, 1),   # ⑫→⑬
    (740, 1, 790, 0),   # ⑬→⑭
]

for y1, lane1, y2, lane2 in arrows:
    x1 = (lane1 + 1) * swimlane_width - 20
    x2 = lane2 * swimlane_width + 20
    
    # 绘制箭头线
    draw.line([(x1, y1 + 20), (x2, y2 + 20)], fill=arrow_color, width=3)
    
    # 绘制箭头头部
    arrow_size = 8
    draw.polygon([
        (x2, y2 + 20),
        (x2 - arrow_size, y2 + 20 - arrow_size),
        (x2 - arrow_size, y2 + 20 + arrow_size)
    ], fill=arrow_color)

# 保存图像
img.save('/home/admin/.openclaw/workspace/智问泳道图.png')
print("泳道图已生成：/home/admin/.openclaw/workspace/智问泳道图.png")
