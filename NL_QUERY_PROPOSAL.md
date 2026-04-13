# 自然语言查询系统方案书

**版本**：v1.0  
**创建时间**：2026-04-13  
**适用系统**：REPORT 报表系统

---

## 📋 目录

1. [需求背景](#需求背景)
2. [方案对比](#方案对比)
3. [推荐方案：规则匹配](#推荐方案规则匹配)
4. [扩展方案：AI 辅助](#扩展方案 ai 辅助)
5. [实现步骤](#实现步骤)
6. [时间评估](#时间评估)
7. [成本分析](#成本分析)
8. [决策建议](#决策建议)

---

## 需求背景

### 业务场景

用户希望通过**自然语言对话**查询业务数据，例如：

- "今天销售额是多少？"
- "这个月哪个商品卖得最好？"
- "A 门店的库存还有多少？"

### 技术挑战

1. **表结构复杂**：AI 需要知道有哪些表、字段含义
2. **SQL 安全**：防止恶意 SQL 注入
3. **响应速度**：用户期望秒级响应
4. **准确率**：查询结果必须准确

---

## 方案对比

### 方案 1：规则匹配 + 模板 SQL

| 维度 | 说明 |
|------|------|
| **原理** | 关键词匹配 → 模板 SQL → 执行查询 |
| **实现难度** | ⭐⭐ 简单 |
| **开发周期** | 1-2 天 |
| **成本** | 低（无额外费用） |
| **响应速度** | <100ms |
| **准确率** | 100%（预设问题） |
| **灵活性** | 低（只能回答预设问题） |
| **安全性** | 高（SQL 固定） |

**优点**：
- ✅ 实现简单，快速上线
- ✅ 完全可控，SQL 安全
- ✅ 无需外网，本地运行
- ✅ 零成本，无额外费用

**缺点**：
- ❌ 只能回答预设问题
- ❌ 需要维护关键词库
- ❌ 无法处理复杂问题

**适用场景**：
- 固定报表查询（销售额、库存、销量等）
- 问题类型有限（10-20 种）
- 对安全性要求高

---

### 方案 2：AI API 方案

| 维度 | 说明 |
|------|------|
| **原理** | 用户问题 → AI API → 生成 SQL → 执行查询 |
| **实现难度** | ⭐⭐⭐ 中等 |
| **开发周期** | 3-5 天 |
| **成本** | 中（API 调用费用） |
| **响应速度** | 2-5 秒 |
| **准确率** | 80-90% |
| **灵活性** | 高（可回答任意问题） |
| **安全性** | 中（需验证 SQL） |

**优点**：
- ✅ 支持复杂问题
- ✅ 自然语言理解强
- ✅ 可扩展性强

**缺点**：
- ❌ 需要外网访问
- ❌ 有 API 费用（约 0.01-0.1 元/次）
- ❌ 响应慢（2-5 秒）
- ❌ SQL 安全性需验证

**推荐 API**：
- 通义千问（阿里）：https://dashscope.console.aliyun.com/
- 文心一言（百度）：https://cloud.baidu.com/product/wenxinworkshop
- Kimi（月之暗面）：https://kimi.moonshot.cn/

---

### 方案 3：混合方案（推荐）

| 维度 | 说明 |
|------|------|
| **原理** | 规则匹配优先，AI 作为补充 |
| **实现难度** | ⭐⭐⭐ 中等 |
| **开发周期** | 5-7 天 |
| **成本** | 低 + 中（常用问题零成本） |
| **响应速度** | <100ms（规则）/ 2-5 秒（AI） |

**流程**：
```
用户问题
   ↓
规则匹配？
├─ 是 → 模板 SQL（快速，零成本）
└─ 否 → AI API（灵活，有成本）
```

---

## 推荐方案：规则匹配

### 实现原理

```
用户问：今天销售额是多少？
   ↓
1. 关键词匹配（今天 + 销售额）
   ↓
2. 匹配到规则：今日销售额
   ↓
3. 执行模板 SQL
   ↓
4. 返回结果
```

### 数据库设计

#### 1. 规则配置表

```sql
-- 自然语言查询规则表
CREATE TABLE NL_QUERY_RULES (
    RULE_ID VARCHAR2(36) CONSTRAINT PK_NL_QUERY_RULES PRIMARY KEY,
    RULE_NAME VARCHAR2(100) NOT NULL,      -- 规则名称
    KEYWORDS VARCHAR2(500) NOT NULL,       -- 关键词（逗号分隔）
    SQL_TEMPLATE VARCHAR2(2000) NOT NULL,  -- SQL 模板
    PARAMS VARCHAR2(500),                  -- 参数说明
    ENABLED CHAR(1) DEFAULT 'Y',           -- 是否启用
    SORT_ORDER NUMBER DEFAULT 0,           -- 排序
    CREATED_TIME DATE DEFAULT SYSDATE,     -- 创建时间
    UPDATED_TIME DATE DEFAULT SYSDATE,     -- 更新时间
    CREATED_BY VARCHAR2(50) DEFAULT 'system'
);

-- 添加表注释
COMMENT ON TABLE NL_QUERY_RULES IS '自然语言查询规则表';
COMMENT ON COLUMN NL_QUERY_RULES.RULE_NAME IS '规则名称';
COMMENT ON COLUMN NL_QUERY_RULES.KEYWORDS IS '关键词（逗号分隔）';
COMMENT ON COLUMN NL_QUERY_RULES.SQL_TEMPLATE IS 'SQL 模板';
COMMENT ON COLUMN NL_QUERY_RULES.PARAMS IS '参数说明';
COMMENT ON COLUMN NL_QUERY_RULES.ENABLED IS '是否启用 Y/N';
COMMENT ON COLUMN NL_QUERY_RULES.SORT_ORDER IS '排序（数字小的优先）';

-- 创建索引
CREATE INDEX IDX_NL_RULES_ENABLED ON NL_QUERY_RULES(ENABLED);
CREATE INDEX IDX_NL_RULES_SORT ON NL_QUERY_RULES(SORT_ORDER);
```

#### 2. 示例数据

```sql
-- 规则 1：今日销售额
INSERT INTO NL_QUERY_RULES VALUES (
    'RULE_001', '今日销售额', '今天，今日，当天，销售额，销售金额',
    'SELECT NVL(SUM(SALE_QTY * SALE_PRICE), 0) AS TOTAL FROM SALES WHERE SALE_DATE = TRUNC(SYSDATE)',
    '', 'Y', 1, SYSDATE, SYSDATE, 'system'
);

-- 规则 2：本月销售额
INSERT INTO NL_QUERY_RULES VALUES (
    'RULE_002', '本月销售额', '本月，这个月，当月，销售额',
    'SELECT NVL(SUM(SALE_QTY * SALE_PRICE), 0) AS TOTAL FROM SALES WHERE TO_CHAR(SALE_DATE, ''YYYY-MM'') = TO_CHAR(SYSDATE, ''YYYY-MM'')',
    '', 'Y', 2, SYSDATE, SYSDATE, 'system'
);

-- 规则 3：商品销量
INSERT INTO NL_QUERY_RULES VALUES (
    'RULE_003', '商品销量', '销量，销售数量，卖了多少',
    'SELECT NVL(SUM(SALE_QTY), 0) AS TOTAL FROM SALES WHERE PLUNO = :PLUNO AND SALE_DATE = TRUNC(SYSDATE)',
    'PLUNO:商品品号', 'Y', 3, SYSDATE, SYSDATE, 'system'
);

-- 规则 4：库存查询
INSERT INTO NL_QUERY_RULES VALUES (
    'RULE_004', '库存查询', '库存，还有多少，剩余',
    'SELECT NVL(STOCK_QTY, 0) AS TOTAL FROM STOCK WHERE PLUNO = :PLUNO',
    'PLUNO:商品品号', 'Y', 4, SYSDATE, SYSDATE, 'system'
);

-- 规则 5：销售排行
INSERT INTO NL_QUERY_RULES VALUES (
    'RULE_005', '销售排行', '排行，排名，最畅销，卖得最好',
    'SELECT PRODUCT_NAME, SUM(SALE_QTY) AS TOTAL FROM SALES GROUP BY PRODUCT_NAME ORDER BY TOTAL DESC FETCH FIRST 10 ROWS ONLY',
    '', 'Y', 5, SYSDATE, SYSDATE, 'system'
);
```

### Java 实现

#### 1. 实体类

```java
package com.report.entity;

import lombok.Data;
import java.util.Date;

/**
 * 自然语言查询规则
 */
@Data
public class NLQueryRule {
    private String ruleId;
    private String ruleName;
    private String keywords;
    private String sqlTemplate;
    private String params;
    private String enabled;
    private Integer sortOrder;
    private Date createdTime;
    private Date updatedTime;
}
```

#### 2. 服务类

```java
package com.report.service;

import com.report.entity.NLQueryRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自然语言查询服务
 */
@Service
public class NLQueryService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 自然语言查询
     * @param question 用户问题
     * @return 查询结果
     */
    public Map<String, Object> query(String question) {
        if (question == null || question.trim().isEmpty()) {
            return error("问题不能为空");
        }
        
        // 1. 匹配规则
        NLQueryRule rule = matchRule(question);
        
        if (rule == null) {
            return error("抱歉，我不理解这个问题。试试问：今天销售额是多少？");
        }
        
        // 2. 提取参数
        Map<String, String> params = extractParams(question, rule);
        
        // 3. 执行 SQL
        try {
            String sql = replaceParams(rule.getSqlTemplate(), params);
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            response.put("ruleName", rule.getRuleName());
            response.put("question", question);
            
            return response;
            
        } catch (Exception e) {
            return error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 匹配规则
     */
    private NLQueryRule matchRule(String question) {
        String sql = "SELECT * FROM NL_QUERY_RULES WHERE ENABLED = 'Y' ORDER BY SORT_ORDER";
        List<Map<String, Object>> rules = jdbcTemplate.queryForList(sql);
        
        for (Map<String, Object> rule : rules) {
            String keywords = (String) rule.get("KEYWORDS");
            String[] keywordArray = keywords.split(",");
            
            for (String keyword : keywordArray) {
                if (question.contains(keyword.trim())) {
                    return convertToRule(rule);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 提取参数
     */
    private Map<String, String> extractParams(String question, NLQueryRule rule) {
        Map<String, String> params = new HashMap<>();
        
        // 从问题中提取品号（假设品号是数字或字母数字组合）
        Pattern pattern = Pattern.compile("(\\d+[A-Z]*|[A-Z]+\\d+)");
        Matcher matcher = pattern.matcher(question);
        
        if (matcher.find()) {
            params.put("PLUNO", matcher.group(1));
        }
        
        return params;
    }
    
    /**
     * 替换 SQL 参数
     */
    private String replaceParams(String sql, Map<String, String> params) {
        String result = sql;
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String placeholder = ":" + entry.getKey();
            String value = "'" + entry.getValue() + "'";
            result = result.replace(placeholder, value);
        }
        
        return result;
    }
    
    /**
     * 错误响应
     */
    private Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
    
    /**
     * 转换实体
     */
    private NLQueryRule convertToRule(Map<String, Object> map) {
        NLQueryRule rule = new NLQueryRule();
        rule.setRuleId((String) map.get("RULE_ID"));
        rule.setRuleName((String) map.get("RULE_NAME"));
        rule.setKeywords((String) map.get("KEYWORDS"));
        rule.setSqlTemplate((String) map.get("SQL_TEMPLATE"));
        rule.setParams((String) map.get("PARAMS"));
        rule.setEnabled((String) map.get("ENABLED"));
        rule.setSortOrder((Integer) map.get("SORT_ORDER"));
        rule.setCreatedTime((Date) map.get("CREATED_TIME"));
        rule.setUpdatedTime((Date) map.get("UPDATED_TIME"));
        return rule;
    }
}
```

#### 3. Controller

```java
package com.report.controller;

import com.report.service.NLQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 自然语言查询 Controller
 */
@RestController
@RequestMapping("/api/nl-query")
@CrossOrigin(origins = "*")
public class NLQueryController {
    
    @Autowired
    private NLQueryService nlQueryService;
    
    /**
     * 自然语言查询
     * @param question 用户问题
     * @return 查询结果
     */
    @PostMapping("/query")
    public Map<String, Object> query(@RequestParam String question) {
        return nlQueryService.query(question);
    }
    
    /**
     * 测试接口
     * @return 测试问题列表
     */
    @GetMapping("/examples")
    public Map<String, Object> getExamples() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("examples", new String[]{
            "今天销售额是多少？",
            "本月销售额是多少？",
            "商品 001 的销量如何？",
            "商品 001 的库存还有多少？",
            "最畅销的商品是哪些？"
        });
        return response;
    }
}
```

### 前端实现

#### 对话框组件

```vue
<template>
  <div class="nl-query-chat">
    <div class="chat-header">
      <h3>🤖 智能问答</h3>
      <el-button size="small" @click="showExamples">示例问题</el-button>
    </div>
    
    <div class="chat-messages" ref="messageContainer">
      <div 
        v-for="(msg, index) in messages" 
        :key="index" 
        :class="['message', msg.type]"
      >
        <div class="message-content">
          {{ msg.content }}
        </div>
        <div class="message-time">
          {{ msg.time }}
        </div>
      </div>
      
      <div v-if="loading" class="message bot">
        <div class="message-content">
          <el-icon class="is-loading"><Loading /></el-icon>
          思考中...
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <el-input
        v-model="question"
        placeholder="问我：今天销售额是多少？"
        @keyup.enter="send"
        :disabled="loading"
      >
        <template #append>
          <el-button 
            type="primary" 
            @click="send" 
            :loading="loading"
          >
            发送
          </el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

const messages = ref([
  { type: 'bot', content: '你好！我是智能助手，可以问我：今天销售额是多少？', time: now() }
])
const question = ref('')
const loading = ref(false)
const messageContainer = ref(null)

const now = () => {
  const d = new Date()
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const scrollToBottom = async () => {
  await nextTick()
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

const showExamples = async () => {
  try {
    const res = await fetch('http://47.100.138.89:8110/api/nl-query/examples')
    const data = await res.json()
    if (data.success) {
      ElMessage.info('试试问：' + data.examples.join('、'))
    }
  } catch (e) {
    ElMessage.error('获取示例失败')
  }
}

const send = async () => {
  if (!question.value.trim() || loading.value) return
  
  const userQuestion = question.value.trim()
  
  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: userQuestion,
    time: now()
  })
  
  question.value = ''
  loading.value = true
  await scrollToBottom()
  
  try {
    const params = new URLSearchParams()
    params.append('question', userQuestion)
    
    const res = await fetch('http://47.100.138.89:8110/api/nl-query/query', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    })
    
    const data = await res.json()
    
    if (data.success) {
      // 格式化结果
      let content = `✅ ${data.ruleName}\n\n`
      
      if (data.data && data.data.length > 0) {
        const row = data.data[0]
        for (const [key, value] of Object.entries(row)) {
          content += `${key}: ${value}\n`
        }
      } else {
        content += '暂无数据'
      }
      
      messages.value.push({
        type: 'bot',
        content: content,
        time: now()
      })
    } else {
      messages.value.push({
        type: 'bot',
        content: '❌ ' + (data.message || '查询失败'),
        time: now()
      })
    }
    
  } catch (e) {
    messages.value.push({
      type: 'bot',
      content: '❌ 网络错误：' + e.message,
      time: now()
    })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}
</script>

<style scoped>
.nl-query-chat {
  display: flex;
  flex-direction: column;
  height: 500px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: white;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.chat-header h3 {
  margin: 0;
  font-size: 16px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background: #f5f5f5;
}

.message {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.message.user {
  align-items: flex-end;
}

.message.bot {
  align-items: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 8px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.message.user .message-content {
  background: #409EFF;
  color: white;
}

.message.bot .message-content {
  background: white;
  color: #333;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.chat-input {
  padding: 15px;
  border-top: 1px solid #e0e0e0;
  background: white;
}
</style>
```

---

## 扩展方案：AI 辅助

### 实现原理

```
用户问题
   ↓
1. 读取表结构（从数据库元数据）
   ↓
2. 构建 Prompt（表结构 + 用户问题）
   ↓
3. 调用 AI API（通义千问）
   ↓
4. AI 返回 SQL
   ↓
5. 验证 SQL（只允许 SELECT）
   ↓
6. 执行 SQL（只读账号）
   ↓
7. 返回结果
```

### 表结构获取

```java
@Service
public class SchemaService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 获取所有表结构
     */
    public String getAllTables() {
        StringBuilder schema = new StringBuilder();
        
        // 1. 查询所有表
        String tablesSql = "SELECT TABLE_NAME, COMMENTS FROM USER_TAB_COMMENTS";
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(tablesSql);
        
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("TABLE_NAME");
            String tableComment = (String) table.get("COMMENTS");
            
            schema.append("表：").append(tableName);
            if (tableComment != null) {
                schema.append("（").append(tableComment).append("）");
            }
            schema.append("\n");
            
            // 2. 查询表的列
            String columnsSql = """
                SELECT COLUMN_NAME, DATA_TYPE, COMMENTS 
                FROM USER_COL_COMMENTS 
                WHERE TABLE_NAME = ?
                ORDER BY COLUMN_ID
                """;
            
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                columnsSql, tableName
            );
            
            for (Map<String, Object> col : columns) {
                String colName = (String) col.get("COLUMN_NAME");
                String dataType = (String) col.get("DATA_TYPE");
                String comment = (String) col.get("COMMENTS");
                
                schema.append("  - ").append(colName);
                schema.append("(").append(dataType).append(")");
                if (comment != null) {
                    schema.append(": ").append(comment);
                }
                schema.append("\n");
            }
            
            schema.append("\n");
        }
        
        return schema.toString();
    }
}
```

### AI 调用

```java
@Service
public class AISQLService {
    
    @Autowired
    private SchemaService schemaService;
    
    @Value("${ai.api.key}")
    private String apiKey;
    
    /**
     * 生成 SQL
     */
    public String generateSQL(String question) {
        // 1. 读取表结构
        String schema = schemaService.getAllTables();
        
        // 2. 构建 Prompt
        String prompt = buildPrompt(schema, question);
        
        // 3. 调用 AI API
        String sql = callDashScope(prompt);
        
        return sql;
    }
    
    /**
     * 构建 Prompt
     */
    private String buildPrompt(String schema, String question) {
        return """
        你是一个 SQL 专家，请根据以下数据库表结构，将用户的自然语言问题转换为 Oracle SQL 查询。
        
        数据库表结构：
        %s
        
        用户问题：%s
        
        要求：
        1. 只返回 SQL，不要解释
        2. 只使用 SELECT 语句
        3. 使用 Oracle 语法
        
        SQL：
        """.formatted(schema, question);
    }
    
    /**
     * 调用通义千问 API
     */
    private String callDashScope(String prompt) {
        // 调用通义千问 API
        // 参考文档：https://help.aliyun.com/document_detail/611472.html
        
        Map<String, Object> request = new HashMap<>();
        request.put("model", "qwen-plus");
        
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        
        request.put("input", Map.of("messages", List.of(message)));
        
        // 调用 HTTP API
        // 返回 AI 生成的 SQL
        
        return "SELECT ...";
    }
    
    /**
     * 验证 SQL
     */
    public boolean validateSQL(String sql) {
        // 只允许 SELECT
        if (!sql.trim().toUpperCase().startsWith("SELECT")) {
            return false;
        }
        
        // 禁止危险操作
        String[] dangerous = {"DROP", "DELETE", "UPDATE", "INSERT", "TRUNCATE"};
        for (String keyword : dangerous) {
            if (sql.toUpperCase().contains(keyword)) {
                return false;
            }
        }
        
        return true;
    }
}
```

---

## 实现步骤

### 第一阶段：规则匹配（1-2 天）

#### Day 1：后端开发

1. **创建数据库表**（30 分钟）
   ```sql
   CREATE TABLE NL_QUERY_RULES ...
   INSERT INTO NL_QUERY_RULES VALUES ...
   ```

2. **实现 Java 服务**（2 小时）
   - NLQueryRule 实体类
   - NLQueryService 服务类
   - NLQueryController 控制器

3. **测试接口**（1 小时）
   ```bash
   curl -X POST "http://localhost:8110/api/nl-query/query?question=今天销售额是多少？"
   ```

#### Day 2：前端开发

1. **创建对话框组件**（2 小时）
   - 消息列表显示
   - 输入框和发送按钮
   - 加载状态

2. **集成到系统**（1 小时）
   - 添加到菜单
   - 配置路由

3. **测试优化**（1 小时）
   - 测试常见问题
   - 优化响应速度

---

### 第二阶段：AI 辅助（3-5 天，可选）

#### Day 3-4：AI 集成

1. **申请 API Key**（1 小时）
   - 注册通义千问账号
   - 创建 API Key

2. **实现 AI 服务**（4 小时）
   - SchemaService 表结构获取
   - AISQLService AI 调用
   - SQL 验证逻辑

3. **测试 AI 生成**（2 小时）
   - 测试复杂问题
   - 优化 Prompt

#### Day 5：混合逻辑

1. **实现混合逻辑**（2 小时）
   - 规则匹配优先
   - AI 作为补充

2. **整体测试**（2 小时）
   - 性能测试
   - 安全测试

---

## 时间评估

| 阶段 | 工作内容 | 时间 |
|------|---------|------|
| **第一阶段** | 规则匹配方案 | 1-2 天 |
| **第二阶段** | AI 辅助方案 | 3-5 天 |
| **总计** | 完整功能 | 4-7 天 |

---

## 成本分析

### 规则匹配方案

| 项目 | 费用 |
|------|------|
| 开发成本 | 1-2 天 × 日薪 |
| API 费用 | 0 元 |
| 服务器 | 现有服务器 |
| **总计** | **约 1000-2000 元**（人工成本） |

### AI 辅助方案

| 项目 | 费用 |
|------|------|
| 开发成本 | 3-5 天 × 日薪 |
| API 费用 | 0.01-0.1 元/次 × 使用量 |
| 服务器 | 现有服务器 |
| **总计** | **约 3000-5000 元 + API 费用** |

**API 费用示例**（通义千问）：
- 1000 次/月 × 0.05 元 = 50 元/月
- 10000 次/月 × 0.05 元 = 500 元/月

---

## 决策建议

### 推荐方案：分阶段实施

**第一阶段（立即实施）**：
- ✅ 规则匹配方案
- ✅ 1-2 天上线
- ✅ 覆盖 80% 常见问题
- ✅ 成本低，风险小

**第二阶段（根据效果决定）**：
- 如果规则匹配效果好 → 扩展规则库
- 如果用户需要更灵活查询 → 添加 AI 辅助

### 决策清单

**选择规则匹配，如果**：
- ✅ 问题类型固定（10-20 种）
- ✅ 对安全性要求高
- ✅ 预算有限
- ✅ 需要快速上线

**选择 AI 辅助，如果**：
- ✅ 问题类型复杂多变
- ✅ 有外网访问条件
- ✅ 预算充足
- ✅ 可以接受 2-5 秒响应

---

## 联系方式

**实施完成后**：
- 测试账号：admin / 123456
- 访问地址：http://47.100.138.89:8081
- 文档位置：/opt/openclaw/workspace/NL_QUERY_PROPOSAL.md

---

**文档版本**：v1.0  
**最后更新**：2026-04-13  
**创建者**：龙虾 AI 助手 🦞
