<template>
  <div class="retrain-page">
    <div class="container">
      <h1>🎯 商品批量训练</h1>
      <p class="subtitle">重新计算所有训练库商品的特征向量，更新特征数据库</p>
      
      <!-- 统计卡片 -->
      <div class="stats">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalSamples }}</div>
          <div class="stat-label">训练商品总数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.extractedFeatures }}</div>
          <div class="stat-label">已提取特征</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.pendingFeatures }}</div>
          <div class="stat-label">待提取特征</div>
        </div>
      </div>
      
      <!-- 操作区域 -->
      <div class="action-area">
        <h2 style="margin-bottom: 20px; color: #333;">开始批量训练</h2>
        <p style="color: #666; margin-bottom: 20px;">
          系统将从 OSS 下载所有训练图片，使用当前配置的算法重新提取特征向量
        </p>
        <el-button 
          type="primary" 
          size="large" 
          @click="startTraining" 
          :disabled="isTraining"
          :loading="isTraining"
        >
          🚀 开始全部训练
        </el-button>
        <el-button 
          type="danger" 
          size="large" 
          @click="cancelTraining" 
          v-if="isTraining"
        >
          ⏹️ 取消训练
        </el-button>
      </div>
      
      <!-- 进度显示 -->
      <div class="progress-section" v-if="showProgress">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
          <h2 style="color: #333;">训练进度</h2>
          <el-tag :type="statusType" size="large" effect="dark">
            {{ statusText }}
          </el-tag>
        </div>
        
        <el-progress 
          :percentage="progress" 
          :stroke-width="30"
          :text-inside="true"
          class="progress-bar"
        />
        
        <div class="progress-info">
          <div class="progress-item">
            <div class="progress-item-label">总图片数</div>
            <div class="progress-item-value">{{ progressData.total }}</div>
          </div>
          <div class="progress-item">
            <div class="progress-item-label">已处理</div>
            <div class="progress-item-value">{{ progressData.processed }}</div>
          </div>
          <div class="progress-item">
            <div class="progress-item-label" style="color: #2ed573;">成功</div>
            <div class="progress-item-value success">{{ progressData.success }}</div>
          </div>
          <div class="progress-item">
            <div class="progress-item-label" style="color: #ff4757;">失败</div>
            <div class="progress-item-value failed">{{ progressData.failed }}</div>
          </div>
        </div>
        
        <div style="margin-top: 20px; color: #666; font-size: 14px;">
          <strong>耗时：</strong> <span>{{ elapsedTime }}</span> 秒
        </div>
      </div>
      
      <!-- 日志区域 -->
      <div class="log-section" v-if="showProgress">
        <h2 style="color: #333; margin-bottom: 15px;">训练日志</h2>
        <div class="log-container">
          <div 
            v-for="(log, index) in logs" 
            :key="index"
            class="log-entry"
            :class="log.type"
          >
            <span class="log-time">[{{ log.time }}]</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 统计数据
const stats = reactive({
  totalSamples: '-',
  extractedFeatures: '-',
  pendingFeatures: '0'
})

// 训练状态
const isTraining = ref(false)
const taskId = ref(null)
const pollingInterval = ref(null)
const startTime = ref(null)
const showProgress = ref(false)

// 进度数据
const progress = ref(0)
const progressData = reactive({
  total: 0,
  processed: 0,
  success: 0,
  failed: 0
})

// 状态
const status = ref('')

// 日志
const logs = ref([])

// 计算属性
const statusText = computed(() => {
  const map = {
    'RUNNING': '运行中',
    'COMPLETED': '已完成',
    'FAILED': '失败',
    'CANCELLED': '已取消'
  }
  return map[status.value] || '未知'
})

const statusType = computed(() => {
  const map = {
    'RUNNING': 'warning',
    'COMPLETED': 'success',
    'FAILED': 'danger',
    'CANCELLED': 'info'
  }
  return map[status.value] || 'info'
})

const elapsedTime = computed(() => {
  if (!startTime.value) return 0
  return Math.floor((Date.now() - startTime.value) / 1000)
})

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await fetch('http://47.100.138.89:8110/api/training/stats')
    const data = await response.json()
    
    stats.totalSamples = data.productCount !== undefined ? data.productCount : '-'
    stats.extractedFeatures = data.productCount !== undefined ? data.productCount : '-'
    stats.pendingFeatures = '0'
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 添加日志
const addLog = (message, type = 'info') => {
  logs.value.push({
    time: new Date().toLocaleTimeString(),
    message,
    type
  })
  // 保持日志数量在 100 条以内
  if (logs.value.length > 100) {
    logs.value.shift()
  }
}

