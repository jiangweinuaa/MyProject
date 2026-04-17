<template>
  <div class="system-user-container">
    <!-- 搜索表单 -->
    <SearchForm v-model="searchForm" @search="handleSearch" @reset="handleReset">
      <template #form-items>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" :body-style="{ padding: '12px 16px' }">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
        <el-button type="danger" :icon="Delete" :disabled="!selectedRows.length" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <BaseTable
      :data="tableData"
      :loading="loading"
      :total="total"
      :page="page"
      :page-size="pageSize"
      selection
      index
      operations
      @update:page="handlePageChange"
      @update:page-size="handlePageSizeChange"
      @page-change="handlePageChange"
    >
      <template #columns>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="roleName" label="角色" width="120" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
      </template>

      <template #operations="{ row }">
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseTable>

    <!-- 新增/编辑对话框 -->
    <BaseDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      :confirm-loading="dialogLoading"
      width="600px"
      @confirm="handleDialogConfirm"
      @cancel="handleDialogCancel"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="formData.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </BaseDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm.vue'
import BaseTable from '@/components/BaseTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'
import { getUserList, createUser, updateUser, deleteUser, type User } from '@/api/system'

const loading = ref(false)
const tableData = ref<User[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const selectedRows = ref<User[]>([])

const searchForm = reactive({
  username: '',
  phone: '',
  status: null as number | null
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<User>({
  username: '',
  phone: '',
  email: '',
  roleId: undefined,
  status: 1
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: page.value,
      pageSize: pageSize.value,
      username: searchForm.username,
      phone: searchForm.phone,
      status: searchForm.status
    })
    if (res.success && res.datas) {
      tableData.value = res.datas.list || []
      total.value = res.datas.total || 0
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadData()
}

const handleReset = () => {
  page.value = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  Object.assign(formData, { username: '', phone: '', email: '', roleId: null, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: User) => {
  dialogTitle.value = '编辑用户'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

const handleDelete = (row: User) => {
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteUser(row.id!)
      if (res.success) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    // 批量删除逻辑
    ElMessage.success('批量删除成功')
    loadData()
  })
}

const handleDialogConfirm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      dialogLoading.value = true
      try {
        const res = formData.id 
          ? await updateUser(formData.id, formData)
          : await createUser(formData)
        if (res.success) {
          ElMessage.success(formData.id ? '编辑成功' : '新增成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        console.error('操作失败:', error)
      } finally {
        dialogLoading.value = false
      }
    }
  })
}

const handleDialogCancel = () => {
  formRef.value?.resetFields()
  dialogVisible.value = false
}

const handlePageChange = (newPage: number, newSize?: number) => {
  page.value = newPage
  if (newSize) pageSize.value = newSize
  loadData()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.system-user-container {
  .toolbar-card {
    margin-bottom: 16px;

    .toolbar {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
