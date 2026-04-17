<template>
  <div class="system-dept-container">
    <el-row :gutter="20">
      <!-- 左侧树形结构 -->
      <el-col :span="10">
        <el-card class="tree-card">
          <template #header>
            <div class="card-header">
              <span>部门树</span>
              <el-button type="primary" size="small" :icon="Plus" @click="handleAdd(null)">
                新增
              </el-button>
            </div>
          </template>
          <el-input
            v-model="searchText"
            placeholder="搜索部门"
            prefix-icon="Search"
            clearable
            style="margin-bottom: 12px"
          />
          <el-tree
            ref="treeRef"
            :data="deptTree"
            :props="{ label: 'deptName', children: 'children' }"
            node-key="id"
            default-expand-all
            highlight-current
            :filter-node-method="filterNode"
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <el-icon><OfficeBuilding /></el-icon>
                <span>{{ node.label }}</span>
                <el-tag v-if="data.leader" size="small" type="info">{{ data.leader }}</el-tag>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧详情表单 -->
      <el-col :span="14">
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <span>{{ formData.id ? '编辑部门' : '新增部门' }}</span>
              <div>
                <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
                <el-button type="danger" :disabled="!formData.id" @click="handleDelete">删除</el-button>
              </div>
            </div>
          </template>
          <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select
                v-model="formData.parentId"
                :data="deptTree"
                :props="{ label: 'deptName', children: 'children', value: 'id' }"
                placeholder="选择上级部门"
                check-strictly
                clearable
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="formData.deptName" placeholder="请输入部门名称" />
            </el-form-item>
            <el-form-item label="部门编码" prop="deptCode">
              <el-input v-model="formData.deptCode" placeholder="请输入部门编码" />
            </el-form-item>
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="formData.leader" placeholder="请输入负责人" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
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
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, OfficeBuilding } from '@element-plus/icons-vue'
import { getDeptTree, createDept, updateDept, deleteDept, type Dept } from '@/api/system'

const treeRef = ref()
const formRef = ref<FormInstance>()
const saveLoading = ref(false)
const searchText = ref('')

const deptTree = ref<Dept[]>([])

const formData = reactive<Dept>({
  parentId: 0,
  deptName: '',
  deptCode: '',
  leader: '',
  phone: '',
  email: '',
  sort: 0,
  status: 1
})

const rules: FormRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  deptCode: [{ required: true, message: '请输入部门编码', trigger: 'blur' }]
}

watch(searchText, (val) => {
  treeRef.value?.filter(val)
})

const filterNode = (value: string, data: Dept) => {
  if (!value) return true
  return data.deptName.includes(value)
}

// 加载部门树
const loadDeptTree = async () => {
  try {
    const res = await getDeptTree()
    if (res.success && res.datas) {
      deptTree.value = res.datas
    }
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

const handleNodeClick = (data: Dept) => {
  Object.assign(formData, { ...data })
}

const handleAdd = (node: any) => {
  if (node) {
    formData.parentId = node.id
  } else {
    Object.assign(formData, {
      id: undefined,
      parentId: 0,
      deptName: '',
      deptCode: '',
      leader: '',
      phone: '',
      email: '',
      sort: 0,
      status: 1
    })
  }
  formRef.value?.clearValidate()
}

const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        const res = formData.id
          ? await updateDept(formData.id, formData)
          : await createDept(formData)
        if (res.success) {
          ElMessage.success(formData.id ? '保存成功' : '新增成功')
          loadDeptTree()
        }
      } catch (error) {
        console.error('操作失败:', error)
      } finally {
        saveLoading.value = false
      }
    }
  })
}

const handleDelete = () => {
  if (!formData.id) return
  ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteDept(formData.id!)
      if (res.success) {
        ElMessage.success('删除成功')
        loadDeptTree()
        handleAdd(null)
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

onMounted(() => {
  loadDeptTree()
  handleAdd(null)
})
</script>

<style lang="scss" scoped>
.system-dept-container {
  .tree-card,
  .form-card {
    min-height: 600px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .tree-node {
    display: flex;
    align-items: center;
    gap: 6px;

    .el-icon {
      font-size: 16px;
    }
  }
}
</style>