// 开始训练
const startTraining = async () => {
  try {
    await ElMessageBox.confirm('确定要开始批量训练吗？这可能需要较长时间。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  
  try {
    const response = await fetch('http://47.100.138.89:8110/api/training/retrain-all', {
      method: 'POST'
    })
    const data = await response.json()
    
    if (data.success) {
      taskId.value = data.taskId
      isTraining.value = true
      showProgress.value = true
      startTime.value = Date.now()
      progress.value = 0
      progressData.total = 0
      progressData.processed = 0
      progressData.success = 0
      progressData.failed = 0
      logs.value = []
      
      addLog('🚀 训练任务已启动，任务 ID: ' + taskId.value, 'info')
      
      pollingInterval.value = setInterval(pollProgress, 2000)
    } else {
      ElMessage.error('启动训练失败：' + data.message)
    }
  } catch (error) {
    console.error('启动训练失败:', error)
    ElMessage.error('启动训练失败：' + error.message)
  }
}

// 轮询进度
const pollProgress = async () => {
  if (!taskId.value) return
  
  try {
    const response = await fetch(`http://47.100.138.89:8110/api/training/retrain-progress/${taskId.value}`)
    const data = await response.json()
    
    if (data.success) {
      updateProgress(data)
      
      if (data.status === 'COMPLETED' || data.status === 'FAILED' || data.status === 'CANCELLED') {
        finishTraining(data)
      }
    }
  } catch (error) {
    console.error('轮询进度失败:', error)
  }
}

// 更新进度显示
const updateProgress = (data) => {
  progress.value = data.progress || 0
  progressData.total = data.total || 0
  progressData.processed = data.processed || 0
  progressData.success = data.trainSuccess || 0
  progressData.failed = data.failed || 0
  status.value = data.status
}

// 训练完成
const finishTraining = (data) => {
  clearInterval(pollingInterval.value)
  pollingInterval.value = null
  isTraining.value = false
  
  if (data.status === 'COMPLETED') {
    addLog('✅ 训练完成！成功：' + data.trainSuccess + ', 失败：' + data.failed, 'success')
    ElMessage.success(`训练完成！成功：${data.trainSuccess}, 失败：${data.failed}, 耗时：${(data.duration / 1000).toFixed(1)}秒`)
  } else if (data.status === 'FAILED') {
    addLog('❌ 训练失败', 'error')
    ElMessage.error('训练失败！')
  } else if (data.status === 'CANCELLED') {
    addLog('⏹️ 训练已取消', 'info')
    ElMessage.info('训练已取消！')
  }
  
  // 重新加载统计数据
  setTimeout(loadStats, 2000)
}

// 取消训练
const cancelTraining = async () => {
  try {
    await ElMessageBox.confirm('确定要取消当前训练吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  
  try {
    const response = await fetch(`http://47.100.138.89:8110/api/training/retrain-cancel/${taskId.value}`, {
      method: 'POST'
    })
    const data = await response.json()
    
    if (data.success) {
      addLog('⏹️ 正在取消训练...', 'info')
    } else {
      ElMessage.error('取消失败：' + data.message)
    }
  } catch (error) {
    console.error('取消训练失败:', error)
    ElMessage.error('取消训练失败：' + error.message)
  }
}

// 生命周期
onMounted(() => {
  loadStats()
})

onBeforeUnmount(() => {
  if (pollingInterval.value) {
    clearInterval(pollingInterval.value)
    pollingInterval.value = null
  }
})
</script>

<style scoped>
.retrain-page {
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

h1 {
  color: #333;
  margin-bottom: 10px;
  font-size: 32px;
}

.subtitle {
  color: #666;
  margin-bottom: 30px;
  font-size: 14px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 15px;
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.action-area {
  background: #f8f9fa;
  padding: 30px;
  border-radius: 15px;
  margin-bottom: 30px;
  text-align: center;
}

.progress-section {
  background: #f8f9fa;
  padding: 30px;
  border-radius: 15px;
  margin-bottom: 30px;
}

.progress-bar {
  margin-bottom: 20px;
}

.progress-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-top: 20px;
}

.progress-item {
  background: white;
  padding: 15px;
  border-radius: 10px;
  text-align: center;
}

.progress-item-label {
  color: #666;
  font-size: 12px;
  margin-bottom: 5px;
}

.progress-item-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.progress-item-value.success {
  color: #2ed573;
}

.progress-item-value.failed {
  color: #ff4757;
}

.log-section {
  background: #2d3436;
  color: #dfe6e9;
  padding: 20px;
  border-radius: 15px;
}

.log-container {
  max-height: 400px;
  overflow-y: auto;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.log-entry {
  margin-bottom: 5px;
  display: flex;
  gap: 10px;
}

.log-time {
  color: #747d8c;
  flex-shrink: 0;
}

.log-message {
  flex: 1;
}

.log-entry.success {
  color: #2ed573;
}

.log-entry.error {
  color: #ff4757;
}

.log-entry.info {
  color: #70a1ff;
}
</style>
