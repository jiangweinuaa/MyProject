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
export default {
  name: 'SmartQuery',
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
    now() {
      const d = new Date()
      return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
    },
    
    addMessage(content, type, sql = '') {
      this.messages.push({
        type,
        content,
        sql,
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
          
          if (data.data && data.data.length > 0) {
            const headers = Object.keys(data.data[0])
            content += '| ' + headers.join(' | ') + ' |\n'
            content += '| ' + headers.map(() => '---').join(' | ') + ' |\n'
            
            data.data.forEach(row => {
              const values = headers.map(h => row[h] !== null ? row[h] : '')
              content += '| ' + values.join(' | ') + ' |\n'
            })
          } else {
            content = '暂无数据'
          }
          
          this.addMessage(content, 'bot', data.sql)
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
  margin-bottom: 8px;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
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
