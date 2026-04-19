# 报表设计器 - 对话式 AI 方案

**版本**：v3.0  
**创建时间**：2026-04-19  
**设计理念**：对话驱动 + 实时预览 + 人工干预（类似 IDE 中的 AI 插件）

---

## 一、核心设计

### 1.1 设计原则

1. **对话驱动** - 所有操作通过自然语言对话完成
2. **实时预览** - 右侧即时展示效果（SQL/数据/图表）
3. **人工干预** - AI 调整不准时，可手动编辑
4. **9 步流程** - AI 引导用户完成 9 个步骤，但用户无感知

### 1.2 界面布局（两栏式）

```
┌─────────────────────────────────────────────────────────────────────────┐
│  📊 AI 报表设计器                          [💾 保存] [🚀 发布] [📋 历史]  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────┐  ┌─────────────────────────┐  │
│  │                                     │  │                         │  │
│  │         实时预览区                   │  │   AI 对话区              │  │
│  │                                     │  │   (类似 Copilot 聊天)     │  │
│  │   ┌─────────────────────────────┐   │  │                         │  │
│  │   │  当前步骤的可视化内容        │   │  │  70% 宽度               │  │
│  │   │  • SQL 编辑器                │   │  │                         │  │
│  │   │  • 数据表格                  │   │  │  🤖: 你好！想创建什么   │  │
│  │   │  • ECharts 图表              │   │  │  报表呢？               │  │
│  │   │  • 布局预览                  │   │  │                         │  │
│  │   │                             │   │  │  👤: 查看上月销售       │  │
│  │   └─────────────────────────────┘   │  │                         │  │
│  │                                     │  │  🤖: 好的，已生成 SQL   │  │
│  │   [✏️ 编辑] [🔄 刷新] [👍 满意]     │  │  帮你看下效果？         │  │
│  │                                     │  │                         │  │
│  │                                     │  │  ┌─────────────────┐   │  │
│  │                                     │  │  │ 输入框...       │   │  │
│  │                                     │  │  └─────────────────┘   │  │
│  │                                     │  │                         │  │
│  └─────────────────────────────────────┘  └─────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

**设计理念**：
- **左侧实时预览** (70%) - 当前步骤的可视化结果 + 手动编辑入口
- **右侧 AI 对话** (30%) - 用户只负责说话，AI 负责执行（类似 IDE 右侧边栏）
- **顶部进度指示** - 显示当前在第几步（但用户无需关心）

---

## 二、AI 对话区设计

### 2.1 对话界面（右侧边栏，30% 宽度）

```
┌─────────────────────────────────────┐
│  🤖 AI 报表助手          (30% 宽度)  │
├─────────────────────────────────────┤
│                                     │
│  🤖: 你好！我是报表助手，想创建什么 │
│      报表呢？说说你的需求～          │
│                                     │
│  👤: 查看上个月各品类的销售情况     │
│                                     │
│  🤖: ✅ 已理解需求！                 │
│      正在生成 SQL...                │
│      • 时间范围：2026-03-01 ~ 2026-03-31
│      • 分组维度：品类名称           │
│      • 指标：销售金额               │
│                                     │
│      帮你看下生成的 SQL？           │
│      [👍 好的] [👎 重新生成]        │
│                                     │
│  👤: 把柱状图改成红色               │
│                                     │
│  🤖: ✅ 已修改图表颜色！            │
│      右边可以看到效果～             │
│                                     │
│  👤: 添加一个门店筛选条件           │
│                                     │
│  🤖: ✅ 已添加门店筛选！            │
│      默认显示全部门店，可切换       │
│                                     │
│  👤: 保存报表，叫"品类销售分析"     │
│                                     │
│  🤖: ✅ 已保存到"销售报表"目录！    │
│      🎉 报表创建完成！              │
│                                     │
├─────────────────────────────────────┤
│  💬 输入你的想法...      [🎤] [发送]│
└─────────────────────────────────────┘
```

### 2.2 对话特性

| 特性 | 说明 |
|------|------|
| **上下文感知** | AI 知道当前在第几步，自动引导 |
| **快捷操作** | 点击 [👍] 确认，[👎] 重新生成 |
| **自然语言** | 用户无需记忆命令，随意表达 |
| **主动引导** | 每步完成后 AI 主动提示下一步 |
| **随时打断** | 用户可随时切换话题或返回修改 |

### 2.3 AI 对话示例

#### 场景 1：创建新报表

```
🤖: 你好！我是报表助手，想创建什么报表呢？

