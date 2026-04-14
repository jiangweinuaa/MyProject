<template>
  <div class="smart-query-page">
    <div class="smart-query-container">
      <div class="chat-header">
        <h1>🤖 智问</h1>
        <p>用自然语言查询数据</p>
      </div>
      
      <div class="chat-messages" ref="messageContainer">
        <div v-if="messages.length === 0" class="welcome-message">
          <h2>你好！我是智问助手 👋</h2>
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
        
        <div 
          v-for="(msg, index) in messages" 
          :key="index" 
          :class="['message', msg.type]"
        >
          <div class="message-content">
            <pre v-if="msg.sql" class="sql-block">{{ msg.sql }}</pre>
            
            <!-- 图表区域（自动显示单个图表） -->
            <div class="chart-section" v-if="shouldShowChart(msg)">
              <!-- 指标卡 -->
              <MetricCard 
                v-if="getChartType(msg) === 'metric'" 
                :data="msg.data" 
              />
              
              <!-- 折线图 -->
              <LineChart 
                v-else-if="getChartType(msg) === 'line'" 
                :data="extractChartData(msg)"
                :height="300"
              />
              
              <!-- 柱状图 -->
              <BarChart 
                v-else-if="getChartType(msg) === 'bar'" 
                :data="extractChartData(msg)"
                :height="300"
              />
              
              <!-- 饼图 -->
              <PieChart 
                v-else-if="getChartType(msg) === 'pie'" 
                :data="extractChartData(msg, 'ratio')"
                :height="300"
              />
              
              <!-- 条形图 -->
              <HorizontalBarChart 
                v-else-if="getChartType(msg) === 'horizontalBar'" 
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
            
            <div class="text-content" v-html="formatContent(msg.content)"></div>
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
      
      <div class="chat-input">
        <el-input
          v-model="question"
          placeholder="问我：今天销售额是多少？"
          @keyup.enter="send"
          :disabled="loading"
          size="large"
        >
          <template #append>
            <el-button type="primary" @click="send" :loading="loading" size="large">
              发送
            </el-button>
          </template>
        </el-input>
      </div>
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

