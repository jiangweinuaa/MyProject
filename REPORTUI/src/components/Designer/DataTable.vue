<template>
  <div class="data-table">
    <div class="card">
      <div class="card-header">
        <div class="header-left">
          <el-icon><Grid /></el-icon>
          <span>步骤 3/9: 数据预览</span>
        </div>
        <div class="header-info">
          <el-tag size="small">📊 {{ rowCount }} 条数据</el-tag>
          <el-tag size="small" type="success">⚡ {{ execTime }}ms</el-tag>
        </div>
      </div>
      
      <div class="card-body">
        <!-- 数据表格 -->
        <el-table 
          :data="tableData" 
          style="width: 100%"
          border
          stripe
          :height="400"
          v-loading="loading"
        >
          <el-table-column
            v-for="(column, index) in columns"
            :key="index"
            :prop="column"
            :label="column"
            :sortable="true"
          />
        </el-table>
        
        <!-- 导出按钮 -->
        <div class="export-actions">
          <el-button size="small" @click="exportExcel">
            <el-icon><Download /></el-icon>
            导出 Excel
          </el-button>
          <el-button size="small" @click="exportCSV">
            <el-icon><Document /></el-icon>
            导出 CSV
          </el-button>
        </div>
        
        <!-- 操作按钮 -->
        <div class="actions">
          <el-button @click="$emit('back')">⬅️ 上一步</el-button>
          <el-button type="success" @click="$emit('confirm')">
            👍 满意，下一步
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { Grid, Download, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      rows: [],
      columns: [],
      rowCount: 0,
      execTime: 0
    })
  }
})

const emit = defineEmits(['confirm', 'back'])

const loading = ref(false)

const tableData = computed(() => {
  return props.data?.rows || []
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

function exportExcel() {
  ElMessage.info('导出 Excel 功能开发中')
}

function exportCSV() {
  ElMessage.info('导出 CSV 功能开发中')
}
</script>

<style scoped lang="scss">
.data-table {
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
    
    .header-info {
      display: flex;
      gap: 8px;
    }
  }
  
  .card-body {
    padding: 20px;
  }
  
  .export-actions {
    display: flex;
    gap: 10px;
    margin-top: 15px;
  }
  
  .actions {
    display: flex;
    justify-content: space-between;
    margin-top: 15px;
    gap: 10px;
    flex-wrap: wrap;
    
    .el-button {
      flex: 0 0 auto;
    }
  }
}

@media screen and (max-width: 768px) {
  .card-body {
    padding: 15px;
  }
  
  .actions {
    gap: 8px;
    
    .el-button {
      flex: 1 1 calc(50% - 4px);
      justify-content: center;
    }
  }
}

@media screen and (max-width: 480px) {
  .actions {
    .el-button {
      flex: 1 1 100%;
      font-size: 13px;
      padding: 10px 15px;
    }
  }
}
</style>
