<template>
  <div class="system-post-container">
    <!-- 搜索表单 -->
    <SearchForm v-model="searchForm" @search="handleSearch" @reset="handleReset">
      <template #form-items>
        <el-form-item label="岗位名称">
          <el-input v-model="searchForm.postName" placeholder="请输入岗位名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="岗位编码">
          <el-input v-model="searchForm.postCode" placeholder="请输入岗位编码" clearable style="width: 200px" />
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
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增岗位</el-button>
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
        <el-table-column prop="postCode" label="岗位编码" width="150" />
        <el-table-column prop="postName" label="岗位名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
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
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="formData.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="formData.postCode" placeholder="请输入岗位编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入岗位描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%" />
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
import { getPostList, createPost, updatePost, deletePost, type Post } from '@/api/system'

const loading = ref(false)
const tableData = ref<Post[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const selectedRows = ref<Post[]>([])

const searchForm = reactive({
  postName: '',
  postCode: '',
  status: null as number | null
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<Post>({
  postCode: '',
  postName: '',
  description: '',
  sort: 0,
  status: 1
})

const rules: FormRules = {
  postName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  postCode: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getPostList({
      page: page.value,
      pageSize: pageSize.value,
      postName: searchForm.postName,
      postCode: searchForm.postCode,
      status: searchForm.status
    })
    if (res.success && res.datas) {
      tableData.value = res.datas.list || []
      total.value = res.datas.total || 0
    }
  } catch (error) {
    console.error('加载岗位列表失败:', error)
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
  dialogTitle.value = '新增岗位'
  Object.assign(formData, { postCode: '', postName: '', description: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: Post) => {
  dialogTitle.value = '编辑岗位'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

const handleDelete = (row: Post) => {
  ElMessageBox.confirm(`确定要删除岗位 "${row.postName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deletePost(row.id!)
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个岗位吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
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
          ? await updatePost(formData.id, formData)
          : await createPost(formData)
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
.system-post-container {
  .toolbar-card {
    margin-bottom: 16px;

    .toolbar {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
