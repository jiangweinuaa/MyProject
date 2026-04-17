# IBPUI

Vue3 企业级中台管理系统前端框架

## 技术栈

- **框架**: Vue 3.5 + Vite 8
- **语法**: Composition API + `<script setup>`
- **语言**: TypeScript 6.0
- **UI 组件库**: Element Plus 2.13
- **路由**: Vue Router 4.6
- **状态管理**: Pinia 3.0
- **网络请求**: Axios 1.15
- **样式**: SCSS
- **代码规范**: ESLint + Prettier

## 项目结构

```
IBPUI/
├── src/
│   ├── api/              # API 接口
│   │   ├── index.ts
│   │   ├── request.ts    # Axios 封装
│   │   └── user.ts       # 用户相关 API
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── layouts/          # 布局组件
│   │   └── MainLayout.vue
│   ├── router/           # 路由配置
│   │   └── index.ts
│   ├── stores/           # Pinia 状态管理
│   │   ├── index.ts
│   │   └── user.ts
│   ├── styles/           # 全局样式
│   │   ├── index.scss
│   │   └── variables.scss
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   │   ├── Home.vue
│   │   ├── Login.vue
│   │   └── NotFound.vue
│   ├── App.vue
│   └── main.ts
├── public/               # 公共静态资源
├── .env                  # 环境变量
├── .env.development      # 开发环境变量
├── .env.production       # 生产环境变量
├── .eslintrc.cjs         # ESLint 配置
├── .prettierrc.cjs       # Prettier 配置
├── index.html
├── package.json
├── tsconfig.json
├── tsconfig.app.json
└── vite.config.ts
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 构建

```bash
# 生产环境构建
npm run build

# 开发环境构建
npm run build:dev
```

### 代码检查

```bash
# ESLint 检查
npm run lint

# 格式化代码
npm run format

# TypeScript 类型检查
npm run type-check
```

## 功能特性

- ✅ 用户登录/登出
- ✅ 路由权限控制
- ✅ 动态菜单
- ✅ Axios 请求封装
- ✅ Pinia 状态管理
- ✅ Element Plus 组件库
- ✅ 响应式布局
- ✅ SCSS 样式变量
- ✅ ESLint + Prettier 代码规范

## 开发规范

### 命名规范

- 组件文件：PascalCase (如 `UserProfile.vue`)
- 工具函数：camelCase (如 `formatDate.ts`)
- Store：camelCase (如 `user.ts`)
- API 文件：camelCase (如 `user.ts`)

### 代码风格

- 使用 Composition API + `<script setup>`
- 使用 TypeScript 类型注解
- 使用 SCSS 编写样式
- 遵循 ESLint + Prettier 规则

## 参考

- [Vue 3 文档](https://vuejs.org/)
- [Vite 文档](https://vitejs.dev/)
- [Element Plus 文档](https://element-plus.org/)
- [Pinia 文档](https://pinia.vuejs.org/)
- [Vue Router 文档](https://router.vuejs.org/)

## License

MIT
