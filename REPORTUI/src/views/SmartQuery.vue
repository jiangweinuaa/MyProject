<template>
  <div class="smart-query-page">
    <div class="smart-query-container">
      <div class="chat-header">
        <h1>🤖 智问</h1>
        <p>用自然语言查询数据</p>
      </div>
      
      <div class="chat-messages" ref="messageContainer">
        <!-- 聊天记录 -->
        <div 
          v-for="(msg, index) in messages" 
          :key="index" 
          :class="['message', msg.type, msg.isWelcome ? 'welcome-message' : '']"
        >
          <div class="message-content">
            <!-- 欢迎语特殊显示 -->
            <div v-if="msg.isWelcome">
              <h2>{{ msg.content }}</h2>
              <p>可以用自然语言查询数据，试试问我：</p>
              <div class="example-questions">
                <el-button 
                  v-for="(example, index) in examples" 
                  :key="index"
                  size="small"
                  @click="askExample(example)"
                >
                  {{ example }}
                </el-button>
              </div>
            </div>
            
            <!-- 普通消息显示 -->
            <template v-else>
            <!-- SQL 代码块已隐藏 -->
            <!-- <pre v-if="msg.sql" class="sql-block">{{ msg.sql }}</pre> -->
            
            <!-- 图表区域（自动显示多个图表） -->
            <div class="chart-section" v-if="shouldShowChart(msg)">
              <!-- 指标卡 -->
              <MetricCard 
                v-if="hasChartType(msg, 'metric')" 
                :data="msg.data" 
              />
              
              <!-- 饼图（放在柱状图前面） -->
              <PieChart 
                v-if="hasChartType(msg, 'pie')" 
                :data="extractChartData(msg, 'ratio')"
                :height="300"
              />
              
              <!-- 对比柱状图 -->
              <CompareBarChart 
                v-if="hasChartType(msg, 'compareBar')" 
                :data="extractChartData(msg)"
                :height="200"
              />
              
              <!-- 折线图 -->
              <LineChart 
                v-if="hasChartType(msg, 'line')" 
                :data="extractChartData(msg)"
                :height="300"
              />
              
              <!-- 柱状图 -->
              <BarChart 
                v-if="hasChartType(msg, 'bar')" 
                :data="extractChartData(msg, 'bar')"
                :height="300"
              />
              
              <!-- 条形图 -->
              <HorizontalBarChart 
                v-if="hasChartType(msg, 'horizontalBar')" 
                :data="extractChartData(msg)"
                :height="300"
              />
              
              <!-- 数据摘要 -->
              <div class="data-summary" v-if="msg.data && msg.data.length > 0">
                <el-tag size="small" type="info">
                  📊 共 {{ msg.data.length }} 条数据
                </el-tag>
                <el-tag size="small" type="success" v-if="getTotalValue(msg.data)">
                  💰 总计：{{ getTotalValue(msg.data) }}
                </el-tag>
              </div>
            </div>
            
            <!-- 表格视图（始终显示） -->
            <div class="table-view" v-if="msg.data && msg.data.length > 0">
              <el-table 
                :data="msg.data" 
                style="width: 100%" 
                border 
                stripe
                :default-sort="{ prop: Object.keys(msg.data[0])[0], order: 'descending' }"
              >
                <el-table-column
                  v-for="column in getColumns(msg.data)"
                  :key="column"
                  :prop="column"
                  :label="column"
                  sortable
                />
              </el-table>
            </div>
            
            <!-- 文字回复内容（不显示 markdown 表格） -->
            <div class="text-content" v-if="msg.content && !msg.content.includes('|')">
              {{ msg.content }}
            </div>
            </template>
          </div>
          <div class="message-time">{{ msg.time }}</div>
        </div>
        
        <div v-if="loading" class="message bot">
          <div class="message-content">
            <div class="loading">
              <span class="loading-spinner">⚙️</span>
              <span>AI 思考中...</span>
            </div>
          </div>
        </div>
      </div>
      
      <ChatInput :loading="loading" @send="handleSendMessage" />
    </div>
  </div>