👤: 查看上个月各品类的销售情况，按金额排序

🤖: ✅ 已理解需求！正在生成 SQL...

     理解结果：
     • 时间范围：2026-03-01 ~ 2026-03-31
     • 分组维度：品类名称
     • 指标：销售金额
     • 排序：降序

     [👍 确认理解] [👎 重新理解]

👤: 👍

🤖: 好的！已生成 SQL，右边可以看到～
     
     共 8 条数据，执行耗时 156ms
     
     [▶️ 执行查询] [✏️ 修改 SQL]

👤: 执行看看

🤖: ✅ 数据已加载！右边表格是预览～

     接下来帮你选个合适的图表？
     
     [👍 推荐图表] [👎 跳过]

👤: 👍

🤖: 根据数据特征，推荐**柱状图**！
     
     因为：
     • 数据是分类对比（各品类）
     • 有明确数值（销售金额）
     • 需要直观对比差异
     
     [👍 用柱状图] [🎨 换一种]

👤: 👍

🤖: ✅ 图表已生成！右边可以看到效果～

     需要调整样式吗？比如颜色、标签等
     
     [🎨 调整样式] [👎 跳过]

...（继续后续步骤）
```

#### 场景 2：对话调整

```
👤: 把柱子改成红色

🤖: ✅ 已修改颜色为红色！

👤: 添加数据标签

🤖: ✅ 已为每个柱子添加数据标签！

👤: 显示同比数据

🤖: ✅ 已添加同比数据系列！
     需要我帮你计算去年同期数据吗？
     
     [👍 计算] [👎 不用]
```

#### 场景 3：人工干预

```
👤: SQL 里的时间范围不对

🤖: 好的，你希望时间范围是？

👤: 2 月 1 日到 2 月 28 日

🤖: ✅ 已修改 SQL 时间范围！
     WHERE BDATE >= TO_DATE('20260201', ...)
       AND BDATE < TO_DATE('20260301', ...)
     
     [▶️ 重新执行] [✏️ 继续修改]

👤: ✏️

