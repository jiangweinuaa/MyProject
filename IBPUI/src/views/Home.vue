<template>
  <div class="home-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <span>欢迎使用 {{ shopName }}</span>
          <span class="system-date">{{ systemDate }}</span>
        </div>
      </template>
      <div class="welcome-content">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-statistic title="待办事项" :value="todoCount">
              <template #suffix>
                <el-badge :value="todoCount" :hidden="todoCount === 0" />
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="系统监控" :value="monitorCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="异常报告" :value="errorCount">
              <template #suffix>
                <el-badge :value="errorCount" type="danger" :hidden="errorCount === 0" />
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="在线用户" :value="onlineCount" />
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 待办事项 -->
    <el-row :gutter="20" class="mt-2">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>待办事项</span>
            <el-button type="text" size="small" @click="loadToDo">刷新</el-button>
          </template>
          <el-table :data="todoList" style="width: 100%" :height="300">
            <el-table-column prop="modularName" label="模块" />
            <el-table-column prop="todoCount" label="数量" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.todoCount > 0 ? 'danger' : 'info'">{{ row.todoCount }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #default="{ row }">
                <el-button type="text" size="small" @click="handleTodoClick(row)" :disabled="row.todoCount === 0">
                  处理
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 系统公告/快捷入口 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="quick-links">
            <el-button 
              v-for="menu in quickMenus" 
              :key="menu.proName"
              :icon="menu.icon || 'Folder'" 
              circle
              @click="handleQuickClick(menu)"
            />
          </div>
        </el-card>

        <el-card class="mt-2">
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="1" size="small">
            <el-descriptions-item label="企业编号">{{ userInfo?.eId }}</el-descriptions-item>
            <el-descriptions-item label="门店名称">{{ userInfo?.shopName }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userInfo?.username }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统监控（如果有数据） -->
    <el-row :gutter="20" class="mt-2" v-if="mainInfo.monitor && mainInfo.monitor.length > 0">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>系统监控</span>
            <el-button type="text" size="small" @click="loadMainInfo">刷新</el-button>
          </template>
          <el-table :data="mainInfo.monitor" style="width: 100%" :height="200">
            <el-table-column prop="itemName" label="监控项" />
            <el-table-column prop="itemValue" label="状态值" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '正常' ? 'success' : 'danger'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getToDoList, getServerDate, getMainInfo } from '@/api/home'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const shopName = computed(() => userInfo.value?.shopName || 'IBPUI')
const systemDate = ref('')

// 待办事项
const todoList = ref<any[]>([])
const todoCount = computed(() => {
  return todoList.value.reduce((sum, item) => sum + (item.todoCount || 0), 0)
})

// 系统监控
const mainInfo = reactive({
  monitor: [],
  monitorSpace: [],
  diskSpace: [],
  etlDown: [],
  wsDown: [],
  erpDown: [],
  etlUp: [],
  wsUp: [],
  erpUp: [],
  eDate: [],
  job: [],
  machine: [],
  version: []
})

const monitorCount = computed(() => mainInfo.monitor?.length || 0)
const errorCount = ref(0)
const onlineCount = ref(0)

// 快捷菜单（从 myPower 读取常用功能）
const quickMenus = ref<any[]>([])

// 加载待办事项
const loadToDo = async () => {
  try {
    const res = await getToDoList()
    if (res.success && res.datas) {
      todoList.value = res.datas || []
    }
  } catch (error) {
    console.error('加载待办事项失败:', error)
  }
}

// 加载系统日期
const loadServerDate = async () => {
  try {
    const res = await getServerDate()
    if (res.success && res.curDate) {
      systemDate.value = res.curDate
    }
  } catch (error) {
    console.error('加载系统日期失败:', error)
  }
}

// 加载系统监控
const loadMainInfo = async () => {
  try {
    const res = await getMainInfo()
    if (res.success && res.datas) {
      Object.assign(mainInfo, res.datas)
    }
  } catch (error) {
    console.error('加载系统监控失败:', error)
  }
}

// 处理待办点击
const handleTodoClick = (row: any) => {
  if (row.proName) {
    router.push(`/${row.proName}`)
  }
}

// 处理快捷菜单点击
const handleQuickClick = (menu: any) => {
  if (menu.path) {
    router.push(menu.path)
  }
}

// 初始化快捷菜单
const initQuickMenus = () => {
  const myPower = userInfo.value?.myPower || []
  // 取前 6 个一级菜单作为快捷入口
  quickMenus.value = myPower
    .filter((item: any) => item.modularLevel === '1')
    .slice(0, 6)
    .map((item: any) => ({
      proName: item.proName,
      icon: item.icon || 'Folder',
      path: `/${item.proName}`,
      modularName: item.modularName
    }))
}

onMounted(() => {
  initQuickMenus()
  loadToDo()
  loadServerDate()
  loadMainInfo()
})
</script>

<style lang="scss" scoped>
.home-container {
  .welcome-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .system-date {
        font-size: 14px;
        color: #909399;
      }
    }

    .welcome-content {
      padding: 20px 0;
    }
  }

  .quick-links {
    display: flex;
    gap: 16px;
    justify-content: center;
    padding: 20px 0;
    flex-wrap: wrap;
  }

  .mt-2 {
    margin-top: 16px;
  }
}
</style>
