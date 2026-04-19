<template>
  <div class="chat-panel">
    <!-- 消息列表 -->
    <div class="message-list" ref="messageContainer">
      <!-- 欢迎语 -->
      <div v-if="messages.length === 0" class="welcome-message">
        <div class="avatar">🤖</div>
        <div class="content">
          <p>你好！我是报表助手，想创建什么报表呢？</p>
          <div class="examples">
            <el-tag 
              v-for="(example, index) in exampleQuestions" 
              :key="index"
              size="small"
              class="example-tag"
              @click="sendExample(example)"
            >
              {{ example }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 消息列表 -->
      <div 
        v-for="(msg, index) in messages" 
        :key="index" 
        :class="['message', msg.type]"
      >
        <div class="avatar">
          {{ msg.type === 'user' ? '👤' : '🤖' }}
        </div>
        <div class="content">
          <div class="text" v-html="formatMessage(msg.content)"></div>
          
          <!-- 快捷操作按钮 -->
          <div v-if="msg.actions && msg.actions.length > 0" class="actions">
            <el-button
              v-for="(action, i) in msg.actions"
              :key="i"
              size="small"
              :type="action.type || ''"
              @click="handleAction(action)"
            >
              {{ action.label }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="message assistant">
        <div class="avatar">🤖</div>
        <div class="content">
          <div class="loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>思考中...</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区 -->
    <div class="input-area">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="2"
        placeholder="描述你的需求，例如：查看上个月各品类的销售情况..."
        @keyup.enter.ctrl="sendMessage"
        :disabled="loading"
        resize="none"
      />
      <div class="input-actions">
        <el-button 
          type="primary" 
          :loading="loading"
          @click="sendMessage"
          :disabled="!inputMessage.trim()"
        >
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { Loading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['send', 'action'])

const messageContainer = ref(null)
const inputMessage = ref('')
const loading = ref(false)

const messages = ref([])

const exampleQuestions = [
  '查看上个月各品类的销售情况',
  '本月销售排行前 10 的商品',
  '各门店的库存周转情况',
  '对比今年和去年的销售趋势'
]

// 格式化消息（支持简单 markdown）
function formatMessage(text) {
  if (!text) return ''
  
  // 换行
  text = text.replace(/\n/g, '<br>')
  
  // 加粗
  text = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
  
  // 列表
  text = text.replace(/^[•\-]\s*(.*)/gm, '<li>$1</li>')
  text = text.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>')
  
  return text
}

// 发送消息
async function sendMessage() {
  const message = inputMessage.value.trim()
  if (!message || loading.value) return
  
  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: message
  })
  
  inputMessage.value = ''
  await scrollToBottom()
  
  // 发送
  loading.value = true
  try {
    emit('send', message)
  } catch (error) {
    ElMessage.error('发送失败：' + error.message)
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

// 发送示例问题
function sendExample(example) {
  inputMessage.value = example
  sendMessage()
}

// 处理快捷操作
function handleAction(action) {
  emit('action', action)
}

// 滚动到底部
async function scrollToBottom() {
  await nextTick()
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

// 添加助手消息（供外部调用）
function addAssistantMessage(content, actions = []) {
  messages.value.push({
    type: 'assistant',
    content,
    actions
  })
  scrollToBottom()
}

// 暴露方法给父组件
defineExpose({
  addAssistantMessage
})
</script>

<style scoped lang="scss">
.chat-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: white;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
  min-height: 0; // 关键：允许 flex 子项缩小
}

.welcome-message {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  
  .avatar {
    font-size: 32px;
    flex-shrink: 0;
  }
  
  .content {
    p {
      margin: 0 0 15px 0;
      color: #606266;
      line-height: 1.6;
    }
    
    .examples {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      
      .example-tag {
        cursor: pointer;
        transition: all 0.2s;
        
        &:hover {
          background: #409EFF;
          color: white;
        }
      }
    }
  }
}

.message {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  
  &.user {
    flex-direction: row-reverse;
    
    .content {
      background: #409EFF;
      color: white;
      border-radius: 12px 12px 0 12px;
    }
  }
  
  &.assistant {
    .content {
      background: white;
      color: #303133;
      border-radius: 12px 12px 12px 0;
      border: 1px solid #e4e7ed;
    }
  }
  
  .avatar {
    font-size: 32px;
    flex-shrink: 0;
  }
  
  .content {
    max-width: 85%;
    padding: 12px 16px;
    line-height: 1.6;
    
    .text {
      word-break: break-word;
    }
    
    .actions {
      margin-top: 10px;
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }
  }
}

.loading {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.input-area {
  border-top: 1px solid #e4e7ed;
  padding: 8px 12px;
  background: white;
  flex-shrink: 0;
  
  :deep(.el-textarea) {
    height: auto !important;
  }
  
  :deep(.el-textarea__inner) {
    resize: none;
    border: 1px solid #dcdfe6;
    padding: 6px 10px;
    font-size: 14px;
    line-height: 1.5;
    height: 40px !important; // 固定高度
    min-height: 40px !important;
    max-height: 40px !important;
  }
  
  .input-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 6px;
  }
}
</style>