🤖: 好的，SQL 编辑器已打开，可以直接修改～
```

---

## 三、实时预览区设计

### 3.1 预览区概览

预览区根据当前步骤**动态切换内容**，始终显示当前步骤的核心内容：

### 3.2 布局比例

| 区域 | 宽度 | 说明 |
|------|------|------|
| **实时预览区** | 70% | 主要工作区，展示 SQL/数据/图表 |
| **AI 对话区** | 30% | 右侧边栏，类似 IDE 的 AI 插件 |

### 3.3 各步骤预览详情

### 3.2 各步骤预览详情

#### 步骤 1：描述需求 → 意图理解面板

```
┌─────────────────────────────────────────────────────┐
│  📋 步骤 1/9: 理解需求                               │
├─────────────────────────────────────────────────────┤
│                                                     │
│  用户输入：查看上个月各品类的销售情况               │
│                                                     │
│  AI 理解结果：                                       │
│  ┌───────────────────────────────────────────────┐ │
│  │ 📅 时间范围：2026-03-01 ~ 2026-03-31          │ │
│  │ 📊 分组维度：品类名称 (CATEGORY_NAME)         │ │
│  │ 📈 指标：销售金额 (SUM(SALE_AMT))             │ │
│  │ 📉 排序：按金额降序                           │ │
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  [✏️ 修改理解]  [👍 确认，生成 SQL]                 │
└─────────────────────────────────────────────────────┘
```

#### 步骤 2：生成 SQL → SQL 编辑器

```
┌─────────────────────────────────────────────────────┐
│  📝 步骤 2/9: 生成 SQL                               │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌───────────────────────────────────────────────┐ │
│  │ SELECT                                        │ │
│  │   CATEGORY_NAME,                              │ │
│  │   SUM(SALE_AMT) AS TOTAL_AMT                  │ │
│  │ FROM DCP_SALE                                 │ │
│  │ WHERE BDATE >= TO_DATE('20260301', 'YYYYMMDD')│ │
│  │   AND BDATE < TO_DATE('20260401', 'YYYYMMDD') │ │
│  │ GROUP BY CATEGORY_NAME                        │ │
│  │ ORDER BY TOTAL_AMT DESC                       │ │
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  ✅ 语法正确  ⚡ 预计耗时：<1 秒  📊 涉及表：DCP_SALE│
│                                                     │
│  [✏️ 编辑]  [🔄 重新生成]  [▶️ 执行查询]            │
└─────────────────────────────────────────────────────┘
```

#### 步骤 3：预览数据 → 数据表格

```
┌─────────────────────────────────────────────────────┐
│  📊 步骤 3/9: 预览数据                               │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌───────────────────────────────────────────────┐ │
│  │ 品类名称    │ 销售金额    │ 占比    │ 同比    │ │
│  ├────────────┼────────────┼─────────┼─────────┤ │
│  │ 面包        │ 125,000    │ 32%     │ +5.2%   │ │
│  │ 蛋糕        │ 98,000     │ 25%     │ +3.1%   │ │
│  │ 饮料        │ 76,000     │ 19%     │ -1.2%   │ │
│  │ 零食        │ 54,000     │ 14%     │ +8.7%   │ │
│  │ 其他        │ 39,000     │ 10%     │ +2.4%   │ │
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  共 8 条数据  ⚡ 执行耗时：156ms  📥 [导出 Excel]     │
│                                                     │
│  [🔍 筛选]  [📊 排序]  [👍 满意，选择图表]          │
└─────────────────────────────────────────────────────┘
```

#### 步骤 4：选择图表 → 图表选择器

```
┌─────────────────────────────────────────────────────┐
│  📈 步骤 4/9: 选择图表                               │
├─────────────────────────────────────────────────────┤
│                                                     │
│  AI 推荐：**柱状图** (适合分类对比数据)              │
│                                                     │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐    │
│  │柱状图│ │折线图│ │ 饼图 │ │条形图│ │指标卡│    │
│  │  ✓  │ │      │ │      │ │      │ │      │    │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘    │
│                                                     │
│  ┌───────────────────────────────────────────────┐ │
│  │                                               │ │
│  │           [ECharts 柱状图预览]                 │ │
│  │                                               │ │
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  [🎨 换一种]  [⚙️ 高级配置]  [👍 确认，调整样式]    │
└─────────────────────────────────────────────────────┘
```

#### 步骤 5：调整样式 → 图表 + 样式面板

```
┌─────────────────────────────────────────────────────┐
│  🎨 步骤 5/9: 调整样式                               │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌─────────────────┐  ┌───────────────────────────┐ │
│  │ 颜色方案        │  │                           │ │
│  │ ● 经典蓝        │  │                           │ │
│  │ ○ 活力橙        │  │    [ECharts 图表]          │ │
│  │ ○ 清新绿        │  │    [实时预览]              │ │
│  │ ○ 自定义        │  │                           │ │
│  └─────────────────┘  │                           │ │
│                       │                           │ │
│  ┌─────────────────┐  │                           │ │
│  │ 显示选项        │  │                           │ │
│  │ ☑ 显示标题      │  │                           │ │
│  │ ☑ 显示图例      │  │                           │ │
│  │ ☑ 显示数据标签  │  │                           │ │
│  │ ☐ 显示网格线    │  │                           │ │
│  └─────────────────┘  └───────────────────────────┘ │
│                                                     │
│  [💬 对话调整]  [👍 确认，添加筛选]                 │
└─────────────────────────────────────────────────────┘
```

#### 步骤 6：添加筛选 → 变量表单

```
┌─────────────────────────────────────────────────────┐
│  🔍 步骤 6/9: 添加筛选条件                           │
├─────────────────────────────────────────────────────┤
│                                                     │
│  AI 已识别以下变量：                                 │
│  ┌───────────────────────────────────────────────┐ │
│  │ 📅 开始日期  [2026-03-01  ▼]  ◼ 必填          │ │
│  │ 📅 结束日期  [2026-03-31  ▼]  ◼ 必填          │ │
│  │ 🏪 门店      [全部门店    ▼]  ◻ 可选          │ │
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  [➕ 添加更多筛选]  [💬 对话添加]                   │
│                                                     │
│  [⏭️ 跳过]  [👍 确认，设置布局]                     │
└─────────────────────────────────────────────────────┘
```

#### 步骤 7：设置布局 → 布局预览

```
┌─────────────────────────────────────────────────────┐
│  📐 步骤 7/9: 设置布局                               │
├─────────────────────────────────────────────────────┤
│                                                     │
│  布局模板：                                         │
│  ┌──────┐ ┌──────┐ ┌──────┐                        │
│  │单图表│ │上下  │ │左右  │                        │
│  │  ✓  │ │双图  │ │双图  │                        │
│  └──────┘ └──────┘ └──────┘                        │
│                                                     │
│  ┌───────────────────────────────────────────────┐ │
│  │  ┌─────────────────────────────────────┐     │ │
│  │  │                                     │     │ │
│  │  │        [图表预览区域]                │     │ │
│  │  │                                     │     │ │
│  │  └─────────────────────────────────────┘     │ │
│  │                                               │ │
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  [⏭️ 跳过]  [👍 确认，命名保存]                     │
└─────────────────────────────────────────────────────┘
```

#### 步骤 8：命名保存 → 保存表单

```
┌─────────────────────────────────────────────────────┐
│  💾 步骤 8/9: 命名保存                               │
├─────────────────────────────────────────────────────┤
│                                                     │
│  报表名称：                                         │
│  ┌───────────────────────────────────────────────┐ │
│  │ 品类销售分析报表                               │ │
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  报表描述：                                         │
│  ┌───────────────────────────────────────────────┐ │
│  │ 查看各品类月度销售情况，按金额降序排列         │ │
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  访问权限：                                         │
│  ○ 公开（所有人可见）  ● 私有（仅自己可见）        │
│                                                     │
│  [💾 保存草稿]  [🚀 保存并发布]                     │
└─────────────────────────────────────────────────────┘
```

#### 步骤 9：选择目录 → 目录树

```
┌─────────────────────────────────────────────────────┐
│  📁 步骤 9/9: 选择菜单目录                           │
├─────────────────────────────────────────────────────┤
│                                                     │
│  报表中心/                                          │
│  ├─ 📈 销售报表                                      │
│  │   ├─ 品类销售分析                                │
│  │   ├─ 门店销售排行                                │
│  │   └─ [➕ 添加到这里]  ← 推荐                     │
│  │                                                │
│  ├─ 📦 库存报表                                      │
│  │   ├─ 库存周转分析                                │
│  │   └─ 缺货预警                                    │
│  │                                                │
│  └─ [➕ 创建新目录]                                  │
│                                                     │
│  [⏭️ 跳过]  [✅ 完成创建]                           │
└─────────────────────────────────────────────────────┘
```

---

## 四、后端服务设计

### 4.1 统一 API 入口

所有对话操作通过统一 API 处理：

```
POST /api/designer/chat
```

**请求格式**：
```json
{
  "sessionId": "DESIGN_SESSION_001",
  "message": "查看上个月各品类的销售情况",
  "context": {
    "step": 1,
    "understanding": {...},
    "sql": "...",
    "data": {...},
    "chartConfig": {...}
  }
}
```

**响应格式**：
```json
{
  "success": true,
  "reply": "✅ 已理解需求！正在生成 SQL...",
  "nextStep": 2,
  "preview": {
    "type": "sql",
    "content": "SELECT ..."
  },
  "actions": [
    {"label": "👍 确认", "action": "confirm"},
    {"label": "👎 重新生成", "action": "regenerate"}
  ]
}
```

### 4.2 对话状态机

```java
package com.report.service;

