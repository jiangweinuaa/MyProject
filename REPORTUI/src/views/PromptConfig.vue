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
              <el-icon><Cpu /></el-icon>
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
      <el-card v-if="aiVersion === 'ALI_MODEL'" class="config-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>🤖 当前使用模型</span>
            <div>
              <el-button type="success" size="small" @click="syncModels" :loading="syncing">
                🔄 同步阿里云模型
              </el-button>
              <el-button type="primary" size="small" @click="showModelSwitchDialog">
                切换模型
              </el-button>
            </div>
          </div>
        </template>
        
        <div class="current-model-display">
          <el-tag size="large" type="success">
            🟢 {{ currentModel || '加载中...' }}
          </el-tag>
          <span class="model-desc">点击"同步阿里云模型"从阿里云获取最新模型列表</span>
        </div>
      </el-card>

      <!-- 角色定义 -->
      <el-card v-if="aiVersion === 'ALI_MODEL'" class="config-card" shadow="hover">
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
      <el-card v-if="aiVersion === 'ALI_MODEL'" class="config-card" shadow="hover">
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

      <!-- 给大模型的表结构配置 -->
      <el-card v-if="aiVersion === 'ALI_MODEL'" class="config-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>📊 给大模型的表结构（AI_TABLE_FILTER）</span>
            <div>
              <el-button type="primary" size="small" @click="showAddTableDialog()">
                ➕ 添加表
              </el-button>
            </div>
          </div>
        </template>

        <el-alert
          title="说明"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 15px"
        >
          配置哪些表的结构会发送给大模型，只有启用的表才会包含在 Prompt 中。
        </el-alert>

        <el-table :data="tableFilter" style="width: 100%" border stripe>
          <el-table-column prop="TABLE_NAME" label="表名" width="200" />
          <el-table-column prop="TABLE_COMMENT" label="表注释" min-width="300" />
          <el-table-column prop="ENABLED" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.ENABLED === 'Y' ? 'success' : 'danger'" size="small">
                {{ row.ENABLED === 'Y' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="SORT_ORDER" label="排序" width="80" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="editTableFilter(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteTableFilter(row.TABLE_NAME)">删除</el-button>
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
          <el-input-number v-model="formData.sortOrder" :min="1" :max="9999" />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input
            v-model="formData.requirement"
            type="textarea"
            :rows="6"
            placeholder="请输入配置内容"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="formData.category" placeholder="CATEGORY = 'ROLE' 为角色定义，其他为要求" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.enabled">
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
      width="90%"
      :fullscreen="false"
      class="model-switch-dialog"
    >
      <el-table :data="modelList" style="width: 100%" border stripe size="small">
        <el-table-column prop="MODEL_ID" label="模型" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.STATUS === '100' || row.STATUS === 100 ? 'success' : 'danger'" size="small">
              {{ row.STATUS === '100' || row.STATUS === 100 ? '可用' : '耗尽' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.STATUS === '100' || row.STATUS === 100"
              type="primary" 
              size="small"
              :loading="switchingModel && switchingModelId === row.MODEL_ID"
              @click="switchToModel(row)"
            >
              {{ currentModel === row.MODEL_ID ? '使用中' : '切换' }}
            </el-button>
            <el-button 
              v-else
              type="danger" 
              size="small"
              @click="showRenewDialog(row)"
            >
              耗尽
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 模型续费确认对话框 -->
    <el-dialog
      v-model="renewDialogVisible"
      title="模型已耗尽"
      width="400px"
      :close-on-click-modal="false"
    >
      <div style="text-align: center; padding: 20px;">
        <p style="font-size: 16px; margin-bottom: 20px;">
          模型 <strong style="color: #F56C6C;">{{ renewModelId }}</strong> 的 Token 已用完
        </p>
        <p style="color: #909399; font-size: 14px;">
          如果您已续费，请点击"我已续费"按钮更新状态
        </p>
      </div>
      <template #footer>
        <el-button @click="renewDialogVisible = false">关闭</el-button>
        <el-button type="success" @click="confirmRenew">我已续费</el-button>
      </template>
    </el-dialog>

    <!-- 添加/编辑表结构对话框 -->
    <el-dialog
      v-model="tableDialogVisible"
      :title="isEditTable ? '编辑表结构' : '添加表结构'"
      width="600px"
    >
      <el-form :model="tableFormData" label-width="100px">
        <el-form-item label="表名" required>
          <el-input v-model="tableFormData.tableName" placeholder="请输入表名（大写）" :disabled="isEditTable" />
        </el-form-item>
        <el-form-item label="表注释" required>
          <el-input v-model="tableFormData.tableComment" type="textarea" :rows="3" placeholder="请输入表注释" />
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="tableFormData.enabled" active-value="Y" inactive-value="N" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="tableFormData.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tableDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTableFilter" :loading="savingTable">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Platform, Cpu } from '@element-plus/icons-vue'
import request from '@/api/request'

// AI 版本
const aiVersion = ref('ALI_MODEL')

// 当前模型
const currentModel = ref('')

// 模型管理
const modelList = ref([])
const modelDialogVisible = ref(false)
const switchingModel = ref(false)
const switchingModelId = ref('')
const syncing = ref(false)

// 续费对话框
const renewDialogVisible = ref(false)
const renewModelId = ref('')
const renewingModel = ref('')

// 角色定义
const roleRequirements = ref([])

// 要求列表
const requirements = ref([])

// 表结构配置
const tableFilter = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref({
  sortOrder: 1,
  requirement: '',
  category: '',
  enabled: 'Y'
})

// 表结构对话框
const tableDialogVisible = ref(false)
const isEditTable = ref(false)
const savingTable = ref(false)
const tableFormData = ref({
  tableName: '',
  tableComment: '',
  enabled: 'Y',
  sortOrder: 99
})

// 刷新缓存
const refreshing = ref(false)

// 加载 AI 版本
const loadAiVersion = async () => {
  try {
    const res = await request({
      url: '/ai/version',
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
      url: '/ai/switch-version',
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
      url: '/ai/current-model',
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
  await loadModelList()
  modelDialogVisible.value = true
}

// 加载模型列表
const loadModelList = async () => {
  try {
    const token = localStorage.getItem('token') || ''
    const res = await fetch('http://47.100.138.89:8110/api/ai-model/list' + (token ? '?token=' + token : ''))
    const data = await res.json()
    if (data.success) {
      modelList.value = data.datas || []
    }
  } catch (error) {
    ElMessage.error('加载模型列表失败：' + error.message)
  }
}

// 同步阿里云模型
const syncModels = async () => {
  syncing.value = true
  try {
    const token = localStorage.getItem('token') || ''
    const res = await fetch('http://47.100.138.89:8110/api/ai/sync-models' + (token ? '?token=' + token : ''), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    })
    const data = await res.json()
    if (data.success) {
      ElMessage.success('✅ ' + (data.message || '同步成功'))
      // 刷新模型列表
      await loadModelList()
    } else {
      ElMessage.error('同步失败：' + (data.message || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('同步失败：' + error.message)
  } finally {
    syncing.value = false
  }
}

// 切换到指定模型
const switchToModel = async (row) => {
  switchingModel.value = true
  switchingModelId.value = row.MODEL_ID
  try {
    const token = localStorage.getItem('token') || ''
    const res = await fetch('http://47.100.138.89:8110/api/ai-model/switch' + (token ? '?token=' + token : ''), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ modelId: row.MODEL_ID })
    })
    const data = await res.json()
    if (data.success) {
      ElMessage.success('✅ 已切换到 ' + (row.MODEL_NAME || row.MODEL_ID))
      currentModel.value = row.MODEL_ID
      modelDialogVisible.value = false
      // 刷新 Prompt 缓存
      await refreshCache()
    } else {
      ElMessage.error('切换失败：' + (data.message || data.serviceDescription))
    }
  } catch (error) {
    ElMessage.error('切换失败：' + error.message)
  } finally {
    switchingModel.value = false
    switchingModelId.value = ''
  }
}

// 显示续费对话框
const showRenewDialog = (row) => {
  renewModelId.value = row.MODEL_ID
  renewDialogVisible.value = true
}

// 确认续费
const confirmRenew = async () => {
  renewingModel.value = true
  try {
    const token = localStorage.getItem('token') || ''
    const res = await fetch('http://47.100.138.89:8110/api/ai-model/renew' + (token ? '?token=' + token : ''), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ modelId: renewModelId.value })
    })
    const data = await res.json()
    if (data.success) {
      ElMessage.success('✅ 模型状态已更新为可用')
      renewDialogVisible.value = false
      // 刷新模型列表
      await loadModelList()
    } else {
      ElMessage.error('更新失败：' + (data.message || data.serviceDescription))
    }
  } catch (error) {
    ElMessage.error('更新失败：' + error.message)
  } finally {
    renewingModel.value = false
  }
}

// 刷新缓存
const refreshCache = async () => {
  refreshing.value = true
  try {
    const res = await request({
      url: '/prompt-config/refresh',
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
      url: '/prompt-config/requirements',
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
    sortOrder: roleRequirements.value.length + requirements.value.length + 1,
    requirement: '',
    category: category,
    enabled: 'Y'
  }
  dialogVisible.value = true
}

// 编辑配置
const editRequirement = (row) => {
  dialogTitle.value = '编辑配置'
  formData.value = {
    sortOrder: row.SORT_ORDER,
    requirement: row.REQUIREMENT,
    category: row.CATEGORY,
    enabled: row.ENABLED
  }
  dialogVisible.value = true
}

// 保存配置
const saveRequirement = async () => {
  try {
    const res = await request({
      url: '/prompt-config/requirements',
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
      url: '/prompt-config/requirements/' + sortOrder,
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

// 加载表结构配置
const loadTableFilter = async () => {
  try {
    const res = await request({
      url: '/prompt-config/table-filter',
      method: 'get'
    })
    
    if (res.success) {
      tableFilter.value = res.data || []
    }
  } catch (error) {
    console.error('加载表结构配置失败:', error)
  }
}

// 显示添加表结构对话框
const showAddTableDialog = () => {
  isEditTable.value = false
  tableFormData.value = {
    tableName: '',
    tableComment: '',
    enabled: 'Y',
    sortOrder: 99
  }
  tableDialogVisible.value = true
}

// 编辑表结构
const editTableFilter = (row) => {
  isEditTable.value = true
  tableFormData.value = {
    tableName: row.TABLE_NAME,
    tableComment: row.TABLE_COMMENT,
    enabled: row.ENABLED,
    sortOrder: row.SORT_ORDER
  }
  tableDialogVisible.value = true
}

// 保存表结构
const saveTableFilter = async () => {
  if (!tableFormData.value.tableName.trim()) {
    ElMessage.warning('请输入表名')
    return
  }
  if (!tableFormData.value.tableComment.trim()) {
    ElMessage.warning('请输入表注释')
    return
  }
  
  savingTable.value = true
  try {
    const url = isEditTable.value 
      ? `/prompt-config/table-filter/${tableFormData.value.tableName}`
      : '/prompt-config/table-filter'
    
    // 后端期望大写字段名
    const requestData = {
      tableName: tableFormData.value.tableName,
      tableComment: tableFormData.value.tableComment,
      enabled: tableFormData.value.enabled,
      sortOrder: tableFormData.value.sortOrder
    }
    
    const res = await request({
      url: url,
      method: isEditTable.value ? 'put' : 'post',
      data: requestData
    })
    
    if (res.success) {
      ElMessage.success('保存成功，配置已立刻生效！')
      tableDialogVisible.value = false
      await loadTableFilter()
      await refreshCache()
    } else {
      ElMessage.error('保存失败：' + res.message)
    }
  } catch (error) {
    console.error('保存表结构失败:', error)
    ElMessage.error('保存失败：' + error.message)
  } finally {
    savingTable.value = false
  }
}

// 删除表结构
const deleteTableFilter = async (tableName) => {
  try {
    await ElMessageBox.confirm(`确定要删除表 ${tableName} 的配置吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request({
      url: `/prompt-config/table-filter/${tableName}`,
      method: 'delete'
    })
    
    if (res.success) {
      ElMessage.success('删除成功，配置已立刻生效！')
      await loadTableFilter()
      await refreshCache()
    } else {
      ElMessage.error('删除失败：' + res.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除表结构失败:', error)
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
  loadTableFilter()
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

/* 移动端优化 */
@media (max-width: 768px) {
  .prompt-config-page {
    padding: 10px;
  }
  
  .model-switch-dialog :deep(.el-dialog__body) {
    padding: 10px;
  }
}
</style>
