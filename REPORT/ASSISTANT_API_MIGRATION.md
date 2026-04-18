# Assistant API 升级指南

## 升级内容

已将智能体调用从旧版 **Apps API** 升级到新版 **Assistant API**，主要变化：

### API 对比

| 项目 | 旧版（Apps） | 新版（Assistant） |
|------|-------------|------------------|
| API 端点 | `/api/v1/apps/{APP_ID}/completion` | `/api/v1/assistant/{assistant_id}/completions` |
| 模型配置 | 固定（在控制台配置） | **动态指定**（通过 `ALI_QWEN` 配置） |
| 响应格式 | `output.text` | `output.text` 或 `choices[0].message.content` |
| Token 统计 | 未实现 | ✅ 已实现（从 `usage` 字段解析） |

## 配置步骤

### 1. 在百炼控制台创建 Assistant

1. 访问 https://bailian.console.aliyun.com/
2. 进入「应用中心」→「我的应用」
3. 点击「创建应用」→ 选择「Assistant」类型
4. 配置应用名称、描述、知识库等
5. 创建完成后，复制 **Assistant ID**

### 2. 配置数据库

在 `PRODUCT_APPKEY` 表中配置：

```sql
-- 配置 Assistant ID（ALI_AGENT）
UPDATE PRODUCT_APPKEY 
SET ACCESSKEYID = 'your-assistant-id-here',
    ACCESSKEYSECRET = 'your-api-key-here'
WHERE PLATFORM = 'ALI_AGENT';

-- 配置默认模型（ALI_QWEN）- 可选，不配置则使用 Assistant 默认模型
UPDATE PRODUCT_APPKEY 
SET ACCESSKEYID = 'qwen-plus-latest'
WHERE PLATFORM = 'ALI_QWEN';
```

### 3. 切换模型

现在可以通过修改 `ALI_QWEN` 配置来动态切换智能体使用的模型：

```sql
-- 切换到 qwen-plus
UPDATE PRODUCT_APPKEY SET ACCESSKEYID = 'qwen-plus' WHERE PLATFORM = 'ALI_QWEN';

-- 切换到 qwen-max
UPDATE PRODUCT_APPKEY SET ACCESSKEYID = 'qwen-max' WHERE PLATFORM = 'ALI_QWEN';

-- 切换到 deepseek-r1
UPDATE PRODUCT_APPKEY SET ACCESSKEYID = 'deepseek-r1' WHERE PLATFORM = 'ALI_QWEN';
```

或者在前端「Prompt 配置」页面 → 「当前使用模型」→ 「切换模型」进行操作。

## 请求/响应示例

### 请求体

```json
{
  "model": "qwen-plus-latest",
  "input": {
    "prompt": "查询销售数据"
  }
}
```

### 响应体

```json
{
  "output": {
    "text": "SELECT * FROM DCP_SALE WHERE ..."
  },
  "usage": {
    "input_tokens": 100,
    "output_tokens": 50
  }
}
```

## 兼容性说明

- ✅ 智能体模式（`ALI_AGENT`）使用新版 Assistant API
- ✅ 大模型模式（`ALI_MODEL`）保持不变，继续使用 DashScope 兼容模式 API
- ✅ 前端配置页面无需修改

## 回滚方案

如果需要回滚到旧版 Apps API：

1. 修改 `AISQLService.java` 中的 `callAssistantAPI` 方法，改回旧版端点
2. 重新编译部署

---

**升级时间：** 2026-04-18  
**版本：** v1.2.2
