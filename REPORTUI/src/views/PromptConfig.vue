<template>
  <div class="prompt-config-page">
    <div class="config-container">
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
      :title="isEdit ? '编辑要求' : '添加要求'"
      width="600px"
    >
      <el-form :model="formData" label-width="80px">
        <el-form-item label="要求内容" required>
          <el-input v-model="formData.requirement" type="textarea" :rows="3" placeholder="请输入要求内容" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="formData.category" placeholder="请选择分类">
            <el-option label="GENERAL" value="GENERAL" />
            <el-option label="SECURITY" value="SECURITY" />
            <el-option label="ORACLE" value="ORACLE" />
            <el-option label="FORMAT" value="FORMAT" />
            <el-option label="PERFORMANCE" value="PERFORMANCE" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="formData.enabled" active-value="Y" inactive-value="N" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="备注说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRequirement" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 角色定义列表
const roleRequirements = ref([])

// 要求列表
const requirements = ref([])
const refreshing = ref(false)

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const defaultCategory = ref('GENERAL')

// 表单数据
const formData = ref({
  requirement: '',
  category: 'GENERAL',
  enabled: 'Y',
  sortOrder: 99,
  remark: ''
})

// 获取分类对应的标签颜色
const getCategoryType = (category) => {
  const types = {
    'GENERAL': '',
    'SECURITY': 'danger',
    'ORACLE': 'warning',
    'FORMAT': 'primary',
    'PERFORMANCE': 'success'
  }
  return types[category] || ''
}

// 加载角色定义
const loadRoleRequirements = async () => {
  try {
    const res = await fetch('http://47.100.138.89:8110/api/prompt-config/requirements')
    const data = await res.json()
    if (data.success) {
      roleRequirements.value = (data.data || []).filter(item => item.CATEGORY === 'ROLE')
      requirements.value = (data.data || []).filter(item => item.CATEGORY !== 'ROLE')
    }
  } catch (error) {
    ElMessage.error('加载配置失败：' + error.message)
  }
}

// 刷新缓存
const refreshCache = async () => {
  refreshing.value = true
  try {
    const res = await fetch('http://47.100.138.89:8110/api/prompt-config/refresh', {
      method: 'POST'
    })
    const data = await res.json()
    if (data.success) {
      ElMessage.success('✅ 配置已立刻生效！')
      // 重新加载数据
      await loadRoleRequirements()
    } else {
      ElMessage.error('刷新失败：' + data.message)
    }
  } catch (error) {
    ElMessage.error('刷新失败：' + error.message)
  } finally {
    refreshing.value = false
  }
}

// 显示添加对话框
const showAddDialog = (category = 'GENERAL') => {
  isEdit.value = false
  defaultCategory.value = category
  formData.value = {
    requirement: category === 'ROLE' ? '你是一个专业的 Oracle SQL 专家，请根据以下数据库表结构，将用户的自然语言问题转换为 Oracle SQL 查询。' : '',
    category: category,
    enabled: 'Y',
    sortOrder: 99,
    remark: ''
  }
  dialogVisible.value = true
}

// 编辑要求
const editRequirement = (row) => {
  isEdit.value = true
  formData.value = {
    requirement: row.REQUIREMENT,
    category: row.CATEGORY,
    enabled: row.ENABLED,
    sortOrder: row.SORT_ORDER,
    remark: row.REMARK || ''
  }
  dialogVisible.value = true
}

// 保存要求
const saveRequirement = async () => {
  if (!formData.value.requirement.trim()) {
    ElMessage.warning('请输入要求内容')
    return
  }

  saving.value = true
  try {
    const url = isEdit.value
      ? `http://47.100.138.89:8110/api/prompt-config/requirements/${formData.value.sortOrder}`
      : 'http://47.100.138.89:8110/api/prompt-config/requirements'
    
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData.value)
    })
    const data = await res.json()
    if (data.success) {
      ElMessage.success('保存成功，配置已立刻生效！')
      dialogVisible.value = false
      await loadRequirements()
    } else {
      ElMessage.error('保存失败：' + data.message)
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  } finally {
    saving.value = false
  }
}

// 删除要求
const deleteRequirement = async (sortOrder) => {
  try {
    await ElMessageBox.confirm('确定要删除这条要求吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await fetch(`http://47.100.138.89:8110/api/prompt-config/requirements/${sortOrder}`, {
      method: 'DELETE'
    })
    const data = await res.json()
    if (data.success) {
      ElMessage.success('删除成功，配置已立刻生效！')
      await loadRequirements()
    } else {
      ElMessage.error('删除失败：' + data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

onMounted(() => {
  loadRoleRequirements()
})
</script>

<style scoped>
.prompt-config-page {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.config-container {
  max-width: 1400px;
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

.card-header span {
  font-size: 16px;
  font-weight: bold;
}
</style>
