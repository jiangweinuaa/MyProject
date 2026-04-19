<template>
  <div class="filter-form">
    <div class="card">
      <div class="card-header">
        <el-icon><Filter /></el-icon>
        <span>步骤 3/4: 添加筛选条件</span>
      </div>
      
      <div class="card-body">
        <!-- AI 识别的变量 -->
        <div v-if="variables && variables.length > 0" class="variables-section">
          <div class="section-title">🤖 AI 已识别的变量</div>
          
          <div v-for="(variable, index) in variables" :key="index" class="variable-item">
            <el-form :inline="true" size="default">
              <el-form-item :label="variable.label">
                <!-- 日期类型 -->
                <el-date-picker
                  v-if="variable.type === 'date'"
                  v-model="variable.value"
                  type="date"
                  placeholder="选择日期"
                  value-format="YYYY-MM-DD"
                />
                
                <!-- 数字类型 -->
                <el-input-number
                  v-else-if="variable.type === 'number'"
                  v-model="variable.value"
                  :min="variable.min"
                  :max="variable.max"
                />
                
                <!-- 字符串类型 -->
                <el-select
                  v-else-if="variable.type === 'select'"
                  v-model="variable.value"
                  placeholder="请选择"
                  clearable
                >
                  <el-option
                    v-for="option in variable.options"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
                
                <!-- 文本类型 -->
                <el-input
                  v-else
                  v-model="variable.value"
                  :placeholder="'请输入' + variable.label"
                  clearable
                />
              </el-form-item>
              
              <el-form-item>
                <el-checkbox v-model="variable.required">必填</el-checkbox>
              </el-form-item>
              
              <el-form-item>
                <el-button type="danger" size="small" @click="removeVariable(index)">
                  删除
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
        
        <!-- 空状态 -->
        <el-empty v-else description="暂无识别的变量" />
        
        <!-- 添加变量 -->
        <div class="add-variable">
          <el-button type="primary" plain @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加筛选条件
          </el-button>
          <el-button type="info" plain @click="autoDetect">
            <el-icon><MagicStick /></el-icon>
            AI 自动识别
          </el-button>
        </div>
        
        <!-- 操作按钮 -->
        <div class="actions">
          <el-button @click="$emit('back')">⬅️ 上一步</el-button>
          <el-button @click="handleSkip">⏭️ 跳过</el-button>
          <el-button type="success" @click="handleConfirm">✅ 确认</el-button>
        </div>
      </div>
    </div>
    
    <!-- 添加变量对话框 -->
    <el-dialog v-model="showAddDialog" title="添加筛选条件" width="500px">
      <el-form :model="newVariable" label-width="80px">
        <el-form-item label="字段名">
          <el-input v-model="newVariable.name" placeholder="例如：shopId" />
        </el-form-item>
        <el-form-item label="显示标签">
          <el-input v-model="newVariable.label" placeholder="例如：门店" />
        </el-form-item>
        <el-form-item label="字段类型">
          <el-select v-model="newVariable.type" placeholder="请选择">
            <el-option label="日期" value="date" />
            <el-option label="数字" value="number" />
            <el-option label="下拉选择" value="select" />
            <el-option label="文本" value="text" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认值">
          <el-input v-model="newVariable.defaultValue" placeholder="可选" />
        </el-form-item>
        <el-form-item label="是否必填">
          <el-switch v-model="newVariable.required" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addVariable">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Filter, Plus, MagicStick } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  variables: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update'])

const showAddDialog = ref(false)

const newVariable = reactive({
  name: '',
  label: '',
  type: 'text',
  defaultValue: '',
  required: false
})

function removeVariable(index) {
  const updated = [...props.variables]
  updated.splice(index, 1)
  emit('update', updated)
}

function addVariable() {
  if (!newVariable.name || !newVariable.label) {
    ElMessage.warning('请填写字段名和显示标签')
    return
  }
  
  const updated = [
    ...props.variables,
    {
      ...newVariable,
      value: newVariable.defaultValue,
      options: newVariable.type === 'select' ? [] : undefined
    }
  ]
  
  emit('update', updated)
  showAddDialog.value = false
  
  // 重置表单
  Object.assign(newVariable, {
    name: '',
    label: '',
    type: 'text',
    defaultValue: '',
    required: false
  })
}

function autoDetect() {
  ElMessage.info('AI 自动识别功能开发中')
}

function handleSkip() {
  emit('update', props.variables)
}

function handleConfirm() {
  emit('update', props.variables)
}
</script>

<style scoped lang="scss">
.filter-form {
  .card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 15px 20px;
    border-bottom: 1px solid #e4e7ed;
    font-weight: 600;
    color: #303133;
  }
  
  .card-body {
    padding: 20px;
  }
  
  .variables-section {
    margin-bottom: 20px;
    
    .section-title {
      font-weight: 600;
      margin-bottom: 15px;
      color: #606266;
    }
    
    .variable-item {
      background: #f5f7fa;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 12px;
    }
  }
  
  .add-variable {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}

// 移动端适配
@media screen and (max-width: 768px) {
  .card-body {
    padding: 15px;
  }
  
  .actions {
    gap: 8px;
    flex-wrap: wrap;
    
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
