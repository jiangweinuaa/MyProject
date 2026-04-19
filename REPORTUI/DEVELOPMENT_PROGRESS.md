# AI 报表设计器 - 开发进度

**创建时间**：2026-04-19  
**状态**：第一阶段完成（基础框架）

---

## ✅ 已完成

### 1. 数据库表
- [x] `AI_REPORT_DEF` - 报表定义表
- [x] `AI_REPORT_MENU` - 菜单表（含初始化数据）
- [x] `AI_REPORT_VERSION` - 版本历史表
- [x] 序列和索引

**文件**：`designer_schema.sql`

### 2. 后端服务
- [x] `ReportDesignerController.java` - Controller
  - `POST /api/designer/chat` - 对话（复用智问）
  - `POST /api/designer/save` - 保存报表
  - `GET /api/designer/menu` - 获取菜单树
  - `GET /api/designer/report/{id}` - 获取报表详情
  - `DELETE /api/designer/report/{id}` - 删除报表

- [x] `ReportSaveService.java` - 保存服务
  - `saveReport()` - 保存/更新报表
  - `getMenuTree()` - 构建菜单树
  - `getReport()` - 获取报表详情

### 3. 前端页面
- [x] `ReportDesigner.vue` - 主页面
  - 两栏布局（70% 预览 + 30% 对话）
  - 9 步流程框架
  - 保存/发布功能

- [x] `ChatPanel.vue` - 对话组件
  - 消息列表
  - 输入框
  - 示例问题
  - 快捷操作

- [x] 预览组件（占位符）
  - `UnderstandingPreview.vue` ✅（已完成）
  - `SQLEditor.vue` - 占位
  - `DataTable.vue` - 占位
  - `ChartSelector.vue` - 占位
  - `StylePanel.vue` - 占位
  - `FilterForm.vue` - 占位
  - `LayoutPreview.vue` - 占位
  - `SaveForm.vue` - 占位
  - `MenuTree.vue` - 占位

### 4. 路由配置
- [x] 添加 `/report-designer` 路由
- [x] 菜单标题：📊 AI 报表设计器

### 5. API 封装
- [x] `src/api/designer.js`
  - `chat()` - 对话
  - `saveReport()` - 保存
  - `getMenuTree()` - 菜单
  - `getReport()` - 详情
  - `deleteReport()` - 删除

---

## 📋 待完成

### 第一阶段（核心功能）
- [ ] 部署数据库表（执行 `designer_schema.sql`）
- [ ] 后端编译并重启服务
- [ ] 测试对话功能（复用智问 API）
- [ ] 测试保存功能

### 第二阶段（预览组件）
- [ ] `SQLEditor.vue` - SQL 代码编辑器
- [ ] `DataTable.vue` - 数据表格（复用 SmartQuery）
- [ ] `ChartSelector.vue` - 图表选择器（复用 AiChart）
- [ ] `StylePanel.vue` - 样式面板
- [ ] `FilterForm.vue` - 动态表单生成
- [ ] `LayoutPreview.vue` - 布局选择器
- [ ] `SaveForm.vue` - 保存表单
- [ ] `MenuTree.vue` - 菜单树（Element Plus Tree）

### 第三阶段（流程优化）
- [ ] 9 步流程自动推进逻辑
- [ ] 步骤回溯功能
- [ ] 对话上下文管理
- [ ] 错误处理和提示优化

### 第四阶段（测试上线）
- [ ] 功能测试
- [ ] 性能优化
- [ ] 用户文档
- [ ] 上线部署

---

## 🚀 快速启动

### 1. 数据库
```sql
-- 在 Oracle 数据库中执行
@designer_schema.sql
```

### 2. 后端
```bash
cd /home/admin/.openclaw/workspace/REPORT
# 编译
mvn clean package
# 重启服务
systemctl restart report-service
```

### 3. 前端
```bash
cd /home/admin/.openclaw/workspace/REPORTUI
# 安装依赖（如果需要）
npm install
# 开发模式
npm run dev
# 或生产构建
npm run build
```

### 4. 访问
- 打开浏览器访问：`http://<服务器 IP>:<端口>/report-designer`
- 或从菜单点击：**📊 AI 报表设计器**

---

## 📝 使用说明

### 创建报表流程
1. 访问 `/report-designer`
2. 在右侧对话区输入需求，例如："查看上个月各品类的销售情况"
3. AI 自动生成 SQL 和图表
4. 跟随 9 步流程完成报表配置
5. 点击"保存草稿"或"发布"

### 复用智问功能
- 对话 API 完全复用智问的 `/api/nl-query/query`
- NL2SQL、图表生成、对话上下文管理等功能直接使用
- 不影响智问原有功能

---

## 🔧 技术栈

- **前端**：Vue3 + Element Plus + ECharts
- **后端**：Spring Boot + JdbcTemplate
- **数据库**：Oracle 11g
- **AI**：阿里云百炼（复用智问配置）

---

## 📊 项目结构

```
REPORTUI/
├── src/
│   ├── views/
│   │   └── ReportDesigner.vue          # 主页面
│   ├── components/
│   │   └── Designer/
│   │       ├── ChatPanel.vue           # 对话组件
│   │       ├── UnderstandingPreview.vue
│   │       ├── SQLEditor.vue
│   │       ├── DataTable.vue
│   │       ├── ChartSelector.vue
│   │       ├── StylePanel.vue
│   │       ├── FilterForm.vue
│   │       ├── LayoutPreview.vue
│   │       ├── SaveForm.vue
│   │       └── MenuTree.vue
│   ├── api/
│   │   └── designer.js                 # API 封装
│   └── router/
│       └── index.js                    # 路由配置（已更新）

REPORT/
└── src/main/java/com/report/
    ├── controller/
    │   ├── NLQueryController.java      # 智问 Controller（复用）
    │   └── ReportDesignerController.java  # 设计器 Controller
    └── service/
        ├── NLQueryService.java         # 智问服务（复用）
        └── ReportSaveService.java      # 保存服务
```

---

**下一步**：执行数据库脚本，启动服务测试基础功能！
