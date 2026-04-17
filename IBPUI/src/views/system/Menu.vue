<template>
  <div class="system-menu-container">
    <el-row :gutter="20">
      <!-- 左侧树形结构 -->
      <el-col :span="8">
        <el-card class="tree-card">
          <template #header>
            <div class="card-header">
              <span>菜单树</span>
              <el-button type="primary" size="small" :icon="Plus" @click="handleAdd(null)">
                新增
              </el-button>
            </div>
          </template>
          <el-tree
            ref="treeRef"
            :data="menuTree"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            default-expand-all
            highlight-current
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <el-icon v-if="data.icon"><component :is="data.icon" /></el-icon>
                <span>{{ node.label }}</span>
                <el-tag v-if="data.type === 1" size="small" type="info">目录</el-tag>
                <el-tag v-else-if="data.type === 2" size="small">菜单</el-tag>
                <el-tag v-else-if="data.type === 3" size="small" type="warning">按钮</el-tag>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧详情表单 -->
      <el-col :span="16">
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <span>{{ formData.id ? '编辑菜单' : '新增菜单' }}</span>
              <div>
                <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
                <el-button type="danger" :disabled="!formData.id" @click="handleDelete">删除</el-button>
              </div>
            </div>
          </template>
          <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
            <el-form-item label="上级菜单" prop="parentId">
              <el-tree-select
                v-model="formData.parentId"
                :data="menuTree"
                :props="{ label: 'name', children: 'children', value: 'id' }"
                placeholder="选择上级菜单"
                check-strictly
                clearable
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="菜单名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入菜单名称" />
            </el-form-item>
            <el-form-item label="菜单类型" prop="type">
              <el-radio-group v-model="formData.type">
                <el-radio :label="1">目录</el-radio>
                <el-radio :label="2">菜单</el-radio>
                <el-radio :label="3">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="formData.type !== 3" label="路由路径" prop="path">
              <el-input v-model="formData.path" placeholder="如：/system/user" />
            </el-form-item>
            <el-form-item v-if="formData.type === 2" label="组件路径" prop="component">
              <el-input v-model="formData.component" placeholder="如：views/system/User.vue" />
            </el-form-item>
            <el-form-item label="图标" prop="icon">
              <el-input v-model="formData.icon" placeholder="如图标：User, Home, Setting" />
            </el-form-item>
            <el-form-item v-if="formData.type === 3" label="权限标识" prop="permission">
              <el-input v-model="formData.permission" placeholder="如：system:user:add" />
            </el-form-item>
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
            <el-form-item label="是否显示" prop="visible">
              <el-radio-group v-model="formData.visible">
                <el-radio :label="1">显示</el-radio>
                <el-radio :label="0">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMenuTree, createMenu, updateMenu, deleteMenu, type Menu } from '@/api/system'

const formRef = ref<FormInstance>()
const saveLoading = ref(false)

const menuTree = ref<Menu[]>([])

const formData = reactive<Menu>({
  parentId: 0,
  name: '',
  path: '',
  component: '',
  icon: '',
  type: 2,
  permission: '',
  sort: 0,
  visible: 1
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
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

const handleNodeClick = (data: Menu) => {
  Object.assign(formData, { ...data })
}

const handleAdd = (node: any) => {
  if (node) {
    formData.parentId = node.id
    formData.type = node.type === 1 ? 2 : 3
  } else {
    Object.assign(formData, {
      id: undefined,
      parentId: 0,
      name: '',
      path: '',
      component: '',
      icon: '',
      type: 1,
      permission: '',
      sort: 0,
      visible: 1
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
          ? await updateMenu(formData.id, formData)
          : await createMenu(formData)
        if (res.success) {
          ElMessage.success(formData.id ? '保存成功' : '新增成功')
          loadMenuTree()
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
  ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteMenu(formData.id!)
      if (res.success) {
        ElMessage.success('删除成功')
        loadMenuTree()
        handleAdd(null)
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

onMounted(() => {
  loadMenuTree()
  handleAdd(null)
})
</script>

<style lang="scss" scoped>
.system-menu-container {
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
