<template>
  <div class="sql-editor">
    <div class="card">
      <div class="card-header">
        <div class="header-left">
          <el-icon><Edit /></el-icon>
          <span>步骤 1/4: SQL 生成与数据预览</span>
        </div>
        <div class="header-status">
          <el-tag v-if="hasData && valid" size="small" type="success">✅ 语法正确</el-tag>
          <el-tag v-else-if="hasData && !valid" size="small" type="danger">❌ 语法错误</el-tag>
        </div>
      </div>
      
      <div class="card-body">
        <!-- SQL 编辑器 -->
        <div class="editor-wrapper">
          <div class="section-title">📝 SQL 语句</div>
          <el-input
            v-model="sqlCode"
            type="textarea"
            :rows="6"
            class="sql-input"
            placeholder="SELECT ..."
            @input="handleInput"
          />
        </div>
        
        <!-- 数据预览 -->
        <div v-if="hasData" class="data-preview">
          <div class="section-title">📊 数据预览（前 10 条）</div>
          <el-table 
            :data="tableData" 
            style="width: 100%"
            border
            stripe
            :height="200"
            size="small"
          >
            <el-table-column
              v-for="(column, index) in columns"
              :key="index"
              :prop="column"
              :label="column"
              :sortable="true"
              min-width="100"
            />
          </el-table>
          
          <div class="data-info">
            <el-tag size="small" type="success">📊 {{ rowCount }} 条数据</el-tag>
            <el-tag size="small" type="info">⚡ {{ execTime }}ms</el-tag>
          </div>
        </div>
        
        <!-- 图表预览 -->
        <div v-if="hasChart" class="chart-preview">
          <div class="section-title">📈 图表预览</div>
          <AiChart 
            :config="chartConfig" 
            :autoresize="true"
          />
        </div>
        
        <!-- 操作按钮 -->
        <div class="actions">
          <el-button @click="handleBack">⬅️ 上一步</el-button>
          <el-button type="primary" @click="handleRegenerate" :loading="regenerating">🔄 重新生成</el-button>
          <el-button type="success" @click="handleConfirm">✅ 确认，下一步</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import AiChart from '../SmartQuery/AiChart.vue'

const props = defineProps({
  sql: {
    type: String,
    default: ''
  },
  data: {
    type: Object,
    default: () => ({
      rows: [],
      columns: [],
      rowCount: 0,
      execTime: 0
    })
  },
  chartConfig: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update', 'execute', 'confirm', 'back'])

const sqlCode = ref(props.sql)
const valid = ref(true)
const regenerating = ref(false)

// 监听父组件传入的 SQL 变化
watch(() => props.sql, (newVal) => {
  if (newVal) {
    sqlCode.value = newVal
  }
}, { immediate: true })

const hasData = computed(() => {
  const data = props.data?.data || props.data?.rows || props.data
  return data && Array.isArray(data) && data.length > 0
})

const hasChart = computed(() => {
  return props.chartConfig && typeof props.chartConfig === 'object' && Object.keys(props.chartConfig).length > 0
})

const tableData = computed(() => {
  return props.data?.data || props.data?.rows || props.data || []
})

const columns = computed(() => {
  if (tableData.value.length === 0) return []
  return Object.keys(tableData.value[0])
})

const rowCount = computed(() => {
  return props.data?.rowCount || tableData.value.length
})

const execTime = computed(() => {
  return props.data?.execTime || 0
})

const chartConfig = computed(() => {
  return props.chartConfig
})

onMounted(() => {
  validateSQL()
  console.log('SQLEditor props:', {
    sql: props.sql,
    data: props.data,
    chartConfig: props.chartConfig,
    hasData: hasData.value,
    hasChart: hasChart.value
  })
})

function handleInput() {
  validateSQL()
}

function validateSQL() {
  const sql = sqlCode.value.trim().toUpperCase()
  valid.value = sql.startsWith('SELECT') || sql.startsWith('WITH')
}

function handleBack() {
  emit('back')
}

// 重新生成：用 SQL 调用 AI 聊天功能
async function handleRegenerate() {
  if (!sqlCode.value.trim()) {
    ElMessage.warning('SQL 不能为空')
    return
  }
  
  regenerating.value = true
  try {
    // 触发父组件的重新生成逻辑
    emit('regenerate', sqlCode.value)
  } catch (error) {
    console.error('重新生成失败:', error)
    ElMessage.error('重新生成失败：' + error.message)
  } finally {
    regenerating.value = false
  }
}

function handleExecute() {
  if (!valid.value) {
    ElMessage.error('SQL 语法有误，请修正后执行')
    return
  }
  
  emit('execute', sqlCode.value)
}

function handleConfirm() {
  emit('update', sqlCode.value)
  emit('confirm')
}
</script>

<style scoped lang="scss">
.sql-editor {
  .card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #e4e7ed;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 600;
      color: #303133;
    }
  }
  
  .card-body {
    padding: 20px;
  }
  
  .section-title {
    font-weight: 600;
    margin-bottom: 10px;
    color: #303133;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .editor-wrapper {
    margin-bottom: 20px;
    
    .sql-input {
      :deep(.el-textarea__inner) {
        font-family: 'Consolas', 'Monaco', monospace;
        font-size: 13px;
        line-height: 1.6;
        background: #f8f9fa;
        border: 1px solid #e4e7ed;
      }
    }
  }
  
  .data-preview {
    margin-bottom: 20px;
    
    .data-info {
      display: flex;
      gap: 10px;
      margin-top: 10px;
    }
  }
  
  .chart-preview {
    margin-bottom: 20px;
    
    :deep(.echarts-container) {
      height: 300px;
    }
  }
  
  .actions {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    flex-wrap: wrap;
    
    .el-button:first-child {
      margin-right: auto;
    }
    
    .el-button {
      flex: 0 0 auto;
    }
  }
}

// 移动端适配
@media screen and (max-width: 768px) {
  .card-body {
    padding: 15px;
  }
  
  .actions {
    gap: 8px;
    
    .el-button {
      flex: 1 1 calc(50% - 4px); // 移动端按钮两列显示
      justify-content: center;
    }
  }
}

@media screen and (max-width: 480px) {
  .actions {
    .el-button {
      flex: 1 1 100%; // 小屏幕单列显示
      font-size: 13px;
      padding: 10px 15px;
    }
  }
}
</style>