export default {
  name: 'SmartQuery',
  components: {
    MetricCard,
    BarChart,
    LineChart,
    PieChart,
    HorizontalBarChart
  },
  data() {
    return {
      question: '',
      loading: false,
      messages: [],
      examples: [
        '今天销售额是多少？',
        '本月销售额是多少？',
        '哪个商品卖得最好？'
      ]
    }
  },
  methods: {
    /**
     * 自动检测图表类型（返回单个图表类型）
     */
    detectChartType(data, sql) {
      if (!data || data.length === 0) {
        console.log('图表检测：无数据')
        return 'table'
      }
      
      const sqlUpper = sql.toUpperCase()
      const rowCount = data.length
      const columns = Object.keys(data[0])
      const columnCount = columns.length
      
      console.log('图表检测参数：rowCount=', rowCount, 'columnCount=', columnCount, 'columns=', columns)
      
      // 1. 单行单列 → 指标卡
      if (rowCount === 1 && columnCount === 1) {
        console.log('图表检测：指标卡')
        return 'metric'
      }
      
      // 2. 包含日期字段 → 折线图
      const dateKeywords = ['BDATE', 'DATE', 'TO_CHAR', 'TRUNC', 'TIME']
      const hasDateField = columns.some(col => 
        dateKeywords.some(keyword => col.toUpperCase().includes(keyword))
      )
      
      if (hasDateField && rowCount > 1) {
        console.log('图表检测：折线图（日期字段）')
        return 'line'
      }
      
      // 3. 有 GROUP BY + 2-4 列 → 柱状图/饼图（支持占比列）
      const hasGroupBy = sqlUpper.includes('GROUP BY')
      console.log('图表检测：hasGroupBy=', hasGroupBy, 'columnCount=', columnCount)
      if (hasGroupBy && columnCount >= 2 && columnCount <= 4) {
        // 检查是否有占比/百分比列
        const hasRatioColumn = columns.some(col => {
          const colUpper = col.toUpperCase()
          return colUpper.includes('占比') || colUpper.includes('PERCENT') || colUpper.includes('RATIO') || colUpper.includes('RATE')
        })
        
        if (hasRatioColumn && rowCount <= 6) {
          console.log('图表检测：饼图（占比分析）')
          return 'pie'
        }
        
        if (rowCount <= 20) {
          console.log('图表检测：柱状图（金额对比）')
          return 'bar'
        }
      }
      
      // 4. 有 ORDER BY + ROWNUM → 排行条形图
      const hasOrderBy = sqlUpper.includes('ORDER BY')
      const hasRownum = sqlUpper.includes('ROWNUM')
      if (hasOrderBy && hasRownum && columnCount >= 2) {
        console.log('图表检测：条形图（排行）')
        return 'horizontalBar'
      }
      
      // 5. 2 列数据 → 柱状图
      if (columnCount === 2 && rowCount <= 15) {
        console.log('图表检测：柱状图（2 列数据）')
        return 'bar'
      }
      
      // 6. 默认表格
      console.log('图表检测：默认表格')
      return 'table'
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
      
      // 查找分类列（字符串列，排除 ID/代码列）
      const categoryColumn = columns.find(col => {
        const sample = data[0][col]
        const colUpper = col.toUpperCase()
        // 跳过 ID、代码等列
        if (colUpper.includes('_ID') || colUpper.includes('CODE') || colUpper.includes('NO')) {
          return false
        }
        return typeof sample === 'string'
      })
      
      // 查找数值列
      let numericColumn = null
      
      if (type === 'ratio') {
        // 饼图：找占比/百分比列
        numericColumn = columns.find(col => {
          const colUpper = col.toUpperCase()
          return colUpper.includes('占比') || colUpper.includes('PERCENT') || colUpper.includes('RATIO') || colUpper.includes('RATE')
        })
      }
      
      // 如果没找到占比列或不是饼图，找金额/销量列
      if (!numericColumn) {
        numericColumn = columns.find(col => {
          const sample = data[0][col]
          const colUpper = col.toUpperCase()
          // 跳过占比、百分比、比率列（除非是饼图）
          if (type !== 'ratio' && (colUpper.includes('占比') || colUpper.includes('PERCENT') || colUpper.includes('RATIO') || colUpper.includes('RATE'))) {
            return false
          }
          // 优先找金额、销量列
          if (colUpper.includes('AMT') || colUpper.includes('SALE') || colUpper.includes('QTY') || colUpper.includes('金额') || colUpper.includes('销量')) {
            return typeof sample === 'number' || !isNaN(Number(sample))
          }
          return typeof sample === 'number' || !isNaN(Number(sample))
        })
      }
      
      console.log('提取图表数据 - categoryColumn:', categoryColumn, 'numericColumn:', numericColumn)
      
      // 确定标题
      let title = '查询结果'
      if (sqlUpper.includes('销售额')) title = '销售额统计'
      else if (sqlUpper.includes('销量')) title = '销量统计'
      else if (sqlUpper.includes('库存')) title = '库存统计'
      else if (sqlUpper.includes('品类')) title = '品类分析'
      
      return {
        xAxis: categoryColumn,
        series: numericColumn,
        title: title,
        data: data
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
      const chartType = this.detectChartType(msg.data, msg.sql)
      console.log('图表检测结果：', chartType, 'data:', msg.data, 'sql:', msg.sql)
      return chartType !== 'table'
    },
    
    /**
     * 获取图表类型
     */
    getChartType(msg) {
      if (!msg.data || msg.data.length === 0 || !msg.sql) return 'table'
      return this.detectChartType(msg.data, msg.sql)
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
      this.$nextTick(() => {
        if (this.$refs.messageContainer) {
          this.$refs.messageContainer.scrollTop = this.$refs.messageContainer.scrollHeight
        }
      })
    },
    
    askExample(question) {
      this.question = question
      this.send()
    },
    
    formatContent(content) {
      // 检测是否是表格格式
      if (content.includes('|') && content.includes('---')) {
        return `<pre class="data-table">${content}</pre>`
      }
      return content
    },
    
    async send() {
      if (!this.question.trim() || this.loading) return
      
      const userQuestion = this.question.trim()
      
      this.addMessage(userQuestion, 'user')
      this.question = ''
      this.loading = true
      
      try {
        const params = new URLSearchParams()
        params.append('question', userQuestion)
        
        const response = await fetch('http://47.100.138.89:8110/api/nl-query/query', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: params
        })
        
        const data = await response.json()
        
        if (data.success) {
          let content = ''
          let chartData = null
          
          if (data.data && data.data.length > 0) {
            const headers = Object.keys(data.data[0])
            content += '| ' + headers.join(' | ') + ' |\n'
            content += '| ' + headers.map(() => '---').join(' | ') + ' |\n'
            
            data.data.forEach(row => {
              const values = headers.map(h => row[h] !== null ? row[h] : '')
              content += '| ' + values.join(' | ') + ' |\n'
            })
            
            // 保存原始数据（用于图表显示）
            chartData = data.data
          } else {
            content = '暂无数据'
          }
          
          this.addMessage(content, 'bot', data.sql, chartData)
        } else {
          this.addMessage('❌ ' + (data.message || '查询失败'), 'bot')
        }
        
      } catch (error) {
        this.addMessage('❌ 网络错误：' + error.message, 'bot')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.smart-query-page {
  height: calc(100vh - 84px);
  background: #f0f2f5;
  padding: 20px;
}

.smart-query-container {
  max-width: 900px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  overflow: hidden;
}

.chat-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
}

.chat-header h1 {
  font-size: 24px;
  margin-bottom: 5px;
}

.chat-header p {
  font-size: 14px;
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
  padding: 60px 20px;
  color: #666;
}

.welcome-message h2 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #333;
}

.example-questions {
  margin-top: 30px;
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
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  word-wrap: break-word;
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
  padding: 20px;
  background: white;
  border-top: 1px solid #e0e0e0;
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
