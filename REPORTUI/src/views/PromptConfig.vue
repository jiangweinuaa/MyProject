<template>
  <div class="prompt-config-page">
    <div class="config-container">
      <!-- AI 版本切换 -->
      <el-card class="config-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>🤖 AI 版本切换</span>
          </div>
        </template>
        
        <div class="ai-version-switch">
          <el-radio-group v-model="aiVersion" @change="handleAiVersionChange" size="large">
            <el-radio-button label="ALI_MODEL">
              <el-icon><Platform /></el-icon>
              直接使用大模型
            </el-radio-button>
            <el-radio-button label="ALI_AGENT">
              <el-icon><Robot /></el-icon>
              使用智能体
            </el-radio-button>
          </el-radio-group>
          
          <div class="version-desc">
            <el-alert
              v-if="aiVersion === 'ALI_MODEL'"
              title="大模型模式：直接调用阿里大模型，需要手动传递表结构，Token 消耗较大"
              type="info"
              :closable="false"
              show-icon
            />
            <el-alert
              v-else
              title="智能体模式：调用阿里百炼智能体，自动从知识库检索表结构，Token 消耗较小"
              type="success"
              :closable="false"
              show-icon
            />
          </div>
        </div>
      </el-card>

      <!-- 模型管理 -->
      <el-card class="config-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>🤖 当前使用模型</span>
            <el-button type="primary" size="small" @click="showModelSwitchDialog">
              切换模型
            </el-button>
          </div>
        </template>
        
        <div class="current-model-display">
          <el-tag size="large" type="success">
            🟢 {{ currentModel || '加载中...' }}
          </el-tag>
          <span class="model-desc">点击"切换模型"可更换为其他可用模型</span>
        </div>
      </el-card>

      <!-- 角色定义 -->
      <el-card class="config-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>🤖 角色定义（CATEGORY = 'ROLE'）</span>
            <el-button type="success" size="small" @click="refreshCache" :loading="refreshing">
              🔄 立刻生效
            </el-button>
          </div>
        </template>
        <el-alert
          title="提示信息"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 15px"
        >
          角色定义使用 CATEGORY = 'ROLE' 的配置，可以配置多条，按 SORT_ORDER 排序拼接。
        </el-alert>
        <el-table :data="roleRequirements" style="width: 100%" border stripe>
          <el-table-column prop="SORT_ORDER" label="排序" width="80" />
          <el-table-column prop="REQUIREMENT" label="角色定义内容" min-width="400" />
          <el-table-column prop="ENABLED" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.ENABLED === 'Y' ? 'success' : 'danger'" size="small">
                {{ row.ENABLED === 'Y' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="editRequirement(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteRequirement(row.SORT_ORDER)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 15px; text-align: right;">
          <el-button type="primary" size="small" @click="showAddDialog('ROLE')">
            ➕ 添加角色定义
          </el-button>
        </div>
      </el-card>

      <!-- 要求列表 -->
      <el-card class="config-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>📋 Prompt 要求列表（CATEGORY != 'ROLE'）</span>
            <div>
              <el-button type="primary" size="small" @click="showAddDialog()">
                ➕ 添加要求
              </el-button>
            </div>
          </div>
        </template>

        <el-table :data="requirements" style="width: 100%" border stripe>
          <el-table-column prop="SORT_ORDER" label="排序" width="80" />
          <el-table-column prop="REQUIREMENT" label="要求内容" min-width="300" />
          <el-table-column prop="CATEGORY" label="分类" width="100">
            <template #default="{ row }">
              <el-tag size="small" :type="getCategoryType(row.CATEGORY)">
                {{ row.CATEGORY }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ENABLED" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.ENABLED === 'Y' ? 'success' : 'danger'" size="small">
                {{ row.ENABLED === 'Y' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="editRequirement(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteRequirement(row.SORT_ORDER)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="formData" label-width="100px">
        <el-form-item label="排序">
          <el-input-number v-model="formData.SORT_ORDER" :min="1" :max="9999" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="formData.REQUIREMENT"
            type="textarea"
            :rows="6"
            placeholder="请输入配置内容"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="formData.CATEGORY" placeholder="CATEGORY = 'ROLE' 为角色定义，其他为要求" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.ENABLED">
            <el-radio label="Y">启用</el-radio>
            <el-radio label="N">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRequirement">保存</el-button>
      </template>
    </el-dialog>

    <!-- 模型切换对话框 -->
    <el-dialog
      v-model="modelDialogVisible"
      title="切换模型"
      width="500px"
    >
      <el-alert
        title="提示信息"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      >
        当前 AI 版本：{{ aiVersion === 'ALI_MODEL' ? '大模型版' : '智能体版' }}
        <br>
        如需切换 AI 版本，请在上方"AI 版本切换"区域操作。
      </el-alert>
      
      <el-form label-width="100px">
        <el-form-item label="当前模型">
          <el-tag size="large" type="success">{{ currentModel }}</el-tag>
        </el-form-item>
        <el-form-item label="选择模型">
          <el-select v-model="selectedModel" placeholder="请选择模型" style="width: 100%">
            <el-option
              v-for="model in availableModels"
              :key="model.MODEL_ID"
              :label="model.MODEL_NAME"
              :value="model.MODEL_ID"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="modelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="switchModel">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Platform, Robot } from '@element-plus/icons-vue'
import request from '@/utils/request'

// AI 版本
const aiVersion = ref('ALI_MODEL')

// 当前模型
const currentModel = ref('')

// 模型对话框
const modelDialogVisible = ref(false)
const selectedModel = ref('')
const availableModels = ref([])

// 角色定义
const roleRequirements = ref([])

// 要求列表
const requirements = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref({
  SORT_ORDER: 1,
  REQUIREMENT: '',
  CATEGORY: '',
  ENABLED: 'Y'
})

// 刷新缓存
const refreshing = ref(false)

// 加载 AI 版本
const loadAiVersion = async () => {
  try {
    const res = await request({
      url: '/api/ai/version',
      method: 'get'
    })
    if (res.success) {
      aiVersion.value = res.version
    }
  } catch (error) {
    console.error('加载 AI 版本失败:', error)
  }
}

// 切换 AI 版本
const handleAiVersionChange = async (value) => {
  try {
    const res = await request({
      url: '/api/ai/switch-version',
      method: 'post',
      data: { version: value }
    })
    
    if (res.success) {
      ElMessage.success('已切换到' + (value === 'ALI_MODEL' ? '大模型模式' : '智能体模式'))
      // 重新加载当前模型
      loadCurrentModel()
    } else {
      ElMessage.error(res.message)
      // 恢复原值
      aiVersion.value = value === 'ALI_MODEL' ? 'ALI_AGENT' : 'ALI_MODEL'
    }
  } catch (error) {
    console.error('切换 AI 版本失败:', error)
    ElMessage.error('切换失败：' + error.message)
    // 恢复原值
    aiVersion.value = value === 'ALI_MODEL' ? 'ALI_AGENT' : 'ALI_MODEL'
  }
}

// 加载当前模型
const loadCurrentModel = async () => {
  try {
    const res = await request({
      url: '/api/ai/current-model',
      method: 'get'
    })
    if (res.success) {
      currentModel.value = res.model
    }
  } catch (error) {
    console.error('加载当前模型失败:', error)
  }
}

// 显示模型切换对话框
const showModelSwitchDialog = async () => {
  modelDialogVisible.value = true
  selectedModel.value = currentModel.value
  
  // 加载可用模型列表
  try {
    const res = await request({
      url: '/api/ai/models',
      method: 'get'
    })
    if (res.success) {
      availableModels.value = res.data || []
    }
  } catch (error) {
    console.error('加载模型列表失败:', error)
  }
}

// 切换模型
const switchModel = async () => {
  try {
    const res = await request({
      url: '/api/ai/switch-model',
      method: 'post',
      data: { modelId: selectedModel.value }
    })
    
    if (res.success) {
      ElMessage.success('模型切换成功')
      currentModel.value = selectedModel.value
      modelDialogVisible.value = false
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error('切换模型失败:', error)
    ElMessage.error('切换失败：' + error.message)
  }
}

// 刷新缓存
const refreshCache = async () => {
  refreshing.value = true
  try {
    const res = await request({
      url: '/api/prompt/refresh',
      method: 'post'
    })
    
    if (res.success) {
      ElMessage.success('缓存刷新成功')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error('刷新缓存失败:', error)
    ElMessage.error('刷新失败：' + error.message)
  } finally {
    refreshing.value = false
  }
}

// 加载配置
const loadRequirements = async () => {
  try {
    const res = await request({
      url: '/api/prompt/requirements',
      method: 'get'
    })
    
    if (res.success) {
      const list = res.data || []
      roleRequirements.value = list.filter(item => item.CATEGORY === 'ROLE')
      requirements.value = list.filter(item => item.CATEGORY !== 'ROLE')
    }
  } catch (error) {
    console.error('加载配置失败:', error)
  }
}

// 显示添加对话框
const showAddDialog = (category = '') => {
  dialogTitle.value = '添加配置'
  formData.value = {
    SORT_ORDER: roleRequirements.value.length + requirements.value.length + 1,
    REQUIREMENT: '',
    CATEGORY: category,
    ENABLED: 'Y'
  }
  dialogVisible.value = true
}

// 编辑配置
const editRequirement = (row) => {
  dialogTitle.value = '编辑配置'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 保存配置
const saveRequirement = async () => {
  try {
    const res = await request({
      url: '/api/prompt/requirement',
      method: 'post',
      data: formData.value
    })
    
    if (res.success) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadRequirements()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error('保存配置失败:', error)
    ElMessage.error('保存失败：' + error.message)
  }
}

// 删除配置
const deleteRequirement = async (sortOrder) => {
  try {
    await ElMessageBox.confirm('确定要删除这条配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request({
      url: '/api/prompt/requirement/' + sortOrder,
      method: 'delete'
    })
    
    if (res.success) {
      ElMessage.success('删除成功')
      loadRequirements()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除配置失败:', error)
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 获取分类类型
const getCategoryType = (category) => {
  const types = {
    'ROLE': 'success',
    'SQL': 'primary',
    'ORACLE': 'warning',
    'TABLE': 'info'
  }
  return types[category] || 'info'
}

// 初始化
onMounted(() => {
  loadAiVersion()
  loadCurrentModel()
  loadRequirements()
})
</script>

<style scoped>
.prompt-config-page {
  padding: 20px;
}

.config-container {
  max-width: 1200px;
  margin: 0 auto;
}

.config-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ai-version-switch {
  padding: 10px 0;
}

.version-desc {
  margin-top: 15px;
}

.current-model-display {
  display: flex;
  align-items: center;
  gap: 15px;
}

.model-desc {
  color: #909399;
  font-size: 14px;
}
</style>