</template>

<script>
// 导入图表组件
import MetricCard from '@/components/SmartQuery/MetricCard.vue'
import BarChart from '@/components/SmartQuery/BarChart.vue'
import LineChart from '@/components/SmartQuery/LineChart.vue'
import PieChart from '@/components/SmartQuery/PieChart.vue'
import HorizontalBarChart from '@/components/SmartQuery/HorizontalBarChart.vue'
import CompareBarChart from '@/components/SmartQuery/CompareBarChart.vue'
import ChatInput from '@/components/SmartQuery/ChatInput.vue'

// 导入 API
import { getConversationList, getConversationHistory, nlQuery } from '@/api/smart-query'

export default {
  name: 'SmartQuery',
  components: {
    MetricCard,
    BarChart,
    LineChart,
    PieChart,
    HorizontalBarChart,
    CompareBarChart,
    ChatInput
  },
  data() {
    return {
      loading: false,
      inputQueue: [],      // 输入队列
      isProcessing: false, // 是否正在处理队列
      messages: [],
      examples: [
        '今天销售额是多少？',
        '昨天销售额是多少？',
        '本月销售额是多少？',
        '哪个商品卖得最好？'
      ],
      hasHistory: false,
      sessionId: null
    }
  },
  mounted() {
    this.loadRecentHistory();
  },
  methods: {
    async loadRecentHistory() {
      try {
        console.log('===== 智问页面加载历史记录 =====');
        console.log('localStorage token:', localStorage.getItem('token') ? '有' : '无');
        
        // 使用 API 模块（axios 会自动添加 token）
        const response = await getConversationList({
          page: 0,
          size: 1
        });
        
        console.log('会话列表响应:', response);
        
        if (response.success && response.data && response.data.length > 0) {
          const latestSession = response.data[0];
          // 检查会话的 userId 是否与当前 token 一致
          const currentToken = localStorage.getItem('token') || '';
          if (currentToken) {
            try {
              const tokenData = JSON.parse(atob(currentToken.split('.')[0] || '{}'));
              const currentOpno = tokenData.OPNO || 'unknown';
              if (latestSession.userId !== currentOpno) {
                // userId 不匹配，创建新会话
                console.log('userId 不匹配，创建新会话');
                this.sessionId = 'session_' + Date.now();
                this.messages.push({
                  type: 'bot',
                  isWelcome: true,
                  content: '你好！我是智问助手 👋',
                  sql: '',
                  data: null,
                  time: this.now()
                });
                return;
              }
            } catch (e) {
              // token 解析失败，使用新会话
              console.log('token 解析失败，使用新会话');
              this.sessionId = 'session_' + Date.now();
              this.messages.push({
                type: 'bot',
                isWelcome: true,
                content: '你好！我是智问助手 👋',
                sql: '',
                data: null,
                time: this.now()
              });
              return;
            }
          }
          // userId 匹配，加载历史会话
          this.sessionId = latestSession.sessionId;
          console.log('加载会话历史:', latestSession.sessionId);
          await this.loadSessionHistory(latestSession.sessionId);
        } else {
          // 没有历史记录，只添加欢迎语
          this.messages.push({
            type: 'bot',
            isWelcome: true,
            content: '你好！我是智问助手 👋',
            sql: '',
            data: null,
            time: this.now()
          });
        }
      } catch (error) {
        console.error('加载历史记录失败:', error.message || error);
      }
    },
    
    async loadSessionHistory(sessionId) {
      try {
        // 使用 API 模块（axios 会自动添加 token）
        const response = await getConversationHistory({
          sessionId: sessionId,
          page: 0,
          size: 50
        });
        
        console.log('会话历史响应:', response);
        
        if (response.success && response.data && response.data.length > 0) {
          this.hasHistory = true;
          
          // 后端按时间倒序返回（新的在前），需要反转为正序显示（旧的在上，新的在下）
          const dialogues = response.data.reverse();
          
          dialogues.forEach(dialogue => {
            this.messages.push({
              type: 'user',
              content: dialogue.question,
              time: this.formatTime(dialogue.createdTime)
            });
            
            let chartData = [];
            try {
              chartData = JSON.parse(dialogue.resultData);
            } catch (e) {
              console.error('解析结果数据失败:', e);
            }
            
            this.messages.push({
              type: 'bot',
              content: `查询完成，共 ${dialogue.rowCount} 条数据`,
              sql: dialogue.sqlGenerated,
              data: chartData,
              time: this.formatTime(dialogue.createdTime)
            });
          });
          
          // 最后添加欢迎语（在最后面，会被新消息推着往上走）
          this.messages.push({
            type: 'bot',
            isWelcome: true,
            content: '你好！我是智问助手 👋',
            sql: '',
            data: null,
            time: this.now()
          });
          
          // 滚动到最底部
          this.$nextTick(() => {
            const container = this.$refs.messageContainer;
            if (container) {
              container.scrollTop = container.scrollHeight;
            }
          });
        }
      } catch (error) {
        console.error('加载会话历史失败:', error);
      }
    },
    
    formatTime(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const now = new Date();
      const diff = now - date;
      
      if (diff < 60000) return '刚刚';
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
      
      return date.toLocaleDateString('zh-CN', {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    /**
     * 自动检测图表类型（返回数组，支持多图表）
     */
    detectChartTypes(data, sql) {
      const result = []
      
      if (!data || data.length === 0) {
        console.log('图表检测：无数据')
        return result
      }
      
      const sqlUpper = sql.toUpperCase()
      const rowCount = data.length
      const columns = Object.keys(data[0])
      const columnCount = columns.length
      
      console.log('图表检测参数：rowCount=', rowCount, 'columnCount=', columnCount, 'columns=', columns)
      
      // 1. 单行单列 → 指标卡
      if (rowCount === 1 && columnCount === 1) {
        result.push('metric')
        console.log('图表检测：指标卡')
        return result
      }
      
      // 1.5 单行多列 (2-4 列) → 对比柱状图
      if (rowCount === 1 && columnCount >= 2 && columnCount <= 6) {
        // 检查是否有对比特征列名
        const hasCompareKeywords = columns.some(col => {
          const colUpper = col.toUpperCase()
          return colUpper.includes('LAST') ||     // 上月/上期
                 colUpper.includes('THIS') ||     // 本月/本期
                 colUpper.includes('PREV') ||     // 前期
                 colUpper.includes('CURR') ||     // 本期
                 colUpper.includes('GROWTH') ||   // 增长
                 colUpper.includes('同比') ||      // 同比
                 colUpper.includes('环比')         // 环比
        })
        
        if (hasCompareKeywords) {
          result.push('compareBar')
          console.log('图表检测：对比柱状图（单行多列对比）')
        }
      }
      
      // 2. 包含日期字段 → 折线图
      const dateKeywords = ['BDATE', 'DATE', 'TO_CHAR', 'TRUNC', 'TIME']
      const hasDateField = columns.some(col => 
        dateKeywords.some(keyword => col.toUpperCase().includes(keyword))
      )
      
      if (hasDateField && rowCount > 1) {
        result.push('line')
        console.log('图表检测：折线图（日期字段）')
      }
      
      // 3. 有 GROUP BY + 2-10 列 → 柱状图/饼图（支持占比列）
      const hasGroupBy = sqlUpper.includes('GROUP BY')
      console.log('图表检测：hasGroupBy=', hasGroupBy, 'columnCount=', columnCount)
      if (hasGroupBy && columnCount >= 2 && columnCount <= 10) {
        // 检查是否有占比/百分比列（用 endsWith 避免误判）
        const hasRatioColumn = columns.some(col => {
          const colUpper = col.toUpperCase()
          return colUpper.endsWith('占比') || 
                 colUpper.endsWith('占比%') || 
                 colUpper.endsWith('PERCENT') || 
                 colUpper.endsWith('PERCENTAGE') || 
                 colUpper.endsWith('RATIO') || 
                 colUpper.endsWith('RATE') || 
                 colUpper.endsWith('率') || 
                 colUpper.endsWith('%')
        })
        
        console.log('图表检测：hasRatioColumn=', hasRatioColumn, 'rowCount=', rowCount)
        
        // 有占比列 → 饼图（放宽行数限制到 30 行）
        if (hasRatioColumn && rowCount <= 30) {
          result.push('pie')
          console.log('图表检测：饼图（占比分析，' + rowCount + '行）')
        }
        
        // 柱状图 - 智能限制数据量（方案 3）
        if (rowCount <= 100) {
          result.push('bar')
          if (rowCount > 50) {
            console.log('图表检测：柱状图（金额对比，' + rowCount + '行，只显示前 50 条）')
          } else {
            console.log('图表检测：柱状图（金额对比，' + rowCount + '行）')
          }
        }
      }
      
      // 4. 有 ORDER BY + ROWNUM → 排行条形图
      const hasOrderBy = sqlUpper.includes('ORDER BY')
      const hasRownum = sqlUpper.includes('ROWNUM')
      if (hasOrderBy && hasRownum && columnCount >= 2) {
        result.push('horizontalBar')
        console.log('图表检测：条形图（排行）')
      }
      
      // 5. 2 列数据 → 柱状图
      if (columnCount === 2 && rowCount <= 15 && !result.includes('bar')) {
        result.push('bar')
        console.log('图表检测：柱状图（2 列数据）')
      }
      
      console.log('最终图表类型：', result)
      
      return result
    },
    
    /**
     * 提取图表数据
     */
    extractChartData(msg, type = null) {
      if (!msg.data || msg.data.length === 0) return null
      
      const data = msg.data
      const columns = Object.keys(data[0])
      const sqlUpper = msg.sql.toUpperCase()
      
      console.log('提取图表数据 - type:', type, 'columns:', columns)
      
      // 查找分类列（字符串列，作为 X 轴）
      const categoryColumn = columns.find(col => {
        const sample = data[0][col]
        // 只要是字符串，就可以作为 X 轴（分类）
        return typeof sample === 'string'
      })
      
      // 查找数值列
      let numericColumn = null
      
      if (type === 'ratio') {
        // 饼图：找占比/百分比列（用 endsWith 避免误判）
        numericColumn = columns.find(col => {
          const colUpper = col.toUpperCase()
          return colUpper.endsWith('占比') || 
                 colUpper.endsWith('占比%') || 
                 colUpper.endsWith('PERCENT') || 
                 colUpper.endsWith('PERCENTAGE') || 
                 colUpper.endsWith('RATIO') || 
                 colUpper.endsWith('RATE') || 
                 colUpper.endsWith('率') || 
                 colUpper.endsWith('%')
        })
      }
      
      // 如果没找到占比列或不是饼图，找金额/销量列
      if (!numericColumn) {
        numericColumn = columns.find(col => {
          const sample = data[0][col]
          const colUpper = col.toUpperCase()
          // 跳过占比、百分比、比率列（用 endsWith 避免误判）
          if (type !== 'ratio' && (
            colUpper.endsWith('占比') || 
            colUpper.endsWith('占比%') || 
            colUpper.endsWith('PERCENT') || 
            colUpper.endsWith('PERCENTAGE') || 
            colUpper.endsWith('RATIO') || 
            colUpper.endsWith('RATE') || 
            colUpper.endsWith('率') || 
            colUpper.endsWith('%')
          )) {
            return false
          }
          // 优先找金额、销量列
          if (colUpper.endsWith('金额') || 
              colUpper.endsWith('AMT') || 
              colUpper.endsWith('销量') || 
              colUpper.endsWith('QTY') || 
              colUpper.endsWith('SALE') || 
              colUpper.endsWith('SUM') || 
              colUpper.endsWith('AVG') || 
              colUpper.endsWith('COUNT') || 
              colUpper.endsWith('TIME') || 
              colUpper.endsWith('MS')) {
            return typeof sample === 'number' || !isNaN(Number(sample))
          }
          return typeof sample === 'number'
        })
      }
      
      console.log('提取图表数据 - categoryColumn:', categoryColumn, 'numericColumn:', numericColumn)
      
      // 确定标题
      let title = '查询结果'
      if (sqlUpper.includes('销售额')) title = '销售额统计'
      else if (sqlUpper.includes('销量')) title = '销量统计'
      else if (sqlUpper.includes('库存')) title = '库存统计'
      else if (sqlUpper.includes('品类')) title = '品类分析'
      
      // 方案 3：智能限制数据量（柱状图超过 50 条只显示前 50 条）
      let chartData = data
      if (type === 'bar' && data.length > 50) {
        chartData = data.slice(0, 50)
        console.log('数据量较多（' + data.length + '条），图表只显示前 50 条')
      }
      
      return {
        xAxis: categoryColumn,
        series: numericColumn,
        title: title,
        data: chartData
      }
    },
    
    /**
     * 是否显示图表
     */
    shouldShowChart(msg) {
      if (!msg.data || msg.data.length === 0 || !msg.sql) {
        console.log('不显示图表：data=', msg.data, 'sql=', msg.sql)
        return false
      }
      const chartTypes = this.detectChartTypes(msg.data, msg.sql)
      console.log('图表检测结果：', chartTypes, 'data:', msg.data, 'sql:', msg.sql)
      return chartTypes.length > 0
    },
    
    /**
     * 检查是否包含指定图表类型
     */
    hasChartType(msg, type) {
      if (!msg.data || msg.data.length === 0 || !msg.sql) return false
      const chartTypes = this.detectChartTypes(msg.data, msg.sql)
      return chartTypes.includes(type)
    },
    
    /**
     * 获取列名
     */
    getColumns(data) {
      if (!data || data.length === 0) return []
      return Object.keys(data[0])
    },
    
    /**
     * 获取总计值
     */
    getTotalValue(data) {
      if (!data || data.length === 0) return null
      const columns = Object.keys(data[0])
      const numericCol = columns.find(col => {
        const sample = data[0][col]
        return typeof sample === 'number'
      })
      if (!numericCol) return null
      
      const total = data.reduce((sum, row) => sum + (row[numericCol] || 0), 0)
      return total.toLocaleString()
    },
    
    now() {
      const d = new Date()
      return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
    },
    
    addMessage(content, type, sql = '', data = null) {
      this.messages.push({
        type,
        content,
        sql,
        data,
        time: this.now()
      })
      
      // 滚动到最底部
      this.$nextTick(() => {
        const container = this.$refs.messageContainer;
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
      });
    },
    
    askExample(question) {
      this.send(question)
    },
    
    formatContent(content) {
      // 检测是否是表格格式
      if (content.includes('|') && content.includes('---')) {
        return `<pre class="data-table">${content}</pre>`
      }
      return content
    },
    
    // 处理输入框发送的消息
    handleSendMessage(question) {
      // 加入队列
      this.inputQueue.push(question)
      
      // 如果当前没有处理，开始处理
      if (!this.isProcessing) {
        this.processQueue()
      }
    },
    
    // 处理队列中的消息
    async processQueue() {
      this.isProcessing = true
      
      while (this.inputQueue.length > 0) {
        const question = this.inputQueue.shift()
        await this.send(question)
      }
      
      this.isProcessing = false
    },
    
    async send(question) {
      if (!question) return
      
      this.addMessage(question, 'user')
      this.loading = true
      
      try {
        // 如果没有 sessionId，创建新的
        if (!this.sessionId) {
          this.sessionId = 'session_' + Date.now();
        }
        
        console.log('发送问题，sessionId:', this.sessionId);
        
        // 使用 API 模块（axios 会自动添加 token 到 sign.token）
        const response = await nlQuery({
          sessionId: this.sessionId,
          question: question
        });
        
        if (response.success) {
          let content = ''
          let chartData = null
          
          if (response.data && response.data.length > 0) {
            const headers = Object.keys(response.data[0])
            content += '| ' + headers.join(' | ') + ' |\n'
            content += '| ' + headers.map(() => '---').join(' | ') + ' |\n'
            
            response.data.forEach(row => {
              const values = headers.map(h => row[h] !== null ? row[h] : '')
              content += '| ' + values.join(' | ') + ' |\n'
            })
            
            // 保存原始数据（用于图表显示）
            chartData = response.data
          } else {
            content = '暂无数据'
          }
          
          this.addMessage(content, 'bot', response.sql, chartData)
        } else {
          this.addMessage('❌ ' + (response.message || '查询失败'), 'bot')
        }
        
      } catch (error) {
        this.addMessage('❌ 网络错误：' + error.message, 'bot')
      } finally {
        this.loading = false
        // 继续处理队列中的下一条消息
        if (this.inputQueue.length > 0) {
          this.processQueue()
        }
      }
    }
  }
}
</script>

<style scoped>
.smart-query-page {
  height: 100%;
  background: #f0f2f5;
  padding: 10px;
  display: flex;
  flex-direction: column;
}

.smart-query-container {
  max-width: 100%;
  margin: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .smart-query-page {
    padding: 0;
  }
  
  .smart-query-container {
    max-width: 100%;
    margin: 0;
    border-radius: 0;
  }
}

.chat-header {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
}

.chat-header h1 {
  font-size: 18px;
  margin-bottom: 3px;
}

.chat-header p {
  font-size: 12px;
  opacity: 0.9;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f5;
}

.welcome-message {
  text-align: center;
  padding: 15px 10px;
  background: #f0f2f5;
  border-radius: 8px;
  margin-bottom: 15px;
}

.welcome-message h2 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}

.welcome-message p {
  color: #666;
  margin-bottom: 12px;
  font-size: 16px;
}

.example-questions {
  margin-top: 15px;
}

.example-questions .el-button {
  margin: 5px;
}

.message {
  margin-bottom: 20px;
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
  display: inline-block;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap;
}

/* 用户消息靠右显示 */
.message.user {
  align-items: flex-end;
}

.message.user .message-content {
  background: #409EFF;
  color: white;
  border-bottom-right-radius: 4px;
  max-width: 70%;
}

/* 机器人消息靠左显示，占满宽度 */
.message.bot {
  align-items: flex-start;
}

.message.bot .message-content {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
  width: 100%;
  max-width: 100%;
}

.message.user .message-content {
  background: #409EFF;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.bot .message-content {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
}

.sql-block {
  background: #2d3748;
  color: #e2e8f0;
  padding: 10px;
  border-radius: 6px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  margin-bottom: 10px;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
}

/* 图表区域样式 */
.chart-section {
  margin-bottom: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

.chart-view {
  padding: 20px;
}

.data-summary {
  padding: 10px 20px;
  background: #f5f7fa;
  border-top: 1px solid #e0e0e0;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

/* 表格视图样式 */
.table-view {
  margin-top: 10px;
}

.data-table {
  background: white;
  padding: 10px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  overflow-x: auto;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.chat-input {
  padding: 15px 20px;
  background: white;
  border-top: 1px solid #e0e0e0;
}

.chat-input .full-width-input {
  width: 100%;
}

.chat-input .full-width-input .el-input__wrapper {
  width: 100%;
  box-sizing: border-box;
}

.loading {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  display: inline-block;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
