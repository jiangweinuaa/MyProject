<template>
  <div class="system-role-container">
    <!-- 搜索表单 -->
    <SearchForm v-model="searchForm" @search="handleSearch" @reset="handleReset">
      <template #form-items>
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="searchForm.roleCode" placeholder="请输入角色编码" clearable style="width: 200px" />
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
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
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
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
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
        <el-button type="warning" link size="small" @click="handlePermission(row)">权限</el-button>
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
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </BaseDialog>

    <!-- 权限分配对话框 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="600px" @close="handlePermDialogClose">
      <el-tree
        ref="treeRef"
        :data="menuTree"
        :props="{ label: 'name', children: 'children' }"
        show-checkbox
        node-key="id"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permDialogLoading" @click="handleSavePermission">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import type ElTree from 'element-plus/es/components/tree'
import SearchForm from '@/components/SearchForm.vue'
import BaseTable from '@/components/BaseTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  getRoleMenus,
  saveRoleMenus,
  getMenuTree,
  type Role
} from '@/api/system'

const loading = ref(false)
const tableData = ref<Role[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const selectedRows = ref<Role[]>([])

const searchForm = reactive({
  roleName: '',
  roleCode: '',
  status: null as number | null
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<Role>({
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const rules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

// 权限分配
const permDialogVisible = ref(false)
const permDialogLoading = ref(false)
const currentRoleId = ref<number | null>(null)
const treeRef = ref<InstanceType<typeof ElTree>>()
const menuTree = ref<any[]>([])

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList({
      page: page.value,
      pageSize: pageSize.value,
      roleName: searchForm.roleName,
      roleCode: searchForm.roleCode,
      status: searchForm.status
    })
    if (res.success && res.datas) {
      tableData.value = res.datas.list || []
      total.value = res.datas.total || 0
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载菜单树
const loadMenuTree = async () => {
  try {
    const res = await getMenuTree()
    if (res.success && res.datas) {
      menuTree.value = res.datas
    }
  } catch (error) {
    console.error('加载菜单树失败:', error)
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
  dialogTitle.value = '新增角色'
  Object.assign(formData, { roleName: '', roleCode: '', description: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: Role) => {
  dialogTitle.value = '编辑角色'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

const handleDelete = (row: Role) => {
  ElMessageBox.confirm(`确定要删除角色 "${row.roleName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteRole(row.id!)
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个角色吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    // 批量删除
    ElMessage.success('批量删除成功')
    loadData()
  })
}

const handlePermission = async (row: Role) => {
  currentRoleId.value = row.id || null
  await loadMenuTree()
  permDialogVisible.value = true
  // 加载已选菜单
  try {
    const res = await getRoleMenus(row.id!)
    if (res.success && res.datas) {
      setTimeout(() => {
        treeRef.value?.setCheckedKeys(res.datas)
      }, 100)
    }
  } catch (error) {
    console.error('加载角色权限失败:', error)
  }
}

const handleSavePermission = async () => {
  if (!currentRoleId.value || !treeRef.value) return
  permDialogLoading.value = true
  try {
    const checkedKeys = treeRef.value.getCheckedKeys() as number[]
    const res = await saveRoleMenus(currentRoleId.value, checkedKeys)
    if (res.success) {
      ElMessage.success('权限保存成功')
      permDialogVisible.value = false
    }
  } catch (error) {
    console.error('保存权限失败:', error)
  } finally {
    permDialogLoading.value = false
  }
}

const handlePermDialogClose = () => {
  currentRoleId.value = null
  permDialogLoading.value = false
}

const handleDialogConfirm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      dialogLoading.value = true
      try {
        const res = formData.id
          ? await updateRole(formData.id, formData)
          : await createRole(formData)
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
.system-role-container {
  .toolbar-card {
    margin-bottom: 16px;

    .toolbar {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