/**
 * 对话式报表设计器 - 状态机
 */
@Service
public class ChatDesignerService {
    
    /**
     * 处理对话消息
     */
    public ChatResponse processMessage(String sessionId, String message, Context context) {
        // 1. 理解用户意图
        Intent intent = aiService.understandIntent(message, context);
        
        // 2. 根据当前步骤和意图执行操作
        return switch (context.getStep()) {
            case 1 -> handleDescribe(intent, context);      // 描述需求
            case 2 -> handleGenerateSQL(intent, context);   // 生成 SQL
            case 3 -> handlePreviewData(intent, context);   // 预览数据
            case 4 -> handleSelectChart(intent, context);   // 选择图表
            case 5 -> handleAdjustStyle(intent, context);   // 调整样式
            case 6 -> handleAddFilters(intent, context);    // 添加筛选
            case 7 -> handleSetLayout(intent, context);     // 设置布局
            case 8 -> handleSaveReport(intent, context);    // 命名保存
            case 9 -> handleSelectMenu(intent, context);    // 选择目录
            default -> error("未知步骤");
        };
    }
    
    /**
     * 步骤 1：描述需求
     */
    private ChatResponse handleDescribe(Intent intent, Context context) {
        // AI 理解用户需求
        Understanding understanding = aiService.understandQuestion(intent.getMessage());
        
        context.setUnderstanding(understanding);
        
        return ChatResponse.builder()
            .reply("""
                ✅ 已理解需求！
                
                - 📅 时间范围：%s
                - 📊 分组维度：%s
                - 📈 指标：%s
                
                帮你看下生成的 SQL？
                """.formatted(
                    understanding.getTimeRange(),
                    understanding.getDimension(),
                    understanding.getMetric()
                ))
            .nextStep(2)
            .preview(Preview.of("understanding", understanding))
            .actions(
                Action.confirm("👍 确认"),
                Action.reject("👎 重新理解")
            )
            .build();
    }
    
