# REPORTUI - 报表系统前端

基于 Vue 3 + Vite + Element Plus 的报表管理系统前端。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Element Plus** - Vue 3 组件库
- **Vue Router** - 官方路由管理器
- **Pinia** - Vue 状态管理
- **Axios** - HTTP 客户端

## 快速开始

### 1. 安装依赖
```bash
cd REPORTUI
npm install
```

### 2. 启动开发服务器
```bash
npm run dev
```

访问：http://localhost:3000

### 3. 构建生产版本
```bash
npm run build
```

### 4. 预览生产构建
```bash
npm run preview
```

## 项目结构

```
REPORTUI/
├── src/
│   ├── api/                    # API 接口
│   │   ├── request.js          # Axios 封装
│   │   └── report.js           # 报表接口
│   ├── assets/                 # 静态资源
│   ├── components/             # 公共组件
│   ├── layout/                 # 布局组件
│   │   └── index.vue           # 主布局（左侧菜单 + 顶部导航）
│   ├── router/                 # 路由配置
│   │   └── index.js
│   ├── store/                  # Pinia 状态管理
│   ├── views/                  # 页面组件
│   │   └── SalesAnalysis.vue   # 销售分析页面
│   ├── App.vue                 # 根组件
│   └── main.js                 # 入口文件
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## 功能菜单

目前包含：
- 📊 **销售分析** - 销售数据报表分析

## API 接口配置

### 后端服务地址
在 `vite.config.js` 中配置了代理：
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8110',  // REPORT 后端服务
      changeOrigin: true
    }
  }
}
```

### 接口调用示例
```javascript
import { getSalesAnalysis } from '@/api/report'

// 调用后端接口
const res = await getSalesAnalysis({
  startDate: '2026-04-01',
  endDate: '2026-04-03',
  shopId: '1002'
})
```

### 请求格式
```json
{
  "serviceId": "GetSalesAnalysis",
  "request": {
    "startDate": "2026-04-01",
    "endDate": "2026-04-03"
  },
  "sign": {
    "key": "digiwin",
    "sign": ""
  }
}
```

### 响应格式
```json
{
  "datas": {
    "totalAmount": 125800.50,
    "orderCount": 156,
    ...
  },
  "success": true,
  "serviceStatus": "000",
  "serviceDescription": "服务执行成功"
}
```

## 添加新页面

### 1. 创建视图组件
在 `src/views/` 下创建新组件，如 `CustomerAnalysis.vue`

### 2. 添加路由
编辑 `src/router/index.js`：
```javascript
{
  path: '/customer-analysis',
  name: 'CustomerAnalysis',
  component: () => import('@/views/CustomerAnalysis.vue'),
  meta: { title: '客户分析', icon: 'User' }
}
```

### 3. 添加菜单
编辑 `src/layout/index.vue` 中的 `menus` 数组：
```javascript
const menus = [
  { path: '/sales-analysis', title: '销售分析', icon: 'DataAnalysis' },
  { path: '/customer-analysis', title: '客户分析', icon: 'User' }
]
```

## 后端接口对接

在 `src/api/report.js` 中定义接口，在页面组件中调用：

```javascript
// src/views/SalesAnalysis.vue
import { getSalesAnalysis } from '@/api/report'

const handleSearch = async () => {
  loading.value = true
  try {
    const res = await getSalesAnalysis(searchForm)
    // 处理返回数据
    salesData.value = res.datas
  } finally {
    loading.value = false
  }
}
```

## 常用 Element Plus 图标

- DataAnalysis - 数据分析
- ShoppingCart - 购物车
- Ticket - 票据
- User - 用户
- DataLine - 数据线条
- TrendCharts - 趋势图表
- Rank - 排行
- Search - 搜索
- Refresh - 刷新
- Download - 下载

## 开发注意事项

1. **代理配置**：开发环境下通过 Vite 代理转发到后端 8110 端口
2. **生产构建**：构建后需修改 `vite.config.js` 中的 `base` 和 API 地址
3. **响应式**：使用 `ref` 和 `reactive` 创建响应式数据
4. **组件通信**：简单场景用 props/emit，复杂状态用 Pinia

## License

MIT
