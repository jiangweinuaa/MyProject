# CRM_WEB - CRM 会员卡管理前端系统

## 📋 项目简介

基于 Vue 3 + Vite + Element Plus 的 CRM 会员卡管理前端系统。

---

## 🏗️ 技术栈

| 组件 | 版本 |
|------|------|
| Vue | 3.4+ |
| Vite | 5.0+ |
| Element Plus | 2.4+ |
| Vue Router | 4.2+ |
| Axios | 1.6+ |

---

## 🚀 快速开始

### 安装依赖
```bash
cd /home/admin/.openclaw/workspace/CRM_WEB
npm install
```

### 启动开发服务器
```bash
npm run dev
```

### 访问地址
- **本地：** http://localhost:3000/
- **局域网：** http://47.100.138.89:3000/

---

## 📁 项目结构

```
CRM_WEB/
├── index.html                 # 入口 HTML
├── package.json               # 项目配置
├── vite.config.js             # Vite 配置
└── src/
    ├── main.js                # 应用入口
    ├── App.vue                # 根组件
    ├── api/
    │   └── card.js            # 卡片 API
    ├── router/
    │   └── index.js           # 路由配置
    ├── views/
    │   ├── Layout.vue         # 布局组件
    │   └── CardQuery.vue      # 卡查询页面
    └── assets/                # 静态资源
```

---

## 🎨 功能说明

### 页面布局
- **左侧菜单区：** 显示系统功能菜单
- **右侧内容区：** 显示页面内容

### 卡信息查询页面
1. **上部区块：** 查询按钮
   - 点击"查询所有卡信息"按钮
   - 调用后端 `GET /api/card/queryAll` 接口

2. **下部区块：** 卡信息表格
   - 展示所有卡片信息
   - 支持行高亮
   - 点击任意行可查看详情

3. **详情弹窗：**
   - 点击表格行触发
   - 调用后端 `POST /api/card/query` 接口
   - 显示卡片完整信息

---

## 📡 API 接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 查询所有 | GET | `/api/card/queryAll` | 获取所有卡片列表 |
| 查询单卡 | POST | `/api/card/query` | 根据卡号查询详情 |

---

## 🔧 配置说明

### Vite 代理配置
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8081',  // 后端服务地址
      changeOrigin: true
    }
  }
}
```

如需连接远程后端，修改 `vite.config.js` 中的 `target` 地址。

---

## 📸 页面截图

### 主页面
- 左侧菜单：卡信息查询
- 右侧：查询按钮 + 数据表格

### 详情弹窗
- 基本信息：卡号、会员、余额等
- 日期信息：生成、发卡、激活日期等

---

## 🛠️ 开发命令

```bash
# 开发模式
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

---

## ⚠️ 注意事项

1. 确保后端服务（CRM Card Service）已启动在 8081 端口
2. 前端开发服务器运行在 3000 端口
3. 跨域问题已通过 Vite 代理解决

---

## 📄 License

MIT License