    /**
     * 步骤 2：生成 SQL
     */
    private ChatResponse handleGenerateSQL(Intent intent, Context context) {
        // AI 生成 SQL
        String sql = aiService.generateSQL(context.getUnderstanding());
        
        context.setSql(sql);
        
        return ChatResponse.builder()
            .reply("✅ SQL 已生成！右边可以看到～\n\n需要执行查询吗？")
            .nextStep(3)
            .preview(Preview.of("sql", sql))
            .actions(
                Action.custom("▶️ 执行查询", "execute"),
                Action.custom("✏️ 修改 SQL", "edit")
            )
            .build();
    }
    
    // ... 其他步骤类似处理
}
```

### 4.3 会话管理

```java
@Data
public class DesignerSession {
    private String sessionId;
    private String userId;
    private int currentStep = 1;
    
    // 各步骤的上下文数据
    private Understanding understanding;
    private String sql;
    private Map<String, Object> data;
    private String chartType;
    private Map<String, Object> chartConfig;
    private List<Variable> variables;
    private Map<String, Object> layout;
    private String reportName;
    private String menuId;
    
    // 对话历史
    private List<ChatMessage> history = new ArrayList<>();
    
    private long createTime = System.currentTimeMillis();
    private long lastAccessTime = System.currentTimeMillis();
}
```

---

## 五、前端实现要点

### 5.1 组件结构

```
ChatDesigner.vue (主组件)
├── PreviewPanel.vue (左侧预览区，70%)
│   ├── UnderstandingPreview.vue (意图理解)
│   ├── SQLEditor.vue (SQL 编辑器)
│   ├── DataTable.vue (数据表格)
│   ├── ChartPreview.vue (图表预览)
│   ├── StylePanel.vue (样式面板)
│   ├── FilterForm.vue (筛选表单)
│   ├── LayoutPreview.vue (布局预览)
│   ├── SaveForm.vue (保存表单)
│   └── MenuTree.vue (目录树)
├── ChatPanel.vue (右侧对话区，30%)
│   ├── MessageList.vue (消息列表)
│   ├── ChatInput.vue (输入框)
│   └── ActionButtons.vue (快捷操作)
│   ├── UnderstandingPreview.vue (意图理解)
│   ├── SQLEditor.vue (SQL 编辑器)
│   ├── DataTable.vue (数据表格)
│   ├── ChartPreview.vue (图表预览)
│   ├── StylePanel.vue (样式面板)
│   ├── FilterForm.vue (筛选表单)
│   ├── LayoutPreview.vue (布局预览)
│   ├── SaveForm.vue (保存表单)
│   └── MenuTree.vue (目录树)
└── ProgressBar.vue (顶部进度条)
```

### 5.2 状态管理（Pinia）

```javascript
import { defineStore } from 'pinia'

