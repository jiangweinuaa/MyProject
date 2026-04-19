<template>
  <div class="save-form">
    <div class="card">
      <div class="card-header">
        <el-icon><DocumentChecked /></el-icon>
        <span>步骤 4/4: 保存报表</span>
      </div>
      
      <div class="card-body">
        <el-form 
          :model="form" 
          label-width="100px"
          label-position="top"
          size="large"
        >
          <!-- 报表名称 -->
          <el-form-item 
            label="📊 报表名称" 
            required
            :rules="[{ required: true, message: '请输入报表名称', trigger: 'blur' }]"
          >
            <el-input 
              v-model="form.name" 
              placeholder="例如：品类销售分析报表"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
          
          <!-- 报表描述 -->
          <el-form-item label="📝 报表描述">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="简要描述报表用途和内容"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          
          <!-- 访问权限 -->
          <el-form-item label="🔒 访问权限">
            <el-radio-group v-model="form.permission" size="large">
              <el-radio value="private">
                <el-icon><Lock /></el-icon>
                私有（仅自己可见）
              </el-radio>
              <el-radio value="public">
                <el-icon><View /></el-icon>
                公开（所有人可见）
              </el-radio>
              <el-radio value="department">
                <el-icon><User /></el-icon>
                部门内可见
              </el-radio>
            </el-radio-group>
          </el-form-item>
          
          <!-- 报表分类 -->
          <el-form-item label="📁 报表分类">
            <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
              <el-option label="销售报表" value="sales" />
              <el-option label="库存报表" value="stock" />
              <el-option label="财务报表" value="finance" />
              <el-option label="自定义报表" value="custom" />
            </el-select>
          </el-form-item>
          
          <!-- 选择菜单目录 -->
          <el-form-item label="📂 选择菜单目录">
            <el-select v-model="form.menuId" placeholder="请选择菜单目录" style="width: 100%">
              <el-option label="销售报表" value="MENU_SALES" />
              <el-option label="库存报表" value="MENU_STOCK" />
              <el-option label="自定义报表" value="MENU_CUSTOM" />
            </el-select>
          </el-form-item>
        </el-form>
        
        <!-- 保存选项 -->
        <div class="save-options">
          <el-checkbox v-model="saveAsDraft">保存为草稿</el-checkbox>
          <el-checkbox v-model="sendNotification">完成后通知我</el-checkbox>
        </div>
        
        <!-- 操作按钮 -->
        <div class="actions">
          <el-button @click="$emit('back')">⬅️ 上一步</el-button>
          <el-button @click="handleSave" :loading="saving" :disabled="!form.name.trim()">
            💾 保存草稿
          </el-button>
          <el-button type="primary" @click="handlePublishClick" :loading="publishing" :disabled="!form.name.trim()">
            🚀 保存并发布
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { DocumentChecked, Lock, View, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  report: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update', 'save', 'publish', 'back'])

const saving = ref(false)
const publishing = ref(false)

const form = reactive({
  name: props.report?.name || '',
  description: props.report?.description || '',
  permission: 'private',
  category: 'sales',
  refreshRate: 'manual',
  menuId: props.report?.menuId || ''
})

// 保存草稿
function handleSave() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入报表名称')
    return
  }
  // 触发父组件的保存逻辑
  emit('save', { ...form, status: 100 })
}

// 发布
function handlePublishClick() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入报表名称')
    return
  }
  // 触发父组件的发布逻辑
  emit('publish', { ...form, status: 200 })
}

// 暴露表单数据和方法给父组件
defineExpose({
  form,
  handleSave,
  handlePublishClick
})
</script>

<style scoped lang="scss">
.save-form {
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
  
  .save-options {
    margin: 20px 0;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 8px;
    display: flex;
    gap: 20px;
  }
  
  .actions {
    display: flex;
    justify-content: flex-end;
    gap: 15px;
    margin-top: 20px;
    
    .el-button {
      min-width: 120px;
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