export const useDesignerStore = defineStore('designer', {
  state: () => ({
    sessionId: null,
    currentStep: 1,
    messages: [],  // 对话历史
    preview: null, // 当前预览内容
    context: {     // 完整上下文
      understanding: null,
      sql: '',
      data: null,
      chartType: '',
      chartConfig: null,
      variables: [],
      layout: null,
      reportName: '',
      menuId: ''
    }
  }),
  
  actions: {
    async sendMessage(message) {
      // 添加用户消息
      this.messages.push({ role: 'user', content: message })
      
      // 调用 API
      const response = await fetch('/api/designer/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sessionId: this.sessionId,
          message,
          context: this.context
        })
      })
      
      const result = await response.json()
      
      // 更新状态
      this.messages.push({ role: 'assistant', content: result.reply })
      this.currentStep = result.nextStep
      this.preview = result.preview
      this.context = { ...this.context, ...result.context }
      
      return result
    },
    
    async handleAction(action) {
      // 处理快捷操作（确认、重新生成等）
      return this.sendMessage(action.command)
    }
  }
})
```

### 5.3 对话组件示例

```vue
<template>
  <div class="chat-panel">
    <div class="message-list">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        <div class="avatar">
          {{ msg.role === 'user' ? '👤' : '🤖' }}
        </div>
        <div class="content">
          <div class="text" v-html="formatMessage(msg.content)"></div>
          <div v-if="msg.actions" class="actions">
            <el-button
              v-for="(action, i) in msg.actions"
              :key="i"
              size="small"
              @click="handleAction(action)"
            >
              {{ action.label }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="input-area">
      <el-input
        v-model="inputMessage"
        placeholder="输入你的想法..."
        @keyup.enter="sendMessage"
      >
        <template #append>
          <el-button type="primary" @click="sendMessage">发送</el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useDesignerStore } from '@/stores/designer'

const store = useDesignerStore()
const inputMessage = ref('')

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return
  
  await store.sendMessage(inputMessage.value.trim())
  inputMessage.value = ''
}

const handleAction = async (action) => {
  await store.handleAction(action)
}

const formatMessage = (text) => {
  // 格式化消息（支持 markdown、emoji 等）
  return text
}
</script>
```

---

## 六、关键特性

### 6.1 对话驱动

- **自然语言** - 用户随意表达，AI 理解执行
- **上下文感知** - AI 记住对话历史，支持多轮对话
- **主动引导** - 每步完成后 AI 提示下一步

### 6.2 实时预览

- **即时反馈** - AI 执行后立即可见效果
- **可视化** - SQL/数据/图表直观展示
- **状态同步** - 对话和预览区实时同步

### 6.3 人工干预

- **随时编辑** - 任何内容都可手动修改
- **混合模式** - AI 生成 + 人工调整
- **版本历史** - 每次修改可追溯

### 6.4 灵活流程

- **非线性** - 用户可随时返回或跳过步骤
- **智能推荐** - AI 根据数据推荐最佳实践
- **个性化** - 支持用户自定义偏好

---

## 七、总结

### 核心优势

1. **零学习成本** - 像聊天一样创建报表
2. **高效开发** - AI 完成 80% 工作，人工只需 20%
3. **灵活可控** - AI 不准时可手动干预
4. **体验流畅** - 对话 + 预览，无需跳转

### 技术要点

1. **统一对话 API** - 简化后端设计
2. **状态机管理** - 跟踪 9 步流程
3. **实时同步** - 对话和预览区状态一致
4. **组件化** - 每步预览独立组件

---

**文档版本**：v3.0  
**创建日期**：2026-04-19  
**适用系统**：REPORT 报表系统
